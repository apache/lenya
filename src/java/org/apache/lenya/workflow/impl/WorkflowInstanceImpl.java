/*
 * WorkflowInstanceImpl.java
 *
 * Created on 9. April 2003, 13:30
 */

package org.apache.lenya.workflow.impl;

import java.util.HashMap;
import java.util.HashSet;
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

/**
 *
 * @author  andreas
 */
public class WorkflowInstanceImpl implements WorkflowInstance {

    /**
     * Creates a new instance of WorkflowInstanceImpl.
     */
    public WorkflowInstanceImpl() {
    }

    /**
     * Creates a new instance of WorkflowInstanceImpl.
     */
    protected WorkflowInstanceImpl(WorkflowImpl workflow) {
        setWorkflow(workflow);
    }

    private WorkflowImpl workflow;

    /**
     * @return
     */
    public Workflow getWorkflow() {
        return getWorkflowImpl();
    }

    protected WorkflowImpl getWorkflowImpl() {
        return workflow;
    }

    /** Returns the transitions that can fire for this user.
     *
     */
    public Event[] getExecutableEvents(Situation situation) {
        Transition transitions[] = getWorkflow().getLeavingTransitions(getCurrentState());
        Set executableEvents = new HashSet();

        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].canFire(situation)) {
                executableEvents.add(transitions[i].getEvent());
            }
        }
        return (Event[]) executableEvents.toArray(
            new Event[executableEvents.size()]);
    }

    /** Indicates that the user invoked an event.
     * @param user The user who invoked the event.
     * @param event The event that was invoked.
     *
     */
    public void invoke(Situation situation, Event event) throws WorkflowException {
        Transition transitions[] = getWorkflow().getLeavingTransitions(getCurrentState());
        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].getEvent().equals(event)) {
                fire((TransitionImpl) transitions[i]);
            }
        }
    }

    protected void fire(TransitionImpl transition) throws WorkflowException {
        Action actions[] = transition.getActions();
        for (int i = 0; i < actions.length; i++) {
            actions[i].execute(this);
        }
        setCurrentState(transition.getDestination());
    }

    private State currentState;

    protected void setCurrentState(State state) {
        assert state != null && ((WorkflowImpl) getWorkflow()).containsState(state);
        this.currentState = state;
    }

    /** Returns the current state of this WorkflowInstance.
     *
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * @param workflow
     */
    protected void setWorkflow(WorkflowImpl workflow) {
        assert workflow != null;
        this.workflow = workflow;
        setCurrentState(getWorkflow().getInitialState());
        initVariableInstances();
    }

    /**
     * Returns a workflow state for a given name.
     */
    protected State getState(String id) throws WorkflowException {
        return getWorkflowImpl().getState(id);
    }

    private Map variableInstances = new HashMap();

    protected void initVariableInstances() {
        variableInstances.clear();
        BooleanVariable variables[] = getWorkflowImpl().getVariables();
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
     */
    protected BooleanVariableInstance getVariableInstance(BooleanVariable variable)
        throws WorkflowException {
        if (!variableInstances.containsKey(variable)) {
            throw new WorkflowException("No instance for variable '" + variable.getName() + "'!");
        }
        return (BooleanVariableInstance) variableInstances.get(variable);
    }

    /* (non-Javadoc)
     * @see org.apache.lenya.workflow.WorkflowInstance#getValue(java.lang.String)
     */
    public boolean getValue(String variableName) throws WorkflowException {
        BooleanVariable variable = getWorkflowImpl().getVariable(variableName);
        BooleanVariableInstance instance = getVariableInstance(variable);
        return instance.getValue();
    }

}
