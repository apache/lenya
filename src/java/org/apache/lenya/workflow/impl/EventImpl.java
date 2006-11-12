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

import org.apache.lenya.workflow.Event;


/**
 * Implementation of an event.
 */
public class EventImpl implements Event {
    
    /**
     * Creates a new instance of EventImpl
     * @param eventName The event name.
     */
    protected EventImpl(String eventName) {
        name = eventName;
    }

    private String name;

    /**
     * Returns a string expression of this object.
     * @return A string.
     */
    public String toString() {
        return getName();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object otherObject) {
        boolean equals = false;

        if (otherObject instanceof EventImpl) {
            EventImpl otherEvent = (EventImpl) otherObject;
            equals = getName().equals(otherEvent.getName());
        } else {
            equals = super.equals(otherObject);
        }

        return equals;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * @see org.apache.lenya.workflow.Event#getName()
     */
    public String getName() {
        return name;
    }
}
