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
package org.apache.lenya.workflow.impl;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowEngine;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowManager;
import org.apache.lenya.workflow.Workflowable;
import org.w3c.dom.Document;

/**
 * Workflow manager implementation.
 * 
 * @version $Id: WorkflowManagerImpl.java 179751 2005-06-03 09:13:35Z andreas $
 */
public class WorkflowManagerImpl extends AbstractLogEnabled implements WorkflowManager,
        Serviceable {

    /**
     * @see org.apache.lenya.workflow.WorkflowManager#invoke(org.apache.lenya.workflow.Workflowable,
     *      java.lang.String, boolean)
     */
    public void invoke(Workflowable workflowable, String event, boolean force)
            throws WorkflowException {
        if (hasWorkflow(workflowable)) {
            WorkflowEngine engine = new WorkflowEngineImpl();
            Situation situation = getSituation();
            Workflow workflow = getWorkflowSchema(workflowable);

            if (force && !engine.canInvoke(workflowable, workflow, situation, event)) {
                throw new WorkflowException("The event [" + event
                        + "] cannot be invoked on the document [" + workflowable
                        + "] in the situation [" + situation + "]");
            }
            engine.invoke(workflowable, workflow, situation, event);
        }
    }

    /**
     * @see org.apache.lenya.workflow.WorkflowManager#invoke(org.apache.lenya.workflow.Workflowable,
     *      java.lang.String)
     */
    public void invoke(Workflowable workflowable, String event) throws WorkflowException {
        invoke(workflowable, event, true);
    }

    /**
     * @see org.apache.lenya.workflow.WorkflowManager#canInvoke(org.apache.lenya.workflow.Workflowable,
     *      java.lang.String)
     */
    public boolean canInvoke(Workflowable workflowable, String event) {
        boolean canInvoke = true;
        try {
            if (hasWorkflow(workflowable)) {
                Workflow workflow = getWorkflowSchema(workflowable);
                WorkflowEngine engine = new WorkflowEngineImpl();
                Situation situation = getSituation();
                canInvoke = engine.canInvoke(workflowable, workflow, situation, event);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return canInvoke;
    }

    protected ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /**
     * @see org.apache.lenya.workflow.WorkflowManager#getWorkflowSchema(org.apache.lenya.workflow.Workflowable)
     */
    public Workflow getWorkflowSchema(Workflowable workflowable) throws WorkflowException {
        WorkflowImpl workflow = null;

        try {
            String uri = workflowable.getWorkflowSchemaURI();
            if (uri != null) {
                Document document = SourceUtil.readDOM(uri, this.manager);
                WorkflowBuilder builder = new WorkflowBuilder(getLogger());
                workflow = builder.buildWorkflow(uri, document);
            }
        } catch (final Exception e) {
            throw new WorkflowException(e);
        }

        return workflow;
    }

    /**
     * @see org.apache.lenya.workflow.WorkflowManager#hasWorkflow(org.apache.lenya.workflow.Workflowable)
     */
    public boolean hasWorkflow(Workflowable workflowable) {
        return workflowable.getWorkflowSchemaURI() != null;
    }

    /**
     * @see org.apache.lenya.workflow.WorkflowManager#getSituation()
     */
    public Situation getSituation() {
        return new SituationImpl();
    }

}