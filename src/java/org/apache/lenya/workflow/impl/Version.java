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

import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.State;

/**
 * A version of the workflow history.
 */
public class Version {

    private Event event;
    private State state;

    /**
     * Returns the event.
     * @return An event.
     */
    public Event getEvent() {
        return this.event;
    }

    /**
     * Returns the state.
     * @return A state.
     */
    public State getState() {
        return this.state;
    }

    /**
     * Ctor.
     * @param _event The event that caused the version change.
     * @param _state The destination state.
     */
    public Version(Event _event, State _state) {
        this.event = _event;
        this.state = _state;
    }


}
