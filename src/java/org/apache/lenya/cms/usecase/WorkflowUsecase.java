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
        try {
            WorkflowInstance instance = getWorkflowInstance(document);
            Event executableEvent = getExecutableEvent(event, document);

            if (executableEvent == null) {
                throw new RuntimeException("The event [" + event
                        + "] is not executable on document [" + document + "]");
            }
            instance.invoke(getSituation(), executableEvent);
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the workflow instance for a document.
     * @param document The document.
     * @return A workflow instance.
     * @throws WorkflowException if an error occurs.
     */
    protected WorkflowInstance getWorkflowInstance(Document document) throws WorkflowException {
        WorkflowFactory factory = WorkflowFactory.newInstance();
        WorkflowInstance instance = factory.buildInstance(document);
        return instance;
    }

    /**
     * Returns the event object if an event is exectuable.
     * @param document The document.
     * @param event The name of the event.
     * @return An event or <code>null</code> if the event is not executable.
     */
    protected Event getExecutableEvent(String event, Document document) {
        Event[] events;
        try {
            events = getWorkflowInstance(document).getExecutableEvents(getSituation());
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
        Event executableEvent = null;
        for (int i = 0; i < events.length; i++) {
            if (events[i].getName().equals(event)) {
                executableEvent = events[i];
            }
        }
        return executableEvent;
    }

    /**
     * Checks if a certain event can be executed on a workflow instance.
     * @param document The document.
     * @param event The event.
     * @return A boolean value.
     */
    protected boolean canExecuteWorkflow(String event, Document document) {
        return getExecutableEvent(event, document) != null;
    }

}