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

package org.apache.lenya.cms.publication.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.TreeSiteManager;
import org.apache.lenya.cms.task.AbstractTask;
import org.apache.lenya.cms.task.ExecutionException;
import org.apache.lenya.cms.task.Task;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowEngine;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.impl.WorkflowEngineImpl;
import org.apache.log4j.Logger;

/**
 * Abstract super class for publication-based tasks.
 */
public abstract class PublicationTask extends AbstractTask {

    private static final Logger log = Logger.getLogger(PublicationTask.class);
    private DocumentIdentityMap map;

    /**
     * Returns the publication used by this task.
     * @return A publication.
     * @throws ExecutionException when an error occurs.
     */
    protected Publication getPublication() throws ExecutionException {
        return getIdentityMap().getPublication();
    }

    /**
     * Returns the document identity map used by this task.
     * @return An identity map.
     * @throws ExecutionException when an error occurs.
     */
    protected DocumentIdentityMap getIdentityMap() throws ExecutionException {
        if (this.map == null) {
            try {
                String publicationId = getParameters().getParameter(Task.PARAMETER_PUBLICATION_ID);
                String servletContextPath = getParameters()
                        .getParameter(Task.PARAMETER_SERVLET_CONTEXT);
                PublicationFactory factory = PublicationFactory.getInstance(new ConsoleLogger());
                Publication publication = factory.getPublication(publicationId, servletContextPath);
                this.map = new DocumentIdentityMap(publication);
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
        }
        return this.map;
    }

    /**
     * Copies the resources of a document to another document.
     * @param sourceDocument The source document.
     * @param destinationDocument The destination document.
     * @throws ExecutionException when something went wrong.
     */
    protected void copyResources(Document sourceDocument, Document destinationDocument)
            throws ExecutionException {

        if (log.isDebugEnabled()) {
            log.debug("Copying resources");
        }

        ResourcesManager sourceManager = sourceDocument.getResourcesManager();
        ResourcesManager destinationManager = destinationDocument.getResourcesManager();

        List resourcesList = new ArrayList(Arrays.asList(sourceManager.getResources()));
        resourcesList.addAll(Arrays.asList(sourceManager.getMetaFiles()));
        File[] resources = (File[]) resourcesList.toArray(new File[resourcesList.size()]);
        File destinationDirectory = destinationManager.getPath();

        for (int i = 0; i < resources.length; i++) {
            File destinationResource = new File(destinationDirectory, resources[i].getName());

            if (log.isDebugEnabled()) {
                log.debug("Copy file [" + resources[i].getAbsolutePath() + "] to ["
                        + destinationResource.getAbsolutePath() + "]");
            }
            try {
                FileUtil.copyFile(resources[i], destinationResource);
            } catch (IOException e) {
                throw new ExecutionException(e);
            }
        }
    }

    /**
     * <code>PARAMETER_WORKFLOW_EVENT</code> The workflow event parameter
     */
    public static final String PARAMETER_WORKFLOW_EVENT = "workflow-event";
    /**
     * <code>PARAMETER_USER_ID</code> The user id parameter
     */
    public static final String PARAMETER_USER_ID = "user-id";
    /**
     * <code>PARAMETER_IP_ADDRESS</code> The IP address parameter
     */
    public static final String PARAMETER_IP_ADDRESS = "ip-address";
    /**
     * <code>PARAMETER_ROLE_IDS</code> The role ids parameter
     */
    public static final String PARAMETER_ROLE_IDS = "role-ids";
    /**
     * <code>ROLE_SEPARATOR_REGEXP</code> The role separator regular expression
     */
    public static final String ROLE_SEPARATOR_REGEXP = ",";

    /**
     * Checks if the workflow event can be invoked on a document.
     * @param document The document.
     * @return A boolean value.
     * @throws ExecutionException when something went wrong.
     */
    protected boolean canWorkflowFire(Document document) throws ExecutionException {

        if (log.isDebugEnabled()) {
            log.debug("Checking workflow of document [" + document + "].");
        }

        boolean canFire = true;

        WorkflowFactory factory = WorkflowFactory.newInstance();
        if (factory.hasWorkflow(document)) {
            try {
                Situation situation = getSituation();
                Workflow workflow = factory.getWorkflow(document);
                WorkflowEngine engine = new WorkflowEngineImpl();
                canFire = engine.canInvoke(document, workflow, situation, getEventName());
            } catch (final ParameterException e) {
                throw new ExecutionException(e);
            } catch (final WorkflowException e) {
                throw new ExecutionException(e);
            }

        }
        return canFire;
    }

    /**
     * Returns the workflow situation.
     * @return A situation.
     * @throws ParameterException when something went wrong.
     */
    protected Situation getSituation() throws ParameterException {
        WorkflowFactory workflowFactory = WorkflowFactory.newInstance();
        String userId = getParameters().getParameter(PARAMETER_USER_ID);
        String machineIp = getParameters().getParameter(PARAMETER_IP_ADDRESS);
        Situation situation = workflowFactory.buildSituation(getRoleIDs(), userId, machineIp);
        return situation;
    }

    /**
     * Invokes the workflow on a document.
     * @param document The document.
     * @throws ExecutionException when something went wrong.
     */
    protected void triggerWorkflow(Document document) throws ExecutionException {

        if (log.isDebugEnabled()) {
            log.debug("Trying to execute workflow on document [" + document.getId() + "].");
        }

        WorkflowFactory factory = WorkflowFactory.newInstance();
        if (factory.hasWorkflow(document)) {
            
            try {
                String userId = getParameters().getParameter(PARAMETER_USER_ID);
                String machineIp = getParameters().getParameter(PARAMETER_IP_ADDRESS);
                
                Workflow workflow = factory.getWorkflow(document);
                Situation situation = factory.buildSituation(getRoleIDs(), userId, machineIp);
                WorkflowEngine engine = new WorkflowEngineImpl();
                engine.invoke(document, workflow, situation, getEventName());
            } catch (final ParameterException e) {
                throw new ExecutionException(e);
            } catch (final WorkflowException e) {
                throw new ExecutionException(e);
            }

        } else {
            if (log.isDebugEnabled()) {
                log.debug("No workflow associated with document.");
            }
        }

    }

    /**
     * Returns the workflow event name.
     * @return A string.
     * @throws ParameterException when the parameter does not exist.
     */
    protected String getEventName() throws ParameterException {
        return getParameters().getParameter(PARAMETER_WORKFLOW_EVENT);
    }

    /**
     * Returns the role IDs.
     * @return An array of strings.
     * @throws ParameterException when the parameter does not exist.
     */
    protected String[] getRoleIDs() throws ParameterException {
        String rolesString = getParameters().getParameter(PARAMETER_ROLE_IDS);
        String[] roles = rolesString.split(ROLE_SEPARATOR_REGEXP);
        return roles;
    }

    protected SiteTree getSiteTree(String area) {
        SiteTree tree;
        try {
            SiteManager manager = getPublication().getSiteManager();
            if (!(manager instanceof TreeSiteManager)) {
                throw new RuntimeException("Only supported for site trees.");
            }
            tree = ((TreeSiteManager) manager).getTree(getIdentityMap(), area);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tree;
    }

}