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
import org.apache.lenya.workflow.WorkflowException;

/**
 *
 * @author  andreas
 */
public class WorkflowImpl implements Workflow {

    /** Creates a new instance of WorkflowImpl */
    protected WorkflowImpl(StateImpl initialState) {
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

    private void addState(StateImpl state) {
        states.put(state.getId(), state);
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
        return (TransitionImpl[]) transitions.toArray(new TransitionImpl[transitions.size()]);
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
        return (Transition[]) leavingTransitions.toArray(new Transition[leavingTransitions.size()]);
    }

    /**
     * Checks if this workflow contains a state.
     * @param state The state to check.
     * @return <code>true</code> if the state is contained, <code>false</code> otherwise.
     */
    protected boolean containsState(State state) {
        return states.containsValue(state);
    }

    protected StateImpl[] getStates() {
        return (StateImpl[]) states.values().toArray(new StateImpl[states.size()]);
    }

    protected StateImpl getState(String name) throws WorkflowException {
        if (!states.containsKey(name)) {
            throw new WorkflowException("Workflow does not contain the state '" + name + "'!");
        }
        return (StateImpl) states.get(name);
    }

    private Map events = new HashMap();

    protected void addEvent(EventImpl event) {
        assert event != null;
        events.put(event.getName(), event);
    }

    public EventImpl getEvent(String name) throws WorkflowException {
        if (!events.containsKey(name)) {
            throw new WorkflowException("Workflow does not contain the event '" + name + "'!");
        }
        return (EventImpl) events.get(name);
    }

    private Map variables = new HashMap();

    protected void addVariable(BooleanVariableImpl variable) {
        assert variable != null;
        variables.put(variable.getName(), variable);
    }

    public BooleanVariableImpl getVariable(String name) throws WorkflowException {
        if (!variables.containsKey(name)) {
            throw new WorkflowException("Workflow does not contain the variable '" + name + "'!");
        }
        return (BooleanVariableImpl) variables.get(name);
    }

    protected BooleanVariableImpl[] getVariables() {
        return (BooleanVariableImpl[]) variables.values().toArray(
            new BooleanVariableImpl[variables.size()]);
    }

}
