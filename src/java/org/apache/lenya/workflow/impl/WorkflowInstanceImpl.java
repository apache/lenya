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

/* $Id: WorkflowInstanceImpl.java,v 1.14 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.workflow.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lenya.workflow.Action;
import org.apache.lenya.workflow.BooleanVariable;
import org.apache.lenya.workflow.BooleanVariableInstance;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.State;
import org.apache.lenya.workflow.Transition;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.lenya.workflow.WorkflowListener;
import org.apache.log4j.Category;


/**
 * Implementation of a workflow instance.
 */
public abstract class WorkflowInstanceImpl implements WorkflowInstance {
    
    private static final Category log = Category.getInstance(WorkflowInstanceImpl.class);
    
    /**
     * Creates a new instance of WorkflowInstanceImpl.
     */
    protected WorkflowInstanceImpl() {
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
        return workflow;
    }

    /** Returns the events that can be invoked in a certain situation.
     * @param situation The situation to check.
     * @return The events that can be invoked.
     * @throws WorkflowException when something went wrong.
     */
    public Event[] getExecutableEvents(Situation situation) throws WorkflowException {
        
        if (log.isDebugEnabled()) {
            log.debug("Resolving executable events");
        }
        
        Transition[] transitions = getWorkflow().getLeavingTransitions(getCurrentState());
        Set executableEvents = new HashSet();

        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].canFire(situation, this)) {
                executableEvents.add(transitions[i].getEvent());
                if (log.isDebugEnabled()) {
                    log.debug("    [" + transitions[i].getEvent() + "] can fire.");
                }
            }
            else {
                if (log.isDebugEnabled()) {
                    log.debug("    [" + transitions[i].getEvent() + "] can not fire.");
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("    Resolving executable events completed.");
        }
        
        return (Event[]) executableEvents.toArray(new Event[executableEvents.size()]);
    }

    /** Invoke an event on this workflow instance.
     * @param situation The situation when the event was invoked.
     * @param event The event that was invoked.
     * @throws WorkflowException when the event may not be invoked.
     */
    public void invoke(Situation situation, Event event)
        throws WorkflowException {
        if (!Arrays.asList(getExecutableEvents(situation)).contains(event)) {
            throw new WorkflowException("The event '" + event +
                "' cannot be invoked in the situation '" + situation + "'.");
        }

        fire(getNextTransition(event));

        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            WorkflowListener listener = (WorkflowListener) iter.next();
            listener.transitionFired(this, situation, event);
        }
    }

    /**
     * Returns the transition that would fire for a given event.
     * @param event The event.
     * @return A transition.
     * @throws WorkflowException if no single transition would fire.
     */
    protected TransitionImpl getNextTransition(Event event) throws WorkflowException {
        TransitionImpl nextTransition = null;
        Transition[] transitions = getWorkflow().getLeavingTransitions(getCurrentState());

        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].getEvent().equals(event)) {
                
                if (nextTransition != null) {
                    throw new WorkflowException("More than one transition found for event [" + event + "]!");
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

        setCurrentState(transition.getDestination());
    }

    private State currentState;

    /**
     * Sets the current state of this instance.
     * @param state The state to set.
     */
    protected void setCurrentState(State state) {
        this.currentState = state;
    }

    /** Returns the current state of this WorkflowInstance.
     * @return A state object.
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * Sets the workflow of this instance.
     * @param workflow A workflow object.
     */
    protected void setWorkflow(WorkflowImpl workflow) {
        this.workflow = workflow;
        setCurrentState(getWorkflow().getInitialState());
        initVariableInstances();
    }

    /**
     * Sets the workflow of this instance.
     * @param workflowName The identifier of the workflow.
     * @throws WorkflowException if something goes wrong.
     */
    protected void setWorkflow(String workflowName) throws WorkflowException {
        setWorkflow(getWorkflow(workflowName));
    }

    /**
     * Factory method to create a workflow object for a given identifier.
     * @param workflowName The workflow identifier.
     * @return A workflow object.
     * @throws WorkflowException when the workflow could not be created.
     */
    protected abstract WorkflowImpl getWorkflow(String workflowName)
        throws WorkflowException;

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
        variableInstances.clear();

        BooleanVariable[] variables = getWorkflowImpl().getVariables();

        for (int i = 0; i < variables.length; i++) {
            BooleanVariableInstance instance = new BooleanVariableInstanceImpl();
            instance.setValue(variables[i].getInitialValue());
            variableInstances.put(variables[i], instance);
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
        if (!variableInstances.containsKey(variable)) {
            throw new WorkflowException("No instance for variable '" + variable.getName() + "'!");
        }

        return (BooleanVariableInstance) variableInstances.get(variable);
    }

    /**
     * @see org.apache.lenya.workflow.WorkflowInstance#getValue(java.lang.String)
     */
    public boolean getValue(String variableName) throws WorkflowException {
        BooleanVariable variable = getWorkflowImpl().getVariable(variableName);
        BooleanVariableInstance instance = getVariableInstance(variable);

        return instance.getValue();
    }

    /**
     * Sets the value of a state variable.
     * @param variableName The variable name.
     * @param value The value to set.
     * @throws WorkflowException when the variable was not found.
     */
    protected void setValue(String variableName, boolean value)
        throws WorkflowException {
        BooleanVariable variable = getWorkflowImpl().getVariable(variableName);
        BooleanVariableInstance instance = getVariableInstance(variable);
        instance.setValue(value);
    }

    private List listeners = new ArrayList();

    /**
     * @see org.apache.lenya.workflow.WorkflowInstance#addWorkflowListener(org.apache.lenya.workflow.WorkflowListener)
     */
    public void addWorkflowListener(WorkflowListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * @see org.apache.lenya.workflow.WorkflowInstance#removeWorkflowListener(org.apache.lenya.workflow.WorkflowListener)
     */
    public void removeWorkflowListener(WorkflowListener listener) {
        listeners.remove(listener);
    }

    /**
     * @see org.apache.lenya.workflow.WorkflowInstance#isSynchronized(org.apache.lenya.workflow.Situation, org.apache.lenya.workflow.Event)
     */
    public boolean isSynchronized(Event event) throws WorkflowException {
        Transition nextTransition = getNextTransition(event);
        return nextTransition.isSynchronized();
    }

}
