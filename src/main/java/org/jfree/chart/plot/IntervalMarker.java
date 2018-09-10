package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.Serializable;

import org.jfree.chart.event.MarkerChangeEvent;
import org.jfree.chart.ui.GradientPaintTransformer;
import org.jfree.chart.ui.LengthAdjustmentType;
import org.jfree.chart.util.ObjectUtils;

/**
 * Represents an interval to be highlighted in some way.
 */
public class IntervalMarker extends Marker implements Cloneable, Serializable {
    private static final long serialVersionUID = -1762344775267627916L;

    /** The start value. */
    private double startValue;

    /** The end value. */
    private double endValue;

    /** The gradient paint transformer (optional). */
    private GradientPaintTransformer gradientPaintTransformer;

    /**
     * Constructs an interval marker.
     *
     * @param start  the start of the interval.
     * @param end  the end of the interval.
     */
    public IntervalMarker(double start, double end) {
        this(start, end, Color.GRAY, new BasicStroke(0.5f), Color.GRAY, new BasicStroke(0.5f), 0.8f);
    }

    /**
     * Creates a new interval marker with the specified range and fill paint.
     * The outline paint and stroke default to {@code null}.
     *
     * @param start  the lower bound of the interval.
     * @param end  the upper bound of the interval.
     * @param paint  the fill paint ({@code null} not permitted).
     *
     * @since 1.0.9
     */
    public IntervalMarker(double start, double end, Paint paint) {
        this(start, end, paint, new BasicStroke(0.5f), null, null, 0.8f);
    }

    /**
     * Constructs an interval marker.
     *
     * @param start  the start of the interval.
     * @param end  the end of the interval.
     * @param paint  the paint ({@code null} not permitted).
     * @param stroke  the stroke ({@code null} not permitted).
     * @param outlinePaint  the outline paint.
     * @param outlineStroke  the outline stroke.
     * @param alpha  the alpha transparency.
     */
    public IntervalMarker(double start, double end,
                          Paint paint, Stroke stroke,
                          Paint outlinePaint, Stroke outlineStroke,
                          float alpha) {

        super(paint, stroke, outlinePaint, outlineStroke, alpha);
        this.startValue = start;
        this.endValue = end;
        this.gradientPaintTransformer = null;
        
        setLabelOffsetType(LengthAdjustmentType.CONTRACT);
    }

    /**
     * Returns the start value for the interval.
     *
     * @return The start value.
     */
    public double getStartValue() {
        return startValue;
    }

    /**
     * Sets the start value for the marker and sends a
     * {@link MarkerChangeEvent} to all registered listeners.
     *
     * @param value  the value.
     *
     * @since 1.0.3
     */
    public void setStartValue(double value) {
    	startValue = value;
        
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Returns the end value for the interval.
     *
     * @return The end value.
     */
    public double getEndValue() {
        return endValue;
    }

    /**
     * Sets the end value for the marker and sends a
     * {@link MarkerChangeEvent} to all registered listeners.
     *
     * @param value  the value.
     *
     * @since 1.0.3
     */
    public void setEndValue(double value) {
    	endValue = value;
        
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Returns the gradient paint transformer.
     *
     * @return The gradient paint transformer (possibly {@code null}).
     */
    public GradientPaintTransformer getGradientPaintTransformer() {
        return gradientPaintTransformer;
    }

    /**
     * Sets the gradient paint transformer and sends a
     * {@link MarkerChangeEvent} to all registered listeners.
     *
     * @param transformer  the transformer ({@code null} permitted).
     */
    public void setGradientPaintTransformer(GradientPaintTransformer transformer) {
    	gradientPaintTransformer = transformer;
    	
        notifyListeners(new MarkerChangeEvent(this));
    }

    
    
    
    
    
    
    
    
    
    /**
     * Tests the marker for equality with an arbitrary object.
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
        if (!(obj instanceof IntervalMarker)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        IntervalMarker that = (IntervalMarker) obj;
        if (this.startValue != that.startValue) {
            return false;
        }
        if (this.endValue != that.endValue) {
            return false;
        }
        if (!ObjectUtils.equal(this.gradientPaintTransformer,
                that.gradientPaintTransformer)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of the marker.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException Not thrown by this class, but the
     *         exception is declared for the use of subclasses.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
