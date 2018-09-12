package org.jfree.chart.panel;

import javax.swing.event.EventListenerList;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.OverlayChangeEvent;
import org.jfree.chart.event.OverlayChangeListener;
import org.jfree.chart.util.Args;

/**
 * A base class for implementing overlays for a {@link ChartPanel}.
 *
 * @since 1.0.13
 */
public class AbstractOverlay {
	private transient EventListenerList changeListeners;

    /**
     * Default constructor.
     */
	public AbstractOverlay() {
		changeListeners = new EventListenerList();
	}

    /**
     * Registers an object for notification of changes to the overlay.
     *
     * @param listener  the listener ({@code null} not permitted).
     *
     * @see #removeChangeListener(OverlayChangeListener)
     */
    public void addChangeListener(OverlayChangeListener listener) {
    	Args.nullNotPermitted(listener, "listener");
        
    	changeListeners.add(OverlayChangeListener.class, listener);
    }

    /**
     * Deregisters an object for notification of changes to the overlay.
     *
     * @param listener  the listener ({@code null} not permitted)
     *
     * @see #addChangeListener(OverlayChangeListener)
	*/
    public void removeChangeListener(OverlayChangeListener listener) {
        Args.nullNotPermitted(listener, "listener");
        
        changeListeners.remove(OverlayChangeListener.class, listener);
    }

    /**
     * Sends a default {@link ChartChangeEvent} to all registered listeners.
     * <P>
     * This method is for convenience only.
     */
    public void fireOverlayChanged() {
        OverlayChangeEvent event = new OverlayChangeEvent(this);
        
        notifyListeners(event);
    }

    /**
     * Sends a {@link ChartChangeEvent} to all registered listeners.
     *
     * @param event  information about the event that triggered the
     *               notification.
     */
	protected void notifyListeners(OverlayChangeEvent event) {
    	Object[] listeners = changeListeners.getListenerList();
    	for(int i = listeners.length - 2; i >= 0; i -= 2) {
            if(listeners[i] == OverlayChangeListener.class) ((OverlayChangeListener) listeners[i + 1]).overlayChanged(event);
    	}
	}
}