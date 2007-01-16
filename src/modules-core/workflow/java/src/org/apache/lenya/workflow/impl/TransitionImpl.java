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

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.lenya.workflow.Action;
import org.apache.lenya.workflow.Condition;
import org.apache.lenya.workflow.Transition;

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
    protected TransitionImpl(String sourceState, String destinationState) {
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
     * @see org.apache.lenya.workflow.Transition#getConditions()
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

    private String event;

    /**
     * Returns the event which invokes this transition.
     * @return An event.
     */
    public String getEvent() {
        return this.event;
    }

    /**
     * Sets the event to invoke this transition.
     * @param anEvent An event.
     */
    public void setEvent(String anEvent) {
        this.event = anEvent;
    }

    private String source;

    /**
     * @see org.apache.lenya.workflow.Transition#getSource()
     */
    public String getSource() {
        return this.source;
    }

    private String destination;

    /**
     * @see org.apache.lenya.workflow.Transition#getDestination()
     */
    public String getDestination() {
        return this.destination;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getEvent() + " [");
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