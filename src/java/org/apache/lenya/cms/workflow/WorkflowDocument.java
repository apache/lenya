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

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.workflow.History;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.impl.WorkflowImpl;
import org.apache.lenya.workflow.impl.WorkflowInstanceImpl;

/**
 * Workflow instance for CMS documents.
 * 
 * @version $Id$
 */
public class WorkflowDocument extends WorkflowInstanceImpl {

    /**
     * Ctor.
     * @param workflow The workflow.
     * @param document the document
     * @throws WorkflowException if an error occurs.
     */
    protected WorkflowDocument(WorkflowImpl workflow, Document document) throws WorkflowException {
        super(workflow);
        assert document != null;
        this.document = document;
    }

    private Document document;

    /**
     * Returns the document of this WorkflowDocument object.
     * @return A document object.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @see org.apache.lenya.workflow.impl.WorkflowInstanceImpl#createHistory()
     */
    protected History createHistory() {
        try {
            return new CMSHistory(this);
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }
}