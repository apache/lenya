/*
 * ActionImpl.java
 *
 * Created on 9. April 2003, 10:04
 */

package org.lenya.cms.workflow.impl;

import org.lenya.cms.workflow.Action;

/**
 *
 * @author  andreas
 */
public class ActionImpl
    implements Action {
    
    /** Creates a new instance of ActionImpl */
    public ActionImpl(String id) {
        assert id != null;
        this.id = id;
    }
    
    private String id;
    
    public String getId() {
        return id;
    }
    
}
