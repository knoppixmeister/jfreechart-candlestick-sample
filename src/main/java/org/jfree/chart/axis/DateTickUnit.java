package org.jfree.chart.axis;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.Args;

/**
 * A tick unit for use by subclasses of {@link DateAxis}.  Instances of this
 * class are immutable.
*/
public class DateTickUnit extends TickUnit implements Serializable {
    private static final long serialVersionUID = -7289292157229621901L;

    /**
     * The units.
     *
     * @since 1.0.13
     */
    private DateTickUnitType unitType;

    /** The unit count. */
    private int count;

    /**
     * The roll unit type.
     *
     * @since 1.0.13
     */
    private DateTickUnitType rollUnitType;

    /** The roll count. */
    private int rollCount;

    /** The date formatter. */
    private DateFormat formatter;

    /**
     * Creates a new date tick unit.
     *
     * @param unitType  the unit type ({@code null} not permitted).
     * @param multiple  the multiple (of the unit type, must be &gt; 0).
     *
     * @since 1.0.13
     */
    public DateTickUnit(DateTickUnitType unitType, int multiple) {
        this(unitType, multiple, DateFormat.getDateInstance(DateFormat.SHORT));
    }

    /**
     * Creates a new date tick unit.
     *
     * @param unitType  the unit type ({@code null} not permitted).
     * @param multiple  the multiple (of the unit type, must be &gt; 0).
     * @param formatter  the date formatter ({@code null} not permitted).
     *
     * @since 1.0.13
     */
    public DateTickUnit(DateTickUnitType unitType, int multiple, DateFormat formatter) {
    	this(unitType, multiple, unitType, multiple, formatter);
    }

    /**
     * Creates a new unit.
     *
     * @param unitType  the unit.
     * @param multiple  the multiple.
     * @param rollUnitType  the roll unit.
     * @param rollMultiple  the roll multiple.
     * @param formatter  the date formatter ({@code null} not permitted).
     *
     * @since 1.0.13
     */
    public DateTickUnit(
    	DateTickUnitType unitType,
    	int multiple,
    	DateTickUnitType rollUnitType,
    	int rollMultiple,
    	DateFormat formatter)
    {
        super(DateTickUnit.getMillisecondCount(unitType, multiple));
        
        Args.nullNotPermitted(formatter, "formatter");
        
        if(multiple <= 0) {
            throw new IllegalArgumentException("Requires 'multiple' > 0.");
        }
        if(rollMultiple <= 0) {
            throw new IllegalArgumentException("Requires 'rollMultiple' > 0.");
        }
        
        this.unitType 		= unitType;
        count 				= multiple;
        this.rollUnitType 	= rollUnitType;
        rollCount 			= rollMultiple;
        this.formatter 		= formatter;
    }

    /**
     * Returns the unit type.
     *
     * @return The unit type (never {@code null}).
     *
     * @since 1.0.13
     */
    public DateTickUnitType getUnitType() {
        return unitType;
    }

    /**
     * Returns the unit multiple.
     *
     * @return The unit multiple (always &gt; 0).
     */
    public int getMultiple() {
        return count;
    }

    /**
     * Returns the roll unit type.
     *
     * @return The roll unit type (never {@code null}).
     *
     * @since 1.0.13
     */
    public DateTickUnitType getRollUnitType() {
        return rollUnitType;
    }

    /**
     * Returns the roll unit multiple.
     *
     * @return The roll unit multiple.
     *
     * @since 1.0.13
     */
    public int getRollMultiple() {
        return this.rollCount;
    }

    /**
     * Formats a value.
     *
     * @param milliseconds  date in milliseconds since 01-01-1970.
     *
     * @return The formatted date.
     */
    @Override
    public String valueToString(double milliseconds) {
        return this.formatter.format(new Date((long) milliseconds));
    }

    /**
     * Formats a date using the tick unit's formatter.
     *
     * @param date  the date.
     *
     * @return The formatted date.
     */
    public String dateToString(Date date) {
        return this.formatter.format(date);
    }

    /**
     * Calculates a new date by adding this unit to the base date.
     *
     * @param base  the base date.
     * @param zone  the time zone for the date calculation.
     *
     * @return A new date one unit after the base date.
     *
     * @since 1.0.6
     */
    public Date addToDate(Date base, TimeZone zone) {
        // as far as I know, the Locale for the calendar only affects week
        // number calculations, and since DateTickUnit doesn't do week
        // arithmetic, the default locale (whatever it is) should be fine
        // here...
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(base);
        calendar.add(this.unitType.getCalendarField(), this.count);
        return calendar.getTime();
    }

    /**
     * Rolls the date forward by the amount specified by the roll unit and
     * count.
     *
     * @param base  the base date.

     * @return The rolled date.
     *
     * @see #rollDate(Date, TimeZone)
     */
    public Date rollDate(Date base) {
        return rollDate(base, TimeZone.getDefault());
    }

    /**
     * Rolls the date forward by the amount specified by the roll unit and
     * count.
     *
     * @param base  the base date.
     * @param zone  the time zone.
     *
     * @return The rolled date.
     *
     * @since 1.0.6
     */
    public Date rollDate(Date base, TimeZone zone) {
        // as far as I know, the Locale for the calendar only affects week
        // number calculations, and since DateTickUnit doesn't do week
        // arithmetic, the default locale (whatever it is) should be fine
        // here...
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(base);
        calendar.add(rollUnitType.getCalendarField(), rollCount);
        
        return calendar.getTime();
    }

    /**
     * Returns a field code that can be used with the {@code Calendar}
     * class.
     *
     * @return The field code.
     */
    public int getCalendarField() {
        return this.unitType.getCalendarField();
    }

    /**
     * Returns the (approximate) number of milliseconds for the given unit and
     * unit count.
     * <P>
     * This value is an approximation some of the time (e.g. months are
     * assumed to have 31 days) but this shouldn't matter.
     *
     * @param unit  the unit.
     * @param count  the unit count.
     *
     * @return The number of milliseconds.
     *
     * @since 1.0.13
     */
    private static long getMillisecondCount(DateTickUnitType unit, int count) {

        if (unit.equals(DateTickUnitType.YEAR)) {
            return (365L * 24L * 60L * 60L * 1000L) * count;
        }
        else if (unit.equals(DateTickUnitType.MONTH)) {
            return (31L * 24L * 60L * 60L * 1000L) * count;
        }
        else if (unit.equals(DateTickUnitType.DAY)) {
            return (24L * 60L * 60L * 1000L) * count;
        }
        else if (unit.equals(DateTickUnitType.HOUR)) {
            return (60L * 60L * 1000L) * count;
        }
        else if (unit.equals(DateTickUnitType.MINUTE)) {
            return (60L * 1000L) * count;
        }
        else if (unit.equals(DateTickUnitType.SECOND)) {
            return 1000L * count;
        }
        else if (unit.equals(DateTickUnitType.MILLISECOND)) {
            return count;
        }
        else {
            throw new IllegalArgumentException("The 'unit' argument has a " 
                    + "value that is not recognised.");
        }

    }

    /**
     * Tests this unit for equality with another object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return {@code true} or {@code false}.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DateTickUnit)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        DateTickUnit that = (DateTickUnit) obj;
        if (!(this.unitType.equals(that.unitType))) {
            return false;
        }
        if (this.count != that.count) {
            return false;
        }
        if (!ObjectUtils.equal(this.formatter, that.formatter)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code for this object.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int result = 19;
        result = 37 * result + this.unitType.hashCode();
        result = 37 * result + this.count;
        result = 37 * result + this.formatter.hashCode();
        return result;
    }

    /**
     * Returns a string representation of this instance, primarily used for
     * debugging purposes.
     *
     * @return A string representation of this instance.
     */
    @Override
    public String toString() {
        return "DateTickUnit[" + this.unitType.toString() + ", "
                + this.count + "]";
    }

}
