package org.jfree.data.xy;

import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;

/**
 * A dataset wrapper class that converts a standard {@link XYDataset} into an
 * {@link IntervalXYDataset} suitable for use in creating XY bar charts.
 */
public class XYBarDataset extends AbstractIntervalXYDataset implements IntervalXYDataset, DatasetChangeListener, PublicCloneable {
	private static final long serialVersionUID = 4289982151975114882L;

	/** The underlying dataset. */
    private XYDataset underlying;

    /** The bar width. */
    private double barWidth;

    /**
     * Creates a new dataset.
     *
     * @param underlying  the underlying dataset ({@code null} not
     *     permitted).
     * @param barWidth  the width of the bars.
     */
    public XYBarDataset(XYDataset underlying, double barWidth) {
        this.underlying = underlying;
        underlying.addChangeListener(this);
        this.barWidth = barWidth;
    }

    /**
     * Returns the underlying dataset that was specified via the constructor.
     *
     * @return The underlying dataset (never {@code null}).
     *
     * @since 1.0.4
     */
    public XYDataset getUnderlyingDataset() {
        return underlying;
    }

    /**
     * Returns the bar width.
     *
     * @return The bar width.
     *
     * @see #setBarWidth(double)
     * @since 1.0.4
     */
    public double getBarWidth() {
        return barWidth;
    }

    /**
     * Sets the bar width and sends a {@link DatasetChangeEvent} to all
     * registered listeners.
     *
     * @param barWidth  the bar width.
     *
     * @see #getBarWidth()
     * @since 1.0.4
     */
    public void setBarWidth(double barWidth) {
        this.barWidth = barWidth;
        
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return The series count.
     */
    @Override
    public int getSeriesCount() {
        return underlying.getSeriesCount();
    }

    /**
     * Returns the key for a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     *
     * @return The series key.
     */
	@Override
	public Comparable getSeriesKey(int series) {
		return underlying.getSeriesKey(series);
	}

    /**
     * Returns the number of items in a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The item count.
     */
    @Override
    public int getItemCount(int series) {
        return underlying.getItemCount(series);
    }

    /**
     * Returns the x-value for an item within a series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The x-value.
     *
     * @see #getXValue(int, int)
     */
    @Override
    public Number getX(int series, int item) {
        return underlying.getX(series, item);
    }

    /**
     * Returns the x-value (as a double primitive) for an item within a series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     *
     * @see #getX(int, int)
     */
    @Override
    public double getXValue(int series, int item) {
        return underlying.getXValue(series, item);
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The y-value (possibly {@code null}).
     *
     * @see #getYValue(int, int)
     */
    @Override
    public Number getY(int series, int item) {
        return underlying.getY(series, item);
    }

    /**
     * Returns the y-value (as a double primitive) for an item within a series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     *
     * @see #getY(int, int)
     */
    @Override
    public double getYValue(int series, int item) {
        return underlying.getYValue(series, item);
    }

    /**
     * Returns the starting X value for the specified series and item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     */
    @Override
    public Number getStartX(int series, int item) {
        Number result = null;
        Number xnum = underlying.getX(series, item);
        if(xnum != null) result = new Double(xnum.doubleValue() - barWidth / 2.0);
        
        return result;
    }

    /**
     * Returns the starting x-value (as a double primitive) for an item within
     * a series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     *
     * @see #getXValue(int, int)
     */
    @Override
    public double getStartXValue(int series, int item) {
        return getXValue(series, item) - barWidth / 2.0;
    }

    /**
     * Returns the ending X value for the specified series and item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     */
    @Override
    public Number getEndX(int series, int item) {
        Number result = null;
        Number xnum = underlying.getX(series, item);
        if(xnum != null) result = new Double(xnum.doubleValue() + barWidth / 2.0);
        
        return result;
    }

    /**
     * Returns the ending x-value (as a double primitive) for an item within
     * a series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     *
     * @see #getXValue(int, int)
     */
    @Override
    public double getEndXValue(int series, int item) {
        return getXValue(series, item) + barWidth / 2.0;
    }

    /**
     * Returns the starting Y value for the specified series and item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     */
    @Override
    public Number getStartY(int series, int item) {
    	return underlying.getY(series, item);
    }

    /**
     * Returns the starting y-value (as a double primitive) for an item within
     * a series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     *
     * @see #getYValue(int, int)
     */
    @Override
    public double getStartYValue(int series, int item) {
    	return getYValue(series, item);
    }

    /**
     * Returns the ending Y value for the specified series and item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     */
    @Override
    public Number getEndY(int series, int item) {
        return underlying.getY(series, item);
    }

    /**
     * Returns the ending y-value (as a double primitive) for an item within
     * a series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     *
     * @see #getYValue(int, int)
     */
    @Override
    public double getEndYValue(int series, int item) {
        return getYValue(series, item);
    }

    /**
     * Receives notification of an dataset change event.
     *
     * @param event  information about the event.
     */
    @Override
    public void datasetChanged(DatasetChangeEvent event) {
        notifyListeners(event);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Tests this dataset for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;

        if(!(obj instanceof XYBarDataset)) return false;

        XYBarDataset that = (XYBarDataset) obj;
        if(!underlying.equals(that.underlying)) return false;

        if(barWidth != that.barWidth) return false;
        
        return true;
    }

    /**
     * Returns an independent copy of the dataset.  Note that:
     * <ul>
     * <li>the underlying dataset is only cloned if it implements the
     * {@link PublicCloneable} interface;</li>
     * <li>the listeners registered with this dataset are not carried over to
     * the cloned dataset.</li>
     * </ul>
     *
     * @return An independent copy of the dataset.
     *
     * @throws CloneNotSupportedException if the dataset cannot be cloned for
     *         any reason.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        XYBarDataset clone = (XYBarDataset) super.clone();
        
        if(underlying instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) underlying;
            clone.underlying = (XYDataset) pc.clone();
        }
        
        return clone;
    }
}
