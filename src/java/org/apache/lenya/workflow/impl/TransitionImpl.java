/*
 * TransitionImpl.java
 *
 * Created on 8. April 2003, 17:49
 */

package org.apache.lenya.workflow.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.lenya.workflow.Action;
import org.apache.lenya.workflow.Condition;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Transition;

/**
 *
 * @author  andreas
 */
public class TransitionImpl implements Transition {

    /** Creates a new instance of TransitionImpl */
    protected TransitionImpl(StateImpl sourceState, StateImpl destinationState) {

        assert sourceState != null;
        assert destinationState != null;

        source = sourceState;
        destination = destinationState;
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
        return (Condition[]) conditions.toArray(
            new Condition[conditions.size()]);
    }

    public void addCondition(Condition condition) {
        assert condition != null;
        conditions.add(condition);
    }

    private Event event;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event anEvent) {
		assert anEvent != null;
        event = anEvent;
    }

    private StateImpl source;

    public StateImpl getSource() {
        return source;
    }

    private StateImpl destination;

    public StateImpl getDestination() {
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        
        String string = getEvent().getName() + " [";
        Condition conditions[] = getConditions();
        for (int i = 0; i < conditions.length; i++) {
            if (i > 0) string += ", ";
            string += conditions[i].toString();
        }
        
        string += "]";
        
        Action actions[] = getActions();
        if (actions.length > 0) {
            string += " / ";
            for (int i = 0; i < actions.length; i++) {
                if (i > 0) string += ", ";
                string += actions[i].toString();
            }
        }
        
        return string;
    }

}
