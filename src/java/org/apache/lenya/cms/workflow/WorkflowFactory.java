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

package org.apache.lenya.cms.workflow;

import java.io.File;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.publication.DefaultDocument;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdToPathMapper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.impl.WorkflowBuilder;
import org.apache.lenya.xml.DocumentHelper;

/**
 * Workflow factory.
 * @deprecated Use WorkflowResolver instead.
 */
public class WorkflowFactory {

    /** Creates a new instance of WorkflowFactory */
    protected WorkflowFactory() {
        // do nothing
    }

    /**
     * Returns an instance of the workflow factory.
     * @return A workflow factory.
     */
    public static WorkflowFactory newInstance() {
        return new WorkflowFactory();
    }

    /**
     * @param document The document.
     * @return The workflow of the document.
     * @throws WorkflowException if the history is not initialized.
     */
    public Workflow getWorkflow(Document document) throws WorkflowException {
        File historyFile = ((DefaultDocument) document).getHistoryFile();
        
        if (!historyFile.exists()) {
            throw new WorkflowException("The history of [" + document + "] does not exist!");
        }
        
        try {
            org.w3c.dom.Document xmlDoc = DocumentHelper.readDocument(historyFile);
            String workflowName = xmlDoc.getDocumentElement()
                    .getAttribute(History.WORKFLOW_ATTRIBUTE);
            WorkflowBuilder builder = new WorkflowBuilder(new ConsoleLogger());
            File workflowDirectory = new File(document.getPublication().getDirectory(),
                    WorkflowResolverImpl.WORKFLOW_DIRECTORY);
            File workflowFile = new File(workflowDirectory, workflowName);
            return builder.buildWorkflow(workflowName, workflowFile);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes the history of a document.
     * @param document The document to delete the instance for.
     * @throws WorkflowException when something went wrong.
     */
    public static void deleteHistory(Document document) throws WorkflowException {
        assert document != null;
        ((DefaultDocument) document).getHistoryFile().delete();
    }

    /**
     * Checks if a workflow is assigned to the document. This is done by looking
     * for the workflow history file.
     * @param document The document to check.
     * @return <code>true</code> if the document has a workflow,
     *         <code>false</code> otherwise.
     */
    public boolean hasWorkflow(Document document) {
        return getHistoryFile(document).exists();
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
     * Builds a situation from a role name set, a user ID and a machine IP
     * address.
     * @param roleIds The role IDs.
     * @param userId The user ID.
     * @param machineIp The machine IP address.
     * @return A situation.
     */
    public Situation buildSituation(String[] roleIds, String userId, String machineIp) {
        return new CMSSituation(roleIds, userId, machineIp);
    }

    /**
     * Returns the path of the history file inside the publication directory.
     * @param document The document.
     * @return A string.
     */
    public String getHistoryPath(Document document) {

        DocumentIdToPathMapper pathMapper = document.getPublication().getPathMapper();
        String documentPath = pathMapper.getPath(document.getId(), document.getLanguage());

        String area = document.getArea();
        if (!area.equals(Publication.ARCHIVE_AREA) && !area.equals(Publication.TRASH_AREA)) {
            area = Publication.AUTHORING_AREA;
        }

        String path = CMSHistory.HISTORY_PATH + "/" + area + "/" + documentPath;
        path = path.replace('/', File.separatorChar);
        return path;
    }

    /**
     * Returns the CMS history file of a document.
     * @param document The document.
     * @return A file.
     * @deprecated Use WorkflowResolver instead.
     */
    protected File getHistoryFile(Document document) {
        File historyFile = new File(document.getPublication().getDirectory(),
                getHistoryPath(document));
        return historyFile;
    }
}