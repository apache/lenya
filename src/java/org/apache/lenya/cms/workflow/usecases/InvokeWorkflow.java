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
package org.apache.lenya.cms.workflow.usecases;

import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 * Invoke a workflow event on the current document. The event is obtained from
 * the <code>lenya.event</code> request parameter.
 * 
 * @version $Id:$
 */
public class InvokeWorkflow extends DocumentUsecase {

    /**
     * The name of the event request parameter.
     */
    public static final String EVENT = "lenya.event";

    /**
     * @return The workflow event to use.
     */
    protected String getEvent() {
        return getParameterAsString(EVENT);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        
        if (!getErrorMessages().isEmpty()) {
            return;
        }
        
        String eventName = getParameterAsString(EVENT);
        WorkflowInstance instance = getWorkflowInstance(getSourceDocument());
        if (!instance.canInvoke(getSituation(), eventName)) {
            addErrorMessage("The event [" + eventName + "] is not executable on document ["
                    + getSourceDocument() + "].");
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        String eventName = getParameterAsString(EVENT);
        triggerWorkflow(eventName);
    }
}