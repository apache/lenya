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

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.lenya.workflow.Action;
import org.apache.lenya.workflow.Condition;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Transition;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;

import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of a transition.
 */
public class TransitionImpl extends AbstractLogEnabled implements Transition {
    
    
    /**
     * Ctor.
     * @param sourceState The source state.
     * @param destinationState The destination state.
     */
    protected TransitionImpl(StateImpl sourceState, StateImpl destinationState) {
        this.source = sourceState;
        this.destination = destinationState;
    }

    private List actions = new ArrayList();
    private boolean isSynchronized = false;

    /**
     * Returns the actions which are assigned tothis transition.
     * @return An array of actions.
     */
    public Action[] getActions() {
        return (Action[]) this.actions.toArray(new Action[this.actions.size()]);
    }

    /**
     * Assigns an action to this transition.
     * @param action The action.
     */
    public void addAction(Action action) {
        this.actions.add(action);
    }

    private List conditions = new ArrayList();

    /**
     * Returns the conditions which are assigned to this transition.
     * @return An array of conditions.
     */
    public Condition[] getConditions() {
        return (Condition[]) this.conditions.toArray(new Condition[this.conditions.size()]);
    }

    /**
     * Assigns a condition to this transition.
     * @param condition The condition.
     */
    public void addCondition(Condition condition) {
        this.conditions.add(condition);
    }

    private Event event;

    /**
     * Returns the event which invokes this transition.
     * @return An event.
     */
    public Event getEvent() {
        return this.event;
    }

    /**
     * Sets the event to invoke this transition.
     * @param anEvent An event.
     */
    public void setEvent(Event anEvent) {
        this.event = anEvent;
    }

    private StateImpl source;

    /**
     * Returns the source state of this transition.
     * @return A state.
     */
    public StateImpl getSource() {
        return this.source;
    }

    private StateImpl destination;

    /**
     * Returns the destination state of this transition.
     * @return A state.
     */
    public StateImpl getDestination() {
        return this.destination;
    }

    /** 
     * Returns if the transition can fire in a certain situation.
     * @param situation The situation.
     * @param instance The workflow instance.
     * @throws WorkflowException when an error occurs.
     * @return A boolean value.
     */
    public boolean canFire(Situation situation, WorkflowInstance instance) throws WorkflowException {
        Condition[] _conditions = getConditions();
        boolean canFire = true;

        int i = 0;
        while (canFire && i < _conditions.length) {
            canFire = canFire && _conditions[i].isComplied(situation, instance);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Condition [" + _conditions[i] + "] returns [" + canFire + "]");
            }
            i++;
        }

        return canFire;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getEvent().getName() + " [");
        Condition[] _conditions = getConditions();

        for (int i = 0; i < _conditions.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }

            buf.append(_conditions[i].toString());
        }

        buf.append("]");

        Action[] _actions = getActions();

        if (_actions.length > 0) {
            buf.append(" / ");

            for (int i = 0; i < _actions.length; i++) {
                if (i > 0) {
                    buf.append(", ");
                }

                buf.append(_actions[i].toString());
            }
        }

        return buf.toString();
    }

    /**
     * Returns if this transition is synchronized.
     * @return A boolean value.
     */
    public boolean isSynchronized() {
        return this.isSynchronized;
    }

    /**
     * Sets if this transition is synchronized.
     * @param _isSynchronized A boolean value.
     */
    protected void setSynchronized(boolean _isSynchronized) {
        this.isSynchronized = _isSynchronized;
    }

}
