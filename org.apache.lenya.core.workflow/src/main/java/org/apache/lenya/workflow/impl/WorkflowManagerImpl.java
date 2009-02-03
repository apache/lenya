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
package org.apache.lenya.workflow.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
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
        Serviceable, Poolable {

    private Map uri2workflow = new HashMap();

    /**
     * @see org.apache.lenya.workflow.WorkflowManager#invoke(org.apache.lenya.workflow.Workflowable,
     *      java.lang.String, boolean)
     */
    public void invoke(Workflowable workflowable, String event, boolean force)
            throws WorkflowException {
        if (hasWorkflow(workflowable)) {
            WorkflowEngine engine = new WorkflowEngineImpl();
            Workflow workflow = getWorkflowSchema(workflowable);

            if (force && !engine.canInvoke(workflowable, workflow, event)) {
                throw new WorkflowException("The event [" + event
                        + "] cannot be invoked on the document [" + workflowable + "]");
            }
            engine.invoke(workflowable, workflow, event);
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
                canInvoke = engine.canInvoke(workflowable, workflow, event);
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
            getLogger().debug("Workflow URI: " + uri);
            if (uri != null) {
                workflow = (WorkflowImpl) this.uri2workflow.get(uri);
                if (workflow == null) {
                    Document document = SourceUtil.readDOM(uri, this.manager);
                    if (document == null) {
                        throw new WorkflowException("Could not read workflow schema from URI ["
                                + uri + "]!");
                    }
                    WorkflowBuilder builder = new WorkflowBuilder(getLogger());
                    workflow = builder.buildWorkflow(uri, document);
                    this.uri2workflow.put(uri, workflow);
                }
            }
        } catch (final WorkflowException e) {
            throw e;
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

}
