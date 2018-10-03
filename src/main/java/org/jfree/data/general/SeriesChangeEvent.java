package org.jfree.data.general;

import java.util.EventObject;

/**
 * An event with details of a change to a series.
*/
public class SeriesChangeEvent extends EventObject {
	private static final long serialVersionUID = -2397757380089292552L;

	/**
     * Constructs a new event.
     *
     * @param source  the source of the change event.
     */
    public SeriesChangeEvent(Object source) {
        super(source);
    }
}
