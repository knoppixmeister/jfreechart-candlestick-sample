package org.jfree.chart.event;

import org.jfree.chart.plot.Plot;

/**
 * An event that can be forwarded to any
 * {@link org.jfree.chart.event.PlotChangeListener} to signal a change to a
 * plot.
*/
public class PlotChangeEvent extends ChartChangeEvent {
	private static final long serialVersionUID = -6086604650049391148L;

	/** The plot that generated the event. */
    private Plot plot;

    /**
     * Creates a new PlotChangeEvent.
     *
     * @param plot  the plot that generated the event.
     */
    public PlotChangeEvent(Plot plot) {
    	super(plot);

    	this.plot = plot;
    }

    /**
     * Returns the plot that generated the event.
     *
     * @return The plot that generated the event.
     */
	public Plot getPlot() {
        return plot;
	}
}
