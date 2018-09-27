package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jfree.chart.HashUtils;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.SerialUtils;

/**
 * A class that captures information about an {@link Axis} belonging to a 
 * chart.
 *
 * @since 1.0.13
 */
public class AxisEntity extends ChartEntity {
	private static final long serialVersionUID = -2644885263263328037L;
	
    private Axis axis;

    /**
     * Creates a new axis entity.
     *
     * @param area  the area ({@code null} not permitted).
     * @param axis  the axis ({@code null} not permitted).
     */
    public AxisEntity(Shape area, Axis axis) {
        this(area, axis, null);
    }

    /**
     * Creates a new axis entity.
     *
     * @param area  the area ({@code null} not permitted).
     * @param axis  the axis ({@code null} not permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     */
    public AxisEntity(Shape area, Axis axis, String toolTipText) {
        // defer argument checks...
        this(area, axis, toolTipText, null);
    }

    /**
     * Creates a new axis entity.
     *
     * @param area  the area ({@code null} not permitted).
     * @param axis  the axis ({@code null} not permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text for HTML image maps ({@code null}
     *                 permitted).
     */
    public AxisEntity(Shape area, Axis axis, String toolTipText, String urlText) {
        super(area, toolTipText, urlText);
        
        Args.nullNotPermitted(axis, "axis");
        
        this.axis = axis;
    }

    /**
     * Returns the axis that occupies the entity area.
     *
     * @return The axis (never {@code null}).
     */
    public Axis getAxis() {
        return axis;
    }

    /**
     * Returns a string representation of the chart entity, useful for
     * debugging.
     *
     * @return A string.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AxisEntity: ");
        sb.append("tooltip = ");
        sb.append(getToolTipText());
        
        return sb.toString();
    }

    
    
    
    
    
    
    
    
    /**
     * Tests the entity for equality with an arbitrary object.
     *
     * @param obj  the object to test against ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AxisEntity)) {
            return false;
        }
        AxisEntity that = (AxisEntity) obj;
        if (!getArea().equals(that.getArea())) {
            return false;
        }
        if (!ObjectUtils.equal(getToolTipText(), that.getToolTipText())) {
            return false;
        }
        if (!ObjectUtils.equal(getURLText(), that.getURLText())) {
            return false;
        }
        if (!(this.axis.equals(that.axis))) {
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
        int result = 39;
        
        result = HashUtils.hashCode(result, getToolTipText());
        result = HashUtils.hashCode(result, getURLText());
        
        return result;
    }

    /**
     * Returns a clone of the entity.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if there is a problem cloning the
     *         entity.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtils.writeShape(getArea(), stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    	stream.defaultReadObject();
    	setArea(SerialUtils.readShape(stream));
    }
}