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
package org.apache.lenya.cms.workflow;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.workflow.WorkflowException;

/**
 * Manager for workflow issues. This is the main entry point for
 * workflow-related tasks. You can safely invoke all methods for non-workflow
 * documents.
 * 
 * @version $Id:$
 */
public interface WorkflowManager {

    /**
     * The Avalon role.
     */
    String ROLE = WorkflowManager.class.getName();

    /**
     * Invokes a workflow event on a document. This is the same as
     * <code>invoke(Document, String, true)</code>.
     * @param document The document.
     * @param event The name of the event.
     * @throws WorkflowException if the event could not be invoked in the
     *             current situation.
     */
    void invoke(Document document, String event) throws WorkflowException;

    /**
     * Invokes a workflow event on a document.
     * @param document The document.
     * @param event The name of the event.
     * @param force If this is set to <code>true</code>, the execution is
     *            forced, which means an exception is thrown if the document in
     *            the set does not support the event. If set to
     *            <code>false</code>, non-supporting documents are ignored.
     * @throws WorkflowException if the event could not be invoked in the
     *             current situation.
     */
    void invoke(Document document, String event, boolean force) throws WorkflowException;

    /**
     * Invokes a workflow event on a document set.
     * @param documentSet The document.
     * @param event The event.
     * @param force If this is set to <code>true</code>, the execution is
     *            forced, which means an exception is thrown if a document in
     *            the set does not support the event. If set to
     *            <code>false</code>, non-supporting documents are ignored.
     * @throws WorkflowException if <code>force</code> is set to
     *             <code>true</code> and a document does not support the
     *             workflow event.
     */
    void invokeOnAll(DocumentSet documentSet, String event, boolean force) throws WorkflowException;

    /**
     * Checks if an event can be invoked on a document.
     * @param document The document.
     * @param event The event.
     * @return A boolean value.
     */
    boolean canInvoke(Document document, String event);

    /**
     * Checks if an event can be invoked on all documents in a set.
     * @param documents The documents.
     * @param event The event.
     * @return if an error occurs.
     */
    boolean canInvokeOnAll(DocumentSet documents, String event);

    /**
     * Copies the workflow history from one document to another.
     * @param source The source document.
     * @param target The target document.
     * @throws WorkflowException if the history could not be copied.
     */
    void copyHistory(Document source, Document target) throws WorkflowException;

    /**
     * Moves the workflow history of a document.
     * @param source The source document.
     * @param target The destination document.
     * @throws WorkflowException if an error occurs.
     */
    void moveHistory(Document source, Document target) throws WorkflowException;

    /**
     * Deletes the workflow history of a document.
     * @param document The document.
     * @throws WorkflowException if an error occurs.
     */
    void deleteHistory(Document document) throws WorkflowException;

    /**
     * Initializes the workflow history of a document.
     * @param document The document.
     * @throws WorkflowException if an error occurs.
     */
    void initializeHistory(Document document) throws WorkflowException;
}