package org.jfree.chart.renderer;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.PlotRenderingInfo;

/**
 * Represents the current state of a renderer.
*/
public class RendererState {
    /** The plot rendering info. */
    private PlotRenderingInfo info;
    
    /** 
     * A flag that indicates whether or not rendering hints should be added to
     * identify chart elements.  It is initialised from the corresponding flag
     * in the JFreeChart instance.
     */
    private boolean elementHinting;

    /**
     * Creates a new state object.
     *
     * @param info  the plot rendering info.
     */
    public RendererState(PlotRenderingInfo info) {
        this.info = info;
        elementHinting = false;
    }
    
    /**
     * Returns the flag that controls whether or not the renderer should 
     * add rendering hints to the output that identify chart elements.
     * 
     * @return A boolean.
     * 
     * @since 1.0.20
     */
    public boolean getElementHinting() {
        return elementHinting;
    }
    
    /**
     * Sets the elementHinting flag.
     * 
     * @param hinting  the new flag value.
     * 
     * @since 1.0.20
     */
    public void setElementHinting(boolean hinting) {
    	elementHinting = hinting;
    }

    /**
     * Returns the plot rendering info.
     *
     * @return The info.
     */
    public PlotRenderingInfo getInfo() {
        return info;
    }

    /**
     * A convenience method that returns a reference to the entity
     * collection (may be {@code null}) being used to record
     * chart entities.
     *
     * @return The entity collection (possibly {@code null}).
     */
    public EntityCollection getEntityCollection() {
        EntityCollection result = null;
        if(info != null) {
            ChartRenderingInfo owner = info.getOwner();
            if(owner != null) result = owner.getEntityCollection();
        }
        
        return result;
    }
}
