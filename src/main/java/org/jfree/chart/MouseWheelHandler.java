package org.jfree.chart;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.io.Serializable;

import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.Zoomable;

/**
 * A class that handles mouse wheel events for the {@link ChartPanel} class.
 */
class MouseWheelHandler implements MouseWheelListener, Serializable {
	private static final long serialVersionUID = -1262113874132490163L;

    private ChartPanel chartPanel;

    double zoomFactor;

    /**
     * Creates a new instance for the specified chart panel.
     *
     * @param chartPanel  the chart panel ({@code null} not permitted).
     */
    public MouseWheelHandler(ChartPanel chartPanel) {
    	this.chartPanel = chartPanel;
    	zoomFactor = 0.10;
    	chartPanel.addMouseWheelListener(this);
	}

    /**
     * Returns the current zoom factor.  The default value is 0.10 (ten
     * percent).
     *
     * @return The zoom factor.
     *
     * @see #setZoomFactor(double)
     */
    public double getZoomFactor() {
        return this.zoomFactor;
    }

    /**
     * Sets the zoom factor.
     */
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    /**
     * Handles a mouse wheel event from the underlying chart panel.
     *
     * @param e  the event.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    	JFreeChart chart = chartPanel.getChart();
    	if(chart == null) return;

    	Plot plot = chart.getPlot();

    	if(plot instanceof Zoomable) {
            Zoomable zoomable = (Zoomable)plot;
            handleZoomable(zoomable, e);
        }
        else if(plot instanceof PiePlot) {
            PiePlot pp = (PiePlot) plot;
            pp.handleMouseWheelRotation(e.getWheelRotation());
        }
    }

    /**
     * Handle the case where a plot implements the {@link Zoomable} interface.
     *
     * @param zoomable  the zoomable plot.
     * @param e  the mouse wheel event.
     */
    private void handleZoomable(Zoomable zoomable, MouseWheelEvent e) {
        // don't zoom unless the mouse pointer is in the plot's data area
        ChartRenderingInfo info = chartPanel.getChartRenderingInfo();
        PlotRenderingInfo plotRenderingInfo = info.getPlotInfo();
        Point2D p = chartPanel.translateScreenToJava2D(e.getPoint());
        if(!plotRenderingInfo.getDataArea().contains(p)) return;

        Plot plot = (Plot)zoomable;
        // do not notify while zooming each axis
        boolean notifyState = plot.isNotify();
        plot.setNotify(false);

        int rotationDir = e.getWheelRotation();
        double zf = 1.0 + zoomFactor;
        if(rotationDir < 0) zf = 1.0 / zf;

        if(chartPanel.isDomainZoomable()) {
            zoomable.zoomDomainAxes(zf, plotRenderingInfo, p, true);
        }

        if(chartPanel.isRangeZoomable()) {
            zoomable.zoomRangeAxes(zf, plotRenderingInfo, p, true);
        }

        plot.setNotify(notifyState);  // this generates the change event too
    }
}
