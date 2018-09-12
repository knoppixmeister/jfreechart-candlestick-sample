package org.jfree.data.general;

import java.io.Serializable;
import org.jfree.chart.util.Args;

/**
 * A class that is used to group datasets (currently not used for any specific
 * purpose).
 */
public class DatasetGroup implements Cloneable, Serializable {
    private static final long serialVersionUID = -3640642179674185688L;

    /** The group id. */
    private String id;

    /**
     * Constructs a new group.
     */
    public DatasetGroup() {
        super();
        
        id = "NOID";
    }

    /**
     * Creates a new group with the specified id.
     *
     * @param id  the identification for the group.
     */
    public DatasetGroup(String id) {
        Args.nullNotPermitted(id, "id");
        
        this.id = id;
    }

    /**
     * Returns the identification string for this group.
     *
     * @return The identification string.
     */
    public String getID() {
        return id;
    }

    /**
     * Clones the group.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException not by this class.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    
    
    
    
    
    
    
    
    
    /**
     * Tests this instance for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DatasetGroup)) {
            return false;
        }
        DatasetGroup that = (DatasetGroup) obj;
        if(!this.id.equals(that.id)) {
            return false;
        }
        
        return true;
    }
}
