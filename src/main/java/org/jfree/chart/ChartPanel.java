package org.jfree.chart;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorManager;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.panel.Overlay;
import org.jfree.chart.event.OverlayChangeEvent;
import org.jfree.chart.event.OverlayChangeListener;
import org.jfree.chart.plot.Pannable;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.Zoomable;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.chart.util.SerialUtils;

/**
 * A Swing GUI component for displaying a {@link JFreeChart} object.
 * <P>
 * The panel registers with the chart to receive notification of changes to any
 * component of the chart.  The chart is redrawn automatically whenever this
 * notification is received.
 */
public class ChartPanel extends JPanel implements 
	ChartChangeListener,
	ChartProgressListener,
	ActionListener,
	MouseListener,
	MouseMotionListener,
	OverlayChangeListener,
	Printable,
	Serializable
{
    private static final long serialVersionUID = 6046366297214274674L;

    /**
     * Default setting for buffer usage.  The default has been changed to
     * {@code true} from version 1.0.13 onwards, because of a severe
     * performance problem with drawing the zoom rectangle using XOR (which
     * now happens only when the buffer is NOT used).
     */
    public static final boolean DEFAULT_BUFFER_USED = true;

    /** The default panel width. */
    public static final int DEFAULT_WIDTH = 1024;

    /** The default panel height. */
    public static final int DEFAULT_HEIGHT = 768;

    /** The default limit below which chart scaling kicks in. */
    public static final int DEFAULT_MINIMUM_DRAW_WIDTH = 300;

    /** The default limit below which chart scaling kicks in. */
    public static final int DEFAULT_MINIMUM_DRAW_HEIGHT = 200;

    /** The default limit above which chart scaling kicks in. */
    public static final int DEFAULT_MAXIMUM_DRAW_WIDTH = 1024;

    /** The default limit above which chart scaling kicks in. */
    public static final int DEFAULT_MAXIMUM_DRAW_HEIGHT = 768;

    /** The minimum size required to perform a zoom on a rectangle */
    public static final int DEFAULT_ZOOM_TRIGGER_DISTANCE = 10;

    /** Properties action command. */
    public static final String PROPERTIES_COMMAND = "PROPERTIES";

    /**
     * Copy action command.
     *
     * @since 1.0.13
     */
    public static final String COPY_COMMAND = "COPY";

    /** Save action command. */
    public static final String SAVE_COMMAND = "SAVE";

    /** Action command to save as PNG. */
    private static final String SAVE_AS_PNG_COMMAND = "SAVE_AS_PNG";
    
    /** Action command to save as SVG. */
    private static final String SAVE_AS_SVG_COMMAND = "SAVE_AS_SVG";
    
    /** Action command to save as PDF. */
    private static final String SAVE_AS_PDF_COMMAND = "SAVE_AS_PDF";
    
    /** Print action command. */
    public static final String PRINT_COMMAND = "PRINT";

    /** Zoom in (both axes) action command. */
    public static final String ZOOM_IN_BOTH_COMMAND = "ZOOM_IN_BOTH";

    /** Zoom in (domain axis only) action command. */
    public static final String ZOOM_IN_DOMAIN_COMMAND = "ZOOM_IN_DOMAIN";

    /** Zoom in (range axis only) action command. */
    public static final String ZOOM_IN_RANGE_COMMAND = "ZOOM_IN_RANGE";

    /** Zoom out (both axes) action command. */
    public static final String ZOOM_OUT_BOTH_COMMAND = "ZOOM_OUT_BOTH";

    /** Zoom out (domain axis only) action command. */
    public static final String ZOOM_OUT_DOMAIN_COMMAND = "ZOOM_DOMAIN_BOTH";

    /** Zoom out (range axis only) action command. */
    public static final String ZOOM_OUT_RANGE_COMMAND = "ZOOM_RANGE_BOTH";

    /** Zoom reset (both axes) action command. */
    public static final String ZOOM_RESET_BOTH_COMMAND = "ZOOM_RESET_BOTH";

    /** Zoom reset (domain axis only) action command. */
    public static final String ZOOM_RESET_DOMAIN_COMMAND = "ZOOM_RESET_DOMAIN";

    /** Zoom reset (range axis only) action command. */
    public static final String ZOOM_RESET_RANGE_COMMAND = "ZOOM_RESET_RANGE";

    /** The chart that is displayed in the panel. */
    private JFreeChart chart;

    /** Storage for registered (chart) mouse listeners. */
    private transient EventListenerList chartMouseListeners;

    /** A flag that controls whether or not the off-screen buffer is used. */
    private boolean useBuffer;

    /** A flag that indicates that the buffer should be refreshed. */
    private boolean refreshBuffer;

    /** A buffer for the rendered chart. */
    private transient Image chartBuffer;

    /** The height of the chart buffer. */
    private int chartBufferHeight;

    /** The width of the chart buffer. */
    private int chartBufferWidth;

    /**
     * The minimum width for drawing a chart (uses scaling for smaller widths).
     */
    private int minimumDrawWidth;

    /**
     * The minimum height for drawing a chart (uses scaling for smaller
     * heights).
     */
    private int minimumDrawHeight;

    /**
     * The maximum width for drawing a chart (uses scaling for bigger
     * widths).
     */
    private int maximumDrawWidth;

    /**
     * The maximum height for drawing a chart (uses scaling for bigger
     * heights).
     */
	private int maximumDrawHeight;

    /** The popup menu for the frame. */
	private JPopupMenu popup;

    /** The drawing info collected the last time the chart was drawn. */
	private ChartRenderingInfo info;

    /** The chart anchor point. */
	private Point2D anchor;

    /** The scale factor used to draw the chart. */
    private double scaleX;

    /** The scale factor used to draw the chart. */
    private double scaleY;

    /** The plot orientation. */
    private PlotOrientation orientation = PlotOrientation.VERTICAL;

    /** A flag that controls whether or not domain zooming is enabled. */
    private boolean domainZoomable = false;

    /** A flag that controls whether or not range zooming is enabled. */
    private boolean rangeZoomable = false;

    /**
     * The zoom rectangle starting point (selected by the user with a mouse
     * click).  This is a point on the screen, not the chart (which may have
     * been scaled up or down to fit the panel).
     */
    private Point2D zoomPoint = null;

    /** The zoom rectangle (selected by the user with the mouse). */
    private transient Rectangle2D zoomRectangle = null;

    /** Controls if the zoom rectangle is drawn as an outline or filled. */
    private boolean fillZoomRectangle = true;

    /** The minimum distance required to drag the mouse to trigger a zoom. */
    private int zoomTriggerDistance;

    /** Menu item for zooming in on a chart (both axes). */
    private JMenuItem zoomInBothMenuItem;

    /** Menu item for zooming in on a chart (domain axis). */
    private JMenuItem zoomInDomainMenuItem;

    /** Menu item for zooming in on a chart (range axis). */
    private JMenuItem zoomInRangeMenuItem;

    /** Menu item for zooming out on a chart. */
    private JMenuItem zoomOutBothMenuItem;

    /** Menu item for zooming out on a chart (domain axis). */
    private JMenuItem zoomOutDomainMenuItem;

    /** Menu item for zooming out on a chart (range axis). */
    private JMenuItem zoomOutRangeMenuItem;

    /** Menu item for resetting the zoom (both axes). */
    private JMenuItem zoomResetBothMenuItem;

    /** Menu item for resetting the zoom (domain axis only). */
    private JMenuItem zoomResetDomainMenuItem;

    /** Menu item for resetting the zoom (range axis only). */
    private JMenuItem zoomResetRangeMenuItem;

    /**
     * The default directory for saving charts to file.
     *
     * @since 1.0.7
     */
    private File defaultDirectoryForSaveAs;

    /** A flag that controls whether or not file extensions are enforced. */
    private boolean enforceFileExtensions;

    /** A flag that indicates if original tooltip delays are changed. */
    private boolean ownToolTipDelaysActive;

    /** Original initial tooltip delay of ToolTipManager.sharedInstance(). */
    private int originalToolTipInitialDelay;

    /** Original reshow tooltip delay of ToolTipManager.sharedInstance(). */
    private int originalToolTipReshowDelay;

    /** Original dismiss tooltip delay of ToolTipManager.sharedInstance(). */
    private int originalToolTipDismissDelay;

    /** Own initial tooltip delay to be used in this chart panel. */
    private int ownToolTipInitialDelay;

    /** Own reshow tooltip delay to be used in this chart panel. */
    private int ownToolTipReshowDelay;

    /** Own dismiss tooltip delay to be used in this chart panel. */
    private int ownToolTipDismissDelay;

    /** The factor used to zoom in on an axis range. */
    private double zoomInFactor = 0.5;

    /** The factor used to zoom out on an axis range. */
    private double zoomOutFactor = 2.0;

    /**
     * A flag that controls whether zoom operations are centred on the
     * current anchor point, or the centre point of the relevant axis.
     *
     * @since 1.0.7
     */
    private boolean zoomAroundAnchor;

    /**
     * The paint used to draw the zoom rectangle outline.
     *
     * @since 1.0.13
     */
    private transient Paint zoomOutlinePaint;

    /**
     * The zoom fill paint (should use transparency).
     *
     * @since 1.0.13
     */
    private transient Paint zoomFillPaint;

    /** The resourceBundle for the localization. */
    protected static ResourceBundle localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.LocalizationBundle");

    /** 
     * Temporary storage for the width and height of the chart 
     * drawing area during panning.
     */
	private double panW, panH;

	/** The last mouse position during panning. */
	private Point panLast;

	private Point panRangeAxisLast;
	private Point panDomainAxisLast;

    /**
     * The mask for mouse events to trigger panning.
     *
     * @since 1.0.13
     */
    //private int panMask = InputEvent.CTRL_MASK;

	/**
     * A list of overlays for the panel.
     *
     * @since 1.0.13
     */
	private List<Overlay> overlays;

	private Cursor defaultCursor 	= Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	private Cursor startMoveCursor 	= Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
	private Cursor moveCursor		= Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);

	private Cursor panDomainAxisCursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
	private Cursor panRangeAxisCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);

    /**
     * Constructs a panel that displays the specified chart.
     *
     * @param chart  the chart.
     */
	public ChartPanel(JFreeChart chart) {
        this(
        	chart,
        	DEFAULT_WIDTH,
        	DEFAULT_HEIGHT,
            DEFAULT_MINIMUM_DRAW_WIDTH,
            DEFAULT_MINIMUM_DRAW_HEIGHT,
            DEFAULT_MAXIMUM_DRAW_WIDTH,
            DEFAULT_MAXIMUM_DRAW_HEIGHT,
            DEFAULT_BUFFER_USED,
            true,  // properties
            true,  // save
            true,  // print
            true,  // zoom
            true   // tooltips
        );
	}

    /**
     * Constructs a panel containing a chart.  The {@code useBuffer} flag
     * controls whether or not an offscreen {@code BufferedImage} is
     * maintained for the chart.  If the buffer is used, more memory is
     * consumed, but panel repaints will be a lot quicker in cases where the
     * chart itself hasn't changed (for example, when another frame is moved
     * to reveal the panel).  WARNING: If you set the {@code useBuffer}
     * flag to false, note that the mouse zooming rectangle will (in that case)
     * be drawn using XOR, and there is a SEVERE performance problem with that
     * on JRE6 on Windows.
     *
     * @param chart  the chart.
     * @param useBuffer  a flag controlling whether or not an off-screen buffer
     *                   is used (read the warning above before setting this
     *                   to {@code false}).
     */
	public ChartPanel(JFreeChart chart, boolean useBuffer) {
        this(
        	chart,
        	DEFAULT_WIDTH,
        	DEFAULT_HEIGHT,
        	DEFAULT_MINIMUM_DRAW_WIDTH,
        	DEFAULT_MINIMUM_DRAW_HEIGHT,
        	DEFAULT_MAXIMUM_DRAW_WIDTH,
        	DEFAULT_MAXIMUM_DRAW_HEIGHT, useBuffer,
        	true,  // properties
        	true,  // save
        	true,  // print
        	true,  // zoom
        	true   // tooltips
        );
	}

    /**
     * Constructs a JFreeChart panel.
     *
     * @param chart  the chart.
     * @param properties  a flag indicating whether or not the chart property
     *                    editor should be available via the popup menu.
     * @param save  a flag indicating whether or not save options should be
     *              available via the popup menu.
     * @param print  a flag indicating whether or not the print option
     *               should be available via the popup menu.
     * @param zoom  a flag indicating whether or not zoom options should
     *              be added to the popup menu.
     * @param tooltips  a flag indicating whether or not tooltips should be
     *                  enabled for the chart.
     */
    public ChartPanel(JFreeChart chart, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips) {
    	this(
    		chart,
    		DEFAULT_WIDTH,
    		DEFAULT_HEIGHT,
    		DEFAULT_MINIMUM_DRAW_WIDTH,
    		DEFAULT_MINIMUM_DRAW_HEIGHT,
    		DEFAULT_MAXIMUM_DRAW_WIDTH,
    		DEFAULT_MAXIMUM_DRAW_HEIGHT,
    		DEFAULT_BUFFER_USED,
    		properties,
    		save,
    		print,
    		zoom,
            tooltips
        );
    }

    /**
     * Constructs a JFreeChart panel.
     *
     * @param chart  the chart.
     * @param width  the preferred width of the panel.
     * @param height  the preferred height of the panel.
     * @param minimumDrawWidth  the minimum drawing width.
     * @param minimumDrawHeight  the minimum drawing height.
     * @param maximumDrawWidth  the maximum drawing width.
     * @param maximumDrawHeight  the maximum drawing height.
     * @param useBuffer  a flag that indicates whether to use the off-screen
     *                   buffer to improve performance (at the expense of
     *                   memory).
     * @param properties  a flag indicating whether or not the chart property
     *                    editor should be available via the popup menu.
     * @param save  a flag indicating whether or not save options should be
     *              available via the popup menu.
     * @param print  a flag indicating whether or not the print option
     *               should be available via the popup menu.
     * @param zoom  a flag indicating whether or not zoom options should be
     *              added to the popup menu.
     * @param tooltips  a flag indicating whether or not tooltips should be
     *                  enabled for the chart.
     */
    public ChartPanel(
    	JFreeChart chart,
    	int width,
    	int height,
    	int minimumDrawWidth,
    	int minimumDrawHeight,
    	int maximumDrawWidth,
    	int maximumDrawHeight,
    	boolean useBuffer,
    	boolean properties,
    	boolean save,
    	boolean print,
    	boolean zoom,
    	boolean tooltips)
    {
    	this(
    		chart,
    		width,
    		height,
    		minimumDrawWidth,
    		minimumDrawHeight,
    		maximumDrawWidth,
    		maximumDrawHeight,
    		useBuffer,
    		properties,
    		true,
    		save,
    		print,
    		zoom, 
    		tooltips
    	);
    }

    /**
     * Constructs a JFreeChart panel.
     *
     * @param chart  the chart.
     * @param width  the preferred width of the panel.
     * @param height  the preferred height of the panel.
     * @param minimumDrawWidth  the minimum drawing width.
     * @param minimumDrawHeight  the minimum drawing height.
     * @param maximumDrawWidth  the maximum drawing width.
     * @param maximumDrawHeight  the maximum drawing height.
     * @param useBuffer  a flag that indicates whether to use the off-screen
     *                   buffer to improve performance (at the expense of
     *                   memory).
     * @param properties  a flag indicating whether or not the chart property
     *                    editor should be available via the popup menu.
     * @param copy  a flag indicating whether or not a copy option should be
     *              available via the popup menu.
     * @param save  a flag indicating whether or not save options should be
     *              available via the popup menu.
     * @param print  a flag indicating whether or not the print option
     *               should be available via the popup menu.
     * @param zoom  a flag indicating whether or not zoom options should be
     *              added to the popup menu.
     * @param tooltips  a flag indicating whether or not tooltips should be
     *                  enabled for the chart.
     *
     * @since 1.0.13
     */
	public ChartPanel(
    	JFreeChart chart,
    	int width,
    	int height,
    	int minimumDrawWidth,
    	int minimumDrawHeight,
    	int maximumDrawWidth,
    	int maximumDrawHeight,
    	boolean useBuffer,
    	boolean properties,
    	boolean copy,
    	boolean save,
    	boolean print,
    	boolean zoom,
    	boolean tooltips)
	{
		setChart(chart);

		chartMouseListeners = new EventListenerList();
        info = new ChartRenderingInfo();
        setPreferredSize(new Dimension(width, height));
        this.useBuffer = useBuffer;
        refreshBuffer = false;
        this.minimumDrawWidth 	= minimumDrawWidth;
        this.minimumDrawHeight 	= minimumDrawHeight;
        this.maximumDrawWidth 	= maximumDrawWidth;
        this.maximumDrawHeight 	= maximumDrawHeight;
        zoomTriggerDistance 	= DEFAULT_ZOOM_TRIGGER_DISTANCE;

        // set up popup menu...
        popup = null;
        if(properties || copy || save || print || zoom) {
        	popup = createPopupMenu(properties, copy, save, print, zoom);
        }

        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setDisplayToolTips(tooltips);
        addMouseListener(this);
        addMouseMotionListener(this);

        defaultDirectoryForSaveAs = null;
        enforceFileExtensions = true;

        // initialize ChartPanel-specific tool tip delays with
        // values the from ToolTipManager.sharedInstance()
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ownToolTipInitialDelay = ttm.getInitialDelay();
        ownToolTipDismissDelay = ttm.getDismissDelay();
        ownToolTipReshowDelay = ttm.getReshowDelay();

        zoomAroundAnchor 	= false;
        zoomOutlinePaint 	= Color.BLUE;
        zoomFillPaint 		= new Color(0, 0, 255, 63);

        //this.panMask = InputEvent.CTRL_MASK;
        
        // for MacOSX we can't use the CTRL key for mouse drags, see:
        // http://developer.apple.com/qa/qa2004/qa1362.html
        //String osName = System.getProperty("os.name").toLowerCase();
        //if(osName.startsWith("mac os x")) panMask = InputEvent.ALT_MASK;

        overlays = new java.util.ArrayList<Overlay>();
        
        setCursor(defaultCursor);
    }

    public void setRegularCursor(Cursor cursor) {
    	defaultCursor = cursor;
    	
    	setCursor(defaultCursor);
    }

    /**
     * Returns the chart contained in the panel.
     *
     * @return The chart (possibly {@code null}).
     */
    public JFreeChart getChart() {
        return chart;
    }

    /**
     * Sets the chart that is displayed in the panel.
     *
     * @param chart  the chart ({@code null} permitted).
     */
    public void setChart(JFreeChart chart) {
        // stop listening for changes to the existing chart
        if(chart != null) {
            chart.removeChangeListener(this);
            chart.removeProgressListener(this);
        }

        // add the new chart
        this.chart = chart;
        if(chart != null) {
        	chart.addChangeListener(this);
        	chart.addProgressListener(this);
            Plot plot = chart.getPlot();
            domainZoomable = false;
            rangeZoomable = false;
            if(plot instanceof Zoomable) {
            	Zoomable z = (Zoomable) plot;

            	domainZoomable = z.isDomainZoomable();
                rangeZoomable = z.isRangeZoomable();
                orientation = z.getOrientation();
            }
        }
        else {
        	domainZoomable = false;
        	rangeZoomable = false;
        }
        
        if(useBuffer) refreshBuffer = true;
        
        repaint();
    }

    /**
     * Returns the minimum drawing width for charts.
     * <P>
     * If the width available on the panel is less than this, then the chart is
     * drawn at the minimum width then scaled down to fit.
     *
     * @return The minimum drawing width.
     */
    public int getMinimumDrawWidth() {
        return minimumDrawWidth;
    }

    /**
     * Sets the minimum drawing width for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available width is
     * less than this amount, the chart will be drawn using the minimum width
     * then scaled down to fit the available space.
     *
     * @param width  The width.
     */
    public void setMinimumDrawWidth(int width) {
    	minimumDrawWidth = width;
    }

    /**
     * Returns the maximum drawing width for charts.
     * <P>
     * If the width available on the panel is greater than this, then the chart
     * is drawn at the maximum width then scaled up to fit.
     *
     * @return The maximum drawing width.
     */
    public int getMaximumDrawWidth() {
        return maximumDrawWidth;
    }

    /**
     * Sets the maximum drawing width for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available width is
     * greater than this amount, the chart will be drawn using the maximum
     * width then scaled up to fit the available space.
     *
     * @param width  The width.
     */
    public void setMaximumDrawWidth(int width) {
    	maximumDrawWidth = width;
    }

    /**
     * Returns the minimum drawing height for charts.
     * <P>
     * If the height available on the panel is less than this, then the chart
     * is drawn at the minimum height then scaled down to fit.
     *
     * @return The minimum drawing height.
     */
    public int getMinimumDrawHeight() {
        return minimumDrawHeight;
    }

    /**
     * Sets the minimum drawing height for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available height is
     * less than this amount, the chart will be drawn using the minimum height
     * then scaled down to fit the available space.
     *
     * @param height  The height.
     */
    public void setMinimumDrawHeight(int height) {
    	minimumDrawHeight = height;
    }

    /**
     * Returns the maximum drawing height for charts.
     * <P>
     * If the height available on the panel is greater than this, then the
     * chart is drawn at the maximum height then scaled up to fit.
     *
     * @return The maximum drawing height.
     */
    public int getMaximumDrawHeight() {
        return maximumDrawHeight;
    }

    /**
     * Sets the maximum drawing height for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available height is
     * greater than this amount, the chart will be drawn using the maximum
     * height then scaled up to fit the available space.
     *
     * @param height  The height.
     */
    public void setMaximumDrawHeight(int height) {
        this.maximumDrawHeight = height;
    }

    /**
     * Returns the X scale factor for the chart.  This will be 1.0 if no
     * scaling has been used.
     *
     * @return The scale factor.
     */
    public double getScaleX() {
        return scaleX;
    }

    /**
     * Returns the Y scale factory for the chart.  This will be 1.0 if no
     * scaling has been used.
     *
     * @return The scale factor.
     */
    public double getScaleY() {
        return scaleY;
    }

    /**
     * Returns the anchor point.
     *
     * @return The anchor point (possibly {@code null}).
     */
    public Point2D getAnchor() {
        return anchor;
    }

    /**
     * Sets the anchor point.  This method is provided for the use of
     * subclasses, not end users.
     *
     * @param anchor  the anchor point ({@code null} permitted).
     */
    protected void setAnchor(Point2D anchor) {
        this.anchor = anchor;
    }

    /**
     * Returns the popup menu.
     *
     * @return The popup menu.
     */
    public JPopupMenu getPopupMenu() {
        return popup;
    }

    /**
     * Sets the popup menu for the panel.
     *
     * @param popup  the popup menu ({@code null} permitted).
     */
    public void setPopupMenu(JPopupMenu popup) {
    	this.popup = popup;
    }

    /**
     * Returns the chart rendering info from the most recent chart redraw.
     *
     * @return The chart rendering info.
     */
    public ChartRenderingInfo getChartRenderingInfo() {
        return info;
    }

    /**
     * A convenience method that switches on mouse-based zooming.
     *
     * @param flag  {@code true} enables zooming and rectangle fill on
     *              zoom.
     */
    public void setMouseZoomable(boolean flag) {
        setMouseZoomable(flag, true);
    }

    /**
     * A convenience method that switches on mouse-based zooming.
     *
     * @param flag  {@code true} if zooming enabled
     * @param fillRectangle  {@code true} if zoom rectangle is filled,
     *                       false if rectangle is shown as outline only.
     */
    public void setMouseZoomable(boolean flag, boolean fillRectangle) {
        setDomainZoomable(flag);
        setRangeZoomable(flag);
        setFillZoomRectangle(fillRectangle);
    }

    /**
     * Returns the flag that determines whether or not zooming is enabled for
     * the domain axis.
     *
     * @return A boolean.
     */
    public boolean isDomainZoomable() {
        return domainZoomable;
    }

    /**
     * Sets the flag that controls whether or not zooming is enabled for the
     * domain axis.  A check is made to ensure that the current plot supports
     * zooming for the domain values.
     *
     * @param flag  {@code true} enables zooming if possible.
     */
    public void setDomainZoomable(boolean flag) {
    	if(flag) {
            Plot plot = chart.getPlot();
            if(plot instanceof Zoomable) {
                Zoomable z = (Zoomable) plot;
                domainZoomable = flag && (z.isDomainZoomable());
            }
    	}
    	else domainZoomable = false;
    }

    /**
     * Returns the flag that determines whether or not zooming is enabled for
     * the range axis.
     *
     * @return A boolean.
     */
    public boolean isRangeZoomable() {
        return rangeZoomable;
    }

    /**
     * A flag that controls mouse-based zooming on the vertical axis.
     *
     * @param flag  {@code true} enables zooming.
     */
	public void setRangeZoomable(boolean flag) {
		if(flag) {
			Plot plot = chart.getPlot();
			if(plot instanceof Zoomable) {
                Zoomable z = (Zoomable)plot;
                rangeZoomable = flag && (z.isRangeZoomable());
            }
		}
		else rangeZoomable = false;
	}

    /**
     * Returns the flag that controls whether or not the zoom rectangle is
     * filled when drawn.
     *
     * @return A boolean.
     */
    public boolean getFillZoomRectangle() {
        return fillZoomRectangle;
    }

    /**
     * A flag that controls how the zoom rectangle is drawn.
     *
     * @param flag  {@code true} instructs to fill the rectangle on
     *              zoom, otherwise it will be outlined.
     */
    public void setFillZoomRectangle(boolean flag) {
    	fillZoomRectangle = flag;
    }

    /**
     * Returns the zoom trigger distance.  This controls how far the mouse must
     * move before a zoom action is triggered.
     *
     * @return The distance (in Java2D units).
     */
    public int getZoomTriggerDistance() {
    	return zoomTriggerDistance;
    }

    /**
     * Sets the zoom trigger distance.  This controls how far the mouse must
     * move before a zoom action is triggered.
     *
     * @param distance  the distance (in Java2D units).
     */
    public void setZoomTriggerDistance(int distance) {
    	zoomTriggerDistance = distance;
    }

    /**
     * Returns the default directory for the "save as" option.
     *
     * @return The default directory (possibly {@code null}).
     *
     * @since 1.0.7
     */
    public File getDefaultDirectoryForSaveAs() {
        return defaultDirectoryForSaveAs;
    }

    /**
     * Sets the default directory for the "save as" option.  If you set this
     * to {@code null}, the user's default directory will be used.
     *
     * @param directory  the directory ({@code null} permitted).
     *
     * @since 1.0.7
     */
    public void setDefaultDirectoryForSaveAs(File directory) {
        if(directory != null) {
            if(!directory.isDirectory()) {
                throw new IllegalArgumentException("The 'directory' argument is not a directory.");
            }
        }
        
        defaultDirectoryForSaveAs = directory;
    }

    /**
     * Returns {@code true} if file extensions should be enforced, and
     * {@code false} otherwise.
     *
     * @return The flag.
     *
     * @see #setEnforceFileExtensions(boolean)
     */
    public boolean isEnforceFileExtensions() {
        return enforceFileExtensions;
    }

    /**
     * Sets a flag that controls whether or not file extensions are enforced.
     *
     * @param enforce  the new flag value.
     *
     * @see #isEnforceFileExtensions()
     */
    public void setEnforceFileExtensions(boolean enforce) {
    	enforceFileExtensions = enforce;
    }

    /**
     * Returns the flag that controls whether or not zoom operations are
     * centered around the current anchor point.
     *
     * @return A boolean.
     *
     * @since 1.0.7
     *
     * @see #setZoomAroundAnchor(boolean)
     */
    public boolean getZoomAroundAnchor() {
        return zoomAroundAnchor;
    }

    /**
     * Sets the flag that controls whether or not zoom operations are
     * centered around the current anchor point.
     *
     * @param zoomAroundAnchor  the new flag value.
     *
     * @since 1.0.7
     *
     * @see #getZoomAroundAnchor()
     */
    public void setZoomAroundAnchor(boolean zoomAroundAnchor) {
    	zoomAroundAnchor = zoomAroundAnchor;
    }

    /**
     * Returns the zoom rectangle fill paint.
     *
     * @return The zoom rectangle fill paint (never {@code null}).
     *
     * @see #setZoomFillPaint(java.awt.Paint)
     * @see #setFillZoomRectangle(boolean)
     *
     * @since 1.0.13
     */
    public Paint getZoomFillPaint() {
        return zoomFillPaint;
    }

    /**
     * Sets the zoom rectangle fill paint.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getZoomFillPaint()
     * @see #getFillZoomRectangle()
     *
     * @since 1.0.13
     */
    public void setZoomFillPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        
        zoomFillPaint = paint;
    }

    /**
     * Returns the zoom rectangle outline paint.
     *
     * @return The zoom rectangle outline paint (never {@code null}).
     *
     * @see #setZoomOutlinePaint(java.awt.Paint)
     * @see #setFillZoomRectangle(boolean)
     *
     * @since 1.0.13
     */
    public Paint getZoomOutlinePaint() {
        return this.zoomOutlinePaint;
    }

    /**
     * Sets the zoom rectangle outline paint.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getZoomOutlinePaint()
     * @see #getFillZoomRectangle()
     *
     * @since 1.0.13
     */
    public void setZoomOutlinePaint(Paint paint) {
        this.zoomOutlinePaint = paint;
    }

    /**
     * The mouse wheel handler.
     */
    private MouseWheelHandler mouseWheelHandler;

    /**
     * Returns {@code true} if the mouse wheel handler is enabled, and
     * {@code false} otherwise.
     *
     * @return A boolean.
     *
     * @since 1.0.13
     */
    public boolean isMouseWheelEnabled() {
        return mouseWheelHandler != null;
    }

    /**
     * Enables or disables mouse wheel support for the panel.
     *
     * @param flag  a boolean.
     *
     * @since 1.0.13
     */
    public void setMouseWheelEnabled(boolean flag) {
        if(flag && mouseWheelHandler == null) mouseWheelHandler = new MouseWheelHandler(this);
        else if(!flag && mouseWheelHandler != null) {
        	removeMouseWheelListener(mouseWheelHandler);
        	mouseWheelHandler = null;
        } 
    }

    /**
     * Add an overlay to the panel.
     *
     * @param overlay  the overlay ({@code null} not permitted).
     *
     * @since 1.0.13
     */
    public void addOverlay(Overlay overlay) {
        Args.nullNotPermitted(overlay, "overlay");
        
        overlays.add(overlay);
        overlay.addChangeListener(this);

        repaint();
    }

    /**
     * Removes an overlay from the panel.
     *
     * @param overlay  the overlay to remove ({@code null} not permitted).
     *
     * @since 1.0.13
     */
    public void removeOverlay(Overlay overlay) {
        Args.nullNotPermitted(overlay, "overlay");
        
        boolean removed = overlays.remove(overlay);
        if(removed) {
            overlay.removeChangeListener(this);
            
            repaint();
        }
    }

    /**
     * Handles a change to an overlay by repainting the panel.
     *
     * @param event  the event.
     *
     * @since 1.0.13
     */
    @Override
    public void overlayChanged(OverlayChangeEvent event) {
        repaint();
    }

    /**
     * Switches the display of tooltips for the panel on or off.  Note that
     * tooltips can only be displayed if the chart has been configured to
     * generate tooltip items.
     *
     * @param flag  {@code true} to enable tooltips, {@code false} to
     *              disable tooltips.
     */
	public void setDisplayToolTips(boolean flag) {
		if(flag) ToolTipManager.sharedInstance().registerComponent(this);
        else ToolTipManager.sharedInstance().unregisterComponent(this);
	}

    /**
     * Returns a string for the tooltip.
     *
     * @param e  the mouse event.
     *
     * @return A tool tip or {@code null} if no tooltip is available.
     */
    @Override
    public String getToolTipText(MouseEvent e) {
        String result = null;
        
        if(info != null) {
            EntityCollection entities = info.getEntityCollection();
            if(entities != null) {
                Insets insets = getInsets();
                ChartEntity entity = entities.getEntity(
                        (int) ((e.getX() - insets.left) / this.scaleX),
                        (int) ((e.getY() - insets.top) / this.scaleY));
                if(entity != null) {
                    result = entity.getToolTipText();
                }
            }
        }
        
        return result;
    }

    /**
     * Translates a Java2D point on the chart to a screen location.
     *
     * @param java2DPoint  the Java2D point.
     *
     * @return The screen location.
     */
    public Point translateJava2DToScreen(Point2D java2DPoint) {
        Insets insets = getInsets();
        int x = (int) (java2DPoint.getX() * this.scaleX + insets.left);
        int y = (int) (java2DPoint.getY() * this.scaleY + insets.top);
        
        return new Point(x, y);
    }

    /**
     * Translates a panel (component) location to a Java2D point.
     *
     * @param screenPoint  the screen location ({@code null} not
     *                     permitted).
     *
     * @return The Java2D coordinates.
     */
    public Point2D translateScreenToJava2D(Point screenPoint) {
    	Insets insets = getInsets();

    	double x = (screenPoint.getX() - insets.left) / scaleX;
    	double y = (screenPoint.getY() - insets.top) / scaleY;

    	return new Point2D.Double(x, y);
    }

    /**
     * Applies any scaling that is in effect for the chart drawing to the
     * given rectangle.
     *
     * @param rect  the rectangle ({@code null} not permitted).
     *
     * @return A new scaled rectangle.
     */
    public Rectangle2D scale(Rectangle2D rect) {
        Insets insets = getInsets();
        double x = rect.getX() * getScaleX() + insets.left;
        double y = rect.getY() * getScaleY() + insets.top;
        double w = rect.getWidth() * getScaleX();
        double h = rect.getHeight() * getScaleY();

        return new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Returns the chart entity at a given point.
     * <P>
     * This method will return null if there is (a) no entity at the given
     * point, or (b) no entity collection has been generated.
     *
     * @param viewX  the x-coordinate.
     * @param viewY  the y-coordinate.
     *
     * @return The chart entity (possibly {@code null}).
     */
    public ChartEntity getEntityForPoint(int viewX, int viewY) {
    	ChartEntity result = null;

    	if(info != null) {
            Insets insets = getInsets();
            
            double x = (viewX - insets.left) / scaleX;
            double y = (viewY - insets.top) / scaleY;
            
            EntityCollection entities = info.getEntityCollection();
            result = entities != null ? entities.getEntity(x, y) : null;
        }
        
        return result;
    }

    /**
     * Returns the flag that controls whether or not the offscreen buffer
     * needs to be refreshed.
     *
     * @return A boolean.
     */
    public boolean getRefreshBuffer() {
    	return refreshBuffer;
    }

    /**
     * Sets the refresh buffer flag.  This flag is used to avoid unnecessary
     * redrawing of the chart when the offscreen image buffer is used.
     *
     * @param flag  {@code true} indicates that the buffer should be
     *              refreshed.
     */
	public void setRefreshBuffer(boolean flag) {
		refreshBuffer = flag;
	}

    /**
     * Paints the component by drawing the chart to fill the entire component,
     * but allowing for the insets (which will be non-zero if a border has been
     * set for this component).  To increase performance (at the expense of
     * memory), an off-screen buffer image can be used.
     *
     * @param g  the graphics device for drawing on.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(chart == null) return;

        Graphics2D g2 = (Graphics2D)g.create();

        // first determine the size of the chart rendering area...
        Dimension size = getSize();
        Insets insets = getInsets();
        Rectangle2D available = new Rectangle2D.Double(insets.left, insets.top, size.getWidth() - insets.left - insets.right, size.getHeight() - insets.top - insets.bottom);

        // work out if scaling is required...
        boolean scale = false;
        double drawWidth = available.getWidth();
        double drawHeight = available.getHeight();

        scaleX = 1.0;
        scaleY = 1.0;

        if(drawWidth < minimumDrawWidth) {
        	scaleX 		= drawWidth / minimumDrawWidth;
            drawWidth 	= minimumDrawWidth;
            scale 		= true;
        }
        else if(drawWidth > maximumDrawWidth) {
        	scaleX 		= drawWidth / maximumDrawWidth;
            drawWidth 	= maximumDrawWidth;
            scale 		= true;
        }

        if(drawHeight < minimumDrawHeight) {
        	scaleY 		= drawHeight / minimumDrawHeight;
            drawHeight 	= minimumDrawHeight;
            scale 		= true;
        }
        else if(drawHeight > maximumDrawHeight) {
        	scaleY 		= drawHeight / maximumDrawHeight;
            drawHeight 	= maximumDrawHeight;
            scale 		= true;
        }

        Rectangle2D chartArea = new Rectangle2D.Double(0.0, 0.0, drawWidth, drawHeight);

        // are we using the chart buffer?
        if(useBuffer) {
            // do we need to resize the buffer?
            if((chartBuffer == null) || (chartBufferWidth != available.getWidth()) || (chartBufferHeight != available.getHeight())) {
            	chartBufferWidth = (int) available.getWidth();
            	chartBufferHeight = (int) available.getHeight();
                GraphicsConfiguration gc = g2.getDeviceConfiguration();

                chartBuffer = gc.createCompatibleImage(chartBufferWidth, chartBufferHeight, Transparency.TRANSLUCENT);
                refreshBuffer = true;
            }

            // do we need to redraw the buffer?
            if(refreshBuffer) {
            	refreshBuffer = false; // clear the flag

                Rectangle2D bufferArea = new Rectangle2D.Double(0, 0, chartBufferWidth, chartBufferHeight);

                // make the background of the buffer clear and transparent
                Graphics2D bufferG2 = (Graphics2D)chartBuffer.getGraphics();
                Composite savedComposite = bufferG2.getComposite();
                bufferG2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
                Rectangle r = new Rectangle(0, 0, chartBufferWidth, chartBufferHeight);
                bufferG2.fill(r);
                bufferG2.setComposite(savedComposite);
                
                if(scale) {
                    AffineTransform saved = bufferG2.getTransform();
                    AffineTransform st = AffineTransform.getScaleInstance(scaleX, scaleY);
                    bufferG2.transform(st);
                    chart.draw(bufferG2, chartArea, anchor, info);
                    bufferG2.setTransform(saved);
                }
                else chart.draw(bufferG2, bufferArea, anchor, info);
                
                bufferG2.dispose();
            }

            // zap the buffer onto the panel...
            g2.drawImage(chartBuffer, insets.left, insets.top, this);
        }
        else { // redrawing the chart every time...
        	AffineTransform saved = g2.getTransform();
        	g2.translate(insets.left, insets.top);

        	if(scale) {
                AffineTransform st = AffineTransform.getScaleInstance(scaleX, scaleY);
                g2.transform(st);
            }
            chart.draw(g2, chartArea, anchor, info);

            g2.setTransform(saved);
        }

        for(Overlay overlay : overlays) {
        	overlay.paintOverlay(g2, this);
        }

        // redraw the zoom rectangle (if present) - if useBuffer is false,
        // we use XOR so we can XOR the rectangle away again without redrawing
        // the chart
        drawZoomRectangle(g2, !useBuffer);

        g2.dispose();

        anchor = null;
    }

    /**
     * Receives notification of changes to the chart, and redraws the chart.
     *
     * @param event  details of the chart change event.
     */
    @Override
    public void chartChanged(ChartChangeEvent event) {
    	refreshBuffer = true;
    	Plot plot = chart.getPlot();
    	if(plot instanceof Zoomable) {
            Zoomable z = (Zoomable) plot;
            orientation = z.getOrientation();
    	}

    	repaint();
	}

    /**
     * Receives notification of a chart progress event.
     *
     * @param event  the event.
     */
    @Override
    public void chartProgress(ChartProgressEvent event) {
        // does nothing - override if necessary
    }

    /**
     * Handles action events generated by the popup menu.
     *
     * @param event  the event.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        // many of the zoom methods need a screen location - all we have is
        // the zoomPoint, but it might be null.  Here we grab the x and y
        // coordinates, or use defaults...
        double screenX = -1.0;
        double screenY = -1.0;

        if(zoomPoint != null) {
            screenX = zoomPoint.getX();
            screenY = zoomPoint.getY();
        }

        if(command.equals(PROPERTIES_COMMAND)) doEditChartProperties();
        else if(command.equals(COPY_COMMAND)) doCopy();
        else if (command.equals(SAVE_AS_PNG_COMMAND)) {
        	try {
                doSaveAs();
            }
            catch(IOException e) {
                JOptionPane.showMessageDialog(this, "I/O error occurred.", localizationResources.getString("Save_as_PNG"), JOptionPane.WARNING_MESSAGE);
            }
        }
        else if(command.equals(SAVE_AS_SVG_COMMAND)) {
            try {
                saveAsSVG(null);
            }
            catch(IOException e) {
                JOptionPane.showMessageDialog(this, "I/O error occurred.", localizationResources.getString("Save_as_SVG"), JOptionPane.WARNING_MESSAGE);
            }
        }
        else if (command.equals(SAVE_AS_PDF_COMMAND)) {
            saveAsPDF(null);
        }
        else if (command.equals(PRINT_COMMAND)) {
            createChartPrintJob();
        }
        else if (command.equals(ZOOM_IN_BOTH_COMMAND)) {
            zoomInBoth(screenX, screenY);
        }
        else if (command.equals(ZOOM_IN_DOMAIN_COMMAND)) {
            zoomInDomain(screenX, screenY);
        }
        else if (command.equals(ZOOM_IN_RANGE_COMMAND)) {
            zoomInRange(screenX, screenY);
        }
        else if (command.equals(ZOOM_OUT_BOTH_COMMAND)) {
            zoomOutBoth(screenX, screenY);
        }
        else if (command.equals(ZOOM_OUT_DOMAIN_COMMAND)) {
            zoomOutDomain(screenX, screenY);
        }
        else if (command.equals(ZOOM_OUT_RANGE_COMMAND)) {
            zoomOutRange(screenX, screenY);
        }
        else if (command.equals(ZOOM_RESET_BOTH_COMMAND)) {
            restoreAutoBounds();
        }
        else if (command.equals(ZOOM_RESET_DOMAIN_COMMAND)) {
            restoreAutoDomainBounds();
        }
        else if(command.equals(ZOOM_RESET_RANGE_COMMAND)) {
            restoreAutoRangeBounds();
        }
    }

    /**
     * Handles a 'mouse entered' event. This method changes the tooltip delays
     * of ToolTipManager.sharedInstance() to the possibly different values set
     * for this chart panel.
     *
     * @param e  the mouse event.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        if(!ownToolTipDelaysActive) {
            ToolTipManager ttm = ToolTipManager.sharedInstance();

            originalToolTipInitialDelay = ttm.getInitialDelay();
            ttm.setInitialDelay(ownToolTipInitialDelay);

            originalToolTipReshowDelay = ttm.getReshowDelay();
            ttm.setReshowDelay(ownToolTipReshowDelay);

            originalToolTipDismissDelay = ttm.getDismissDelay();
            ttm.setDismissDelay(ownToolTipDismissDelay);

            ownToolTipDelaysActive = true;
        }
    }

    /**
     * Handles a 'mouse exited' event. This method resets the tooltip delays of
     * ToolTipManager.sharedInstance() to their
     * original values in effect before mouseEntered()
     *
     * @param e  the mouse event.
     */
    @Override
    public void mouseExited(MouseEvent e) {
        if(ownToolTipDelaysActive) {
            // restore original tooltip dealys
            ToolTipManager ttm = ToolTipManager.sharedInstance();
            
            ttm.setInitialDelay(originalToolTipInitialDelay);
            ttm.setReshowDelay(originalToolTipReshowDelay);
            ttm.setDismissDelay(originalToolTipDismissDelay);

            ownToolTipDelaysActive = false;
        }
	}

    /**
     * Handles a 'mouse pressed' event.
     * <P>
     * This event is the popup trigger on Unix/Linux.  For Windows, the popup
     * trigger is the 'mouse released' event.
     *
     * @param e  The mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
    	if(chart == null) return;

    	Plot plot = chart.getPlot();

    	//int mods = e.getModifiers();

        System.out.println("MOUSE_PRESS");

        //System.out.println("IS_RGT_MOUSE: "+SwingUtilities.isRightMouseButton(e));
        //System.out.println("POPUP_TRG: "+e.isPopupTrigger());

        if(SwingUtilities.isRightMouseButton(e)) {//&& e.isPopupTrigger()
        	if(popup != null) displayPopupMenu(e.getX(), e.getY());
        }
        else {

        //if((mods & panMask) == panMask) {
        	System.out.println("PAN_START");

        	//can we pan this plot?
        	if(plot instanceof Pannable) {
        		Pannable pannable = (Pannable) plot;
        		if(pannable.isDomainPannable() || pannable.isRangePannable()) {
        			Rectangle2D screenDataArea = getScreenDataArea(e.getX(), e.getY());
	        		if(screenDataArea != null && screenDataArea.contains(e.getPoint())) {
                    	panW	=	screenDataArea.getWidth();
                    	panH	=	screenDataArea.getHeight();
                    	panLast	=	e.getPoint();

                    	//System.out.println(	getScreenDataArea()	);

                        //setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    	setCursor(startMoveCursor);
                    }
                    else {
                    	//System.out.println("222222");

                    	if(screenDataArea == null) return;

                    	setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                    	//setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                    	
                    	//System.out.println(	"PANNABLE: "+((Pannable) plot).isRangePannable()	);

                    	System.out.println(
                    		"X: "+e.getX()+
                    		"; SCDA_W: "+screenDataArea.getWidth() +
                    		"; FW: "+getWidth()
                    	);
                    	System.out.println("Y: "+e.getY()+"; SCDA_H: "+screenDataArea.getHeight() + "; FH: "+getHeight());
                    	
                    	if(	e.getX() > screenDataArea.getWidth() && e.getX() < getWidth() &&
                    		e.getY() < screenDataArea.getHeight())
                    	{
                    		setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));

                    		panRangeAxisLast = e.getPoint();
                    		panDomainAxisLast = null;
                    		panLast = null;
                    	}
                    	else if(
                    		e.getY() > screenDataArea.getHeight() && e.getY() < getHeight() &&
                    		e.getX() < screenDataArea.getWidth()
                    	)
                    	{
                    		setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));

                    		panDomainAxisLast = e.getPoint();
                    		panRangeAxisLast = null;
                    		panLast = null;
                    	}
                    	else {
                    		setCursor(defaultCursor);

                    		panDomainAxisLast = null;
                    		panRangeAxisLast = null;
                    		panLast = null;
                    	}
                    }
        		}

        		//the actual panning occurs later in the mouseDragged() 
        	}
            else System.out.println("NO_PANN");
        }

        /*
        }
        else if(zoomRectangle == null) {
        	Rectangle2D screenDataArea = getScreenDataArea(e.getX(), e.getY());
            if(screenDataArea != null) zoomPoint = getPointInRectangle(e.getX(), e.getY(), screenDataArea);
            else zoomPoint = null;

            if(e.isPopupTrigger()) {
                if(popup != null) displayPopupMenu(e.getX(), e.getY());
            }
        }
        */
	}

    /**
     * Returns a point based on (x, y) but constrained to be within the bounds
     * of the given rectangle.  This method could be moved to JCommon.
     *
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param area  the rectangle ({@code null} not permitted).
     *
     * @return A point within the rectangle.
     */
	private Point2D getPointInRectangle(int x, int y, Rectangle2D area) {
		double xx = Math.max(area.getMinX(), Math.min(x, area.getMaxX()));
		double yy = Math.max(area.getMinY(), Math.min(y, area.getMaxY()));

		return new Point2D.Double(xx, yy);
	}

    /**
     * Handles a 'mouse dragged' event.
     *
     * @param e  the mouse event.
     */
	@Override
	public void mouseDragged(MouseEvent e) {
		System.out.println("M_DRG");

		//if the popup menu has already been triggered, then ignore dragging...
		if(popup != null && popup.isShowing()) return;

		//handle panning if we have a start point
		if(panLast != null) {
    		System.out.println("PAN_LAST");

    		setCursor(moveCursor);

    		double dx = e.getX() - panLast.getX();
    		double dy = e.getY() - panLast.getY();

    		if(dx == 0.0 && dy == 0.0) return;

    		double wPercent	=	-dx / panW;
    		double hPercent	=	dy / panH;
    		boolean old 	=	chart.getPlot().isNotify();

            chart.getPlot().setNotify(false);

            Pannable p = (Pannable)chart.getPlot();
            if(p.getOrientation() == PlotOrientation.VERTICAL) {
            	System.out.println("VERT_ORIENT. H_PERC: "+hPercent);

            	System.out.println("PAN_LAST: "+panLast.getX());

            	p.panDomainAxes(wPercent, info.getPlotInfo(), panLast);
            	p.panRangeAxes(hPercent, info.getPlotInfo(), panLast);
            }
            /*
            else {
            	p.panDomainAxes(hPercent, info.getPlotInfo(), panLast);
            	p.panRangeAxes(wPercent, info.getPlotInfo(), panLast);
            }
            */

            panLast = e.getPoint();
            chart.getPlot().setNotify(old);

            return;
    	}
    	else if(panRangeAxisLast != null || panDomainAxisLast != null) {
    		System.out.println("PAN AXES");

    		Zoomable zoomable = (Zoomable)getChart().getPlot();
			PlotRenderingInfo plotRenderingInfo = getChartRenderingInfo().getPlotInfo();

    		if(panRangeAxisLast != null) {
    			System.out.println("PAN RANGE AXIS");
    			
    			setCursor(panRangeAxisCursor);

    			double dy = e.getY()-panRangeAxisLast.getY();

    			if(dy == 0.0) return;
 
    			System.out.println("DY: "+dy);

    			if(dy > 0) {
    				zoomable.zoomRangeAxes(
    					1.05,
    					plotRenderingInfo,
    					null,
    					false
    				);
    			}
    			else {
    				zoomable.zoomRangeAxes(
    					0.95,
        				plotRenderingInfo,
        				null,
        				false
        			);
    			}

    			panRangeAxisLast = e.getPoint();
    		}

    		if(panDomainAxisLast != null) {
    			System.out.println("PAN DOMAIN AXIS");
    			
    			setCursor(panDomainAxisCursor);
    			
    			double dx = e.getX()-panDomainAxisLast.getX();
    			if(dx == 0) return;
    			
    			if(dx > 0) {
    				zoomable.zoomDomainAxes(
    					1.05 ,
    					plotRenderingInfo,
    					null,
    					false
    				);
    			}
    			else {
    				zoomable.zoomDomainAxes(
    					0.95,
    					plotRenderingInfo,
    					null,
    					false
    				);
    			}

    			panDomainAxisLast = e.getPoint();
    		}

    		return;
    	}

    	// if no initial zoom point was set, ignore dragging...
    	if(zoomPoint == null) return;

    	Graphics2D g2 = (Graphics2D)getGraphics();

        // erase the previous zoom rectangle (if any).  We only need to do
        // this is we are using XOR mode, which we do when we're not using
        // the buffer (if there is a buffer, then at the end of this method we
        // just trigger a repaint)
    	if(!useBuffer) drawZoomRectangle(g2, true);

    	boolean hZoom, vZoom;
    	if(orientation == PlotOrientation.HORIZONTAL) {
        	hZoom = rangeZoomable;
        	vZoom = domainZoomable;
        }
        else {
        	hZoom = domainZoomable;
        	vZoom = rangeZoomable;
        }

    	Rectangle2D scaledDataArea = getScreenDataArea((int)zoomPoint.getX(), (int)zoomPoint.getY());
    	if(hZoom && vZoom) {
            // selected rectangle shouldn't extend outside the data area...
        	double xmax = Math.min(e.getX(), scaledDataArea.getMaxX());
        	double ymax = Math.min(e.getY(), scaledDataArea.getMaxY());

            zoomRectangle = new Rectangle2D.Double(
            	zoomPoint.getX(),
            	zoomPoint.getY(),
            	xmax - zoomPoint.getX(),
            	ymax - zoomPoint.getY()
            );
        }
        else if(hZoom) {
        	double xmax = Math.min(e.getX(), scaledDataArea.getMaxX());

        	zoomRectangle = new Rectangle2D.Double(
            	zoomPoint.getX(),
            	scaledDataArea.getMinY(),
            	xmax - zoomPoint.getX(),
            	scaledDataArea.getHeight()
            );
        }
        else if(vZoom) {
        	double ymax = Math.min(e.getY(), scaledDataArea.getMaxY());

        	zoomRectangle = new Rectangle2D.Double(
        		scaledDataArea.getMinX(),
        		zoomPoint.getY(),
        		scaledDataArea.getWidth(),
        		ymax - zoomPoint.getY()
        	);
        }

        //Draw the new zoom rectangle...
    	if(useBuffer) repaint();
    	else {
            // with no buffer, we use XOR to draw the rectangle "over" the
            // chart...
        	drawZoomRectangle(g2, true);
    	}

    	g2.dispose();
	}

    /**
     * Handles a 'mouse released' event.  On Windows, we need to check if this
     * is a popup trigger, but only if we haven't already been tracking a zoom
     * rectangle.
     *
     * @param e  information about the event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        //if we've been panning, we need to reset now that the mouse is 
        //released...

    	if(panLast != null || panRangeAxisLast != null || panDomainAxisLast != null) {
        	panLast = null;

        	panDomainAxisLast = null;
        	panRangeAxisLast = null;

        	//setCursor(Cursor.getDefaultCursor());
        	setCursor(defaultCursor);
        }
    	/*
        else if(panRangeAxisLast != null && panDomainAxisLast != null) {
        	panRangeAxisLast = null;
        	panDomainAxisLast = null;
        }
        */
        else if(zoomRectangle != null) {
        	boolean hZoom, vZoom;

        	if(orientation == PlotOrientation.HORIZONTAL) {
                hZoom = rangeZoomable;
                vZoom = domainZoomable;
        	}
        	else {
                hZoom = domainZoomable;
                vZoom = rangeZoomable;
            }

            boolean zoomTrigger1 = hZoom && Math.abs(e.getX() - zoomPoint.getX()) >= zoomTriggerDistance;
            boolean zoomTrigger2 = vZoom && Math.abs(e.getY() - zoomPoint.getY()) >= zoomTriggerDistance;
            
            if(zoomTrigger1 || zoomTrigger2) {
                if((hZoom && (e.getX() < zoomPoint.getX())) || (vZoom && (e.getY() < zoomPoint.getY()))) restoreAutoBounds();
                else {
                	double x, y, w, h;
                	Rectangle2D screenDataArea = getScreenDataArea((int)zoomPoint.getX(), (int)zoomPoint.getY());
                	double maxX = screenDataArea.getMaxX();
                	double maxY = screenDataArea.getMaxY();
                    // for mouseReleased event, (horizontalZoom || verticalZoom)
                    // will be true, so we can just test for either being false;
                    // otherwise both are true
                    
                    if(!vZoom) {
                        x = zoomPoint.getX();
                        y = screenDataArea.getMinY();
                        w = Math.min(zoomRectangle.getWidth(), maxX - zoomPoint.getX());
                        h = screenDataArea.getHeight();
                    }
                    else if(!hZoom) {
                        x = screenDataArea.getMinX();
                        y = zoomPoint.getY();
                        w = screenDataArea.getWidth();
                        h = Math.min(zoomRectangle.getHeight(), maxY - zoomPoint.getY());
                    }
                    else {
                        x = zoomPoint.getX();
                        y = zoomPoint.getY();
                        w = Math.min(this.zoomRectangle.getWidth(), maxX - zoomPoint.getX());
                        h = Math.min(this.zoomRectangle.getHeight(), maxY - zoomPoint.getY());
                    }
                    
                    Rectangle2D zoomArea = new Rectangle2D.Double(x, y, w, h);
                    zoom(zoomArea);
                }
                
                zoomPoint = null;
                zoomRectangle = null;
            }
            else {
                // erase the zoom rectangle
                Graphics2D g2 = (Graphics2D) getGraphics();
                if(useBuffer) repaint();
                else drawZoomRectangle(g2, true);
 
                g2.dispose();
                zoomPoint = null;
                zoomRectangle = null;
            }
        }
        else if(e.isPopupTrigger()) {
            if(popup != null) displayPopupMenu(e.getX(), e.getY());
        }
    }

    /**
     * Receives notification of mouse clicks on the panel. These are
     * translated and passed on to any registered {@link ChartMouseListener}s.
     *
     * @param event  Information about the mouse event.
     */
	@Override
	public void mouseClicked(MouseEvent event) {
		System.out.println("MS_CLK: CNT: "+event.getClickCount());

		if(event.getClickCount() == 2) {
			restoreAutoRangeBounds();
			restoreAutoDomainBounds();
		}

		Insets insets = getInsets();
		final int x = (int) ((event.getX() - insets.left) / scaleX);
		final int y = (int) ((event.getY() - insets.top) / scaleY);

        anchor = new Point2D.Double(x, y);
        if(chart == null) return;

        chart.setNotify(true);  // force a redraw
        // new entity code...
        Object[] listeners = chartMouseListeners.getListeners(ChartMouseListener.class);
        if(listeners.length == 0) return;

        ChartEntity entity = null;
        if(info != null) {
            EntityCollection entities = info.getEntityCollection();
            if(entities != null) entity = entities.getEntity(x, y);
        }

        ChartMouseEvent chartEvent = new ChartMouseEvent(getChart(), event, entity);
        for(int i = listeners.length-1; i >= 0; i -= 1) {
            ((ChartMouseListener) listeners[i]).chartMouseClicked(chartEvent);
        }
	}

    /**
     * Implementation of the MouseMotionListener's method.
     *
     * @param e  the event.
     */
	@Override
	public void mouseMoved(MouseEvent e) {    	
		Graphics2D g2 = (Graphics2D)getGraphics();
		g2.dispose();

		Object[] listeners = chartMouseListeners.getListeners(ChartMouseListener.class);
		if(listeners.length == 0) return;

		Insets insets = getInsets();
		int x = (int) ((e.getX() - insets.left) / scaleX);
		int y = (int) ((e.getY() - insets.top) / scaleY);

		ChartEntity entity = null;
		if(info != null) {
			EntityCollection entities = info.getEntityCollection();
			if(entities != null) entity = entities.getEntity(x, y);
		}

        // we can only generate events if the panel's chart is not null
        // (see bug report 1556951)
		if(chart != null) {
			ChartMouseEvent event = new ChartMouseEvent(getChart(), e, entity);
			for(int i=listeners.length-1; i >= 0; i -= 1) {
                ((ChartMouseListener) listeners[i]).chartMouseMoved(event);
            }
        }
    }

    /**
     * Zooms in on an anchor point (specified in screen coordinate space).
     *
     * @param x  the x value (in screen coordinates).
     * @param y  the y value (in screen coordinates).
     */
    public void zoomInBoth(double x, double y) {
    	Plot plot = chart.getPlot();
    	if(plot == null) return;

        // here we tweak the notify flag on the plot so that only
        // one notification happens even though we update multiple
        // axes...
        boolean savedNotify = plot.isNotify();
        plot.setNotify(false);
        zoomInDomain(x, y);
        zoomInRange(x, y);
        plot.setNotify(savedNotify);
	}

    /**
     * Decreases the length of the domain axis, centered about the given
     * coordinate on the screen.  The length of the domain axis is reduced
     * by the value of {@link #getZoomInFactor()}.
     *
     * @param x  the x coordinate (in screen coordinates).
     * @param y  the y-coordinate (in screen coordinates).
     */
    public void zoomInDomain(double x, double y) {
    	Plot plot = chart.getPlot();

        if(plot instanceof Zoomable) {
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            Zoomable z = (Zoomable) plot;
            z.zoomDomainAxes(
            	zoomInFactor,
            	info.getPlotInfo(),
            	translateScreenToJava2D(new Point((int)x, (int)y)),
            	zoomAroundAnchor
            );
            plot.setNotify(savedNotify);
        }
	}

    /**
     * Decreases the length of the range axis, centered about the given
     * coordinate on the screen.  The length of the range axis is reduced by
     * the value of {@link #getZoomInFactor()}.
     *
     * @param x  the x-coordinate (in screen coordinates).
     * @param y  the y coordinate (in screen coordinates).
     */
    public void zoomInRange(double x, double y) {
    	Plot plot = chart.getPlot();

    	if(plot instanceof Zoomable) {
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...

    		boolean savedNotify = plot.isNotify();
    		plot.setNotify(false);
    		Zoomable z = (Zoomable)plot;
            z.zoomRangeAxes(
            	zoomInFactor,
            	info.getPlotInfo(),
            	translateScreenToJava2D(new Point((int)x, (int)y)),
            	zoomAroundAnchor
            );
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Zooms out on an anchor point (specified in screen coordinate space).
     *
     * @param x  the x value (in screen coordinates).
     * @param y  the y value (in screen coordinates).
     */
    public void zoomOutBoth(double x, double y) {
        Plot plot = chart.getPlot();
        if(plot == null) return;

        // here we tweak the notify flag on the plot so that only
        // one notification happens even though we update multiple
        // axes...
        boolean savedNotify = plot.isNotify();
        plot.setNotify(false);
        zoomOutDomain(x, y);
        zoomOutRange(x, y);
        plot.setNotify(savedNotify);
    }

    /**
     * Increases the length of the domain axis, centered about the given
     * coordinate on the screen.  The length of the domain axis is increased
     * by the value of {@link #getZoomOutFactor()}.
     *
     * @param x  the x coordinate (in screen coordinates).
     * @param y  the y-coordinate (in screen coordinates).
     */
    public void zoomOutDomain(double x, double y) {
        Plot plot = chart.getPlot();
        if(plot != null && plot instanceof Zoomable) {
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            Zoomable z = (Zoomable) plot;
            z.zoomDomainAxes(zoomOutFactor, info.getPlotInfo(), translateScreenToJava2D(new Point((int)x, (int)y)), zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
	}

    /**
     * Increases the length the range axis, centered about the given
     * coordinate on the screen.  The length of the range axis is increased
     * by the value of {@link #getZoomOutFactor()}.
     *
     * @param x  the x coordinate (in screen coordinates).
     * @param y  the y-coordinate (in screen coordinates).
     */
    public void zoomOutRange(double x, double y) {
        Plot plot = chart.getPlot();
        
        if(plot instanceof Zoomable) {
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            Zoomable z = (Zoomable) plot;
            z.zoomRangeAxes(
            	zoomOutFactor,
            	info.getPlotInfo(),
            	translateScreenToJava2D(new Point((int) x, (int) y)),
            	this.zoomAroundAnchor
            );
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Zooms in on a selected region.
     *
     * @param selection  the selected region.
     */
    public void zoom(Rectangle2D selection) {
        // get the origin of the zoom selection in the Java2D space used for
        // drawing the chart (that is, before any scaling to fit the panel)
        Point2D selectOrigin = translateScreenToJava2D(new Point(
        	(int) Math.ceil(selection.getX()),
        	(int) Math.ceil(selection.getY()))
        );
        PlotRenderingInfo plotInfo = info.getPlotInfo();
        Rectangle2D scaledDataArea = getScreenDataArea((int) selection.getCenterX(), (int) selection.getCenterY());

        if((selection.getHeight() > 0) && (selection.getWidth() > 0)) {
            double hLower = (selection.getMinX() - scaledDataArea.getMinX()) / scaledDataArea.getWidth();
            double hUpper = (selection.getMaxX() - scaledDataArea.getMinX()) / scaledDataArea.getWidth();
            double vLower = (scaledDataArea.getMaxY() - selection.getMaxY()) / scaledDataArea.getHeight();
            double vUpper = (scaledDataArea.getMaxY() - selection.getMinY()) / scaledDataArea.getHeight();

            Plot p = chart.getPlot();
            if(p instanceof Zoomable) {
                // here we tweak the notify flag on the plot so that only
                // one notification happens even though we update multiple
                // axes...
                boolean savedNotify = p.isNotify();
                p.setNotify(false);
                Zoomable z = (Zoomable) p;
                if(z.getOrientation() == PlotOrientation.HORIZONTAL) {
                    z.zoomDomainAxes(vLower, vUpper, plotInfo, selectOrigin);
                    z.zoomRangeAxes(hLower, hUpper, plotInfo, selectOrigin);
                }
                else {
                    z.zoomDomainAxes(hLower, hUpper, plotInfo, selectOrigin);
                    z.zoomRangeAxes(vLower, vUpper, plotInfo, selectOrigin);
                }
                
                p.setNotify(savedNotify);
            }
        }
    }

    /**
     * Restores the auto-range calculation on both axes.
     */
    public void restoreAutoBounds() {
        Plot plot = chart.getPlot();
        if(plot == null) return;

        // here we tweak the notify flag on the plot so that only
        // one notification happens even though we update multiple
        // axes...
        boolean savedNotify = plot.isNotify();
        
        plot.setNotify(false);
        restoreAutoDomainBounds();
        restoreAutoRangeBounds();
        plot.setNotify(savedNotify);
    }

    /**
     * Restores the auto-range calculation on the domain axis.
     */
    public void restoreAutoDomainBounds() {
        Plot plot = chart.getPlot();
        
        if(plot instanceof Zoomable) {
            Zoomable z = (Zoomable) plot;
            
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            
            // we need to guard against this.zoomPoint being null
            Point2D zp = (zoomPoint != null ? zoomPoint : new Point());
            z.zoomDomainAxes(0.0, info.getPlotInfo(), zp);
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Restores the auto-range calculation on the range axis.
     */
	public void restoreAutoRangeBounds() {
		Plot plot = chart.getPlot();

		if(plot != null && plot instanceof Zoomable) {
			Zoomable z = (Zoomable) plot;
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...

            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);

            // we need to guard against this.zoomPoint being null
            Point2D zp = (zoomPoint != null ? zoomPoint : new Point());
            z.zoomRangeAxes(0.0, info.getPlotInfo(), zp);
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Returns the data area for the chart (the area inside the axes) with the
     * current scaling applied (that is, the area as it appears on screen).
     *
     * @return The scaled data area.
     */
    public Rectangle2D getScreenDataArea() {
        Rectangle2D dataArea = info.getPlotInfo().getDataArea();
        
        Insets insets = getInsets();
        
        double x = dataArea.getX() * scaleX + insets.left;
        double y = dataArea.getY() * scaleY + insets.top;
        double w = dataArea.getWidth() * scaleX;
        double h = dataArea.getHeight() * scaleY;
        
        return new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Returns the data area (the area inside the axes) for the plot or subplot,
     * with the current scaling applied.
     *
     * @param x  the x-coordinate (for subplot selection).
     * @param y  the y-coordinate (for subplot selection).
     *
     * @return The scaled data area.
     */
    public Rectangle2D getScreenDataArea(int x, int y) {
        PlotRenderingInfo plotInfo = info.getPlotInfo();

        Rectangle2D result;
        if(plotInfo.getSubplotCount() == 0) result = getScreenDataArea();
        else {
            // get the origin of the zoom selection in the Java2D space used for
            // drawing the chart (that is, before any scaling to fit the panel)
        	Point2D selectOrigin = translateScreenToJava2D(new Point(x, y));

        	int subplotIndex = plotInfo.getSubplotIndex(selectOrigin);
            if(subplotIndex == -1) return null;

            result = scale(plotInfo.getSubplotInfo(subplotIndex).getDataArea());
        }

        return result;
    }

    /**
     * Returns the initial tooltip delay value used inside this chart panel.
     *
     * @return An integer representing the initial delay value, in milliseconds.
     *
     * @see javax.swing.ToolTipManager#getInitialDelay()
     */
    public int getInitialDelay() {
        return ownToolTipInitialDelay;
    }

    /**
     * Returns the reshow tooltip delay value used inside this chart panel.
     *
     * @return An integer representing the reshow  delay value, in milliseconds.
     *
     * @see javax.swing.ToolTipManager#getReshowDelay()
     */
    public int getReshowDelay() {
        return ownToolTipReshowDelay;
    }

    /**
     * Returns the dismissal tooltip delay value used inside this chart panel.
     *
     * @return An integer representing the dismissal delay value, in
     *         milliseconds.
     *
     * @see javax.swing.ToolTipManager#getDismissDelay()
     */
    public int getDismissDelay() {
        return ownToolTipDismissDelay;
    }

    /**
     * Specifies the initial delay value for this chart panel.
     *
     * @param delay  the number of milliseconds to delay (after the cursor has
     *               paused) before displaying.
     *
     * @see javax.swing.ToolTipManager#setInitialDelay(int)
     */
    public void setInitialDelay(int delay) {
    	ownToolTipInitialDelay = delay;
    }

    /**
     * Specifies the amount of time before the user has to wait initialDelay
     * milliseconds before a tooltip will be shown.
     *
     * @param delay  time in milliseconds
     *
     * @see javax.swing.ToolTipManager#setReshowDelay(int)
     */
    public void setReshowDelay(int delay) {
    	ownToolTipReshowDelay = delay;
    }

    /**
     * Specifies the dismissal delay value for this chart panel.
     *
     * @param delay the number of milliseconds to delay before taking away the
     *              tooltip
     *
     * @see javax.swing.ToolTipManager#setDismissDelay(int)
     */
    public void setDismissDelay(int delay) {
    	ownToolTipDismissDelay = delay;
    }

    /**
     * Returns the zoom in factor.
     *
     * @return The zoom in factor.
     *
     * @see #setZoomInFactor(double)
     */
    public double getZoomInFactor() {
        return zoomInFactor;
    }

    /**
     * Sets the zoom in factor.
     *
     * @param factor  the factor.
     *
     * @see #getZoomInFactor()
     */
    public void setZoomInFactor(double factor) {
    	zoomInFactor = factor;
    }

    /**
     * Returns the zoom out factor.
     *
     * @return The zoom out factor.
     *
     * @see #setZoomOutFactor(double)
     */
    public double getZoomOutFactor() {
    	return zoomOutFactor;
    }

    /**
     * Sets the zoom out factor.
     *
     * @param factor  the factor.
     *
     * @see #getZoomOutFactor()
     */
    public void setZoomOutFactor(double factor) {
    	zoomOutFactor = factor;
    }

    /**
     * Draws zoom rectangle (if present).
     * The drawing is performed in XOR mode, therefore
     * when this method is called twice in a row,
     * the second call will completely restore the state
     * of the canvas.
     *
     * @param g2 the graphics device.
     * @param xor  use XOR for drawing?
     */
    private void drawZoomRectangle(Graphics2D g2, boolean xor) {
    	if(zoomRectangle != null) {
            if(xor) {
                 // Set XOR mode to draw the zoom rectangle
                g2.setXORMode(Color.GRAY);
            }
            
            if(fillZoomRectangle) {
                g2.setPaint(zoomFillPaint);
                g2.fill(zoomRectangle);
            }
            else {
                g2.setPaint(zoomOutlinePaint);
                g2.draw(zoomRectangle);
            }
            
            if(xor) {
                // Reset to the default 'overwrite' mode
                g2.setPaintMode();
            }
        }
    }

    /**
     * Displays a dialog that allows the user to edit the properties for the
     * current chart.
     *
     * @since 1.0.3
     */
    public void doEditChartProperties() {
    	ChartEditor editor = ChartEditorManager.getChartEditor(chart);

    	int result = JOptionPane.showConfirmDialog(
        	this,
        	editor,
        	localizationResources.getString("Chart_Properties"),
        	JOptionPane.OK_CANCEL_OPTION,
        	JOptionPane.PLAIN_MESSAGE
        );
        
        if(result == JOptionPane.OK_OPTION) editor.updateChart(this.chart);
    }

    /**
     * Copies the current chart to the system clipboard.
     * 
     * @since 1.0.13
     */
    public void doCopy() {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Insets insets = getInsets();
        int w = getWidth() - insets.left - insets.right;
        int h = getHeight() - insets.top - insets.bottom;
        ChartTransferable selection = new ChartTransferable(this.chart, w, h,
                getMinimumDrawWidth(), getMinimumDrawHeight(),
                getMaximumDrawWidth(), getMaximumDrawHeight(), true);
        systemClipboard.setContents(selection, null);
    }

    /**
     * Opens a file chooser and gives the user an opportunity to save the chart
     * in PNG format.
     *
     * @throws IOException if there is an I/O error.
     */
    public void doSaveAs() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(this.defaultDirectoryForSaveAs);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(localizationResources.getString("PNG_Image_Files"), "png");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);

        int option = fileChooser.showSaveDialog(this);
        if(option == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            if(isEnforceFileExtensions()) {
                if(!filename.endsWith(".png")) filename = filename + ".png";
            }
            ChartUtils.saveChartAsPNG(new File(filename), chart, getWidth(), getHeight());
        }
    }
    
    /**
     * Saves the chart in SVG format (a filechooser will be displayed so that
     * the user can specify the filename).  Note that this method only works
     * if the JFreeSVG library is on the classpath...if this library is not 
     * present, the method will fail.
     */
    private void saveAsSVG(File f) throws IOException {
        File file = f;
        
        if(file == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(this.defaultDirectoryForSaveAs);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(localizationResources.getString("SVG_Files"), "svg");
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setFileFilter(filter);

            int option = fileChooser.showSaveDialog(this);
            if(option == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getPath();
                if(isEnforceFileExtensions()) {
                    if(!filename.endsWith(".svg")) filename = filename + ".svg";
                }

                file = new File(filename);
                if(file.exists()) {
                    String fileExists = localizationResources.getString("FILE_EXISTS_CONFIRM_OVERWRITE");
                    int response = JOptionPane.showConfirmDialog(this, 
                            fileExists,
                            localizationResources.getString("Save_as_SVG"),
                            JOptionPane.OK_CANCEL_OPTION);
                    if(response == JOptionPane.CANCEL_OPTION) file = null;
                }
            }
        }

        if(file != null) {
            // use reflection to get the SVG string
            String svg = generateSVG(getWidth(), getHeight());
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                writer.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
                writer.write(svg + "\n");
                writer.flush();
            }
            finally {
                try {
                	if(writer != null) writer.close();
                }
                catch(Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
    
    /**
     * Generates a string containing a rendering of the chart in SVG format.
     * This feature is only supported if the JFreeSVG library is included on 
     * the classpath.
     * 
     * @return A string containing an SVG element for the current chart, or 
     *     {@code null} if there is a problem with the method invocation
     *     by reflection.
     */
    private String generateSVG(int width, int height) {
        Graphics2D g2 = createSVGGraphics2D(width, height);
        if(g2 == null) {
            throw new IllegalStateException("JFreeSVG library is not present.");
        }
        // we suppress shadow generation, because SVG is a vector format and
        // the shadow effect is applied via bitmap effects...
        g2.setRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION, true);
        String svg = null;
        Rectangle2D drawArea = new Rectangle2D.Double(0, 0, width, height);
        this.chart.draw(g2, drawArea);
        try {
            Method m = g2.getClass().getMethod("getSVGElement");
            svg = (String) m.invoke(g2);
        }
        catch (NoSuchMethodException e) {
            // null will be returned
        } catch (SecurityException e) {
            // null will be returned
        } catch (IllegalAccessException e) {
            // null will be returned
        }
        catch (IllegalArgumentException e) {
            // null will be returned
        }
        catch (InvocationTargetException e) {
        }

        return svg;
    }

    private Graphics2D createSVGGraphics2D(int w, int h) {
        try {
            Class svgGraphics2d = Class.forName("org.jfree.graphics2d.svg.SVGGraphics2D");
            Constructor ctor = svgGraphics2d.getConstructor(int.class, int.class);
            
            return (Graphics2D) ctor.newInstance(w, h);
        }
        catch (ClassNotFoundException ex) {
            return null;
        }
        catch (NoSuchMethodException ex) {
            return null;
        } catch (SecurityException ex) {
            return null;
        } catch (InstantiationException ex) {
            return null;
        } catch (IllegalAccessException ex) {
            return null;
        } catch (IllegalArgumentException ex) {
            return null;
        } catch (InvocationTargetException ex) {
            return null;
        }
    }

    /**
     * Saves the chart in PDF format (a filechooser will be displayed so that
     * the user can specify the filename).  Note that this method only works
     * if the OrsonPDF library is on the classpath...if this library is not
     * present, the method will fail.
     */
    private void saveAsPDF(File f) {
        File file = f;
        
        if(file == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(this.defaultDirectoryForSaveAs);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    localizationResources.getString("PDF_Files"), "pdf");
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setFileFilter(filter);

            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getPath();
                if (isEnforceFileExtensions()) {
                    if (!filename.endsWith(".pdf")) {
                        filename = filename + ".pdf";
                    }
                }
                file = new File(filename);
                if (file.exists()) {
                    String fileExists = localizationResources.getString(
                            "FILE_EXISTS_CONFIRM_OVERWRITE");
                    int response = JOptionPane.showConfirmDialog(this, 
                            fileExists,
                            localizationResources.getString("Save_as_PDF"),
                            JOptionPane.OK_CANCEL_OPTION);
                    if (response == JOptionPane.CANCEL_OPTION) {
                        file = null;
                    }
                }
            }
        }
        
        if (file != null) {
            writeAsPDF(file, getWidth(), getHeight());
        }
    }
    
    /**
     * Writes the current chart to the specified file in PDF format.  This 
     * will only work when the OrsonPDF library is found on the classpath.
     * Reflection is used to ensure there is no compile-time dependency on
     * OrsonPDF (which is non-free software).
     * 
     * @param file  the output file ({@code null} not permitted).
     * @param w  the chart width.
     * @param h  the chart height.
     */
    private void writeAsPDF(File file, int w, int h) {
        if (!ChartUtils.isOrsonPDFAvailable()) {
            throw new IllegalStateException(
                    "OrsonPDF is not present on the classpath.");
        }
        
        Args.nullNotPermitted(file, "file");
        
        try {
            Class pdfDocClass = Class.forName("com.orsonpdf.PDFDocument");
            Object pdfDoc = pdfDocClass.newInstance();
            Method m = pdfDocClass.getMethod("createPage", Rectangle2D.class);
            Rectangle2D rect = new Rectangle(w, h);
            Object page = m.invoke(pdfDoc, rect);
            Method m2 = page.getClass().getMethod("getGraphics2D");
            Graphics2D g2 = (Graphics2D) m2.invoke(page);
            // we suppress shadow generation, because PDF is a vector format and
            // the shadow effect is applied via bitmap effects...
            g2.setRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION, true);
            Rectangle2D drawArea = new Rectangle2D.Double(0, 0, w, h);
            this.chart.draw(g2, drawArea);
            Method m3 = pdfDocClass.getMethod("writeToFile", File.class);
            m3.invoke(pdfDoc, file);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        } catch (SecurityException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates a print job for the chart.
     */
    public void createChartPrintJob() {
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat pf = job.defaultPage();
        PageFormat pf2 = job.pageDialog(pf);
        
        if(pf2 != pf) {
            job.setPrintable(this, pf2);
            if(job.printDialog()) {
                try {
                    job.print();
                }
                catch (PrinterException e) {
                    JOptionPane.showMessageDialog(this, e);
                }
            }
        }
    }

    /**
     * Prints the chart on a single page.
     *
     * @param g  the graphics context.
     * @param pf  the page format to use.
     * @param pageIndex  the index of the page. If not {@code 0}, nothing
     *                   gets printed.
     *
     * @return The result of printing.
     */
    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        if(pageIndex != 0) return NO_SUCH_PAGE;
        
        Graphics2D g2 = (Graphics2D) g;
        double x = pf.getImageableX();
        double y = pf.getImageableY();
        double w = pf.getImageableWidth();
        double h = pf.getImageableHeight();
        
        chart.draw(g2, new Rectangle2D.Double(x, y, w, h), this.anchor, null);
        
        return PAGE_EXISTS;
    }

    /**
     * Adds a listener to the list of objects listening for chart mouse events.
     *
     * @param listener  the listener ({@code null} not permitted).
     */
    public void addChartMouseListener(ChartMouseListener listener) {
        Args.nullNotPermitted(listener, "listener");
        
        chartMouseListeners.add(ChartMouseListener.class, listener);
    }

    /**
     * Removes a listener from the list of objects listening for chart mouse
     * events.
     *
     * @param listener  the listener.
     */
    public void removeChartMouseListener(ChartMouseListener listener) {
    	chartMouseListeners.remove(ChartMouseListener.class, listener);
    }

    /**
     * Returns an array of the listeners of the given type registered with the
     * panel.
     *
     * @param listenerType  the listener type.
     *
     * @return An array of listeners.
     */
    @Override
    public EventListener[] getListeners(Class listenerType) {
        if(listenerType == ChartMouseListener.class) {
            // fetch listeners from local storage
            return chartMouseListeners.getListeners(listenerType);
        }
        else return getListeners(listenerType);
	}

    /**
     * Creates a popup menu for the panel.
     *
     * @param properties  include a menu item for the chart property editor.
     * @param save  include a menu item for saving the chart.
     * @param print  include a menu item for printing the chart.
     * @param zoom  include menu items for zooming.
     *
     * @return The popup menu.
     * 
     * @deprecated Use #createPopupMenu(boolean, boolean, boolean, boolean, boolean) 
     *     as this includes an explicit flag for the {@code copy} menu item. 
     */
    protected JPopupMenu createPopupMenu(boolean properties, boolean save, boolean print, boolean zoom) {
        return createPopupMenu(properties, false, save, print, zoom);
    }

    /**
     * Creates a popup menu for the panel.  This method includes code that
     * auto-detects JFreeSVG and OrsonPDF (via reflection) and, if they are
     * present (and the {@code save} argument is {@code true}, adds a menu item
     * for each.
     *
     * @param properties  include a menu item for the chart property editor.
     * @param copy include a menu item for copying to the clipboard.
     * @param save  include one or more menu items for saving the chart to 
     *     supported image formats.
     * @param print  include a menu item for printing the chart.
     * @param zoom  include menu items for zooming.
     *
     * @return The popup menu.
     *
     * @since 1.0.13
     */
    protected JPopupMenu createPopupMenu(boolean properties, boolean copy, boolean save, boolean print, boolean zoom) {
        JPopupMenu result = new JPopupMenu(localizationResources.getString("Chart") + ":");
        boolean separator = false;

        if(properties) {
            JMenuItem propertiesItem = new JMenuItem(localizationResources.getString("Properties..."));
            propertiesItem.setActionCommand(PROPERTIES_COMMAND);
            propertiesItem.addActionListener(this);
            result.add(propertiesItem);
            separator = true;
        }

        if(copy) {
            if(separator) result.addSeparator();

            JMenuItem copyItem = new JMenuItem(localizationResources.getString("Copy"));
            copyItem.setActionCommand(COPY_COMMAND);
            copyItem.addActionListener(this);
            result.add(copyItem);
            separator = !save;
        }

        if(save) {
            if(separator) result.addSeparator();

            JMenu saveSubMenu = new JMenu(localizationResources.getString("Save_as"));
            JMenuItem pngItem = new JMenuItem(localizationResources.getString("PNG..."));
            pngItem.setActionCommand("SAVE_AS_PNG");
            pngItem.addActionListener(this);
            saveSubMenu.add(pngItem);
            
            if(ChartUtils.isJFreeSVGAvailable()) {
                JMenuItem svgItem = new JMenuItem(localizationResources.getString(
                        "SVG..."));
                svgItem.setActionCommand("SAVE_AS_SVG");
                svgItem.addActionListener(this);
                saveSubMenu.add(svgItem);                
            }
            
            if (ChartUtils.isOrsonPDFAvailable()) {
                JMenuItem pdfItem = new JMenuItem(
                        localizationResources.getString("PDF..."));
                pdfItem.setActionCommand("SAVE_AS_PDF");
                pdfItem.addActionListener(this);
                saveSubMenu.add(pdfItem);
            }
            result.add(saveSubMenu);
            separator = true;
        }

        if(print) {
            if(separator) result.addSeparator();

            JMenuItem printItem = new JMenuItem(
                    localizationResources.getString("Print..."));
            printItem.setActionCommand(PRINT_COMMAND);
            printItem.addActionListener(this);
            result.add(printItem);
            separator = true;
        }

        if(zoom) {
            if(separator) result.addSeparator();

            JMenu zoomInMenu = new JMenu(localizationResources.getString("Zoom_In"));

            zoomInBothMenuItem = new JMenuItem(localizationResources.getString("All_Axes"));
            zoomInBothMenuItem.setActionCommand(ZOOM_IN_BOTH_COMMAND);
            zoomInBothMenuItem.addActionListener(this);
            zoomInMenu.add(zoomInBothMenuItem);

            zoomInMenu.addSeparator();

            zoomInDomainMenuItem = new JMenuItem(localizationResources.getString("Domain_Axis"));
            zoomInDomainMenuItem.setActionCommand(ZOOM_IN_DOMAIN_COMMAND);
            zoomInDomainMenuItem.addActionListener(this);
            zoomInMenu.add(zoomInDomainMenuItem);

            this.zoomInRangeMenuItem = new JMenuItem(
                    localizationResources.getString("Range_Axis"));
            this.zoomInRangeMenuItem.setActionCommand(ZOOM_IN_RANGE_COMMAND);
            this.zoomInRangeMenuItem.addActionListener(this);
            zoomInMenu.add(this.zoomInRangeMenuItem);

            result.add(zoomInMenu);

            JMenu zoomOutMenu = new JMenu(localizationResources.getString("Zoom_Out"));

            this.zoomOutBothMenuItem = new JMenuItem(
                    localizationResources.getString("All_Axes"));
            this.zoomOutBothMenuItem.setActionCommand(ZOOM_OUT_BOTH_COMMAND);
            this.zoomOutBothMenuItem.addActionListener(this);
            zoomOutMenu.add(this.zoomOutBothMenuItem);

            zoomOutMenu.addSeparator();

            this.zoomOutDomainMenuItem = new JMenuItem(
                    localizationResources.getString("Domain_Axis"));
            this.zoomOutDomainMenuItem.setActionCommand(
                    ZOOM_OUT_DOMAIN_COMMAND);
            this.zoomOutDomainMenuItem.addActionListener(this);
            zoomOutMenu.add(this.zoomOutDomainMenuItem);

            this.zoomOutRangeMenuItem = new JMenuItem(
                    localizationResources.getString("Range_Axis"));
            this.zoomOutRangeMenuItem.setActionCommand(ZOOM_OUT_RANGE_COMMAND);
            this.zoomOutRangeMenuItem.addActionListener(this);
            zoomOutMenu.add(this.zoomOutRangeMenuItem);

            result.add(zoomOutMenu);

            JMenu autoRangeMenu = new JMenu(localizationResources.getString("Auto_Range"));

            this.zoomResetBothMenuItem = new JMenuItem(localizationResources.getString("All_Axes"));
            this.zoomResetBothMenuItem.setActionCommand(ZOOM_RESET_BOTH_COMMAND);
            this.zoomResetBothMenuItem.addActionListener(this);
            autoRangeMenu.add(this.zoomResetBothMenuItem);

            autoRangeMenu.addSeparator();
            this.zoomResetDomainMenuItem = new JMenuItem(localizationResources.getString("Domain_Axis"));
            this.zoomResetDomainMenuItem.setActionCommand(ZOOM_RESET_DOMAIN_COMMAND);
            this.zoomResetDomainMenuItem.addActionListener(this);
            autoRangeMenu.add(this.zoomResetDomainMenuItem);

            zoomResetRangeMenuItem = new JMenuItem(localizationResources.getString("Range_Axis"));
            zoomResetRangeMenuItem.setActionCommand(ZOOM_RESET_RANGE_COMMAND);
            zoomResetRangeMenuItem.addActionListener(this);
            autoRangeMenu.add(this.zoomResetRangeMenuItem);

            result.addSeparator();
            result.add(autoRangeMenu);
        }

        return result;
    }

    /**
     * The idea is to modify the zooming options depending on the type of chart
     * being displayed by the panel.
     *
     * @param x  horizontal position of the popup.
     * @param y  vertical position of the popup.
     */
    protected void displayPopupMenu(int x, int y) {
        if(this.popup == null) return;

        // go through each zoom menu item and decide whether or not to
        // enable it...
        boolean isDomainZoomable = false;
        boolean isRangeZoomable = false;
        Plot plot = chart != null ? chart.getPlot() : null;
        if(plot instanceof Zoomable) {
            Zoomable z = (Zoomable) plot;
            isDomainZoomable = z.isDomainZoomable();
            isRangeZoomable = z.isRangeZoomable();
        }

        if(zoomInDomainMenuItem != null) {
        	zoomInDomainMenuItem.setEnabled(isDomainZoomable);
        }
        if (this.zoomOutDomainMenuItem != null) {
            this.zoomOutDomainMenuItem.setEnabled(isDomainZoomable);
        }
        if (this.zoomResetDomainMenuItem != null) {
            this.zoomResetDomainMenuItem.setEnabled(isDomainZoomable);
        }

        if (this.zoomInRangeMenuItem != null) {
            this.zoomInRangeMenuItem.setEnabled(isRangeZoomable);
        }
        if (this.zoomOutRangeMenuItem != null) {
            this.zoomOutRangeMenuItem.setEnabled(isRangeZoomable);
        }

        if (this.zoomResetRangeMenuItem != null) {
            this.zoomResetRangeMenuItem.setEnabled(isRangeZoomable);
        }

        if (this.zoomInBothMenuItem != null) {
            this.zoomInBothMenuItem.setEnabled(isDomainZoomable
                    && isRangeZoomable);
        }
        if (this.zoomOutBothMenuItem != null) {
            this.zoomOutBothMenuItem.setEnabled(isDomainZoomable
                    && isRangeZoomable);
        }
        if (this.zoomResetBothMenuItem != null) {
            this.zoomResetBothMenuItem.setEnabled(isDomainZoomable
                    && isRangeZoomable);
        }

        popup.show(this, x, y);
    }

    /**
     * Updates the UI for a LookAndFeel change.
     */
    @Override
    public void updateUI() {
        // here we need to update the UI for the popup menu, if the panel
        // has one...
    	if(popup != null) SwingUtilities.updateComponentTreeUI(popup);
        
        super.updateUI();
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtils.writePaint(this.zoomFillPaint, stream);
        SerialUtils.writePaint(this.zoomOutlinePaint, stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.zoomFillPaint = SerialUtils.readPaint(stream);
        this.zoomOutlinePaint = SerialUtils.readPaint(stream);

        // we create a new but empty chartMouseListeners list
        this.chartMouseListeners = new EventListenerList();

        // register as a listener with sub-components...
        if (this.chart != null) {
            this.chart.addChangeListener(this);
        }

    }

}
