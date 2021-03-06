package org.jfree.chart.plot;

import java.awt.geom.Point2D;

/**
 * Maintains state information about crosshairs on a plot between successive
 * calls to the renderer's draw method.  This class is used internally by
 * JFreeChart - it is not intended for external use.
 */
public class CrosshairState {
    /**
     * A flag that controls whether the distance is calculated in data space
     * or Java2D space.
     */
    private boolean calculateDistanceInDataSpace = false;

    /** The x-value (in data space) for the anchor point. */
    private double anchorX;

    /** The y-value (in data space) for the anchor point. */
    private double anchorY;

    /** The anchor point in Java2D space - if null, don't update crosshair. */
    private Point2D anchor;

    /** The x-value for the current crosshair point. */
    private double crosshairX;

    /** The y-value for the current crosshair point. */
    private double crosshairY;

    /**
     * The dataset index that the crosshair point relates to (this determines
     * the axes that the crosshairs will be plotted against).
     *
     * @since 1.0.11
     */
    private int datasetIndex;

    /**
     * The smallest distance (so far) between the anchor point and a data
     * point.
     */
    private double distance;

    /**
     * Creates a new {@code crosshairState} instance that calculates
     * distance in Java2D space.
     */
	public CrosshairState() {
		this(false);
	}

    /**
     * Creates a new {@code crosshairState} instance.  Determination of the
     * data point nearest the anchor point can be calculated in either
     * dataspace or Java2D space.  The former should only be used for charts
     * with a single set of axes.
     *
     * @param calculateDistanceInDataSpace  a flag that controls whether the
     *                                      distance is calculated in data
     *                                      space or Java2D space.
     */
    public CrosshairState(boolean calculateDistanceInDataSpace) {
        this.calculateDistanceInDataSpace = calculateDistanceInDataSpace;
    }

    /**
     * Returns the distance between the anchor point and the current crosshair
     * point.
     *
     * @return The distance.
     *
     * @see #setCrosshairDistance(double)
     * @since 1.0.3
     */
    public double getCrosshairDistance() {
        return distance;
    }

    /**
     * Sets the distance between the anchor point and the current crosshair
     * point.  As each data point is processed, its distance to the anchor
     * point is compared with this value and, if it is closer, the data point
     * becomes the new crosshair point.
     *
     * @param distance  the distance.
     *
     * @see #getCrosshairDistance()
     */
	public void setCrosshairDistance(double distance) {
		this.distance = distance;
	}

    /**
     * Updates the crosshair point.
     * 
     * @param x  the x-value.
     * @param y  the y-value.
     * @param datasetIndex  the dataset index.
     * @param transX  the x-value in Java2D space.
     * @param transY  the y-value in Java2D space.
     * @param orientation  the plot orientation ({@code null} not permitted).
     */
    public void updateCrosshairPoint(double x, double y, int datasetIndex, double transX, double transY, PlotOrientation orientation) {
    	if(anchor != null) {
    		double d = 0.0;

    		if(calculateDistanceInDataSpace) d = (x - anchorX) * (x - anchorX) + (y - anchorY) * (y - anchorY);
            else {
                // anchor point is in Java2D coordinates
                double xx = this.anchor.getX();
                double yy = this.anchor.getY();

                if(orientation == PlotOrientation.HORIZONTAL) {
                    double temp = yy;
                    yy = xx;
                    xx = temp;
                }

                d = (transX - xx) * (transX - xx) + (transY - yy) * (transY - yy);
            }

            if(d < distance) {
            	crosshairX = x;
            	crosshairY = y;
            	this.datasetIndex = datasetIndex;
            	distance = d;
            }
        }
    }
    
    /**
     * Checks to see if the specified data point is the closest to the
     * anchor point and, if yes, updates the current state.
     * 
     * @param x  the x-value.
     * @param transX  the x-value in Java2D space.
     * @param datasetIndex  the dataset index.
     * 
     * @since 1.0.20
     */
    public void updateCrosshairX(double x, double transX, int datasetIndex) {
        if(anchor == null) return;

        double d = Math.abs(transX - anchor.getX());
        
        if(d < distance) {
            crosshairX = x;
            this.datasetIndex = datasetIndex;
            distance = d;
        }        
    }

    /**
     * Evaluates a y-value and if it is the closest to the anchor y-value it
     * becomes the new crosshair value.
     * <P>
     * Used in cases where only the y-axis is numerical.
     *
     * @param candidateY  y position of the candidate for the new crosshair
     *                    point.
     * @param transY  the y-value in Java2D space.
     * @param datasetIndex  the index of the range axis for this y-value.
     *
     * @since 1.0.20
     */
    public void updateCrosshairY(double candidateY, double transY, int datasetIndex) {
        if(anchor == null) return;

        double d = Math.abs(transY - anchor.getY());
        
        if(d < this.distance) {
        	crosshairY = candidateY;
            this.datasetIndex = datasetIndex;
            distance = d;
        }
    }

    /**
     * Returns the anchor point.
     *
     * @return The anchor point.
     *
     * @see #setAnchor(Point2D)
     *
     * @since 1.0.3
     */
    public Point2D getAnchor() {
        return anchor;
    }

    /**
     * Sets the anchor point.  This is usually the mouse click point in a chart
     * panel, and the crosshair point will often be the data item that is
     * closest to the anchor point.
     * <br><br>
     * Note that the x and y coordinates (in data space) are not updated by
     * this method - the caller is responsible for ensuring that this happens
     * in sync.
     *
     * @param anchor  the anchor point ({@code null} permitted).
     *
     * @see #getAnchor()
     */
    public void setAnchor(Point2D anchor) {
        this.anchor = anchor;
    }

    /**
     * Returns the x-coordinate (in data space) for the anchor point.
     *
     * @return The x-coordinate of the anchor point.
     *
     * @since 1.0.3
     */
    public double getAnchorX() {
        return anchorX;
    }

    /**
     * Sets the x-coordinate (in data space) for the anchor point.  Note that
     * this does NOT update the anchor itself - the caller is responsible for
     * ensuring this is done in sync.
     *
     * @param x  the x-coordinate.
     *
     * @since 1.0.3
     */
    public void setAnchorX(double x) {
    	anchorX = x;
    }

    /**
     * Returns the y-coordinate (in data space) for the anchor point.
     *
     * @return The y-coordinate of teh anchor point.
     *
     * @since 1.0.3
     */
    public double getAnchorY() {
        return anchorY;
    }

    /**
     * Sets the y-coordinate (in data space) for the anchor point.  Note that
     * this does NOT update the anchor itself - the caller is responsible for
     * ensuring this is done in sync.
     *
     * @param y  the y-coordinate.
     *
     * @since 1.0.3
     */
    public void setAnchorY(double y) {
    	anchorY = y;
    }

    /**
     * Get the x-value for the crosshair point.
     *
     * @return The x position of the crosshair point.
     *
     * @see #setCrosshairX(double)
     */
    public double getCrosshairX() {
        return crosshairX;
    }

    /**
     * Sets the x coordinate for the crosshair.  This is the coordinate in data
     * space measured against the domain axis.
     *
     * @param x the coordinate.
     *
     * @see #getCrosshairX()
     * @see #setCrosshairY(double)
     * @see #updateCrosshairPoint(double, double, int, double, double,
     * PlotOrientation)
     */
    public void setCrosshairX(double x) {
    	crosshairX = x;
    }

    /**
     * Get the y-value for the crosshair point.  This is the coordinate in data
     * space measured against the range axis.
     *
     * @return The y position of the crosshair point.
     *
     * @see #setCrosshairY(double)
     */
    public double getCrosshairY() {
        return crosshairY;
    }

    /**
     * Sets the y coordinate for the crosshair.
     *
     * @param y  the y coordinate.
     *
     * @see #getCrosshairY()
     * @see #setCrosshairX(double)
     * @see #updateCrosshairPoint(double, double, int, double, double,
     * PlotOrientation)
     */
    public void setCrosshairY(double y) {
    	crosshairY = y;
    }

    /**
     * Returns the dataset index that the crosshair values relate to.  The
     * dataset is mapped to specific axes, and this is how the crosshairs are
     * mapped also.
     *
     * @return The dataset index.
     *
     * @see #setDatasetIndex(int)
     *
     * @since 1.0.11
     */
    public int getDatasetIndex() {
        return datasetIndex;
    }

    /**
     * Sets the dataset index that the current crosshair values relate to.
     *
     * @param index  the dataset index.
     *
     * @see #getDatasetIndex()
     *
     * @since 1.0.11
     */
    public void setDatasetIndex(int index) {
    	datasetIndex = index;
    }
}
