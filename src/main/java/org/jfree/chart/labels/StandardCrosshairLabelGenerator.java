package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.*;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.util.Args;

/**
 * A default label generator.
 */
public class StandardCrosshairLabelGenerator implements CrosshairLabelGenerator, Serializable {
	private static final long serialVersionUID = -3759646778571809347L;

	/** The label format string. */
    private String labelTemplate;

    /** A number formatter for the value. */
    private NumberFormat numberFormat;

    /**
     * Creates a new instance with default attributes.
     */
    public StandardCrosshairLabelGenerator() {
    	this("{0}", NumberFormat.getNumberInstance());
    }

    /**
     * Creates a new instance with the specified attributes.
     *
     * @param labelTemplate  the label template ({@code null} not
     *     permitted).
     * @param numberFormat  the number formatter ({@code null} not
     *     permitted).
     */
    public StandardCrosshairLabelGenerator(String labelTemplate, NumberFormat numberFormat) {
    	super();

    	if(labelTemplate == null) {
            throw new IllegalArgumentException("Null 'labelTemplate' argument.");
    	}
    	if(numberFormat == null) {
            throw new IllegalArgumentException("Null 'numberFormat' argument.");
    	}

    	this.labelTemplate = labelTemplate;
    	this.numberFormat = numberFormat;
    }

    /**
     * Returns the label template string.
     *
     * @return The label template string (never {@code null}).
     */
    public String getLabelTemplate() {
        return labelTemplate;
    }

    /**
     * Returns the number formatter.
     *
     * @return The formatter (never {@code null}).
     */
    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    /**
     * Returns a string that can be used as the label for a crosshair.
     *
     * @param crosshair  the crosshair ({@code null} not permitted).
     *
     * @return The label (possibly {@code null}).
     */
    @Override
    public String generateLabel(Crosshair crosshair) {
    	Args.nullNotPermitted(crosshair, "crosshair");

    	return MessageFormat.format(
								labelTemplate,
								new Object[] {
									numberFormat.format(crosshair.getValue())
								}
    						);
	}

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Tests this generator for equality with an arbitrary object.
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
        if (!(obj instanceof StandardCrosshairLabelGenerator)) {
            return false;
        }
        StandardCrosshairLabelGenerator that = (StandardCrosshairLabelGenerator) obj;
        if (!this.labelTemplate.equals(that.labelTemplate)) {
            return false;
        }
        if (!this.numberFormat.equals(that.numberFormat)) {
            return false;
        }
 
        return true;
    }

    /**
     * Returns a hash code for this instance.
     *
     * @return A hash code for this instance.
     */
    @Override
    public int hashCode() {
        return labelTemplate.hashCode();
    }
}
