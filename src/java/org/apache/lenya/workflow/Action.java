/*
 * Action.java
 *
 * Created on 8. April 2003, 17:11
 */

package org.apache.lenya.workflow;

/**
 *
 * @author  andreas
 */
public interface Action {
    
    /**
     * Executes this action for a given workflow instance.
     */
    void execute(WorkflowInstance instance) throws WorkflowException;
    
}
