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
package org.apache.lenya.cms.usecase;

import org.apache.lenya.util.Assert;

/**
 * A simple state machine.
 */
public class StateMachine {

    String currentState;
    private Model model;

    /**
     * @param model The model to use.
     */
    public StateMachine(Model model) {
        this.model = model;
        this.currentState = model.getInitialState();
    }

    void invoke(String event) {
        Transition transition = getTransition(event);
        checkTransition(event, transition);
        this.currentState = transition.destination;
    }

    protected Transition getTransition(String event) {
        Assert.notNull("event", event);
        Transition[] transitions = this.model.getTransitions();
        Transition transition = null;
        for (int i = 0; i < transitions.length; i++) {
            Transition t = transitions[i];
            if (canFire(t, event)) {
                if (transition != null) {
                    throw new IllegalStateException("More than 1 transition for event [" + event
                            + "] in state [" + this.currentState + "]!");
                }
                transition = t;
            }
        }
        return transition;
    }

    protected boolean canFire(Transition t, String event) {
        return t.getSource().equals(this.currentState) && t.getEvent().equals(event);
    }
    
    void checkEvent(String event) {
        Transition transition = getTransition(event);
        checkTransition(event, transition);
    }

    protected void checkTransition(String event, Transition transition) {
        if (transition == null) {
            throw new IllegalStateException("No transition found for event [" + event
                    + "] in state [" + this.currentState + "]!");
        }
    }

    /**
     * A state machine model.
     */
    public static class Model {

        private String initialState;

        /**
         * @param initialState The initial state.
         * @param transitions The transitions.
         */
        public Model(String initialState, Transition[] transitions) {
            this.transitions = transitions;
            this.initialState = initialState;
        }

        /**
         * @return The initial state.
         */
        public String getInitialState() {
            return this.initialState;
        }

        private Transition[] transitions;

        /**
         * @return The transitions.
         */
        public Transition[] getTransitions() {
            return this.transitions;
        }
    }

    /**
     * A transition switches from a source state to a destination state if an
     * event is invoked.
     */
    public static class Transition {

        /**
         * @param source The source state.
         * @param destination The destination state.
         * @param event The event.
         */
        public Transition(String source, String destination, String event) {
            this.source = source;
            this.destination = destination;
            this.event = event;
        }

        private String source;
        private String destination;
        private String event;

        /**
         * @return The destination state.
         */
        public String getDestination() {
            return destination;
        }

        /**
         * @return The event.
         */
        public String getEvent() {
            return event;
        }

        /**
         * @return The source state.
         */
        public String getSource() {
            return source;
        }
    }

}
