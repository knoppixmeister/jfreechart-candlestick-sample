package org.jfree.chart.ui;

import java.awt.geom.Rectangle2D;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Used to indicate the edge of a rectangle.
*/
public final class RectangleEdge implements Serializable {
    private static final long serialVersionUID = -7400988293691093548L;

    /** Top. */
    public static final RectangleEdge TOP = new RectangleEdge("RectangleEdge.TOP");

    /** Bottom. */
    public static final RectangleEdge BOTTOM = new RectangleEdge("RectangleEdge.BOTTOM");

    /** Left. */
    public static final RectangleEdge LEFT = new RectangleEdge("RectangleEdge.LEFT");

    /** Right. */
	public static final RectangleEdge RIGHT = new RectangleEdge("RectangleEdge.RIGHT");

	/** The name. */
	private String name;

    /**
     * Private constructor.
     *
     * @param name  the name.
     */
	private RectangleEdge(String name) {
		this.name = name;
	}

    /**
     * Returns a string representing the object.
     *
     * @return The string.
     */
    public String toString() {
        return name;
    }

    /**
     * Returns {@code true} if the edge is {@code TOP} or 
     * {@code BOTTOM}, and {@code false} otherwise.
     * 
     * @param edge  the edge.
     * 
     * @return A boolean.
	*/
    public static boolean isTopOrBottom(RectangleEdge edge) {
        return (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM);    
    }
    
    
    /**
     * Returns {@code true} if the edge is {@code LEFT} or 
     * {@code RIGHT}, and {@code false} otherwise.
     * 
     * @param edge  the edge.
     * 
     * @return A boolean.
	*/
	public static boolean isLeftOrRight(RectangleEdge edge) {
		return (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT);    
	}

    /**
     * Returns the opposite edge.
     * 
     * @param edge  an edge.
     * 
     * @return The opposite edge.
     */
    public static RectangleEdge opposite(RectangleEdge edge) {
    	RectangleEdge result = null;

        if(edge == RectangleEdge.TOP) result = RectangleEdge.BOTTOM;
        else if(edge == RectangleEdge.BOTTOM) result = RectangleEdge.TOP;
        else if(edge == RectangleEdge.LEFT) result = RectangleEdge.RIGHT;
        else if(edge == RectangleEdge.RIGHT) result = RectangleEdge.LEFT;

        return result;
	}

    /**
     * Returns the x or y coordinate of the specified edge.
     * 
     * @param rectangle  the rectangle.
     * @param edge  the edge.
     * 
     * @return The coordinate.
     */
    public static double coordinate(Rectangle2D rectangle, RectangleEdge edge) {
    	double result = 0.0;

    	if(edge == RectangleEdge.TOP) result = rectangle.getMinY();
    	else if(edge == RectangleEdge.BOTTOM) result = rectangle.getMaxY();
        else if(edge == RectangleEdge.LEFT) result = rectangle.getMinX();
        else if(edge == RectangleEdge.RIGHT) result = rectangle.getMaxX();

    	return result;
	}

    
    
    
    
    
    
    
    
    
    /**
     * Returns {@code true} if this object is equal to the specified 
     * object, and {@code false} otherwise.
     *
     * @param o  the other object.
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RectangleEdge)) {
            return false;
        }

        final RectangleEdge order = (RectangleEdge) o;
        if (!this.name.equals(order.name)) {
            return false;
        }

        return true;

    }

    /**
     * Returns a hash code value for the object.
     *
     * @return the hashcode
     */
    public int hashCode() {
        return this.name.hashCode();
    }

    /**
     * Ensures that serialization returns the unique instances.
     * 
     * @return The object.
     * 
     * @throws ObjectStreamException if there is a problem.
     */
    private Object readResolve() throws ObjectStreamException {
        RectangleEdge result = null;
        if(this.equals(RectangleEdge.TOP)) result = RectangleEdge.TOP;
        else if(this.equals(RectangleEdge.BOTTOM)) result = RectangleEdge.BOTTOM;
        else if(this.equals(RectangleEdge.LEFT)) result = RectangleEdge.LEFT;
        else if(this.equals(RectangleEdge.RIGHT)) result = RectangleEdge.RIGHT;
        
        return result;
    }
}
