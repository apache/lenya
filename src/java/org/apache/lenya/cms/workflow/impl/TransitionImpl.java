/*
 * TransitionImpl.java
 *
 * Created on 8. April 2003, 17:49
 */

package org.lenya.cms.workflow.impl;

import java.util.ArrayList;
import java.util.List;
import org.lenya.cms.workflow.Action;
import org.lenya.cms.workflow.Condition;
import org.lenya.cms.workflow.Event;
import org.lenya.cms.workflow.Situation;
import org.lenya.cms.workflow.State;
import org.lenya.cms.workflow.Transition;

/**
 *
 * @author  andreas
 */
public class TransitionImpl
    implements Transition {
    
    /** Creates a new instance of TransitionImpl */
    public TransitionImpl(State source, State destination) {
        
        assert source != null;
        assert destination != null;
        
        this.source = source;
        this.destination = destination;
    }
    
    private List actions = new ArrayList();
    
    public Action[] getActions() {
        return (Action[]) actions.toArray(new Action[actions.size()]);
    }
    
    public void addAction(Action action) {
        assert action != null;
        actions.add(action);
    }
    
    private List conditions = new ArrayList();
    
    public Condition[] getConditions() {
        return (Condition[]) conditions.toArray(new Condition[conditions.size()]);
    }
    
    public void addCondition(Condition condition) {
        assert condition != null;
        conditions.add(condition);
    }
    
    private Event event;
    
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
        assert event != null;
    }
    
    private State source;
    
    public State getSource() {
        return source;
    }
    
    private State destination;
    
    public State getDestination() {
        return destination;
    }
    
    /** Returns if the transition can fire in a certain situation.
     *
     */
    public boolean canFire(Situation situation) {
        Condition conditions[] = getConditions();
        boolean canFire = true;
        for (int i = 0; i < conditions.length; i++) {
            if (!conditions[i].isComplied(situation)) {
                canFire = false;
            }
        }
        return canFire;
    }
    
}
