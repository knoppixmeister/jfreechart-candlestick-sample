package org.jfree.data.general;

import java.io.Serializable;
import java.util.EventObject;

/**
 * An event with details of a change to a series.
*/
public class SeriesChangeEvent extends EventObject implements Serializable {
    private static final long serialVersionUID = 1593866085210089052L;

    /**
     * Constructs a new event.
     *
     * @param source  the source of the change event.
     */
    public SeriesChangeEvent(Object source) {
        super(source);
    }
}
