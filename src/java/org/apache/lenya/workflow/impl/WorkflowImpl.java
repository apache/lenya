/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id: WorkflowImpl.java,v 1.10 2004/03/01 16:18:21 gregor Exp $  */

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
 * Implementation of a workflow schema.
 */
public class WorkflowImpl implements Workflow {
    
    /**
     * Creates a new instance of WorkflowImpl.
     * @param initialState the initial state of the workflow.
     */
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

    /**
     * Adds a state.
     * @param state A state.
     */
    private void addState(StateImpl state) {
        states.put(state.getId(), state);
    }

    /**
     * Adds a transition.
     * @param transition The transition.
     */
    protected void addTransition(TransitionImpl transition) {
        transitions.add(transition);
        addState(transition.getSource());
        addState(transition.getDestination());
    }

    /**
     * Returns the transitions.
     * @return An array of transitions.
     */
    protected TransitionImpl[] getTransitions() {
        return (TransitionImpl[]) transitions.toArray(new TransitionImpl[transitions.size()]);
    }

    /** Returns the destination state of a transition.
     * @param transition A transition.
     * @return The destination state.
     *
     */
    protected State getDestination(Transition transition) {
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

    /**
     * Returns the states.
     * @return An array of states.
     */
    protected StateImpl[] getStates() {
        return (StateImpl[]) states.values().toArray(new StateImpl[states.size()]);
    }

    /**
     * Returns the state with a certain name.
     * @param name The state name.
     * @return A state.
     * @throws WorkflowException when the state does not exist.
     */
    protected StateImpl getState(String name) throws WorkflowException {
        if (!states.containsKey(name)) {
            throw new WorkflowException("Workflow does not contain the state '" + name + "'!");
        }

        return (StateImpl) states.get(name);
    }

    private Map events = new HashMap();

    /**
     * Adds an event.
     * @param event An event.
     */
    protected void addEvent(EventImpl event) {
        events.put(event.getName(), event);
    }

    /**
     * Returns the event for a certain event name.
     * @param name A string.
     * @return The event with this name.
     * @throws WorkflowException when no event with the given name exists.
     */
    public EventImpl getEvent(String name) throws WorkflowException {
        if (!events.containsKey(name)) {
            throw new WorkflowException("Workflow does not contain the event '" + name + "'!");
        }

        return (EventImpl) events.get(name);
    }

    private Map variables = new HashMap();

    /**
     * Adds a variable.
     * @param variable A variable.
     */
    protected void addVariable(BooleanVariableImpl variable) {
        variables.put(variable.getName(), variable);
    }

    /**
     * Returns the variable for a certain name.
     * @param name The name of the variable.
     * @return A variable.
     * @throws WorkflowException if no variable with the given name exists.
     */
    public BooleanVariableImpl getVariable(String name)
        throws WorkflowException {
        if (!variables.containsKey(name)) {
            throw new WorkflowException("Workflow does not contain the variable '" + name + "'!");
        }

        return (BooleanVariableImpl) variables.get(name);
    }

    /**
     * Returns the variables.
     * @return An array of variables.
     */
    protected BooleanVariableImpl[] getVariables() {
        return (BooleanVariableImpl[]) variables.values().toArray(new BooleanVariableImpl[variables.size()]);
    }

    /**
     * @see org.apache.lenya.workflow.Workflow#getVariableNames()
     */
    public String[] getVariableNames() {
        BooleanVariableImpl[] variables = getVariables();
        String[] names = new String[variables.length];
        for (int i = 0; i <names.length; i++) {
            names[i] = variables[i].getName();
        }
        return names;
    }
}
