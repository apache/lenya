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

/* $Id: WorkflowDocument.java,v 1.7 2004/03/01 16:18:16 gregor Exp $  */

package org.apache.lenya.cms.workflow;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.impl.WorkflowImpl;
import org.apache.lenya.workflow.impl.WorkflowInstanceImpl;

public class WorkflowDocument extends WorkflowInstanceImpl {
	
	/**
	 * Create a new <code>WorkflowDocument</code>
	 * 
	 * @param document the document
	 * 
	 * @throws WorkflowException if an error occurs
	 */
    protected WorkflowDocument(Document document) throws WorkflowException {
        assert document != null;
        this.document = document;
    }

    private Document document;

    /**
     * Returns the document of this WorkflowDocument object.
     * @return A document object.
     */
    protected Document getDocument() {
        return document;
    }

    /**
     * DOCUMENT ME!
     *
     * @param situation DOCUMENT ME!
     * @param eventName DOCUMENT ME!
     *
     * @throws WorkflowException DOCUMENT ME!
     */
    public void invoke(Situation situation, String eventName)
        throws WorkflowException {
        assert eventName != null;

        Event event = ((WorkflowImpl) getWorkflow()).getEvent(eventName);
        invoke(situation, event);
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.workflow.impl.WorkflowInstanceImpl#getWorkflow(java.lang.String)
     */
    protected WorkflowImpl getWorkflow(String workflowName)
        throws WorkflowException {
        WorkflowImpl workflow = (WorkflowImpl) WorkflowFactory.buildWorkflow(document.getPublication(),
                workflowName);

        return workflow;
    }
}
