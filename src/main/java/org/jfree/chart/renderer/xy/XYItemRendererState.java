package org.jfree.chart.renderer.xy;

import java.awt.geom.Line2D;

import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.RendererState;
import org.jfree.data.xy.XYDataset;

/**
 * The state for an {@link XYItemRenderer}.
 */
public class XYItemRendererState extends RendererState {
    /**
     * The first item in the series that will be displayed.
     *
     * @since 1.0.11
     */
    private int firstItemIndex;

    /**
     * The last item in the current series that will be displayed.
     *
     * @since 1.0.11
     */
    private int lastItemIndex;

    /**
     * A line object that the renderer can reuse to save instantiating a lot
     * of objects.
     */
    public Line2D workingLine;

    /**
     * A flag that controls whether the plot should pass ALL data items to the
     * renderer, or just the items that will be visible.
     *
     * @since 1.0.6
     */
    private boolean processVisibleItemsOnly;

    /**
     * Creates a new state.
     *
     * @param info  the plot rendering info.
     */
    public XYItemRendererState(PlotRenderingInfo info) {
        super(info);
        
        workingLine 			= new Line2D.Double();
        processVisibleItemsOnly = true;
    }

    /**
     * Returns the flag that controls whether the plot passes all data
     * items in each series to the renderer, or just the visible items.  The
     * default value is {@code true}.
     *
     * @return A boolean.
     *
     * @since 1.0.6
     *
     * @see #setProcessVisibleItemsOnly(boolean)
     */
    public boolean getProcessVisibleItemsOnly() {
        return processVisibleItemsOnly;
    }

    /**
     * Sets the flag that controls whether the plot passes all data
     * items in each series to the renderer, or just the visible items.
     *
     * @param flag  the new flag value.
     *
     * @since 1.0.6
     */
    public void setProcessVisibleItemsOnly(boolean flag) {
    	processVisibleItemsOnly = flag;
    }

    /**
     * Returns the first item index (this is updated with each call to
     * {@link #startSeriesPass(XYDataset, int, int, int, int, int)}.
     *
     * @return The first item index.
     *
     * @since 1.0.11
     */
    public int getFirstItemIndex() {
        return firstItemIndex;
    }

    /**
     * Returns the last item index (this is updated with each call to
     * {@link #startSeriesPass(XYDataset, int, int, int, int, int)}.
     *
     * @return The last item index.
     *
     * @since 1.0.11
     */
    public int getLastItemIndex() {
        return lastItemIndex;
    }

    /**
     * This method is called by the {@link XYPlot} when it starts a pass
     * through the (visible) items in a series.  The default implementation
     * records the first and last item indices - override this method to
     * implement additional specialised behaviour.
     *
     * @param dataset  the dataset.
     * @param series  the series index.
     * @param firstItem  the index of the first item in the series.
     * @param lastItem  the index of the last item in the series.
     * @param pass  the pass index.
     * @param passCount  the number of passes.
     *
     * @see #endSeriesPass(XYDataset, int, int, int, int, int)
     *
     * @since 1.0.11
     */
    public void startSeriesPass(XYDataset dataset, int series, int firstItem, int lastItem, int pass, int passCount) {
    	firstItemIndex = firstItem;
        lastItemIndex = lastItem;
    }

    /**
     * This method is called by the {@link XYPlot} when it ends a pass
     * through the (visible) items in a series.  The default implementation
     * does nothing, but you can override this method to implement specialised
     * behaviour.
     *
     * @param dataset  the dataset.
     * @param series  the series index.
     * @param firstItem  the index of the first item in the series.
     * @param lastItem  the index of the last item in the series.
     * @param pass  the pass index.
     * @param passCount  the number of passes.
     *
     * @see #startSeriesPass(XYDataset, int, int, int, int, int)
     *
     * @since 1.0.11
     */
    public void endSeriesPass(XYDataset dataset, int series, int firstItem, int lastItem, int pass, int passCount) {
        // do nothing...this is just a hook for subclasses
    }
}
