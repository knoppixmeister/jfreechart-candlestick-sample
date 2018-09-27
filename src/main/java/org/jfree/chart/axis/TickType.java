package org.jfree.chart.axis;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Used to indicate the tick type (MAJOR or MINOR).
 *
 * @since 1.0.7
 */
public final class TickType implements Serializable {
	private static final long serialVersionUID = -6226884506776312727L;

	/** Major tick. */
    public static final TickType MAJOR = new TickType("MAJOR");

    /** Minor tick. */
    public static final TickType MINOR = new TickType("MINOR");

    /** The name. */
    private String name;

    /**
     * Private constructor.
     *
     * @param name  the name.
     */
    private TickType(String name) {
        this.name = name;
    }

    /**
     * Returns a string representing the object.
     *
     * @return The string.
     */
    @Override
    public String toString() {
        return name;
    }

    
    
    
    
    
    /**
     * Returns {@code true} if this object is equal to the specified
     * object, and {@code false} otherwise.
     *
     * @param obj  the other object.
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof TickType)) return false;

        TickType that = (TickType) obj;
        if(!name.equals(that.name)) return false;
        
        return true;
    }

    /**
     * Ensures that serialization returns the unique instances.
     *
     * @return The object.
     *
     * @throws ObjectStreamException if there is a problem.
     */
    private Object readResolve() throws ObjectStreamException {
        Object result = null;
        
        if(this.equals(TickType.MAJOR)) result = TickType.MAJOR;
        else if(this.equals(TickType.MINOR)) result = TickType.MINOR;
        
        return result;
    }
}
