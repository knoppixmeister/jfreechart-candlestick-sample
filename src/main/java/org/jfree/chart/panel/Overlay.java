package org.jfree.chart.panel;

import java.awt.Graphics2D;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.event.OverlayChangeListener;

/**
 * An {@code Overlay} is anything that can be drawn over top of a chart to add
 * additional information to the chart.  This interface defines the operations
 * that must be supported for an overlay that can be added to a 
 * {@link ChartPanel} in Swing.
 * <br><br>
 * Note: if you are using JavaFX rather than Swing, then you need to look at 
 * the {@code OverlayFX} interface in the <b>JFreeChart-FX</b> project.
 *
 * @since 1.0.13
 */
public interface Overlay {
    /**
     * Paints the visual representation of the overlay.  This method will be
     * called by the {@link ChartPanel} after the underlying chart has been 
     * fully rendered.  When implementing this method, the {@code chartPanel} 
     * argument can be used to get state information from the chart (you can, 
     * for example, extract the axis ranges for the chart).
     *
     * @param g2  the graphics target (never {@code null}).
     * @param chartPanel  the chart panel (never {@code null}).
     */
    public void paintOverlay(Graphics2D g2, ChartPanel chartPanel);

    /**
     * Registers a change listener with the overlay.  Typically this method
     * not be called by user code, it exists so that the {@link ChartPanel}
     * can register and receive notification of changes to the overlay (such
     * changes will trigger an automatic repaint of the chart).
     * 
     * @param listener  the listener ({@code null} not permitted).
     * 
     * @see #removeChangeListener(org.jfree.chart.event.OverlayChangeListener) 
     */
    public void addChangeListener(OverlayChangeListener listener);

    /**
     * Deregisters a listener from the overlay.
     * 
     * @param listener  the listener ({@code null} not permitted).
     * 
     * @see #addChangeListener(org.jfree.chart.event.OverlayChangeListener) 
     */
    public void removeChangeListener(OverlayChangeListener listener);
}
