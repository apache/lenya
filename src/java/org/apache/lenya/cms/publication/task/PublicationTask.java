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

/* $Id: PublicationTask.java,v 1.7 2004/03/01 16:18:27 gregor Exp $  */

package org.apache.lenya.cms.publication.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.cms.task.AbstractTask;
import org.apache.lenya.cms.task.ExecutionException;
import org.apache.lenya.cms.task.Task;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.SynchronizedWorkflowInstances;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.log4j.Category;

/**
 * Abstract super class for publication-based tasks.
 */
public abstract class PublicationTask extends AbstractTask {

    private static final Category log = Category.getInstance(PublicationTask.class);

    private Publication publication;

    /**
     * Returns the publication used by this task.
     * @return A publication.
     * @throws ExecutionException when an error occurs.
     */
    protected Publication getPublication() throws ExecutionException {
        if (publication == null) {
            try {
                String publicationId = getParameters().getParameter(Task.PARAMETER_PUBLICATION_ID);
                String servletContextPath =
                    getParameters().getParameter(Task.PARAMETER_SERVLET_CONTEXT);
                publication = PublicationFactory.getPublication(publicationId, servletContextPath);
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
        }
        return publication;
    }

    /**
     * Copies the resources of a document to another document.
     * @param sourceDocument The source document.
     * @param destinationDocument The destination document.
     * @throws PublicationException when something went wrong.
     * @throws ExecutionException when something went wrong.
     * @throws IOException when something went wrong.
     */
    protected void copyResources(Document sourceDocument, Document destinationDocument)
        throws ExecutionException {

        if (log.isDebugEnabled()) {
            log.debug("Copying resources");
        }

        ResourcesManager sourceManager = new ResourcesManager(sourceDocument);
        ResourcesManager destinationManager = new ResourcesManager(destinationDocument);

        List resourcesList = new ArrayList(Arrays.asList(sourceManager.getResources()));
        resourcesList.addAll(Arrays.asList(sourceManager.getMetaFiles()));
        File[] resources = (File[]) resourcesList.toArray(new File[resourcesList.size()]);
        File destinationDirectory = destinationManager.getPath();

        for (int i = 0; i < resources.length; i++) {
            File destinationResource = new File(destinationDirectory, resources[i].getName());

            if (log.isDebugEnabled()) {
                log.debug(
                    "Copy file ["
                        + resources[i].getAbsolutePath()
                        + "] to ["
                        + destinationResource.getAbsolutePath()
                        + "]");
            }
            try {
                FileUtil.copyFile(resources[i], destinationResource);
            } catch (IOException e) {
                throw new ExecutionException(e);
            }
        }
    }

    public static final String PARAMETER_WORKFLOW_EVENT = "workflow-event";
    public static final String PARAMETER_USER_ID = "user-id";
    public static final String PARAMETER_IP_ADDRESS = "ip-address";
    public static final String PARAMETER_ROLE_IDS = "role-ids";
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

                SynchronizedWorkflowInstances instance;
                try {
                    instance = factory.buildSynchronizedInstance(document);
                } catch (WorkflowException e) {
                    throw new ExecutionException(e);
                }
                Event event = getExecutableEvent(instance, situation);
                
                if (event == null) {
                    canFire = false;
                }
                
            }
            catch (Exception e) {
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
     * @throws ParameterException when something went wrong.
     * @throws WorkflowException when something went wrong.
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

                SynchronizedWorkflowInstances instance;
                try {
                    instance = factory.buildSynchronizedInstance(document);
                } catch (WorkflowException e) {
                    throw new ExecutionException(e);
                }
                Situation situation = factory.buildSituation(getRoleIDs(), userId, machineIp);

                Event event = getExecutableEvent(instance, situation);

                assert event != null;

                if (log.isDebugEnabled()) {
                    log.debug("Invoking event [" + event.getName() + "]");
                }
                instance.invoke(situation, event);
                if (log.isDebugEnabled()) {
                    log.debug("Invoking transition completed.");
                }
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("No workflow associated with document.");
            }
        }

    }

    /**
     * Returns the executable event for the provided {@link #PARAMETER_WORKFLOW_EVENT} parameter.
     * @param instance The workflow instance.
     * @param situation The situation.
     * @return An event.
     * @throws WorkflowException when something went wrong.
     * @throws ParameterException when the {@link #PARAMETER_WORKFLOW_EVENT} parameter could not be resolved.
     */
    protected Event getExecutableEvent(SynchronizedWorkflowInstances instance, Situation situation)
        throws WorkflowException, ParameterException {

        String workflowEvent = getEventName();

        Event event = null;
        Event[] events = instance.getExecutableEvents(situation);

        if (log.isDebugEnabled()) {
            log.debug("Workflow event name: [" + workflowEvent + "]");
            log.debug("Resolved executable events.");
        }

        for (int i = 0; i < events.length; i++) {
            if (events[i].getName().equals(workflowEvent)) {
                event = events[i];
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Executable event found: [" + event + "]");
        }
        
        if (event == null) {
            log.error("Event [" + workflowEvent + "] cannot be invoked!");
        }

        return event;
    }

    /**
     * Returns the workflow event name.
     * @return A string.
     * @throws ParameterException when the parameter does not exist.
     */
    protected String getEventName() throws ParameterException {
        String workflowEvent = getParameters().getParameter(PARAMETER_WORKFLOW_EVENT);
        return workflowEvent;
    }

    /**
     * Returns the role IDs.
     * @return An array of strings.
     */
    protected String[] getRoleIDs() throws ParameterException {
        String rolesString = getParameters().getParameter(PARAMETER_ROLE_IDS);
        String[] roles = rolesString.split(ROLE_SEPARATOR_REGEXP);
        return roles;
    }

}
