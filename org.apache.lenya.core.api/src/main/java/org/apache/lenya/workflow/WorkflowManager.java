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
package org.apache.lenya.workflow;

/**
 * Manager for workflow issues. This is the main entry point for
 * workflow-related tasks. You can safely invoke all methods for non-workflow
 * documents.
 * 
 * @version $Id: WorkflowManager.java 179751 2005-06-03 09:13:35Z andreas $
 */
public interface WorkflowManager {

    /**
     * The Avalon role.
     */
    String ROLE = WorkflowManager.class.getName();

    /**
     * Invokes a workflow event on a document. This is the same as
     * <code>invoke(Document, String, true)</code>.
     * @param workflowable The workflowable.
     * @param event The name of the event.
     * @throws WorkflowException if the event could not be invoked in the
     *             current situation.
     */
    void invoke(Workflowable workflowable, String event) throws WorkflowException;

    /**
     * Invokes a workflow event on a document.
     * @param workflowable The document.
     * @param event The name of the event.
     * @param force If this is set to <code>true</code>, the execution is
     *            forced, which means an exception is thrown if the workflowable in
     *            the set does not support the event. If set to
     *            <code>false</code>, non-supporting documents are ignored.
     * @throws WorkflowException if the event could not be invoked in the
     *             current situation.
     */
    void invoke(Workflowable workflowable, String event, boolean force) throws WorkflowException;

    /**
     * Checks if an event can be invoked on a document.
     * @param workflowable The workflowable.
     * @param event The event.
     * @return A boolean value.
     */
    boolean canInvoke(Workflowable workflowable, String event);

    /**
     * Checks if a workflowable has a workflow.
     * @param workflowable The workflowable.
     * @return A boolean value.
     */
    boolean hasWorkflow(Workflowable workflowable);

    /**
     * Resolves the workflow schema of a workflowable.
     * @param workflowable The workflowable.
     * @return A workflow schema.
     * @throws WorkflowException if the document has no workflow.
     */
    Workflow getWorkflowSchema(Workflowable workflowable) throws WorkflowException;

}