/*
 * WorkflowInstance.java
 *
 * Created on 8. April 2003, 17:14
 */

package org.apache.lenya.workflow;

/**
 *
 * @author  andreas
 */
public interface WorkflowInstance {

    /**
     * Returns the workflow this instance belongs to. 
     * @return A Workflow object.
     */
    Workflow getWorkflow();

    /**
     * Returns the current state of this WorkflowInstance.
     */
    State getCurrentState();

    /**
     * Returns the transitions that can fire for this user.
     */
    Event[] getExecutableEvents(Situation situation);

    /**
     * Indicates that the user invoked an event.
     * @param user The user who invoked the event.
     * @param event The event that was invoked.
     * @throws WorkflowException
     */
    void invoke(Situation situation, Event event) throws WorkflowException;
    
    /**
     * Returns the current value of a variable.
     * @param variable A variable.
     * @return A boolean value.
     * @throws WorkflowException
     */
    boolean getValue(String variableName) throws WorkflowException;

}
