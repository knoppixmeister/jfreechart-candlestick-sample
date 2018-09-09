package org.jfree.chart.event;

import org.jfree.chart.plot.Marker;

/**
 * An event that can be forwarded to any {@link MarkerChangeListener} to
 * signal a change to a {@link Marker}.
 *
 * @since 1.0.3
 */
public class MarkerChangeEvent extends ChartChangeEvent {
	private static final long serialVersionUID = -7830762061038492844L;

	/** The plot that generated the event. */
    private Marker marker;

    /**
     * Creates a new {@code MarkerChangeEvent} instance.
     *
     * @param marker  the marker that triggered the event ({@code null}
     *     not permitted).
     *
     * @since 1.0.3
     */
    public MarkerChangeEvent(Marker marker) {
        super(marker);
        this.marker = marker;
    }

    /**
     * Returns the marker that triggered the event.
     *
     * @return The marker that triggered the event (never {@code null}).
     *
     * @since 1.0.3
     */
	public Marker getMarker() {
		return marker;
	}
}
