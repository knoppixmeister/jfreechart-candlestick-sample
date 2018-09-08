package org.jfree.data.time.ohlc;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.time.RegularTimePeriod;

/**
 * An item representing data in the form {@code (time-period, open, high, low, 
 * close)}.
*/
public class OHLCItem extends ComparableObjectItem {
	private static final long serialVersionUID = -2037305235343073140L;

	/**
     * Creates a new instance of {@code OHLCItem}.
     *
     * @param period  the time period.
     * @param open  the open-value.
     * @param high  the high-value.
     * @param low  the low-value.
     * @param close  the close-value.
     */
	public OHLCItem(RegularTimePeriod period, double open, double high, double low, double close) {
		super(period, new OHLC(open, high, low, close, 0));
	}

	public OHLCItem(RegularTimePeriod period, double open, double high, double low, double close, double volume) {
		super(period, new OHLC(open, high, low, close, volume));
	}

    /**
     * Returns the period.
     *
     * @return The period (never {@code null}).
     */
    public RegularTimePeriod getPeriod() {
    	return (RegularTimePeriod) getComparable();
    }

    /**
     * Returns the y-value.
     *
     * @return The y-value.
     */
    public double getYValue() {
        return getCloseValue();
    }

    /**
     * Returns the open value.
     *
     * @return The open value.
     */
	public double getOpenValue() {
		OHLC ohlc = (OHLC) getObject();

    	return ohlc != null ? ohlc.getOpen() : Double.NaN;
	}

    /**
     * Returns the high value.
     *
     * @return The high value.
     */
	public double getHighValue() {
    	OHLC ohlc = (OHLC)getObject();

    	return ohlc != null ? ohlc.getHigh() : Double.NaN;
	}

    /**
     * Returns the low value.
     *
     * @return The low value.
	*/
	public double getLowValue() {
		OHLC ohlc = (OHLC)getObject();

		return ohlc != null ? ohlc.getLow() : Double.NaN;
	}

    /**
     * Returns the close value.
     *
     * @return The close value.
	*/
	public double getCloseValue() {
		OHLC ohlc = (OHLC) getObject();

		return ohlc != null ? ohlc.getClose() : Double.NaN;        
	}

	public void updatePrice(double newPrice) {
		OHLC ohlc = (OHLC) getObject();

		if(newPrice > ohlc.getHigh()) ohlc.setHigh(newPrice);
		else if(newPrice < ohlc.getLow()) ohlc.setLow(newPrice);

		ohlc.setClose(newPrice);
		
		setObject(ohlc);
	}

	public double getVolume() {
		OHLC ohlc = (OHLC) getObject();

		return ohlc.getVolume();
	}
	
	public void setVolume(double volume) {
		OHLC ohlc = (OHLC)getObject();

		ohlc.setVolume(volume);
		
		setObject(ohlc);
	}
}
