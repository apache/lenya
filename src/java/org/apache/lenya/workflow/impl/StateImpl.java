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

import org.apache.lenya.workflow.State;


/**
 * Implementation of a state.
 */
public class StateImpl implements State {
    
    /**
     * Creates a new instance of StateImpl
     * @param stateId The state ID.
     */
    protected StateImpl(String stateId) {
        id = stateId;
    }

    private String id;

    /**
     * Returns the state ID.
     * @return A string.
     */
    public String getId() {
        return id;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getId();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object) {
        boolean result = false;

        if (object instanceof StateImpl) {
            result = getId().equals(((StateImpl) object).getId());
        } else {
            result = super.equals(object);
        }

        return result;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getId().hashCode();
    }
}
