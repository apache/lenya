/*
 * StateImpl.java
 *
 * Created on 8. April 2003, 18:35
 */

package org.apache.lenya.cms.workflow.impl;

import org.apache.lenya.cms.workflow.State;

/**
 *
 * @author  andreas
 */
public class StateImpl
    implements State {
    
    /** Creates a new instance of StateImpl */
    public StateImpl(String id) {
        assert id != null;
        this.id = id;
    }
    
    private String id;
    
    public String getId() {
        return id;
    }
    
    public String toString() {
        return getId();
    }
    
}
