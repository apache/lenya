/*
 * WorkflowImpl.java
 *
 * Created on 8. April 2003, 17:04
 */

package org.apache.lenya.workflow.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.lenya.workflow.State;
import org.apache.lenya.workflow.Transition;
import org.apache.lenya.workflow.Workflow;

/**
 *
 * @author  andreas
 */
public class WorkflowImpl implements Workflow {

    /** Creates a new instance of WorkflowImpl */
    protected WorkflowImpl(State initialState) {
        this.initialState = initialState;
        addState(initialState);
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
    private Map states = new HashMap();
    
    private void addState(State state) {
        states.put(((StateImpl) state).getId(), state);
    }

    /**
     * Adds a transition.
     */
    protected void addTransition(TransitionImpl transition) {
        assert transition != null;
        transitions.add(transition);
        addState(transition.getSource());
        addState(transition.getDestination());
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
        return states.containsValue(state);
    }
    
    protected State[] getStates() {
        return (State[]) states.values().toArray(new State[states.size()]);
    }
    
    protected State getState(String name) {
        return (State) states.get(name); 
    }
    
}
