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

import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.components.ContextHelper;
import org.apache.lenya.cms.cocoon.workflow.WorkflowHelper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.workflow.WorkflowResolver;
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
            instance.invoke(getSituation(), event);
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
        
        WorkflowInstance instance = null;
        WorkflowResolver resolver = null;
        try {
            resolver = (WorkflowResolver) manager.lookup(WorkflowResolver.ROLE);
            instance = resolver.getWorkflowInstance(document);
        } catch (ServiceException e) {
            throw new WorkflowException(e);
        }
        finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
        return instance;
    }

    /**
     * Returns if a document has a workflow.
     * @param document The document.
     * @return A boolean value.
     * @throws WorkflowException if an error occurs.
     */
    protected boolean hasWorkflow(Document document) throws WorkflowException {
        
        boolean hasWorkflow = false;
        WorkflowResolver resolver = null;
        try {
            resolver = (WorkflowResolver) manager.lookup(WorkflowResolver.ROLE);
            hasWorkflow = resolver.hasWorkflow(document);
        } catch (ServiceException e) {
            throw new WorkflowException(e);
        }
        finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
        return hasWorkflow;
    }

}