/*
 * WorkflowInstanceImpl.java
 *
 * Created on 9. April 2003, 13:30
 */

package org.apache.lenya.workflow.impl;

import java.util.HashSet;
import java.util.Set;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.State;
import org.apache.lenya.workflow.Transition;
import org.apache.lenya.workflow.Workflow;
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
    protected WorkflowInstanceImpl(Workflow workflow) {
        setWorkflow(workflow);
    }

    private Workflow workflow;

    /**
     * @return
     */
    public Workflow getWorkflow() {
        return workflow;
    }

    /** Returns the transitions that can fire for this user.
     *
     */
    public Transition[] getExecutableTransitions(Situation situation) {
        Transition transitions[] =
            getWorkflow().getLeavingTransitions(getCurrentState());
        Set executableTransitions = new HashSet();

        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].canFire(situation)) {
                executableTransitions.add(transitions[i]);
            }
        }
        return (Transition[]) executableTransitions.toArray(
            new Transition[executableTransitions.size()]);
    }

    /** Indicates that the user invoked an event.
     * @param user The user who invoked the event.
     * @param event The event that was invoked.
     *
     */
    public void invoke(Situation situation, Event event) {
        Transition transitions[] = getExecutableTransitions(situation);
        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].getEvent().equals(event)) {
                fire((TransitionImpl) transitions[i]);
            }
        }
    }

    protected void fire(TransitionImpl transition) {
        setCurrentState(transition.getDestination());
    }

    private State currentState;

    protected void setCurrentState(State state) {
        assert state != null
            && ((WorkflowImpl) getWorkflow()).containsState(state);
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
	protected void setWorkflow(Workflow workflow) {
		assert workflow != null;
		this.workflow = workflow;
		setCurrentState(getWorkflow().getInitialState());
	}
	
	/**
	 * Returns a workflow state for a given name.
	 */
	protected State getState(String id) {
		return ((WorkflowImpl) getWorkflow()).getState(id);
	}

}
