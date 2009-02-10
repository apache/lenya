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
package org.apache.lenya.cms.workflow;

import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Session;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowManager;
import org.apache.lenya.workflow.Workflowable;

/**
 * Utility class for workflow tasks.
 * 
 * @version $Id:$
 */
public class WorkflowUtil {

    /**
     * Invokes a workflow event on a document. This is the same as
     * <code>invoke(Document, String, true)</code>.
     * @param document The document.
     * @param event The name of the event.
     * @throws WorkflowException if the event could not be invoked in the current situation.
     */
    public static void invoke(Document document, String event) throws WorkflowException {
        WorkflowManager wfManager = getWorkflowManager();
        Workflowable workflowable = getWorkflowable(document);
        wfManager.invoke(workflowable, event);
    }

    /**
     * Invokes a workflow event on a document.
     * @param document The document.
     * @param event The name of the event.
     * @param force If this is set to <code>true</code>, the execution is forced, which means an
     *            exception is thrown if the workflowable in the set does not support the event. If
     *            set to <code>false</code>, non-supporting documents are ignored.
     * @throws WorkflowException if the event could not be invoked in the current situation.
     */
    public static void invoke(Document document, String event, boolean force)
            throws WorkflowException {
        WorkflowManager wfManager = getWorkflowManager();
        Workflowable workflowable = getWorkflowable(document);
        wfManager.invoke(workflowable, event, force);
    }

    /**
     * Invokes a workflow event on a document set.
     * @param documentSet The document.
     * @param event The event.
     * @param force If this is set to <code>true</code>, the execution is forced, which means an
     *            exception is thrown if a document in the set does not support the event. If set to
     *            <code>false</code>, non-supporting documents are ignored.
     * @throws WorkflowException if <code>force</code> is set to <code>true</code> and a document
     *             does not support the workflow event.
     */
    public static void invoke(DocumentSet documentSet, String event, boolean force)
            throws WorkflowException {
        WorkflowManager wfManager = getWorkflowManager();
        Document[] documents = documentSet.getDocuments();
        for (int i = 0; i < documents.length; i++) {
            Workflowable workflowable = new DocumentWorkflowable(documents[i]);
            wfManager.invoke(workflowable, event, force);
        }
    }

    protected static WorkflowManager getWorkflowManager() {
        return (WorkflowManager) WebAppContextUtils.getCurrentWebApplicationContext().getBean(
                WorkflowManager.ROLE);
    }

    /**
     * Checks if an event can be invoked on a document.
     * @param document The document.
     * @param event The event.
     * @return A boolean value.
     * @throws WorkflowException
     */
    public static boolean canInvoke(Document document, String event)
            throws WorkflowException {
        Workflowable workflowable = new DocumentWorkflowable(document);
        return getWorkflowManager().canInvoke(workflowable, event);
    }

    /**
     * Checks if an event can be invoked on all documents in a set.
     * @param session The repository session.
     * @param documents The documents.
     * @param event The event.
     * @return if an error occurs.
     * @throws WorkflowException
     */
    public static boolean canInvoke(Session session, DocumentSet documents, String event)
            throws WorkflowException {
        WorkflowManager wfManager = getWorkflowManager();

        boolean canInvoke = true;
        Document[] documentArray = documents.getDocuments();
        for (int i = 0; i < documentArray.length; i++) {
            Workflowable workflowable = new DocumentWorkflowable(documentArray[i]);
            canInvoke = canInvoke && wfManager.canInvoke(workflowable, event);
        }
        return canInvoke;
    }

    /**
     * Returns if a document has a workflow.
     * @param logger The logger.
     * @param document The document.
     * @return A boolean value.
     * @throws WorkflowException if an error occurs.
     */
    public static boolean hasWorkflow(Document document) throws WorkflowException {
        Workflowable workflowable = new DocumentWorkflowable(document);
        return getWorkflowManager().hasWorkflow(workflowable);
    }

    /**
     * Returns the workflow schema of a document.
     * @param logger The logger.
     * @param document The document.
     * @return A workflow schema.
     * @throws WorkflowException if an error occurs.
     */
    public static Workflow getWorkflowSchema(Document document)
            throws WorkflowException {
        WorkflowManager wfManager = getWorkflowManager();
        Workflowable workflowable = getWorkflowable(document);
        if (wfManager.hasWorkflow(workflowable)) {
            return wfManager.getWorkflowSchema(workflowable);
        } else {
            throw new WorkflowException("The document [" + document + "] has no workflow!");
        }
    }

    /**
     * Returns a workflowable for a document.
     * @param logger The logger.
     * @param document The document.
     * @return A workflowable.
     */
    public static Workflowable getWorkflowable(Document document) {
        return new DocumentWorkflowable(document);
    }

}