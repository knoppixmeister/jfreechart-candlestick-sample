package org.jfree.chart.axis;

import java.io.Serializable;

/**
 * Base class representing a tick unit.  This determines the spacing of the
 * tick marks on an axis.
 * <P>
 * This class (and any subclasses) should be immutable, the reason being that
 * ORDERED collections of tick units are maintained and if one instance can be
 * changed, it may destroy the order of the collection that it belongs to.
 * In addition, if the implementations are immutable, they can belong to
 * multiple collections.
 *
 * @see ValueAxis
 */
public abstract class TickUnit implements Comparable, Serializable {
    private static final long serialVersionUID = 510179855057013974L;

    /** The size of the tick unit. */
    private double size;

    /**
     * The number of minor ticks.
     *
     * @since 1.0.7
     */
    private int minorTickCount;

    /**
     * Constructs a new tick unit.
     *
     * @param size  the tick unit size.
     */
    public TickUnit(double size) {
        this.size = size;
    }

    /**
     * Constructs a new tick unit.
     *
     * @param size  the tick unit size.
     * @param minorTickCount  the minor tick count.
     *
     * @since 1.0.7
     */
    public TickUnit(double size, int minorTickCount) {
        this.size = size;
        
        this.minorTickCount = minorTickCount;
    }

    /**
     * Returns the size of the tick unit.
     *
     * @return The size of the tick unit.
     */
    public double getSize() {
        return size;
    }

    /**
     * Returns the minor tick count.
     *
     * @return The minor tick count.
     *
     * @since 1.0.7
     */
    public int getMinorTickCount() {
        return minorTickCount;
    }

    /**
     * Converts the supplied value to a string.
     * <P>
     * Subclasses may implement special formatting by overriding this method.
     *
     * @param value  the data value.
     *
     * @return Value as string.
     */
    public String valueToString(double value) {
        return String.valueOf(value);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Compares this tick unit to an arbitrary object.
     *
     * @param object  the object to compare against.
     *
     * @return {@code 1} if the size of the other object is less than this,
     *      {@code 0} if both have the same size and {@code -1} this
     *      size is less than the others.
     */
    @Override
    public int compareTo(Object object) {
        if (object instanceof TickUnit) {
            TickUnit other = (TickUnit) object;
            if (this.size > other.getSize()) {
                return 1;
            }
            else if (this.size < other.getSize()) {
                return -1;
            }
            else {
                return 0;
            }
        }
        else {
            return -1;
        }

    }

    /**
     * Tests this unit for equality with another object.
     *
     * @param obj  the object.
     *
     * @return {@code true} or {@code false}.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TickUnit)) {
            return false;
        }
        TickUnit that = (TickUnit) obj;
        if (this.size != that.size) {
            return false;
        }
        if (this.minorTickCount != that.minorTickCount) {
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
        long temp = this.size != +0.0d ? Double.doubleToLongBits(this.size)
                : 0L;
        return (int) (temp ^ (temp >>> 32));
    }

}
