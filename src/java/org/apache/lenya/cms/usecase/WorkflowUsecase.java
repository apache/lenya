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

package org.apache.lenya.cms.usecase;

import java.util.Map;

import org.apache.cocoon.components.ContextHelper;
import org.apache.lenya.cms.cocoon.workflow.WorkflowHelper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 */
public class WorkflowUsecase extends AbstractUsecase {

    private Situation situation;

    /**
     * Returns the workflow situation.
     * @return A situation.
     */
    protected Situation getSituation() {
        return this.situation;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doInitialize()
     */
    protected void doInitialize() {
        super.doInitialize();
        Map objectModel = ContextHelper.getObjectModel(getContext());
        try {
            this.situation = WorkflowHelper.buildSituation(objectModel);
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Triggers a workflow event on a document.
     * @param event The event.
     * @param document The document.
     */
    protected void triggerWorkflow(String event, Document document) {
        WorkflowFactory factory = WorkflowFactory.newInstance();
        try {
            WorkflowInstance instance = factory.buildInstance(document);
            Event[] events = instance.getExecutableEvents(getSituation());
            Event executableEvent = null;
            for (int i = 0; i < events.length; i++) {
                if (events[i].getName().equals(event)) {
                    executableEvent = events[i];
                }
            }

            if (executableEvent == null) {
                throw new RuntimeException("The event [" + event
                        + "] is not executable on document [" + document + "]");
            }
            instance.invoke(getSituation(), executableEvent);
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

}
