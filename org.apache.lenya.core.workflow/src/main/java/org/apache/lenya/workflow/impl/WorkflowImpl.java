/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
    protected WorkflowImpl(String _name, String _initialState) {
        this.initialState = _initialState;
        this.name = _name;
        addState(_initialState);
    }

    private String initialState;
    private String name;

    /**
     * Returns the initial state of this workflow.
     * @return The initial state.
     *  
     */
    public String getInitialState() {
        return this.initialState;
    }

    private Set transitions = new HashSet();
    private Set states = new HashSet();

    /**
     * Adds a state.
     * @param state A state.
     */
    private void addState(String state) {
        this.states.add(state);
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
     * @see org.apache.lenya.workflow.Workflow#getLeavingTransitions(java.lang.String)
     */
    public Transition[] getLeavingTransitions(String state) throws WorkflowException {
        Set leavingTransitions = new HashSet();
        TransitionImpl[] _transitions = getTransitions();
        for (int i = 0; i < _transitions.length; i++) {
            if (_transitions[i].getSource().equals(state)) {
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
    protected boolean containsState(String state) {
        return this.states.contains(state);
    }

    public String[] getStates() {
        return (String[]) this.states.toArray(new String[this.states.size()]);
    }

    private Set events = new HashSet();

    /**
     * Adds an event.
     * @param event An event.
     */
    protected void addEvent(String event) {
        this.events.add(event);
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

    /**
     * @see org.apache.lenya.workflow.Workflow#getInitialValue(java.lang.String)
     */
    public boolean getInitialValue(String variableName) throws WorkflowException {
        BooleanVariableImpl[] variables = getVariables();
        for (int i = 0; i < variables.length; i++) {
            if (variables[i].getName().equals(variableName)) {
                return variables[i].getInitialValue();
            }
        }
        throw new WorkflowException("The variable [" + variableName + "] does not exist.");
    }

    /**
     * @see org.apache.lenya.workflow.Workflow#getEvents()
     */
    public String[] getEvents() {
        return (String[]) this.events.toArray(new String[this.events.size()]);
    }
}