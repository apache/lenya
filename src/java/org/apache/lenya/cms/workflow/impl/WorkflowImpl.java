/*
 * WorkflowImpl.java
 *
 * Created on 8. April 2003, 17:04
 */

package org.apache.lenya.cms.workflow.impl;

import java.util.HashSet;
import java.util.Set;
import org.apache.lenya.cms.workflow.State;
import org.apache.lenya.cms.workflow.Transition;
import org.apache.lenya.cms.workflow.Workflow;

/**
 *
 * @author  andreas
 */
public class WorkflowImpl implements Workflow {

    /** Creates a new instance of WorkflowImpl */
    public WorkflowImpl(State initialState) {
        this.initialState = initialState;
    }

    private State initialState;

    /** Returns the initial state of this workflow.
     * @return The initial state.
     *
     */
    public State getInitialState() {
        return initialState;
    }

    private Set transitions = new HashSet();

    /**
     * Adds a transition.
     */
    protected void addTransition(TransitionImpl transition) {
        assert transition != null;
        transitions.add(transition);
    }

    protected TransitionImpl[] getTransitions() {
        return (TransitionImpl[]) transitions.toArray(
            new TransitionImpl[transitions.size()]);
    }

    /** Returns the destination state of a transition.
     * @param transition A transition.
     * @return The destination state.
     *
     */
    protected State getDestination(Transition transition) {
        assert transition instanceof TransitionImpl;
        return ((TransitionImpl) transition).getDestination();
    }

    /** Returns the transitions that leave a state.
     * @param state A state.
     * @return The transitions that leave the state.
     *
     */
    public Transition[] getLeavingTransitions(State state) {
        Set leavingTransitions = new HashSet();
        TransitionImpl[] transitions = getTransitions();
        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].getSource() == state) {
                leavingTransitions.add(transitions[i]);
            }
        }
        return (Transition[]) leavingTransitions.toArray(
            new Transition[leavingTransitions.size()]);
    }

    /**
     * Checks if this workflow contains a state.
     * @param state The state to check.
     * @return <code>true</code> if the state is contained, <code>false</code> otherwise.
     */
    protected boolean containsState(State state) {
        Transition transitions[] = getTransitions();
        boolean result = false;
        for (int i = 0; i < transitions.length; i++) {
            TransitionImpl transition = (TransitionImpl) transitions[i];
            if (transition.getSource().equals(state)
                || transition.getDestination().equals(state)) {
                result = true;
            }
        }
        return result;
    }

}
