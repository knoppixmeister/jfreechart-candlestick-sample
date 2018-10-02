package org.jfree.data.time.ohlc;

import org.jfree.chart.util.Args;
import org.jfree.data.*;
import org.jfree.data.time.RegularTimePeriod;

/**
 * A list of ({@link RegularTimePeriod}, open, high, low, close) data items.
 *
 * @since 1.0.4
 *
 * @see OHLCSeriesCollection
 */
public class OHLCSeries extends ComparableObjectSeries {
	private static final long serialVersionUID = 8830417599373881612L;

	/**
     * Creates a new empty series.  By default, items added to the series will
     * be sorted into ascending order by period, and duplicate periods will
     * not be allowed.
     *
     * @param key  the series key ({@code null} not permitted).
     */
	public OHLCSeries(Comparable key) {
		super(key, true, false);
	}

    /**
     * Returns the time period for the specified item.
     *
     * @param index  the item index.
     *
     * @return The time period.
     */
	public RegularTimePeriod getPeriod(int index) {
		return ((OHLCItem) getDataItem(index)).getPeriod();
	}

    /**
     * Returns the data item at the specified index.
     *
     * @param index  the item index.
     *
     * @return The data item.
     */
	@Override
	public ComparableObjectItem getDataItem(int index) {
		return super.getDataItem(index);
	}

	/**
     * Adds a data item to the series.
     *
     * @param period  the period.
     * @param open  the open-value.
     * @param high  the high-value.
     * @param low  the low-value.
     * @param close  the close-value.
	*/
	public void add(RegularTimePeriod period, double open, double high, double low, double close) {
    	if(getItemCount() > 0) {
    		OHLCItem item0 = (OHLCItem) getDataItem(0);
            if(!period.getClass().equals(item0.getPeriod().getClass())) {
            	throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
    	}

        super.add(new OHLCItem(period, open, high, low, close, 0), true);
	}

	public void add(RegularTimePeriod period, double open, double high, double low, double close, double volume) {
    	if(getItemCount() > 0) {
    		OHLCItem item0 = (OHLCItem) getDataItem(0);
            if(!period.getClass().equals(item0.getPeriod().getClass())) {
            	throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
    	}

        super.add(new OHLCItem(period, open, high, low, close, volume), true);
	}

	public void add(RegularTimePeriod period, double open, double high, double low, double close, double volume, int position) {
    	if(getItemCount() > 0) {
    		OHLCItem item0 = (OHLCItem) getDataItem(0);
            if(!period.getClass().equals(item0.getPeriod().getClass())) {
            	throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
    	}

    	super.add(new OHLCItem(period, open, high, low, close, volume), position, true);
	}

	/**
     * Adds a data item to the series.  The values from the item passed to
     * this method will be copied into a new object.
     * 
     * @param item  the item ({@code null} not permitted).
     * 
     * @since 1.0.17
     */
    public void add(OHLCItem item) {
        Args.nullNotPermitted(item, "item");

        add(
        	item.getPeriod(),
        	item.getOpenValue(),
        	item.getHighValue(),
        	item.getLowValue(),
        	item.getCloseValue(),
        	item.getVolume()
        );
	}

	public void add(OHLCItem item, int position) {
        Args.nullNotPermitted(item, "item");
        Args.requireNonNegative(position, "position");

        add(
        	item.getPeriod(),
        	item.getOpenValue(),
        	item.getHighValue(),
        	item.getLowValue(),
        	item.getCloseValue(),
        	item.getVolume(),
        	position
		);
    }

    /*
     * update last candle close price (same as last price)
     */
	public void updatePrice(double newPrice) {
		updatePrice(newPrice, getItemCount()-1);
	}

	public void updatePrice(double newPrice, int index) {
		((OHLCItem)getDataItem(index)).updatePrice(newPrice);

		fireSeriesChanged();
	}

	public void updataVolume(double newVolume) {
		updateVolume(newVolume, getItemCount()-1);
	}

	public void updateVolume(double newVolume, int index) {
		((OHLCItem)getDataItem(index)).setVolume(newVolume);

		fireSeriesChanged();
	}

	public void updatePriceVolume(double newPrice, double newVolume) {
		updatePriceVolume(newPrice, newVolume, getItemCount()-1);
	}

	public void updatePriceVolume(double newPrice, double newVolume, int index) {
		((OHLCItem)getDataItem(index)).updatePrice(newPrice);
		((OHLCItem)getDataItem(index)).setVolume(newVolume);

		fireSeriesChanged();
	}
	
    /**
     * Removes the item with the specified index.
     *
     * @param index  the item index.
     * 
     * @return The item removed.
     *
     * @since 1.0.14
     */
	@Override
	public ComparableObjectItem remove(int index) {
		return super.remove(index);
	}
}
