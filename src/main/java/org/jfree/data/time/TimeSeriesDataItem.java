package org.jfree.data.time;

import java.io.Serializable;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.Args;

/**
 * Represents one data item in a time series.
 * <P>
 * The time period can be any of the following:
 * <ul>
 * <li>{@link Year}</li>
 * <li>{@link Quarter}</li>
 * <li>{@link Month}</li>
 * <li>{@link Week}</li>
 * <li>{@link Day}</li>
 * <li>{@link Hour}</li>
 * <li>{@link Minute}</li>
 * <li>{@link Second}</li>
 * <li>{@link Millisecond}</li>
 * <li>{@link FixedMillisecond}</li>
 * </ul>
 *
 * The time period is an immutable property of the data item.  Data items will
 * often be sorted within a list, and allowing the time period to be changed
 * could destroy the sort order.
 * <P>
 * Implements the {@code Comparable} interface so that standard Java
 * sorting can be used to keep the data items in order.
 *
 */
public class TimeSeriesDataItem implements Cloneable, Comparable, Serializable {
    private static final long serialVersionUID = -2235346966016401302L;

    /** The time period. */
    private RegularTimePeriod period;

    /** The value associated with the time period. */
    private Number value;

    /**
     * Constructs a new data item that associates a value with a time period.
     *
     * @param period  the time period ({@code null} not permitted).
     * @param value  the value ({@code null} permitted).
     */
    public TimeSeriesDataItem(RegularTimePeriod period, Number value) {
        Args.nullNotPermitted(period, "period");
        
        this.period = period;
        this.value = value;
    }

    /**
     * Constructs a new data item that associates a value with a time period.
     *
     * @param period  the time period ({@code null} not permitted).
     * @param value  the value associated with the time period.
     */
    public TimeSeriesDataItem(RegularTimePeriod period, double value) {
        this(period, new Double(value));
    }

    /**
     * Returns the time period.
     *
     * @return The time period (never {@code null}).
     */
    public RegularTimePeriod getPeriod() {
        return period;
    }

    /**
     * Returns the value.
     *
     * @return The value ({@code null} possible).
     *
     * @see #setValue(java.lang.Number)
     */
    public Number getValue() {
        return value;
    }

    /**
     * Sets the value for this data item.
     *
     * @param value  the value ({@code null} permitted).
     *
     * @see #getValue()
     */
    public void setValue(Number value) {
        this.value = value;
    }

    
    
    
    
    
    
    /**
     * Tests this object for equality with an arbitrary object.
     *
     * @param obj  the other object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TimeSeriesDataItem)) {
            return false;
        }
        TimeSeriesDataItem that = (TimeSeriesDataItem) obj;
        if (!ObjectUtils.equal(this.period, that.period)) {
            return false;
        }
        if (!ObjectUtils.equal(this.value, that.value)) {
            return false;
        }
        
        return true;
    }

    /**
     * Returns a hash code.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int result;
        result = (period != null ? period.hashCode() : 0);
        result = 29 * result + (value != null ? value.hashCode() : 0);
        
        return result;
    }

    /**
     * Returns an integer indicating the order of this data pair object
     * relative to another object.
     * <P>
     * For the order we consider only the timing:
     * negative == before, zero == same, positive == after.
     *
     * @param o1  The object being compared to.
     *
     * @return An integer indicating the order of the data item object
     *         relative to another object.
     */
    @Override
    public int compareTo(Object o1) {
        int result;

        // CASE 1 : Comparing to another TimeSeriesDataItem object
        // -------------------------------------------------------
        if (o1 instanceof TimeSeriesDataItem) {
            TimeSeriesDataItem datapair = (TimeSeriesDataItem) o1;
            result = getPeriod().compareTo(datapair.getPeriod());
        }
        // CASE 2 : Comparing to a general object
        // ---------------------------------------------
        else {
            // consider time periods to be ordered after general objects
            result = 1;
        }

        return result;
    }

    /**
     * Clones the data item.  Note: there is no need to clone the period or
     * value since they are immutable classes.
     *
     * @return A clone of the data item.
     */
    @Override
    public Object clone() {
        Object clone = null;
        
        try {
            clone = super.clone();
        }
        catch(CloneNotSupportedException e) { // won't get here...
            e.printStackTrace();
        }
        
        return clone;
    }
}
