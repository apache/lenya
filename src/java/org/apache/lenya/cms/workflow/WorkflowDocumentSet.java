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

/* $Id: WorkflowDocumentSet.java,v 1.3 2004/03/01 16:18:16 gregor Exp $  */

package org.apache.lenya.cms.workflow;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentSet;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.lenya.workflow.impl.SynchronizedWorkflowInstancesImpl;

/**
 * A synchronized workflow instance representing a set of documents.
 */
public class WorkflowDocumentSet extends SynchronizedWorkflowInstancesImpl {

    /**
     * Ctor.
     * @param documentSet The document set.
     * @param mainDocument The main document of the set, i.e. the document to
     * invoke the workflow on if the transition is not synchronized.
     * @throws WorkflowException when something went wrong.
     */
    public WorkflowDocumentSet(DocumentSet documentSet, Document mainDocument) throws WorkflowException {
        Document[] documents = documentSet.getDocuments();
        WorkflowInstance[] instances = new WorkflowInstance[documents.length];
        WorkflowFactory factory = WorkflowFactory.newInstance();
        for (int i = 0; i < documents.length; i++) {
            instances[i] = factory.buildInstance(documents[i]);
        }
        setInstances(instances);
        setMainInstance(factory.buildInstance(mainDocument));
    }

}
