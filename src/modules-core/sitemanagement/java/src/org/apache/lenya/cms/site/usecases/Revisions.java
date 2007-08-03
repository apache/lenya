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
package org.apache.lenya.cms.site.usecases;

import java.util.Vector;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.rc.RCML;
import org.apache.lenya.cms.workflow.WorkflowUtil;

/**
 * Usecase to display revisions of a resource.
 * 
 * @version $Id$
 */
public class Revisions extends SiteUsecase {

    private RCML rcml = null;

    public static final String WORKFLOW_EVENT_EDIT = "edit";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters() TODO
     *      filter out checkin entries
     */
    protected void initParameters() {
        super.initParameters();
        Document sourceDoc = getSourceDocument();
        if (sourceDoc != null) {
            try {
                this.rcml = sourceDoc.getRepositoryNode().getRcml();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
    
            Vector entries;
            try {
                entries = this.rcml.getBackupEntries();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
            setParameter("entries", entries);
           
            Boolean canRollback;
            try { 
                canRollback = new Boolean(WorkflowUtil.canInvoke(
                    this.manager, 
                    getDocumentFactory().getSession(),
                    getLogger(),
                    sourceDoc,
                    WORKFLOW_EVENT_EDIT));
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
            setParameter("canRollback", canRollback);

            String workflowState;
            try {
                // looks like this throws a null pointer exception
                // when a document has never been edited :(
                // guess this should be fixed elsewhere. for now
                // let's abuse the exception handler.
                workflowState = WorkflowUtil.getWorkflowable(
                    this.manager, 
                    getDocumentFactory().getSession(),
                    getLogger(),
                    sourceDoc
                ).getLatestVersion().getState();
                setParameter("workflowState", workflowState);
            } catch (final Exception e) {
                setParameter("workflowState", ""); //FIXME: should return Workflow.getInitialState(). But then again, there should not be an NPE anyways...
                //throw new RuntimeException(e);
            }
            
/*
            // since we need both state and canInvoke, we could deal with the avalon
            // component ourselves rather than using WorkflowUtil - saves one
            // service manager lookup.
            // problem is that DocumentWorkflowable is not public and Workflowable is abstract :(

            WorkflowManager wfManager = null;
            String workflowState;
            Boolean canRollback;
            try {
                wfManager = (WorkflowManager) this.manager.lookup(WorkflowManager.ROLE);
                Workflowable workflowable = new DocumentWorkflowable(
                    this.manager, 
                    getDocumentFactory().getSession(),
                    sourceDoc,
                    getLogger()
                );
                workflowState = workflowable.getLatestVersion().getState();
                canRollback = new Boolean(wfManager.canInvoke(workflowable, WORKFLOW_EVENT_EDIT));
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            } finally {
                if (wfManager != null) {
                    manager.release(wfManager);
                }
            }
            setParameter("workflowState", workflowState);
            setParameter("canRollback", canRollback);
*/

        }
    }
}
