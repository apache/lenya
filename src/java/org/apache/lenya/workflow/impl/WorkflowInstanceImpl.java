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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.lenya.workflow.Action;
import org.apache.lenya.workflow.BooleanVariable;
import org.apache.lenya.workflow.BooleanVariableInstance;
import org.apache.lenya.workflow.History;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.State;
import org.apache.lenya.workflow.Transition;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.lenya.workflow.WorkflowListener;

/**
 * Implementation of a workflow instance.
 */
public abstract class WorkflowInstanceImpl extends AbstractLogEnabled implements WorkflowInstance {

    /**
     * Creates a new instance of WorkflowInstanceImpl.
     * @param _workflow The workflow implementation to use.
     */
    protected WorkflowInstanceImpl(WorkflowImpl _workflow) {
        this.workflow = _workflow;
        initVariableInstances();
    }
    
    private History history;
    
    /**
     * @see org.apache.lenya.workflow.WorkflowInstance#getHistory()
     */
    public History getHistory() {
        initializeHistory();
        return this.history;
    }
    
    /**
     * @return The history of this instance.
     */
    protected abstract History createHistory();
    
    /**
     * Initializes the history.
     */
    protected void initializeHistory() {
        if (this.history == null) {
            this.history = createHistory();
        }
    }

    private WorkflowImpl workflow;

    /**
     * Returns the workflow object of this instance.
     * @return A workflow object.
     */
    public Workflow getWorkflow() {
        return getWorkflowImpl();
    }

    /**
     * Returns the workflow object of this instance.
     * @return A workflow object.
     */
    protected WorkflowImpl getWorkflowImpl() {
        return this.workflow;
    }

    /**
     * @see org.apache.lenya.workflow.WorkflowInstance#getExecutableEvents(org.apache.lenya.workflow.Situation)
     */
    public String[] getExecutableEvents(Situation situation) {

        initializeHistory();
        
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Resolving executable events");
        }

        Transition[] transitions = getWorkflow().getLeavingTransitions(getCurrentState());
        Set executableEvents = new HashSet();

        try {
            for (int i = 0; i < transitions.length; i++) {
                if (transitions[i].canFire(situation, this)) {
                    executableEvents.add(transitions[i].getEvent().getName());
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("    [" + transitions[i].getEvent() + "] can fire.");
                    }
                } else {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("    [" + transitions[i].getEvent() + "] can not fire.");
                    }
                }
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("    Resolving executable events completed.");
            }
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }

        return (String[]) executableEvents.toArray(new String[executableEvents.size()]);
    }

    /**
     * @see org.apache.lenya.workflow.WorkflowInstance#invoke(org.apache.lenya.workflow.Situation,
     *      java.lang.String)
     */
    public void invoke(Situation situation, String event) throws WorkflowException {

        initializeHistory();
        
        if (!Arrays.asList(getExecutableEvents(situation)).contains(event)) {
            throw new WorkflowException("The event '" + event
                    + "' cannot be invoked in the situation '" + situation + "'.");
        }

        TransitionImpl transition = getNextTransition(event);
        fire(transition);

        for (Iterator iter = this.listeners.iterator(); iter.hasNext();) {
            WorkflowListener listener = (WorkflowListener) iter.next();
            listener.transitionFired(this, situation, event, transition.getDestination());
        }
    }

    /**
     * Returns the transition that would fire for a given event.
     * @param event The event.
     * @return A transition.
     * @throws WorkflowException if no single transition would fire.
     */
    protected TransitionImpl getNextTransition(String event) throws WorkflowException {
        
        TransitionImpl nextTransition = null;
        Transition[] transitions = getWorkflow().getLeavingTransitions(getCurrentState());

        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].getEvent().getName().equals(event)) {

                if (nextTransition != null) {
                    throw new WorkflowException("More than one transition found for event ["
                            + event + "]!");
                }

                nextTransition = (TransitionImpl) transitions[i];
            }
        }

        if (nextTransition == null) {
            throw new WorkflowException("No transition found for event [" + event + "]!");
        }

        return nextTransition;
    }

    /**
     * Invokes a transition.
     * @param transition The transition to invoke.
     * @throws WorkflowException if something goes wrong.
     */
    protected void fire(TransitionImpl transition) throws WorkflowException {
        
        Action[] actions = transition.getActions();

        for (int i = 0; i < actions.length; i++) {
            actions[i].execute(this);
        }
    }

    /**
     * Returns the current state of this WorkflowInstance.
     * @return A state object.
     */
    public State getCurrentState() {

        initializeHistory();
        
        State state = null;
        try {
            if (getHistory().isInitialized()) {
                state = getHistory().getLastState();
            } else {
                state = getWorkflow().getInitialState();
            }
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
        return state;
    }

    /**
     * Returns a workflow state for a given name.
     * @param id The state id.
     * @return A workflow object.
     * @throws WorkflowException when the state was not found.
     */
    protected State getState(String id) throws WorkflowException {
        return getWorkflowImpl().getState(id);
    }

    private Map variableInstances = new HashMap();

    /**
     * Initializes the variable instances in the initial state.
     */
    protected void initVariableInstances() {
        this.variableInstances.clear();

        BooleanVariable[] variables = getWorkflowImpl().getVariables();

        for (int i = 0; i < variables.length; i++) {
            BooleanVariableInstance instance = new BooleanVariableInstanceImpl();
            instance.setValue(variables[i].getInitialValue());
            this.variableInstances.put(variables[i], instance);
        }
    }

    /**
     * Returns the corresponding instance of a workflow variable.
     * @param variable A variable of the corresponding workflow.
     * @return A variable instance object.
     * @throws WorkflowException when the variable instance was not found.
     */
    protected BooleanVariableInstance getVariableInstance(BooleanVariable variable)
            throws WorkflowException {
        if (!this.variableInstances.containsKey(variable)) {
            throw new WorkflowException("No instance for variable '" + variable.getName() + "'!");
        }

        return (BooleanVariableInstance) this.variableInstances.get(variable);
    }

    /**
     * @see org.apache.lenya.workflow.WorkflowInstance#getValue(java.lang.String)
     */
    public boolean getValue(String variableName) {
        
        initializeHistory();
        
        boolean value = false;
        try {
            BooleanVariable variable = getWorkflowImpl().getVariable(variableName);
            BooleanVariableInstance instance = getVariableInstance(variable);
            value = instance.getValue();
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }

        return value;
    }

    /**
     * Sets the value of a state variable.
     * @param variableName The variable name.
     * @param value The value to set.
     * @throws WorkflowException when the variable was not found.
     */
    protected void setValue(String variableName, boolean value) throws WorkflowException {
        BooleanVariable variable = getWorkflowImpl().getVariable(variableName);
        BooleanVariableInstance instance = getVariableInstance(variable);
        instance.setValue(value);
    }

    private List listeners = new ArrayList();

    /**
     * @see org.apache.lenya.workflow.WorkflowInstance#addWorkflowListener(org.apache.lenya.workflow.WorkflowListener)
     */
    public void addWorkflowListener(WorkflowListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    /**
     * @see org.apache.lenya.workflow.WorkflowInstance#removeWorkflowListener(org.apache.lenya.workflow.WorkflowListener)
     */
    public void removeWorkflowListener(WorkflowListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * @see org.apache.lenya.workflow.WorkflowInstance#isSynchronized(String)
     */
    public boolean isSynchronized(String event) throws WorkflowException {
        
        initializeHistory();
        
        Transition nextTransition = getNextTransition(event);
        return nextTransition.isSynchronized();
    }

    /**
     * @see org.apache.lenya.workflow.WorkflowInstance#canInvoke(org.apache.lenya.workflow.Situation,
     *      java.lang.String)
     */
    public boolean canInvoke(Situation situation, String event) {
        initializeHistory();
        return Arrays.asList(getExecutableEvents(situation)).contains(event);
    }
}