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
package org.apache.lenya.cms.workflow.usecases;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.Workflowable;

/**
 * Wrap a workflowable for easy evaluation in JX template.
 */
public class WorkflowableWrapper extends AbstractLogEnabled {

    private MultiWorkflow usecase;
    private Workflowable workflowable;
    private ServiceManager manager;
    private Document document;
    private Session session;

    /**
     * Ctor.
     * @param workflow
     * @param manager The service manager.
     * @param session The session.
     * @param document The document to wrap.
     * @param logger The logger.
     */
    public WorkflowableWrapper(MultiWorkflow usecase, ServiceManager manager, Session session,
            Document document, Logger logger) {
        this.usecase = usecase;
        this.document = document;
        this.manager = manager;
        this.session = session;
        ContainerUtil.enableLogging(this, logger);
    }

    protected Workflowable getWorkflowable() {
        if (this.workflowable == null) {
            this.workflowable = WorkflowUtil.getWorkflowable(this.manager, this.session,
                    getLogger(), this.document);
        }
        return this.workflowable;
    }

    /**
     * @return The state of the latest version.
     * @throws WorkflowException
     */
    public String getState() throws WorkflowException {
        String state;
        if (getWorkflowable().getVersions().length > 0) {
            state = getWorkflowable().getLatestVersion().getState();
        } else {
            Workflow workflow = getWorkflowSchema();
            state = workflow.getInitialState();
        }
        return state;
    }

    /**
     * @return All states supported by the workflow schema.
     * @throws WorkflowException
     */
    public String[] getStates() throws WorkflowException {
        return getWorkflowSchema().getStates();
    }

    protected Workflow getWorkflowSchema() throws WorkflowException {
        Workflow workflow = WorkflowUtil.getWorkflowSchema(this.manager, this.session, getLogger(),
                this.document);
        return workflow;
    }

    /**
     * @return The path of the document.
     * @throws DocumentException If the document is not referenced in the site
     *         structure.
     */
    public String getPath() throws DocumentException {
        return this.document.getPath();
    }

    /**
     * @return The language of the document.
     */
    public String getLanguage() {
        return this.document.getLanguage();
    }

    /**
     * @return The web application URL of the document.
     */
    public String getUrl() {
        return this.document.getCanonicalWebappURL();
    }

    /**
     * @return All executable events in alphabetical order.
     * @throws WorkflowException if an error occurs.
     */
    public String[] getUsecases() throws WorkflowException {
        SortedSet usecases = new TreeSet();
        String[] events = getWorkflowSchema().getEvents();
        for (int i = 0; i < events.length; i++) {
            if (WorkflowUtil.canInvoke(this.manager, this.session, getLogger(), this.document,
                    events[i])) {
                String[] eventUsecases = this.usecase.getUsecases(events[i]);
                usecases.addAll(Arrays.asList(eventUsecases));
            }
        }

        return (String[]) usecases.toArray(new String[usecases.size()]);
    }
}
