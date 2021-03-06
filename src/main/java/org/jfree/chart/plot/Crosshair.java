package org.jfree.chart.plot;

import java.awt.*;
import java.beans.*;
import java.io.*;
import org.jfree.chart.HashUtils;
import org.jfree.chart.labels.CrosshairLabelGenerator;
import org.jfree.chart.labels.StandardCrosshairLabelGenerator;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.util.PaintUtils;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.SerialUtils;

/**
 * A {@code Crosshair} represents a value on a chart and is usually displayed
 * as a line perpendicular to the x or y-axis (and sometimes including a label
 * that shows the crosshair value as text).  Instances of this class are used
 * to store the cross hair value plus the visual characteristics of the line
 * that will be rendered once the instance is added to a 
 * {@link CrosshairOverlay} (or {@code CrosshairOverlaydFX} if you are using 
 * the JavaFX extensions for JFreeChart).
 * <br><br>
 * Crosshairs support a property change mechanism which is used by JFreeChart
 * to automatically repaint the overlay whenever a crosshair attribute is 
 * updated.
 */
public class Crosshair implements Cloneable, PublicCloneable, Serializable {
	private static final long serialVersionUID = 3256699334899814313L;

	/** Flag controlling visibility. */
    private boolean visible;

    /** The crosshair value. */
    private double value;

    /** The paint for the crosshair line. */
    private transient Paint paint;

    /** The stroke for the crosshair line. */
    private transient Stroke stroke;

    /**
     * A flag that controls whether or not the crosshair has a label
     * visible.
     */
    private boolean labelVisible;

    /**
     * The label anchor.
     */
    private RectangleAnchor labelAnchor;

    /** A label generator. */
    private CrosshairLabelGenerator labelGenerator;

    /**
     * The x-offset in Java2D units.
     */
    private double labelXOffset;

    /**
     * The y-offset in Java2D units.
     */
    private double labelYOffset;

    /**
     * The label font.
     */
    private Font labelFont;

    /**
     * The label paint.
     */
    private transient Paint labelPaint;

    /**
     * The label background paint.
     */
    private transient Paint labelBackgroundPaint;

    /** A flag that controls the visibility of the label outline. */
    private boolean labelOutlineVisible;

    /** The label outline stroke. */
    private transient Stroke labelOutlineStroke;

    /** The label outline paint. */
    private transient Paint labelOutlinePaint;

    /** Property change support. */
    private transient PropertyChangeSupport pcs;

    /**
     * Creates a new crosshair with value 0.0.
     */
	public Crosshair() {
        this(0.0);
	}

    /**
     * Creates a new crosshair with the specified value.
     *
     * @param value  the value.
     */
    public Crosshair(double value) {
       this(value, Color.BLACK, new BasicStroke(1.0f));
    }

    /**
     * Creates a new crosshair value with the specified value and line style.
     *
     * @param value  the value.
     * @param paint  the line paint ({@code null} not permitted).
     * @param stroke  the line stroke ({@code null} not permitted).
     */
    public Crosshair(double value, Paint paint, Stroke stroke) {
        Args.nullNotPermitted(paint, "paint");
        Args.nullNotPermitted(stroke, "stroke");

        visible = true;
        this.value = value;
        this.paint = paint;
        this.stroke = stroke;
        labelVisible = false;
        labelGenerator = new StandardCrosshairLabelGenerator();
        labelAnchor = RectangleAnchor.BOTTOM_LEFT;
        labelXOffset = 3.0;
        labelYOffset = 3.0;
        labelFont = new Font("Tahoma", Font.PLAIN, 12);
        labelPaint = Color.BLACK;
        labelBackgroundPaint = new Color(0, 0, 255, 63);
        labelOutlineVisible = true;
        labelOutlinePaint = Color.BLACK;
        labelOutlineStroke = new BasicStroke(0.5f);
        
        pcs = new PropertyChangeSupport(this);
	}

    /**
     * Returns the flag that indicates whether or not the crosshair is
     * currently visible.
     *
     * @return A boolean.
     *
     * @see #setVisible(boolean)
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the flag that controls the visibility of the crosshair and sends
     * a proerty change event (with the name 'visible') to all registered
     * listeners.
     *
     * @param visible  the new flag value.
     *
     * @see #isVisible()
     */
    public void setVisible(boolean visible) {
        boolean old = this.visible;
        this.visible = visible;
        
        pcs.firePropertyChange("visible", old, visible);
    }

    /**
     * Returns the crosshair value.
     *
     * @return The crosshair value.
     *
     * @see #setValue(double)
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the crosshair value and sends a property change event with the name
     * 'value' to all registered listeners.
     *
     * @param value  the value.
     *
     * @see #getValue()
     */
    public void setValue(double value) {
        Double oldValue = new Double(this.value);
        
        this.value = value;
        
        pcs.firePropertyChange("value", oldValue, new Double(value));
    }

    /**
     * Returns the paint for the crosshair line.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setPaint(java.awt.Paint)
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     * Sets the paint for the crosshair line and sends a property change event
     * with the name "paint" to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getPaint()
     */
    public void setPaint(Paint paint) {
        Paint old = this.paint;
        this.paint = paint;
        
        pcs.firePropertyChange("paint", old, paint);
    }

    /**
     * Returns the stroke for the crosshair line.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setStroke(java.awt.Stroke)
     */
    public Stroke getStroke() {
        return stroke;
    }

    /**
     * Sets the stroke for the crosshair line and sends a property change event
     * with the name "stroke" to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getStroke()
     */
    public void setStroke(Stroke stroke) {
        Stroke old = this.stroke;
        
        this.stroke = stroke;
        
        pcs.firePropertyChange("stroke", old, stroke);
    }

    /**
     * Returns the flag that controls whether or not a label is drawn for
     * this crosshair.
     *
     * @return A boolean.
     *
     * @see #setLabelVisible(boolean)
     */
    public boolean isLabelVisible() {
        return labelVisible;
    }

    /**
     * Sets the flag that controls whether or not a label is drawn for the
     * crosshair and sends a property change event (with the name
     * 'labelVisible') to all registered listeners.
     *
     * @param visible  the new flag value.
     *
     * @see #isLabelVisible()
     */
    public void setLabelVisible(boolean visible) {
    	boolean old = labelVisible;

    	labelVisible = visible;

    	pcs.firePropertyChange("labelVisible", old, visible);
    }

    /**
     * Returns the crosshair label generator.
     *
     * @return The label crosshair generator (never {@code null}).
     *
     * @see #setLabelGenerator(org.jfree.chart.labels.CrosshairLabelGenerator)
     */
    public CrosshairLabelGenerator getLabelGenerator() {
        return labelGenerator;
    }

    /**
     * Sets the crosshair label generator and sends a property change event
     * (with the name 'labelGenerator') to all registered listeners.
     *
     * @param generator  the new generator ({@code null} not permitted).
     *
     * @see #getLabelGenerator()
     */
    public void setLabelGenerator(CrosshairLabelGenerator generator) {
        Args.nullNotPermitted(generator, "generator");
        
        CrosshairLabelGenerator old = labelGenerator;
        labelGenerator = generator;
        
        pcs.firePropertyChange("labelGenerator", old, generator);
    }

    /**
     * Returns the label anchor point.
     *
     * @return the label anchor point (never {@code null}.
     *
     * @see #setLabelAnchor(org.jfree.chart.ui.RectangleAnchor)
     */
    public RectangleAnchor getLabelAnchor() {
        return labelAnchor;
    }

    /**
     * Sets the label anchor point and sends a property change event (with the
     * name 'labelAnchor') to all registered listeners.
     *
     * @param anchor  the anchor ({@code null} not permitted).
     *
     * @see #getLabelAnchor()
     */
    public void setLabelAnchor(RectangleAnchor anchor) {
        RectangleAnchor old = labelAnchor;
        
        labelAnchor = anchor;
        
        pcs.firePropertyChange("labelAnchor", old, anchor);
    }

    /**
     * Returns the x-offset for the label (in Java2D units).
     *
     * @return The x-offset.
     *
     * @see #setLabelXOffset(double)
     */
    public double getLabelXOffset() {
        return labelXOffset;
    }

    /**
     * Sets the x-offset and sends a property change event (with the name
     * 'labelXOffset') to all registered listeners.
     *
     * @param offset  the new offset.
     *
     * @see #getLabelXOffset()
     */
    public void setLabelXOffset(double offset) {
        Double old = labelXOffset;
        labelXOffset = offset;
        
        pcs.firePropertyChange("labelXOffset", old, offset);
    }

    /**
     * Returns the y-offset for the label (in Java2D units).
     *
     * @return The y-offset.
     *
     * @see #setLabelYOffset(double)
     */
    public double getLabelYOffset() {
        return labelYOffset;
    }

    /**
     * Sets the y-offset and sends a property change event (with the name
     * 'labelYOffset') to all registered listeners.
     *
     * @param offset  the new offset.
     *
     * @see #getLabelYOffset()
     */
    public void setLabelYOffset(double offset) {
        Double old = labelYOffset;
        labelYOffset = offset;
        
        pcs.firePropertyChange("labelYOffset", old, offset);
    }

    /**
     * Returns the label font.
     *
     * @return The label font (never {@code null}).
     *
     * @see #setLabelFont(java.awt.Font)
     */
    public Font getLabelFont() {
        return labelFont;
    }

    /**
     * Sets the label font and sends a property change event (with the name
     * 'labelFont') to all registered listeners.
     *
     * @param font  the font ({@code null} not permitted).
     *
     * @see #getLabelFont()
     */
    public void setLabelFont(Font font) {
        Args.nullNotPermitted(font, "font");
        
        Font old = labelFont;
        labelFont = font;
        pcs.firePropertyChange("labelFont", old, font);
    }

    /**
     * Returns the label paint.  The default value is {@code Color.BLACK}.
     *
     * @return The label paint (never {@code null}).
     *
     * @see #setLabelPaint
     */
    public Paint getLabelPaint() {
        return labelPaint;
    }

    /**
     * Sets the label paint and sends a property change event (with the name
     * 'labelPaint') to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getLabelPaint()
     */
    public void setLabelPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        
        Paint old = labelPaint;
        labelPaint = paint;
        pcs.firePropertyChange("labelPaint", old, paint);
    }

    /**
     * Returns the label background paint.
     *
     * @return The label background paint (possibly {@code null}).
     *
     * @see #setLabelBackgroundPaint(java.awt.Paint)
     */
    public Paint getLabelBackgroundPaint() {
        return labelBackgroundPaint;
    }

    /**
     * Sets the label background paint and sends a property change event with
     * the name 'labelBackgroundPaint') to all registered listeners.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getLabelBackgroundPaint()
     */
    public void setLabelBackgroundPaint(Paint paint) {
        Paint old = labelBackgroundPaint;
        
        labelBackgroundPaint = paint;
        
        pcs.firePropertyChange("labelBackgroundPaint", old, paint);
    }

    /**
     * Returns the flag that controls the visibility of the label outline.
     * The default value is {@code true}.
     *
     * @return A boolean.
     *
     * @see #setLabelOutlineVisible(boolean)
     */
    public boolean isLabelOutlineVisible() {
        return labelOutlineVisible;
    }

    /**
     * Sets the flag that controls the visibility of the label outlines and
     * sends a property change event (with the name "labelOutlineVisible") to
     * all registered listeners.
     *
     * @param visible  the new flag value.
     *
     * @see #isLabelOutlineVisible()
     */
    public void setLabelOutlineVisible(boolean visible) {
    	boolean old = labelOutlineVisible;

    	labelOutlineVisible = visible;

    	pcs.firePropertyChange("labelOutlineVisible", old, visible);
    }

    /**
     * Returns the label outline paint.
     *
     * @return The label outline paint (never {@code null}).
     *
     * @see #setLabelOutlinePaint(java.awt.Paint)
     */
    public Paint getLabelOutlinePaint() {
        return labelOutlinePaint;
    }

    /**
     * Sets the label outline paint and sends a property change event (with the
     * name "labelOutlinePaint") to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getLabelOutlinePaint()
     */
    public void setLabelOutlinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        
        Paint old = labelOutlinePaint;
        labelOutlinePaint = paint;
        
        pcs.firePropertyChange("labelOutlinePaint", old, paint);
    }

    /**
     * Returns the label outline stroke. The default value is 
     * {@code BasicStroke(0.5)}.
     *
     * @return The label outline stroke (never {@code null}).
     *
     * @see #setLabelOutlineStroke(java.awt.Stroke)
     */
    public Stroke getLabelOutlineStroke() {
        return labelOutlineStroke;
    }

    /**
     * Sets the label outline stroke and sends a property change event (with
     * the name 'labelOutlineStroke') to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getLabelOutlineStroke()
     */
    public void setLabelOutlineStroke(Stroke stroke) {
    	Args.nullNotPermitted(stroke, "stroke");

    	Stroke old = labelOutlineStroke;
    	labelOutlineStroke = stroke;
    	
    	pcs.firePropertyChange("labelOutlineStroke", old, stroke);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Tests this crosshair for equality with an arbitrary object.
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
        if (!(obj instanceof Crosshair)) {
            return false;
        }
        Crosshair that = (Crosshair) obj;
        if (this.visible != that.visible) {
            return false;
        }
        if (this.value != that.value) {
            return false;
        }
        if (!PaintUtils.equal(this.paint, that.paint)) {
            return false;
        }
        if (!this.stroke.equals(that.stroke)) {
            return false;
        }
        if (this.labelVisible != that.labelVisible) {
            return false;
        }
        if (!this.labelGenerator.equals(that.labelGenerator)) {
            return false;
        }
        if (!this.labelAnchor.equals(that.labelAnchor)) {
            return false;
        }
        if (this.labelXOffset != that.labelXOffset) {
            return false;
        }
        if (this.labelYOffset != that.labelYOffset) {
            return false;
        }
        if (!this.labelFont.equals(that.labelFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.labelBackgroundPaint,
                that.labelBackgroundPaint)) {
            return false;
        }
        if (this.labelOutlineVisible != that.labelOutlineVisible) {
            return false;
        }
        if (!PaintUtils.equal(this.labelOutlinePaint,
                that.labelOutlinePaint)) {
            return false;
        }
        if (!this.labelOutlineStroke.equals(that.labelOutlineStroke)) {
            return false;
        }
        
        return true;  // can't find any difference
    }

    /**
     * Returns a hash code for this instance.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        
        hash = HashUtils.hashCode(hash, this.visible);
        hash = HashUtils.hashCode(hash, this.value);
        hash = HashUtils.hashCode(hash, this.paint);
        hash = HashUtils.hashCode(hash, this.stroke);
        hash = HashUtils.hashCode(hash, this.labelVisible);
        hash = HashUtils.hashCode(hash, this.labelAnchor);
        hash = HashUtils.hashCode(hash, this.labelGenerator);
        hash = HashUtils.hashCode(hash, this.labelXOffset);
        hash = HashUtils.hashCode(hash, this.labelYOffset);
        hash = HashUtils.hashCode(hash, this.labelFont);
        hash = HashUtils.hashCode(hash, this.labelPaint);
        hash = HashUtils.hashCode(hash, this.labelBackgroundPaint);
        hash = HashUtils.hashCode(hash, this.labelOutlineVisible);
        hash = HashUtils.hashCode(hash, this.labelOutlineStroke);
        hash = HashUtils.hashCode(hash, this.labelOutlinePaint);
        
        return hash;
    }

    /**
     * Returns an independent copy of this instance.
     *
     * @return An independent copy of this instance.
     *
     * @throws java.lang.CloneNotSupportedException if there is a problem with
     *         cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        // FIXME: clone generator
        return super.clone();
    }

    /**
     * Adds a property change listener.
     *
     * @param l  the listener.
     *
     * @see #removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
    	pcs.addPropertyChangeListener(l);
    }

    /**
     * Removes a property change listener.
     *
     * @param l  the listener.
     *
     * @see #addPropertyChangeListener(java.beans.PropertyChangeListener) 
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
    	pcs.removePropertyChangeListener(l);
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
        SerialUtils.writePaint(this.paint, stream);
        SerialUtils.writeStroke(this.stroke, stream);
        SerialUtils.writePaint(this.labelPaint, stream);
        SerialUtils.writePaint(this.labelBackgroundPaint, stream);
        SerialUtils.writeStroke(this.labelOutlineStroke, stream);
        SerialUtils.writePaint(this.labelOutlinePaint, stream);
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
        paint = SerialUtils.readPaint(stream);
        stroke = SerialUtils.readStroke(stream);
        labelPaint = SerialUtils.readPaint(stream);
        labelBackgroundPaint = SerialUtils.readPaint(stream);
        labelOutlineStroke = SerialUtils.readStroke(stream);
        labelOutlinePaint = SerialUtils.readPaint(stream);
        pcs = new PropertyChangeSupport(this);
    }
}
