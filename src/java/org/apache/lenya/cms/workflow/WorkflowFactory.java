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

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdToPathMapper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.util.LanguageVersions;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.SynchronizedWorkflowInstances;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.lenya.workflow.impl.HistoryImpl;
import org.apache.lenya.workflow.impl.SynchronizedWorkflowInstancesImpl;
import org.apache.lenya.workflow.impl.WorkflowBuilder;
import org.apache.lenya.workflow.impl.WorkflowImpl;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Element;

/**
 * Workflow factory.
 * @deprecated Use WorkflowResolver instead.
 */
public class WorkflowFactory {

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
    public WorkflowInstance buildExistingInstance(Document document) throws WorkflowException {

        File file = getHistoryFile(document);
        WorkflowDocument workflowDocument = null;

        if (file.exists()) {
            org.w3c.dom.Document xml;
            try {
                xml = DocumentHelper.readDocument(file);
            } catch (Exception e) {
                throw new WorkflowException(e);
            }

            Element documentElement = xml.getDocumentElement();
            String workflowName = documentElement.getAttribute(HistoryImpl.WORKFLOW_ATTRIBUTE);

            workflowDocument = (WorkflowDocument) buildNewInstance(document, workflowName);
        }

        return workflowDocument;
    }

    /**
     * Creates a new workflow instance.
     * @param document The document to create the instance for.
     * @param workflowName The name of the workflow.
     * @return A workflow instance.
     * @throws WorkflowException when something went wrong.
     */
    public WorkflowInstance buildNewInstance(Document document, String workflowName)
            throws WorkflowException {

        File workflowDirectory = new File(document.getPublication().getDirectory(),
                WorkflowResolverImpl.WORKFLOW_DIRECTORY);
        File workflowFile = new File(workflowDirectory, workflowName);

        WorkflowBuilder builder = new WorkflowBuilder(new ConsoleLogger());
        WorkflowImpl workflow = builder.buildWorkflow(workflowName, workflowFile);

        WorkflowDocument workflowDocument = new WorkflowDocument(workflow, document);
        ContainerUtil.enableLogging(workflowDocument, new ConsoleLogger());
        return workflowDocument;
    }

    /**
     * Creates a new synchronized workflow instances object..
     * @param document The document to create the instances for.
     * @return A synchronized workflow instances object.
     * @throws WorkflowException when something went wrong.
     */
    public SynchronizedWorkflowInstances buildSynchronizedInstance(Document document)
            throws WorkflowException {
        assert document != null;
        LanguageVersions versions;
        try {
            versions = new LanguageVersions(document);
        } catch (DocumentException e) {
            throw new WorkflowException(e);
        }

        Document[] documents = versions.getDocuments();
        WorkflowInstance[] instances = new WorkflowInstance[documents.length];
        for (int i = 0; i < documents.length; i++) {
            instances[i] = buildExistingInstance(documents[i]);
        }

        SynchronizedWorkflowInstances set = new SynchronizedWorkflowInstancesImpl(instances,
                buildExistingInstance(document));
        ContainerUtil.enableLogging(set, new ConsoleLogger());
        return set;
    }

    /**
     * Deletes the history of a document.
     * @param document The document to delete the instance for.
     * @throws WorkflowException when something went wrong.
     */
    public static void deleteHistory(Document document) throws WorkflowException {
        assert document != null;
        WorkflowInstance instance = WorkflowFactory.newInstance().buildExistingInstance(document);
        instance.getHistory().delete();
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