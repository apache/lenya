/*
 * Workflow.java
 *
 * Created on 8. April 2003, 17:03
 */

package org.apache.lenya.workflow;

/**
 *
 * @author  andreas
 */
public interface Workflow {
    
    String NAMESPACE = "http://apache.org/cocoon/lenya/workflow/1.0";
    String DEFAULT_PREFIX = "wf";

    /**
     * Returns the initial state of this workflow.
     * @return The initial state
     */
    State getInitialState();
    
    /**
     * Returns the transitions that leave a state.
     * This method is used, e.g., to disable menu items.
     * @param state A state.
     * @return The transitions that leave the state.
     */
    Transition[] getLeavingTransitions(State state);
    
}
