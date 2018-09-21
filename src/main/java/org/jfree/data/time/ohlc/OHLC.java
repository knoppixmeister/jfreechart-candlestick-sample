package org.jfree.data.time.ohlc;

import java.io.Serializable;
import org.jfree.chart.HashUtils;

/**
 * A data record containing open-high-low-close data (immutable).  This class 
 * is used internally by the {@link OHLCItem} class.
 *
 * @since 1.0.4
 */
public class OHLC implements Serializable {
	private static final long serialVersionUID = 601747095563683939L;

	/** The open value. */
    private double open;

    /** The close value. */
    private double close;

    /** The high value. */
    private double high;

    /** The low value. */
    private double low;

    private double volume;

    /**
     * Creates a new instance of {@code OHLC}.
     *
     * @param open  the open value.
     * @param close  the close value.
     * @param high  the high value.
     * @param low  the low value.
     */
	public OHLC(double open, double high, double low, double close) {
		this.open 	= open;
        this.close 	= close;
        this.high 	= high;
        this.low 	= low;
        volume		= 0;
	}

	public OHLC(double open, double high, double low, double close, double volume) {
		this.open 	= open;
        this.close 	= close;
        this.high 	= high;
        this.low 	= low;
        this.volume = volume;
	}

	public void setClose(double close) {
		this.close = close;
	}
	
	public void setLow(double low) {
		this.low = low;
	}
	
	public void setHigh(double high) {
		this.high = high;
	}
	
    /**
     * Returns the open value.
     *
     * @return The open value.
     */
	public double getOpen() {
		return open;
	}

    /**
     * Returns the close value.
     *
     * @return The close value.
     */
    public double getClose() {
        return close;
    }

    /**
     * Returns the high value.
     *
     * @return The high value.
     */
	public double getHigh() {
		return high;
	}

    /**
     * Returns the low value.
     *
     * @return The low value.
	*/
	public double getLow() {
		return low;
	}

	public void updatePrice(double price) {
    	if(price < low) low = price;
    	else if(price > high) high = price;

    	close = price;
    }

	public double getVolume() {
		return volume;
	}

	public void setVolume(double newVolume) {
    	volume = newVolume;
    }

	public boolean isBullCandle() {
		return close > open;
	}

	public boolean isBearCandle() {
		return close < open;
	}

	public boolean isDojee() {
		return open == close;
	}

	
	
	
	
    /**
     * Tests this instance for equality with an arbitrary object.
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
        if (!(obj instanceof OHLC)) {
            return false;
        }
        OHLC that = (OHLC) obj;
        if (this.open != that.open) {
            return false;
        }
        if (this.close != that.close) {
            return false;
        }
        if (this.high != that.high) {
            return false;
        }
        if (this.low != that.low) {
            return false;
        }
        
        return true;
	}

    /**
     * Returns a hash code for this instance.
     *
     * @return A hash code.
     */
	@Override
	public int hashCode() {
		int result = 193;

		result = HashUtils.hashCode(result, open);
		result = HashUtils.hashCode(result, high);
		result = HashUtils.hashCode(result, low);
		result = HashUtils.hashCode(result, close);

        return result;
	}
}
