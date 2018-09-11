package org.jfree.chart.block;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.util.PaintUtils;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.SerialUtils;

/**
 * A border for a block.  This class is immutable.
 */
public class BlockBorder implements BlockFrame, Serializable {
    private static final long serialVersionUID = 4961579220410228283L;

    /** An empty border. */
    public static final BlockBorder NONE = new BlockBorder(RectangleInsets.ZERO_INSETS, Color.WHITE);

    /** The space reserved for the border. */
    private RectangleInsets insets;

    /** The border color. */
    private transient Paint paint;

    /**
     * Creates a default border.
     */
	public BlockBorder() {
        this(Color.BLACK);
	}

    /**
     * Creates a new border with the specified color.
     *
     * @param paint  the color ({@code null} not permitted).
     */
    public BlockBorder(Paint paint) {
        this(new RectangleInsets(1, 1, 1, 1), paint);
    }

    /**
     * Creates a new border with the specified line widths (in black).
     *
     * @param top  the width of the top border.
     * @param left  the width of the left border.
     * @param bottom  the width of the bottom border.
     * @param right  the width of the right border.
     */
    public BlockBorder(double top, double left, double bottom, double right) {
        this(new RectangleInsets(top, left, bottom, right), Color.BLACK);
    }

    /**
     * Creates a new border with the specified line widths (in black).
     *
     * @param top  the width of the top border.
     * @param left  the width of the left border.
     * @param bottom  the width of the bottom border.
     * @param right  the width of the right border.
     * @param paint  the border paint ({@code null} not permitted).
     */
    public BlockBorder(double top, double left, double bottom, double right, Paint paint) {
        this(new RectangleInsets(top, left, bottom, right), paint);
    }

    /**
     * Creates a new border.
     *
     * @param insets  the border insets ({@code null} not permitted).
     * @param paint  the paint ({@code null} not permitted).
     */
    public BlockBorder(RectangleInsets insets, Paint paint) {
        Args.nullNotPermitted(insets, "insets");
        Args.nullNotPermitted(paint, "paint");
        
        this.insets = insets;
        this.paint = paint;
    }

    /**
     * Returns the space reserved for the border.
     *
     * @return The space (never {@code null}).
     */
    @Override
    public RectangleInsets getInsets() {
        return insets;
    }

    /**
     * Returns the paint used to draw the border.
     *
     * @return The paint (never {@code null}).
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     * Draws the border by filling in the reserved space.
     *
     * @param g2  the graphics device.
     * @param area  the area.
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area) {
        // this default implementation will just fill the available
        // border space with a single color
        double t = insets.calculateTopInset(area.getHeight());
        double b = insets.calculateBottomInset(area.getHeight());
        double l = insets.calculateLeftInset(area.getWidth());
        double r = insets.calculateRightInset(area.getWidth());
        double x = area.getX();
        double y = area.getY();
        double w = area.getWidth();
        double h = area.getHeight();
        
        g2.setPaint(paint);
        
        Rectangle2D rect = new Rectangle2D.Double();
        
        if(t > 0.0) {
            rect.setRect(x, y, w, t);
            g2.fill(rect);
        }
        
        if(b > 0.0) {
            rect.setRect(x, y + h - b, w, b);
            g2.fill(rect);
        }
        
        if(l > 0.0) {
            rect.setRect(x, y, l, h);
            g2.fill(rect);
        }

        if(r > 0.0) {
            rect.setRect(x + w - r, y, r, h);
            g2.fill(rect);
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Tests this border for equality with an arbitrary instance.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BlockBorder)) {
            return false;
        }
        BlockBorder that = (BlockBorder) obj;
        if (!this.insets.equals(that.insets)) {
            return false;
        }
        if (!PaintUtils.equal(this.paint, that.paint)) {
            return false;
        }
        return true;
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
        SerialUtils.writePaint(this.paint, stream);
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
        paint = SerialUtils.readPaint(stream);
    }
}
