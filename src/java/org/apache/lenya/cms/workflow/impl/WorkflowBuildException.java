/*
 * WorkflowBuildException.java
 *
 * Created on 8. April 2003, 20:01
 */

package org.apache.lenya.cms.workflow.impl;

/**
 *
 * @author  andreas
 */
public class WorkflowBuildException
    extends Exception {
        
    /** Creates a new instance of WorkflowBuildException */
    public WorkflowBuildException() {
        super(MESSAGE);
    }
    
    /** Creates a new instance of WorkflowBuildException */
    public WorkflowBuildException(String message) {
        super(message);
    }
    
    public WorkflowBuildException(Throwable cause) {
        super(MESSAGE, cause);
    }
    
    public WorkflowBuildException(String message, Throwable cause) {
        super(message, cause);
    }
    
    protected static final String MESSAGE = "Workflow building failed: ";
    
    
}
