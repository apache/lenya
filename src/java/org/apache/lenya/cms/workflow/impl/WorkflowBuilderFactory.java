/*
 * WorkflowFactory.java
 *
 * Created on 8. April 2003, 18:08
 */

package org.lenya.cms.workflow.impl;

/**
 *
 * @author  andreas
 */
public class WorkflowBuilderFactory {
    
    /** Creates a new instance of WorkflowFactory */
    public WorkflowBuilderFactory() {
    }
    
    public static WorkflowBuilderFactory newInstance() {
        return new WorkflowBuilderFactory();
    }
    
    public WorkflowBuilder createBuilder() {
        return new WorkflowBuilder();
    }
    
}
