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
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;

/**
 * Class to resolve workflow-related objects.
 * 
 * @version $Id$
 */
public interface WorkflowResolver {

    /**
     * The Avalon component role.
     */
    String ROLE = WorkflowResolver.class.getName();

    /**
     * Checks if a document has a workflow.
     * @param document The document.
     * @return A boolean value.
     */
    boolean hasWorkflow(Document document);

    /**
     * Resolves the workflow schema of a document.
     * @param document The document.
     * @return A workflow schema.
     * @throws WorkflowException if the document has no workflow.
     */
    Workflow getWorkflowSchema(Document document) throws WorkflowException;

    /**
     * Resolves the current workflow situation.
     * @return A situation object.
     */
    Situation getSituation();

}