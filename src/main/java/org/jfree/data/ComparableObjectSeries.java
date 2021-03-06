package org.jfree.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.Args;

import org.jfree.data.general.Series;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesException;

/**
 * A (possibly ordered) list of (Comparable, Object) data items.
 *
 * @since 1.0.3
 */
public class ComparableObjectSeries extends Series implements Cloneable, Serializable {
	private static final long serialVersionUID = 7328575189460421654L;

	/** Storage for the data items in the series. */
    protected List data;

    /** The maximum number of items for the series. */
    private int maximumItemCount = Integer.MAX_VALUE;

    /** A flag that controls whether the items are automatically sorted. */
    private boolean autoSort;

    /** A flag that controls whether or not duplicate x-values are allowed. */
    private boolean allowDuplicateXValues;

    /**
     * Creates a new empty series.  By default, items added to the series will
     * be sorted into ascending order by x-value, and duplicate x-values will
     * be allowed (these defaults can be modified with another constructor.
     *
     * @param key  the series key ({@code null} not permitted).
     */
    public ComparableObjectSeries(Comparable key) {
        this(key, true, true);
    }

    /**
     * Constructs a new series that contains no data.  You can specify
     * whether or not duplicate x-values are allowed for the series.
     *
     * @param key  the series key ({@code null} not permitted).
     * @param autoSort  a flag that controls whether or not the items in the
     *                  series are sorted.
     * @param allowDuplicateXValues  a flag that controls whether duplicate
     *                               x-values are allowed.
     */
    public ComparableObjectSeries(Comparable key, boolean autoSort, boolean allowDuplicateXValues) {
        super(key);
        
        data 						= new java.util.ArrayList();
        this.autoSort 				= autoSort;
        this.allowDuplicateXValues 	= allowDuplicateXValues;
    }

    /**
     * Returns the flag that controls whether the items in the series are
     * automatically sorted.  There is no setter for this flag, it must be
     * defined in the series constructor.
     *
     * @return A boolean.
     */
    public boolean getAutoSort() {
        return autoSort;
    }

    /**
     * Returns a flag that controls whether duplicate x-values are allowed.
     * This flag can only be set in the constructor.
     *
     * @return A boolean.
     */
    public boolean getAllowDuplicateXValues() {
        return allowDuplicateXValues;
    }

    /**
     * Returns the number of items in the series.
     *
     * @return The item count.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Returns the maximum number of items that will be retained in the series.
     * The default value is {@code Integer.MAX_VALUE}.
     *
     * @return The maximum item count.
     * @see #setMaximumItemCount(int)
     */
    public int getMaximumItemCount() {
        return maximumItemCount;
    }

    /**
     * Sets the maximum number of items that will be retained in the series.
     * If you add a new item to the series such that the number of items will
     * exceed the maximum item count, then the first element in the series is
     * automatically removed, ensuring that the maximum item count is not
     * exceeded.
     * <p>
     * Typically this value is set before the series is populated with data,
     * but if it is applied later, it may cause some items to be removed from
     * the series (in which case a {@link SeriesChangeEvent} will be sent to
     * all registered listeners.
     *
     * @param maximum  the maximum number of items for the series.
     */
    public void setMaximumItemCount(int maximum) {
    	maximumItemCount = maximum;
        boolean dataRemoved = false;
        
        while(data.size() > maximum) {
        	data.remove(0);
            dataRemoved = true;
        }
        
        if(dataRemoved) fireSeriesChanged();
    }

    /**
     * Adds new data to the series and sends a {@link SeriesChangeEvent} to
     * all registered listeners.
     * <P>
     * Throws an exception if the x-value is a duplicate AND the
     * allowDuplicateXValues flag is false.
     *
     * @param x  the x-value ({@code null} not permitted).
     * @param y  the y-value ({@code null} permitted).
     */
    protected void add(Comparable x, Object y) {
        add(x, y, true);
    }

    /**
     * Adds new data to the series and, if requested, sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     * <P>
     * Throws an exception if the x-value is a duplicate AND the
     * allowDuplicateXValues flag is false.
     *
     * @param x  the x-value ({@code null} not permitted).
     * @param y  the y-value ({@code null} permitted).
     * @param notify  a flag the controls whether or not a
     *                {@link SeriesChangeEvent} is sent to all registered
     *                listeners.
     */
    protected void add(Comparable x, Object y, boolean notify) {
        // delegate argument checking to XYDataItem...
        ComparableObjectItem item = new ComparableObjectItem(x, y);
        add(item, notify);
    }

    /**
     * Adds a data item to the series and, if requested, sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     *
     * @param item  the (x, y) item ({@code null} not permitted).
     * @param notify  a flag that controls whether or not a
     *                {@link SeriesChangeEvent} is sent to all registered
     *                listeners.
     */
    protected void add(ComparableObjectItem item, boolean notify) {
        Args.nullNotPermitted(item, "item");
        
        if(autoSort) {
            int index = Collections.binarySearch(data, item);
            
            if(index < 0) data.add(-index - 1, item);
            else {
                if(allowDuplicateXValues) {
                    // need to make sure we are adding *after* any duplicates
                    int size = data.size();
                    while(index < size && item.compareTo(data.get(index)) == 0) {
                        index++;
                    }

                    if(index < data.size()) data.add(index, item);
                    else data.add(item);
                }
                else throw new SeriesException("X-value already exists.");
            }
        }
        else {
            if(!allowDuplicateXValues) {
                // can't allow duplicate values, so we need to check whether
                // there is an item with the given x-value already
                int index = indexOf(item.getComparable());
                if(index >= 0) throw new SeriesException("X-value already exists.");
            }

            data.add(item);
        }
        
        if(getItemCount() > maximumItemCount) data.remove(0);

        if(notify) fireSeriesChanged();
    }
    
    protected void add(ComparableObjectItem item, int position, boolean notify) {
        Args.nullNotPermitted(item, "item");
        Args.requireNonNegative(position, "position");
        
        System.out.println("AAAA");
        
        if(autoSort) {
        	System.out.println("BBB");
        	
            int index = Collections.binarySearch(data, item);
            
            System.out.println("IDX: "+index);
            
            if(index < 0) data.add(-index - 1, item);
            else {
                if(allowDuplicateXValues) {
                    // need to make sure we are adding *after* any duplicates
                    int size = this.data.size();
                    while(index < size && item.compareTo(data.get(index)) == 0) {
                        index++;
                    }
                    
                    if(index < data.size()) data.add(index, item);
                    else data.add(item);
                }
                else throw new SeriesException("X-value already exists.");
            }
        }
        else {
            if(!allowDuplicateXValues) {
            	int index = indexOf(item.getComparable());
            	if(index >= 0) throw new SeriesException("X-value already exists.");
            }

            data.add(position, item);
        }
        
        if(getItemCount() > maximumItemCount) data.remove(0);

        if(notify) fireSeriesChanged();
    }

    /**
     * Returns the index of the item with the specified x-value, or a negative
     * index if the series does not contain an item with that x-value.  Be
     * aware that for an unsorted series, the index is found by iterating
     * through all items in the series.
     *
     * @param x  the x-value ({@code null} not permitted).
     *
     * @return The index.
     */
    public int indexOf(Comparable x) {
        if(autoSort) return Collections.binarySearch(data, new ComparableObjectItem(x, null));
        else {
            for(int i = 0; i < data.size(); i++) {
                ComparableObjectItem item = (ComparableObjectItem) data.get(i);
                if(item.getComparable().equals(x)) return i;
            }

            return -1;
        }
    }

    /**
     * Updates an item in the series.
     *
     * @param x  the x-value ({@code null} not permitted).
     * @param y  the y-value ({@code null} permitted).
     *
     * @throws SeriesException if there is no existing item with the specified
     *         x-value.
     */
    protected void update(Comparable x, Object y) {
        int index = indexOf(x);
        if(index < 0) throw new SeriesException("No observation for x = " + x);
        else {
            ComparableObjectItem item = getDataItem(index);
            item.setObject(y);
            
            fireSeriesChanged();
        }
    }

    /**
     * Updates the value of an item in the series and sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     *
     * @param index  the item (zero based index).
     * @param y  the new value ({@code null} permitted).
     */
    protected void updateByIndex(int index, Object y) {
        ComparableObjectItem item = getDataItem(index);
        item.setObject(y);
        
        fireSeriesChanged();
    }

    /**
     * Return the data item with the specified index.
     *
     * @param index  the index.
     *
     * @return The data item with the specified index.
     */
    protected ComparableObjectItem getDataItem(int index) {
        return (ComparableObjectItem) data.get(index);
    }

    /**
     * Deletes a range of items from the series and sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     *
     * @param start  the start index (zero-based).
     * @param end  the end index (zero-based).
     */
    protected void delete(int start, int end) {
        for(int i = start; i <= end; i++) {
            data.remove(start);
        }
        
        fireSeriesChanged();
    }

    /**
     * Removes all data items from the series and, unless the series is
     * already empty, sends a {@link SeriesChangeEvent} to all registered
     * listeners.
     */
    public void clear() {
        if(data.size() > 0) {
        	data.clear();
        	
            fireSeriesChanged();
        }
    }

    /**
     * Removes the item at the specified index and sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     *
     * @param index  the index.
     *
     * @return The item removed.
     */
    protected ComparableObjectItem remove(int index) {
        ComparableObjectItem result = (ComparableObjectItem) data.remove(index);
        fireSeriesChanged();
        
        return result;
    }

    /**
     * Removes the item with the specified x-value and sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     *
     * @param x  the x-value.

     * @return The item removed.
     */
    public ComparableObjectItem remove(Comparable x) {
    	return remove(indexOf(x));
    }

    
    
    
    
    
    
    
    
    /**
     * Tests this series for equality with an arbitrary object.
     *
     * @param obj  the object to test against for equality
     *             ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ComparableObjectSeries)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        ComparableObjectSeries that = (ComparableObjectSeries) obj;
        if (this.maximumItemCount != that.maximumItemCount) {
            return false;
        }
        if (this.autoSort != that.autoSort) {
            return false;
        }
        if (this.allowDuplicateXValues != that.allowDuplicateXValues) {
            return false;
        }
        if (!ObjectUtils.equal(this.data, that.data)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        // it is too slow to look at every data item, so let's just look at
        // the first, middle and last items...
        int count = getItemCount();
        if(count > 0) {
            ComparableObjectItem item = getDataItem(0);
            result = 29 * result + item.hashCode();
        }
        if(count > 1) {
            ComparableObjectItem item = getDataItem(count - 1);
            result = 29 * result + item.hashCode();
        }
        if(count > 2) {
            ComparableObjectItem item = getDataItem(count / 2);
            result = 29 * result + item.hashCode();
        }
        result = 29 * result + maximumItemCount;
        result = 29 * result + (autoSort ? 1 : 0);
        result = 29 * result + (allowDuplicateXValues ? 1 : 0);
        
        return result;
    }
}
