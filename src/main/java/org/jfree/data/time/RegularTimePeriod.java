package org.jfree.data.time;

import java.lang.reflect.Constructor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.chart.date.MonthConstants;

/**
 * An abstract class representing a unit of time.  Convenient methods are
 * provided for calculating the next and previous time periods.  Conversion
 * methods are defined that return the first and last milliseconds of the time
 * period.  The results from these methods are timezone dependent.
 * <P>
 * This class is immutable, and all subclasses should be immutable also.
 */
public abstract class RegularTimePeriod implements TimePeriod, Comparable, MonthConstants {
    /**
     * Creates a time period that includes the specified millisecond, assuming
     * the given time zone.
     *
     * @param c  the time period class.
     * @param millisecond  the time.
     * @param zone  the time zone.
     * @param locale  the locale.
     *
     * @return The time period.
     */

	public static RegularTimePeriod createInstance(Class c, Date millisecond, TimeZone zone, Locale locale) {
		RegularTimePeriod result = null;

		try {
            Constructor constructor = c.getDeclaredConstructor(new Class[] {Date.class, TimeZone.class, Locale.class});
            result = (RegularTimePeriod) constructor.newInstance(new Object[] {millisecond, zone, locale});
		}
		catch(Exception e) {
        }

        return result;
    }

    /**
     * Returns a subclass of {@link RegularTimePeriod} that is smaller than
     * the specified class.
     *
     * @param c  a subclass of {@link RegularTimePeriod}.
     *
     * @return A class.
     */
	public static Class downsize(Class c) {
        if(c.equals(Year.class)) return Quarter.class;
        else if(c.equals(Quarter.class)) return Month.class;
        else if(c.equals(Month.class)) return Day.class;
        else if(c.equals(Day.class)) return Hour.class;
        else if(c.equals(Hour.class)) return Minute.class;
        else if(c.equals(Minute.class)) return Second.class;
        else if(c.equals(Second.class)) return Millisecond.class;
        else return Millisecond.class;
	}

    /**
     * Returns the time period preceding this one, or {@code null} if some
     * lower limit has been reached.
     *
     * @return The previous time period (possibly {@code null}).
     */
    public abstract RegularTimePeriod previous();

    /**
     * Returns the time period following this one, or {@code null} if some
     * limit has been reached.
     *
     * @return The next time period (possibly {@code null}).
     */
    public abstract RegularTimePeriod next();

    /**
     * Returns a serial index number for the time unit.
     *
     * @return The serial index number.
     */
    public abstract long getSerialIndex();

    //////////////////////////////////////////////////////////////////////////

    /**
     * Recalculates the start date/time and end date/time for this time period
     * relative to the supplied calendar (which incorporates a time zone).
     *
     * @param calendar  the calendar ({@code null} not permitted).
     *
     * @since 1.0.3
     */
    public abstract void peg(Calendar calendar);

    /**
     * Returns the date/time that marks the start of the time period.  This
     * method returns a new {@code Date} instance every time it is called.
     *
     * @return The start date/time.
     *
     * @see #getFirstMillisecond()
     */
    @Override
    public Date getStart() {
        return new Date(getFirstMillisecond());
    }

    /**
     * Returns the date/time that marks the end of the time period.  This
     * method returns a new {@code Date} instance every time it is called.
     *
     * @return The end date/time.
     *
     * @see #getLastMillisecond()
     */
    @Override
    public Date getEnd() {
        return new Date(getLastMillisecond());
    }

    /**
     * Returns the first millisecond of the time period.  This will be
     * determined relative to the time zone specified in the constructor, or
     * in the calendar instance passed in the most recent call to the
     * {@link #peg(Calendar)} method.
     *
     * @return The first millisecond of the time period.
     *
     * @see #getLastMillisecond()
     */
    public abstract long getFirstMillisecond();

    /**
     * Returns the first millisecond of the time period, evaluated using the
     * supplied calendar (which incorporates a timezone).
     *
     * @param calendar  the calendar ({@code null} not permitted).
     *
     * @return The first millisecond of the time period.
     *
     * @throws NullPointerException if {@code calendar} is {@code null}.
     *
     * @see #getLastMillisecond(Calendar)
     */
    public abstract long getFirstMillisecond(Calendar calendar);

    /**
     * Returns the last millisecond of the time period.  This will be
     * determined relative to the time zone specified in the constructor, or
     * in the calendar instance passed in the most recent call to the
     * {@link #peg(Calendar)} method.
     *
     * @return The last millisecond of the time period.
     *
     * @see #getFirstMillisecond()
     */
    public abstract long getLastMillisecond();

    /**
     * Returns the last millisecond of the time period, evaluated using the
     * supplied calendar (which incorporates a timezone).
     *
     * @param calendar  the calendar ({@code null} not permitted).
     *
     * @return The last millisecond of the time period.
     *
     * @see #getFirstMillisecond(Calendar)
     */
    public abstract long getLastMillisecond(Calendar calendar);

    /**
     * Returns the millisecond closest to the middle of the time period.
     *
     * @return The middle millisecond.
     */
    public long getMiddleMillisecond() {
        long m1 = getFirstMillisecond();
        long m2 = getLastMillisecond();
        return m1 + (m2 - m1) / 2;
    }

    /**
     * Returns the millisecond closest to the middle of the time period,
     * evaluated using the supplied calendar (which incorporates a timezone).
     *
     * @param calendar  the calendar.
     *
     * @return The middle millisecond.
     */
    public long getMiddleMillisecond(Calendar calendar) {
        long m1 = getFirstMillisecond(calendar);
        long m2 = getLastMillisecond(calendar);
        return m1 + (m2 - m1) / 2;
    }

    /**
     * Returns the millisecond (relative to the epoch) corresponding to the 
     * specified {@code anchor} using the supplied {@code calendar} 
     * (which incorporates a time zone).
     * 
     * @param anchor  the anchor ({@code null} not permitted).
     * @param calendar  the calendar ({@code null} not permitted).
     * 
     * @return Milliseconds since the epoch.
     * 
     * @since 1.0.18
     */
    public long getMillisecond(TimePeriodAnchor anchor, Calendar calendar) {
        if (anchor.equals(TimePeriodAnchor.START)) {
            return getFirstMillisecond(calendar);
        } else if (anchor.equals(TimePeriodAnchor.MIDDLE)) {
            return getMiddleMillisecond(calendar);
        } else if (anchor.equals(TimePeriodAnchor.END)) {
            return getLastMillisecond(calendar);
        } else {
            throw new IllegalStateException("Unrecognised anchor: " + anchor);
        }
    }
    
    /**
     * Returns a string representation of the time period.
     *
     * @return The string.
     */
    @Override
    public String toString() {
        return String.valueOf(getStart());
    }

}
