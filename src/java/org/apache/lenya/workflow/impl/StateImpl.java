/*
 * StateImpl.java
 *
 * Created on 8. April 2003, 18:35
 */

package org.apache.lenya.workflow.impl;

import org.apache.lenya.workflow.State;

/**
 *
 * @author  andreas
 */
public class StateImpl implements State {

    /** Creates a new instance of StateImpl */
    protected StateImpl(String id) {
        assert id != null;
        this.id = id;
    }

    private String id;

    /**
     * @return
     */
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getId();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object) {
        boolean result = false;
        if (object instanceof StateImpl) {
            result = getId().equals(((StateImpl) object).getId());
        } else {
            result = super.equals(object);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getId().hashCode();
    }

}
