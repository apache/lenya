/*
 * ActionImpl.java
 *
 * Created on 9. April 2003, 10:04
 */

package org.apache.lenya.workflow.impl;

import org.apache.lenya.workflow.Action;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 *
 * @author  andreas
 */
public class ActionImpl
    implements Action {
    
    /** Creates a new instance of ActionImpl */
    protected ActionImpl(String actionId) {
        assert actionId != null;
        id = actionId;
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

    /* (non-Javadoc)
     * @see org.apache.lenya.workflow.Action#execute(org.apache.lenya.workflow.WorkflowInstance)
     */
    public void execute(WorkflowInstance instance) throws WorkflowException {
    }

}
