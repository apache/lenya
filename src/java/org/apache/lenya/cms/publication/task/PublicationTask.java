/*
$Id: PublicationTask.java,v 1.4 2003/12/02 14:34:46 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
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
 * 
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
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

        String workflowEvent = getParameters().getParameter(PARAMETER_WORKFLOW_EVENT);

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

        return event;
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
