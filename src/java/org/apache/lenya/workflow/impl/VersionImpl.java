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

import java.util.HashMap;
import java.util.Map;

import org.apache.lenya.workflow.Version;

/**
 * A version of the workflow history.
 */
public class VersionImpl implements Version {

    private String event;
    private String state;
    private Map variableValues = new HashMap();
    
    /**
     * @see org.apache.lenya.workflow.Version#getEvent()
     */
    public String getEvent() {
        return this.event;
    }

    /**
     * @see org.apache.lenya.workflow.Version#getState()
     */
    public String getState() {
        return this.state;
    }

    /**
     * Ctor.
     * @param _event The event that caused the version change.
     * @param _state The destination state.
     */
    public VersionImpl(String _event, String _state) {
        this.event = _event;
        this.state = _state;
    }

    /**
     * @see org.apache.lenya.workflow.Version#getValue(java.lang.String)
     */
    public boolean getValue(String variableName) {
        Boolean value = (Boolean) this.variableValues.get(variableName);
        return value.booleanValue();
    }

    /**
     * @see org.apache.lenya.workflow.Version#setValue(java.lang.String, boolean)
     */
    public void setValue(String variableName, boolean value) {
        this.variableValues.put(variableName, Boolean.valueOf(value));
    }

}
