package org.jfree.chart;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.SerialUtils;

/**
 * A structure for storing rendering information from one call to the
 * JFreeChart.draw() method.
 * <P>
 * An instance of the {@link JFreeChart} class can draw itself within an
 * arbitrary rectangle on any {@code Graphics2D}.  It is assumed that
 * client code will sometimes render the same chart in more than one view, so
 * the {@link JFreeChart} instance does not retain any information about its
 * rendered dimensions.  This information can be useful sometimes, so you have
 * the option to collect the information at each call to
 * {@code JFreeChart.draw()}, by passing an instance of this
 * {@code ChartRenderingInfo} class.
 */
public class ChartRenderingInfo implements Cloneable, Serializable {
    private static final long serialVersionUID = 2751952018173406822L;

    /** The area in which the chart is drawn. */
    private transient Rectangle2D chartArea;

    /** Rendering info for the chart's plot (and subplots, if any). */
    private PlotRenderingInfo plotInfo;

    /**
     * Storage for the chart entities.  Since retaining entity information for
     * charts with a large number of data points consumes a lot of memory, it
     * is intended that you can set this to {@code null} to prevent the
     * information being collected.
     */
    private EntityCollection entities;

    /**
     * Constructs a new ChartRenderingInfo structure that can be used to
     * collect information about the dimensions of a rendered chart.
     */
    public ChartRenderingInfo() {
        this(new StandardEntityCollection());
    }

    /**
     * Constructs a new instance. If an entity collection is supplied, it will
     * be populated with information about the entities in a chart.  If it is
     * {@code null}, no entity information (including tool tips) will
     * be collected.
     *
     * @param entities  an entity collection ({@code null} permitted).
     */
    public ChartRenderingInfo(EntityCollection entities) {
    	chartArea 		= new Rectangle2D.Double();
    	plotInfo 		= new PlotRenderingInfo(this);
    	this.entities 	= entities;
    }

    /**
     * Returns the area in which the chart was drawn.
     *
     * @return The area in which the chart was drawn.
     *
     * @see #setChartArea(Rectangle2D)
     */
    public Rectangle2D getChartArea() {
        return chartArea;
    }

    /**
     * Sets the area in which the chart was drawn.
     *
     * @param area  the chart area.
     *
     * @see #getChartArea()
     */
    public void setChartArea(Rectangle2D area) {
    	chartArea.setRect(area);
    }

    /**
     * Returns the collection of entities maintained by this instance.
     *
     * @return The entity collection (possibly {@code null}).
     *
     * @see #setEntityCollection(EntityCollection)
     */
    public EntityCollection getEntityCollection() {
        return entities;
    }

    /**
     * Sets the entity collection.
     *
     * @param entities  the entity collection ({@code null} permitted).
     *
     * @see #getEntityCollection()
     */
    public void setEntityCollection(EntityCollection entities) {
        this.entities = entities;
    }

    /**
     * Clears the information recorded by this object.
     */
    public void clear() {
    	chartArea.setRect(0.0, 0.0, 0.0, 0.0);
    	plotInfo = new PlotRenderingInfo(this);
        if(entities != null) entities.clear();
    }

    /**
     * Returns the rendering info for the chart's plot.
     *
     * @return The rendering info for the plot.
     */
    public PlotRenderingInfo getPlotInfo() {
        return plotInfo;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Tests this object for equality with an arbitrary object.
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
        if (!(obj instanceof ChartRenderingInfo)) {
            return false;
        }
        ChartRenderingInfo that = (ChartRenderingInfo) obj;
        if (!ObjectUtils.equal(this.chartArea, that.chartArea)) {
            return false;
        }
        if (!ObjectUtils.equal(this.plotInfo, that.plotInfo)) {
            return false;
        }
        if (!ObjectUtils.equal(this.entities, that.entities)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of this object.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the object cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        ChartRenderingInfo clone = (ChartRenderingInfo) super.clone();
        if(chartArea != null) {
            clone.chartArea = (Rectangle2D) this.chartArea.clone();
        }
        if(entities instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.entities;
            clone.entities = (EntityCollection) pc.clone();
        }
        
        return clone;
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
        
        SerialUtils.writeShape(chartArea, stream);
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
        chartArea = (Rectangle2D) SerialUtils.readShape(stream);
    }
}
