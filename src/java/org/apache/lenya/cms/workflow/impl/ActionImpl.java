/*
 * ActionImpl.java
 *
 * Created on 9. April 2003, 10:04
 */

package org.apache.lenya.cms.workflow.impl;

import org.apache.lenya.cms.workflow.Action;

/**
 *
 * @author  andreas
 */
public class ActionImpl
    implements Action {
    
    /** Creates a new instance of ActionImpl */
    protected ActionImpl(String id) {
        assert id != null;
        this.id = id;
    }
    
    private String id;
    
    public String getId() {
        return id;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getId();
    }

}
