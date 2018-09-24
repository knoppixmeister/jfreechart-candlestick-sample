package org.jfree.chart.plot;

import java.awt.geom.*;
import java.io.*;
import java.util.List;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.util.*;

/**
 * Stores information about the dimensions of a plot and its subplots.
*/
public class PlotRenderingInfo implements Cloneable, Serializable {
	private static final long serialVersionUID = 8446720134379617220L;

    /** The owner of this info. */
	private ChartRenderingInfo owner;

    /** The plot area. */
    private transient Rectangle2D plotArea;

	/** The data area. */
	private transient Rectangle2D dataArea;

	/**
     * Storage for the plot rendering info objects belonging to the subplots.
	*/
	private List subplotInfo;

    /**
     * Creates a new instance.
     *
     * @param owner  the owner ({@code null} permitted).
	*/
	public PlotRenderingInfo(ChartRenderingInfo owner) {
    	this.owner = owner;

    	dataArea = new Rectangle2D.Double();

    	subplotInfo = new java.util.ArrayList();
	}

    /**
     * Returns the owner (as specified in the constructor).
     *
     * @return The owner (possibly {@code null}).
	*/
	public ChartRenderingInfo getOwner() {
		return owner;
	}

    /**
     * Returns the plot area (in Java2D space).
     *
     * @return The plot area (possibly {@code null}).
     *
     * @see #setPlotArea(Rectangle2D)
     */
    public Rectangle2D getPlotArea() {
        return plotArea;
    }

    /**
     * Sets the plot area.
     *
     * @param area  the plot area (in Java2D space, {@code null}
     *     permitted but discouraged)
     *
     * @see #getPlotArea()
     */
    public void setPlotArea(Rectangle2D area) {
    	plotArea = area;
    }

    /**
     * Returns the plot's data area (in Java2D space).
     *
     * @return The data area (possibly {@code null}).
     *
     * @see #setDataArea(Rectangle2D)
     */
	public Rectangle2D getDataArea() {
		return dataArea;
	}

    /**
     * Sets the data area.
     *
     * @param area  the data area (in Java2D space, {@code null} permitted
     *     but discouraged).
     *
     * @see #getDataArea()
     */
    public void setDataArea(Rectangle2D area) {
    	dataArea = area;
    }

    /**
     * Returns the number of subplots (possibly zero).
     *
     * @return The subplot count.
     */
    public int getSubplotCount() {
        return subplotInfo.size();
    }

    /**
     * Adds the info for a subplot.
     *
     * @param info  the subplot info.
     *
     * @see #getSubplotInfo(int)
     */
    public void addSubplotInfo(PlotRenderingInfo info) {
    	subplotInfo.add(info);
    }

    /**
     * Returns the info for a subplot.
     *
     * @param index  the subplot index.
     *
     * @return The info.
     *
     * @see #addSubplotInfo(PlotRenderingInfo)
     */
    public PlotRenderingInfo getSubplotInfo(int index) {
        return (PlotRenderingInfo)subplotInfo.get(index);
    }

    /**
     * Returns the index of the subplot that contains the specified
     * (x, y) point (the "source" point).  The source point will usually
     * come from a mouse click on a {@link org.jfree.chart.ChartPanel},
     * and this method is then used to determine the subplot that
     * contains the source point.
     *
     * @param source  the source point (in Java2D space, {@code null} not
     * permitted).
     *
     * @return The subplot index (or -1 if no subplot contains {@code source}).
     */
	public int getSubplotIndex(Point2D source) {
		Args.nullNotPermitted(source, "source");

		int subplotCount = getSubplotCount();
		for(int i=0; i<subplotCount; i++) {
			Rectangle2D area = getSubplotInfo(i).getDataArea();

    		//System.out.println("");
    		//System.out.println("SRC_X: "+source.getX()+"; _Y: "+source.getY());
    		//System.out.println("AREA_X: "+area.getX()+"; AREA_WIDTH: "+area.getWidth()+"; AREA_HEIGHT: "+area.getHeight());
    		//System.out.println("");

    		if(area.contains(source)) return i;
		}

		return -1;
	}

    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Tests this instance for equality against an arbitrary object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;

        if(!(obj instanceof PlotRenderingInfo)) return false;

        PlotRenderingInfo that = (PlotRenderingInfo) obj;
        if(!ObjectUtils.equal(this.dataArea, that.dataArea)) {
            return false;
        }
        if (!ObjectUtils.equal(this.plotArea, that.plotArea)) {
            return false;
        }
        if (!ObjectUtils.equal(this.subplotInfo, that.subplotInfo)) {
            return false;
        }
        
        return true;
    }

    /**
     * Returns a clone of this object.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if there is a problem cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        PlotRenderingInfo clone = (PlotRenderingInfo) super.clone();
        if (this.plotArea != null) {
            clone.plotArea = (Rectangle2D) this.plotArea.clone();
        }
        if (this.dataArea != null) {
            clone.dataArea = (Rectangle2D) this.dataArea.clone();
        }
        clone.subplotInfo = new java.util.ArrayList(this.subplotInfo.size());
        for(int i = 0; i < this.subplotInfo.size(); i++) {
            PlotRenderingInfo info = (PlotRenderingInfo) this.subplotInfo.get(i);
            clone.subplotInfo.add(info.clone());
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
        SerialUtils.writeShape(dataArea, stream);
        SerialUtils.writeShape(plotArea, stream);
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
        
        dataArea = (Rectangle2D) SerialUtils.readShape(stream);
        plotArea = (Rectangle2D) SerialUtils.readShape(stream);
    }
}