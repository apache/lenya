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

/* $Id: ActionImpl.java,v 1.9 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.workflow.impl;

import org.apache.lenya.workflow.Action;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;


/**
 * Basic action implementation.
 */
public class ActionImpl implements Action {
    
    /**
     * Creates a new instance of ActionImpl.
     * @param actionId The action ID.
     */
    protected ActionImpl(String actionId) {
        id = actionId;
    }

    private String id;

    /**
     * Returns the action ID.
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
     * @see org.apache.lenya.workflow.Action#execute(org.apache.lenya.workflow.WorkflowInstance)
     */
    public void execute(WorkflowInstance instance) throws WorkflowException {
    }
}
