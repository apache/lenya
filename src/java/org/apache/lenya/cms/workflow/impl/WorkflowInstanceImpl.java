/*
 * WorkflowInstanceImpl.java
 *
 * Created on 9. April 2003, 13:30
 */

package org.lenya.cms.workflow.impl;

import java.util.HashSet;
import java.util.Set;
import org.lenya.cms.ac.User;
import org.lenya.cms.publication.Document;
import org.lenya.cms.workflow.Event;
import org.lenya.cms.workflow.Situation;
import org.lenya.cms.workflow.State;
import org.lenya.cms.workflow.Transition;
import org.lenya.cms.workflow.Workflow;
import org.lenya.cms.workflow.WorkflowInstance;

/**
 *
 * @author  andreas
 */
public class WorkflowInstanceImpl
    implements WorkflowInstance {
    
    /** Creates a new instance of WorkflowInstanceImpl */
    public WorkflowInstanceImpl(Document document) {
        assert document != null;
        this.document = document;
        setCurrentState(getWorkflow().getInitialState());
    }
    
    private Document document;
    
    /** Returns the document of this WorkflowInstance.
     *
     */
    public Document getDocument() {
        return document;
    }    
    
    public Workflow getWorkflow() {
        return getDocument().getType().getWorkflow();
    }
    
    /** Returns the transitions that can fire for this user.
     *
     */
    public Transition[] getExecutableTransitions(User user) {
        Situation situation = new SituationImpl(getDocument(), user);
        Transition transitions[] = getWorkflow().getLeavingTransitions(getCurrentState());
        Set executableTransitions = new HashSet();
        
        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].canFire(situation)) {
                executableTransitions.add(transitions[i]);
            }
        }
        return (Transition[])
            executableTransitions.toArray(new Transition[executableTransitions.size()]);
    }    
    
    /** Indicates that the user invoked an event.
     * @param user The user who invoked the event.
     * @param event The event that was invoked.
     *
     */
    public void invoke(User user, Event event) {
        Transition transitions[] = getExecutableTransitions(user);
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
    
    public void setCurrentState(State state) {
        assert state != null;
        this.currentState = state;
    }
    
    /** Returns the current state of this WorkflowInstance.
     *
     */
    public State getCurrentState() {
        return currentState;
    }
    
}
