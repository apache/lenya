/*
 * WorkflowInstance.java
 *
 * Created on 8. April 2003, 17:14
 */

package org.apache.lenya.cms.workflow;

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
    Transition[] getExecutableTransitions(Situation situation);

    /**
     * Indicates that the user invoked an event.
     * @param user The user who invoked the event.
     * @param event The event that was invoked.
     */
    void invoke(Situation situation, Event event);

}
