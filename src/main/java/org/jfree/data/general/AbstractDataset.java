package org.jfree.data.general;

import java.io.*;
import java.util.*;
import javax.swing.event.EventListenerList;
import org.jfree.chart.util.Args;

/**
 * An abstract implementation of the {@link Dataset} interface, containing a
 * mechanism for registering change listeners.
 */
public abstract class AbstractDataset implements Dataset, Cloneable, Serializable, ObjectInputValidation {
	private static final long serialVersionUID = 1918768939869230744L;

    /** The group that the dataset belongs to. */
    private DatasetGroup group;

    /** Storage for registered change listeners. */
    private transient EventListenerList listenerList;
    
    /** 
     * A flag that can be used to temporarily suppress dataset change event
     * notifications.
     */
    private boolean notify;

    /**
     * Constructs a dataset. By default, the dataset is assigned to its own
     * group.
     */
    protected AbstractDataset() {
    	group = new DatasetGroup();
    	listenerList = new EventListenerList();
    	notify = true;
    }

    /**
     * Returns the dataset group for the dataset.
     *
     * @return The group (never {@code null}).
     *
     * @see #setGroup(DatasetGroup)
     */
	@Override
	public DatasetGroup getGroup() {
		return group;
	}

    /**
     * Sets the dataset group for the dataset.
     *
     * @param group  the group ({@code null} not permitted).
     *
     * @see #getGroup()
     */
    @Override
    public void setGroup(DatasetGroup group) {
    	Args.nullNotPermitted(group, "group");

    	this.group = group;
    }

    /**
     * Returns the value of the notify flag.  The default value is 
     * {@code true}.  If this is {@code false}, calls to the 
     * {@link #fireDatasetChanged()} method will NOT trigger a dataset
     * change event.
     * 
     * @return A boolean.
     * 
     * @since 1.0.17
     */
    public boolean getNotify() {
        return notify;
    }

    /**
     * Sets the notify flag, which controls whether or not the {@link #fireDatasetChanged()}
     * method notifies listeners.  Setting this flag to {@code true} will
     * trigger a {@code DatasetChangeEvent} because there may be 
     * queued up changes.
     * 
     * @param notify  the new flag value.
     * 
     * @since 1.0.17
     */
    public void setNotify(boolean notify) {
        this.notify = notify;

        if(notify) fireDatasetChanged();    
    }
    
    /**
     * Registers an object to receive notification of changes to the dataset.
     *
     * @param listener  the object to register.
     *
     * @see #removeChangeListener(DatasetChangeListener)
     */
    @Override
    public void addChangeListener(DatasetChangeListener listener) {
    	listenerList.add(DatasetChangeListener.class, listener);
    }

    /**
     * Deregisters an object so that it no longer receives notification of
     * changes to the dataset.
     *
     * @param listener  the object to deregister.
     *
     * @see #addChangeListener(DatasetChangeListener)
     */
    @Override
    public void removeChangeListener(DatasetChangeListener listener) {
    	listenerList.remove(DatasetChangeListener.class, listener);
    }

    /**
     * Returns {@code true} if the specified object is registered with
     * the dataset as a listener.  Most applications won't need to call this
     * method, it exists mainly for use by unit testing code.
     *
     * @param listener  the listener.
     *
     * @return A boolean.
     *
     * @see #addChangeListener(DatasetChangeListener)
     * @see #removeChangeListener(DatasetChangeListener)
     */
    public boolean hasListener(EventListener listener) {
        return Arrays.asList(listenerList.getListenerList()).contains(listener);
    }

    /**
     * Notifies all registered listeners that the dataset has changed, 
     * provided that the {@code notify} flag has not been set to 
     * {@code false}.
     *
     * @see #addChangeListener(DatasetChangeListener)
     */
    protected void fireDatasetChanged() {
    	if(notify) notifyListeners(new DatasetChangeEvent(this, this));
    }

    /**
     * Notifies all registered listeners that the dataset has changed.
     *
     * @param event  contains information about the event that triggered the
     *               notification.
     *
     * @see #addChangeListener(DatasetChangeListener)
     * @see #removeChangeListener(DatasetChangeListener)
     */
    protected void notifyListeners(DatasetChangeEvent event) {
        Object[] listeners = listenerList.getListenerList();

        for(int i = listeners.length-2; i >= 0; i -= 2) {
            if(listeners[i] == DatasetChangeListener.class) ((DatasetChangeListener) listeners[i + 1]).datasetChanged(event);
        }
    }

    /**
     * Validates the object. We use this opportunity to call listeners who have
     * registered during the deserialization process, as listeners are not
     * serialized. This method is called by the serialization system after the
     * entire graph is read.
     *
     * This object has registered itself to the system with a priority of 10.
     * Other callbacks may register with a higher priority number to be called
     * before this object, or with a lower priority number to be called after
     * the listeners were notified.
     *
     * All listeners are supposed to have register by now, either in their
     * readObject or validateObject methods. Notify them that this dataset has
     * changed.
     *
     * @exception InvalidObjectException If the object cannot validate itself.
     */
    @Override
    public void validateObject() throws InvalidObjectException {
        fireDatasetChanged();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Returns a clone of the dataset. The cloned dataset will NOT include the
     * {@link DatasetChangeListener} references that have been registered with
     * this dataset.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  if the dataset does not support
     *                                     cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractDataset clone = (AbstractDataset) super.clone();
        clone.listenerList = new EventListenerList();
        
        return clone;
    }

    /**
     * Handles serialization.
     *
     * @param stream  the output stream.
     *
     * @throws IOException if there is an I/O problem.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Restores a serialized object.
     *
     * @param stream  the input stream.
     *
     * @throws IOException if there is an I/O problem.
     * @throws ClassNotFoundException if there is a problem loading a class.
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        listenerList = new EventListenerList();
        stream.registerValidation(this, 10);  // see comments about priority of
                                              // 10 in validateObject()
    }
}
