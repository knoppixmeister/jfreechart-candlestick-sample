package org.jfree.data;

import java.io.Serializable;
import org.jfree.chart.util.*;

/**
 * Represents one (Comparable, Object) data item for use in a
 * {@link ComparableObjectSeries}.
 */
public class ComparableObjectItem implements Cloneable, Comparable, Serializable {
	private static final long serialVersionUID = 2751513470325494890L;

    /** The x-value. */
    private Comparable x;

    /** The y-value. */
    private Object obj;

    /**
     * Constructs a new data item.
     *
     * @param x  the x-value ({@code null} NOT permitted).
     * @param y  the y-value ({@code null} permitted).
     */
    public ComparableObjectItem(Comparable x, Object y) {
        Args.nullNotPermitted(x, "x");

        this.x = x;
        obj = y;
    }

    /**
     * Returns the x-value.
     *
     * @return The x-value (never {@code null}).
     */
    protected Comparable getComparable() {
        return x;
    }

    /**
     * Returns the y-value.
     *
     * @return The y-value (possibly {@code null}).
     */
    protected Object getObject() {
        return obj;
    }

    /**
     * Sets the y-value for this data item.  Note that there is no
     * corresponding method to change the x-value.
     *
     * @param y  the new y-value ({@code null} permitted).
     */
    protected void setObject(Object y) {
    	obj = y;
    }

    /**
     * Returns an integer indicating the order of this object relative to
     * another object.
     * <P>
     * For the order we consider only the x-value:
     * negative == "less-than", zero == "equal", positive == "greater-than".
     *
     * @param o1  the object being compared to.
     *
     * @return An integer indicating the order of this data pair object
     *      relative to another object.
     */
    @Override
    public int compareTo(Object o1) {
        int result;

        // CASE 1 : Comparing to another ComparableObjectItem object
        // ---------------------------------------------------------
        if(o1 instanceof ComparableObjectItem) {
            ComparableObjectItem that = (ComparableObjectItem) o1;
            
            return x.compareTo(that.x);
        }
        // CASE 2 : Comparing to a general object
        // ---------------------------------------------
        else {
            // consider these to be ordered after general objects
            result = 1;
        }

        return result;
    }

    /**
     * Returns a clone of this object.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException not thrown by this class, but
     *         subclasses may differ.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    
    
    
    
    
    /**
     * Tests if this object is equal to another.
     *
     * @param obj  the object to test against for equality ({@code null}
     *             permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
    	if(obj == this) return true;

    	if(!(obj instanceof ComparableObjectItem)) return false;

    	ComparableObjectItem that = (ComparableObjectItem) obj;
    	if(!x.equals(that.x)) return false;

        if(!ObjectUtils.equal(obj, that.obj)) return false;

        return true;
	}

    /**
     * Returns a hash code.

     * @return A hash code.
     */
	@Override
	public int hashCode() {
		int result;

        result = x.hashCode();
        result = 29 * result + (obj != null ? obj.hashCode() : 0);

        return result;
    }
}
