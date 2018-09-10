package org.jfree.data.xy;

import java.io.Serializable;

/**
 * Represents an (x, y) coordinate.
 *
 * @since 1.0.6
 */
public class XYCoordinate implements Comparable, Serializable {
	private static final long serialVersionUID = 8223833301144654823L;

	/** The x-coordinate. */
    private double x;

    /** The y-coordinate. */
    private double y;

    /**
     * Creates a new coordinate for the point (0.0, 0.0).
     */
	public XYCoordinate() {
		this(0.0, 0.0);
	}

    /**
     * Creates a new coordinate for the point (x, y).
     *
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     */
	public XYCoordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}

    /**
     * Returns the x-coordinate.
     *
     * @return The x-coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y-coordinate.
     *
     * @return The y-coordinate.
     */
    public double getY() {
        return y;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Tests this coordinate for equality with an arbitrary object.
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
        if (!(obj instanceof XYCoordinate)) {
            return false;
        }
        XYCoordinate that = (XYCoordinate) obj;
        if (this.x != that.x) {
            return false;
        }
        if (this.y != that.y) {
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
        long temp = Double.doubleToLongBits(this.x);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        
        return result;
    }

    /**
     * Returns a string representation of this instance, primarily for
     * debugging purposes.
     *
     * @return A string.
     */
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    /**
     * Compares this instance against an arbitrary object.
     *
     * @param obj  the object ({@code null} not permitted).
     *
     * @return An integer indicating the relative order of the items.
     */
    @Override
    public int compareTo(Object obj) {
    	if(!(obj instanceof XYCoordinate)) {
    		throw new IllegalArgumentException("Incomparable object.");
    	}

        XYCoordinate that = (XYCoordinate) obj;
        if(x > that.x) return 1;
        else if(x < that.x) return -1;
        else {
            if(y > that.y) return 1;
            else if(y < that.y) return -1;
        }

        return 0;
    }
}
