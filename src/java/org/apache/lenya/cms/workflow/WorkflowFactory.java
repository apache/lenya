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

/* $Id: WorkflowFactory.java,v 1.30 2004/03/01 16:18:16 gregor Exp $  */

package org.apache.lenya.cms.workflow;

import java.io.File;

import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.LanguageVersions;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.SynchronizedWorkflowInstances;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.lenya.workflow.impl.History;
import org.apache.lenya.workflow.impl.WorkflowBuilder;

/**
 * Workflow factory.
 */
public class WorkflowFactory {
    public static final String WORKFLOW_DIRECTORY =
        "config/workflow".replace('/', File.separatorChar);

    /** Creates a new instance of WorkflowFactory */
    protected WorkflowFactory() {
    }

    /**
     * Returns an instance of the workflow factory.
     * @return A workflow factory.
     */
    public static WorkflowFactory newInstance() {
        return new WorkflowFactory();
    }

    /**
     * Creates a new workflow instance.
     * @param document The document to create the instance for.
     * @return A workflow instance.
     * @throws WorkflowException when something went wrong.
     */
    public WorkflowInstance buildInstance(Document document) throws WorkflowException {
        assert document != null;
        return getHistory(document).getInstance();
    }

    /**
     * Creates a new synchronized workflow instances object..
     * @param document The document to create the instances for.
     * @return A synchronized workflow instances object.
     * @throws WorkflowException when something went wrong.
     */
    public SynchronizedWorkflowInstances buildSynchronizedInstance(Document document) throws WorkflowException {
        assert document != null;
        LanguageVersions versions;
        try {
            versions = new LanguageVersions(document);
        } catch (DocumentException e) {
            throw new WorkflowException(e);
        }
        return new WorkflowDocumentSet(versions, document);
    }

    /**
     * Moves the history of a document.
     * @param oldDocument The document to move the instance for.
     * @param newDocument The new document.
     * @throws WorkflowException when something went wrong.
     */
    public static void moveHistory(Document oldDocument, Document newDocument) throws WorkflowException {
        assert oldDocument != null;
        new CMSHistory(oldDocument).move(newDocument);
    }

    /**
     * Deletes the history of a document.
     * @param document The document to delete the instance for.
     * @throws WorkflowException when something went wrong.
     */
    public static void deleteHistory(Document document) throws WorkflowException {
        assert document != null;
        getHistory(document).delete();
    }

    /**
     * Checks if a workflow is assigned to the document.
     * This is done by looking for the workflow history file.
     * @param document The document to check.
     * @return <code>true</code> if the document has a workflow, <code>false</code> otherwise.
     */
    public boolean hasWorkflow(Document document) {
        return getHistory(document).isInitialized();
    }

    /**
     * Builds a workflow for a given publication.
     * @param publication The publication.
     * @param workflowFileName The workflow definition filename.
     * @return A workflow object.
     * @throws WorkflowException when something went wrong.
     */
    protected static Workflow buildWorkflow(Publication publication, String workflowFileName)
        throws WorkflowException {
        assert publication != null;
        assert(workflowFileName != null) && !"".equals(workflowFileName);

        File workflowDirectory = new File(publication.getDirectory(), WORKFLOW_DIRECTORY);
        File workflowFile = new File(workflowDirectory, workflowFileName);
        Workflow workflow = WorkflowBuilder.buildWorkflow(workflowFile);

        return workflow;
    }

    /**
     * Creates a situation for a set of roles and an identity.
     * @param roles The roles.
     * @param identity The identity.
     * @return A workflow situation.
     * @throws WorkflowException when something went wrong.
     */
    public Situation buildSituation(Role[] roles, Identity identity) throws WorkflowException {
        if (identity == null) {
            throw new WorkflowException("Session does not contain identity!");
        }
        String userId = null;
        User user = identity.getUser();
        if (user != null) {
            userId = user.getId();
        }
        
        String machineIp = null;
        Machine machine = identity.getMachine();
        if (machine != null) {
            machineIp = machine.getIp();
        }
        
        String[] roleIds = new String[roles.length];
        for (int i = 0; i < roles.length; i++) {
            roleIds[i] = roles[i].getId();
        }
        
        return buildSituation(roleIds, userId, machineIp);
    }

    /**
     * Builds a situation from a role name set, a user ID and a machine IP address.
     * @param roleIds The role IDs.
     * @param userId The user ID.
     * @param machineIp The machine IP address.
     * @return A situation.
     */
    public Situation buildSituation(String[] roleIds, String userId, String machineIp) {
        return new CMSSituation(roleIds, userId, machineIp);
    }

    /**
     * Initializes the history of a document.
     * @param document The document object.
     * @param workflowId The ID of the workflow.
     * @param situation The current situation.
     * @throws WorkflowException When something goes wrong.
     */
    public static void initHistory(Document document, String workflowId, Situation situation) throws WorkflowException {
        new CMSHistory(document).initialize(workflowId, situation);
    }
    
    /**
     * Returns the workflow history of a document.
     * @param document A document.
     * @return A workflow history.
     */
    public static History getHistory(Document document) {
        return new CMSHistory(document);
    }

    /**
     * Initializes the workflow history of a document that is a copy of
     * another document.
     * @param sourceDocument The original document.
     * @param destinationDocument The document to initialize the history for.
     * @throws WorkflowException When something goes wrong.
     */
    public static void initHistory(Document sourceDocument, Document destinationDocument, Situation situation)
        throws WorkflowException {
        CMSHistory history = new CMSHistory(sourceDocument);
        history.initialize(destinationDocument, situation);
    }

}
