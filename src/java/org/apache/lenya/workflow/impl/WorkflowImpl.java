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

/* $Id$  */

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
     * @param _name The name.
     * @param _initialState the initial state of the workflow.
     */
    protected WorkflowImpl(String _name, StateImpl _initialState) {
        this.initialState = _initialState;
        this.name = _name;
        addState(_initialState);
    }

    private State initialState;
    private String name;

    /**
     * Returns the initial state of this workflow.
     * @return The initial state.
     *  
     */
    public State getInitialState() {
        return this.initialState;
    }

    private Set transitions = new HashSet();
    private Map states = new HashMap();

    /**
     * Adds a state.
     * @param state A state.
     */
    private void addState(StateImpl state) {
        this.states.put(state.getId(), state);
    }

    /**
     * Adds a transition.
     * @param transition The transition.
     */
    protected void addTransition(TransitionImpl transition) {
        this.transitions.add(transition);
        addState(transition.getSource());
        addState(transition.getDestination());
    }

    /**
     * Returns the transitions.
     * @return An array of transitions.
     */
    protected TransitionImpl[] getTransitions() {
        return (TransitionImpl[]) this.transitions.toArray(new TransitionImpl[this.transitions.size()]);
    }

    /**
     * Returns the destination state of a transition.
     * @param transition A transition.
     * @return The destination state.
     *  
     */
    protected State getDestination(Transition transition) {
        return ((TransitionImpl) transition).getDestination();
    }

    /**
     * Returns the transitions that leave a state.
     * @param state A state.
     * @return The transitions that leave the state.
     *  
     */
    public Transition[] getLeavingTransitions(State state) {
        Set leavingTransitions = new HashSet();
        TransitionImpl[] _transitions = getTransitions();

        for (int i = 0; i < _transitions.length; i++) {
            if (_transitions[i].getSource() == state) {
                leavingTransitions.add(_transitions[i]);
            }
        }

        return (Transition[]) leavingTransitions.toArray(new Transition[leavingTransitions.size()]);
    }

    /**
     * Checks if this workflow contains a state.
     * @param state The state to check.
     * @return <code>true</code> if the state is contained, <code>false</code>
     *         otherwise.
     */
    protected boolean containsState(State state) {
        return this.states.containsValue(state);
    }

    /**
     * Returns the states.
     * @return An array of states.
     */
    protected StateImpl[] getStates() {
        return (StateImpl[]) this.states.values().toArray(new StateImpl[this.states.size()]);
    }

    /**
     * Returns the state with a certain name.
     * @param _name The state name.
     * @return A state.
     * @throws WorkflowException when the state does not exist.
     */
    protected StateImpl getState(String _name) throws WorkflowException {
        if (!this.states.containsKey(_name)) {
            throw new WorkflowException("Workflow does not contain the state '" + _name + "'!");
        }

        return (StateImpl) this.states.get(_name);
    }

    private Map events = new HashMap();

    /**
     * Adds an event.
     * @param event An event.
     */
    protected void addEvent(EventImpl event) {
        this.events.put(event.getName(), event);
    }

    /**
     * Returns the event for a certain event name.
     * @param _name A string.
     * @return The event with this name.
     * @throws WorkflowException when no event with the given name exists.
     */
    public EventImpl getEvent(String _name) throws WorkflowException {
        if (!this.events.containsKey(_name)) {
            throw new WorkflowException("Workflow does not contain the event '" + _name + "'!");
        }

        return (EventImpl) this.events.get(_name);
    }

    private Map variables = new HashMap();

    /**
     * Adds a variable.
     * @param variable A variable.
     */
    protected void addVariable(BooleanVariableImpl variable) {
        this.variables.put(variable.getName(), variable);
    }

    /**
     * Returns the variable for a certain name.
     * @param _name The name of the variable.
     * @return A variable.
     * @throws WorkflowException if no variable with the given name exists.
     */
    public BooleanVariableImpl getVariable(String _name) throws WorkflowException {
        if (!this.variables.containsKey(_name)) {
            throw new WorkflowException("Workflow does not contain the variable '" + _name + "'!");
        }

        return (BooleanVariableImpl) this.variables.get(_name);
    }

    /**
     * Returns the variables.
     * @return An array of variables.
     */
    protected BooleanVariableImpl[] getVariables() {
        return (BooleanVariableImpl[]) this.variables.values().toArray(new BooleanVariableImpl[this.variables
                .size()]);
    }

    /**
     * @see org.apache.lenya.workflow.Workflow#getVariableNames()
     */
    public String[] getVariableNames() {
        BooleanVariableImpl[] _variables = getVariables();
        String[] names = new String[_variables.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = _variables[i].getName();
        }
        return names;
    }

    /**
     * @see org.apache.lenya.workflow.Workflow#getName()
     */
    public String getName() {
        return this.name;
    }
}