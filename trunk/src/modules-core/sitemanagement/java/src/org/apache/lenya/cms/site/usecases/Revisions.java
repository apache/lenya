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

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.repository.History;
import org.apache.lenya.cms.repository.Revision;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.Workflowable;

/**
 * Usecase to display revisions of a resource.
 * 
 * @version $Id$
 */
public class Revisions extends SiteUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters() TODO
     *      filter out checkin entries
     */
    protected void initParameters() {
        super.initParameters();
        Document sourceDoc = getSourceDocument();
        if (sourceDoc != null) {
            try {
                History history = sourceDoc.getRepositoryNode().getHistory();

                int[] numbers = history.getRevisionNumbers();
                Revision[] revisions = new Revision[numbers.length];
                for (int i = 0; i < numbers.length; i++) {
                    revisions[i] = history.getRevision(numbers[i]);
                }
                setParameter("revisions", revisions);

                Boolean canRollback = Boolean.valueOf(WorkflowUtil.canInvoke(this.manager,
                        getDocumentFactory().getSession(), getLogger(), sourceDoc, getEvent()));
                setParameter("canRollback", canRollback);

                if (WorkflowUtil.hasWorkflow(this.manager, getSession(), getLogger(), sourceDoc)) {
                    Workflowable workflowable = WorkflowUtil.getWorkflowable(this.manager,
                            getSession(), getLogger(), sourceDoc);
                    Version latestVersion = workflowable.getLatestVersion();
                    String state;
                    if (latestVersion != null) {
                        state = latestVersion.getState();
                    } else {
                        Workflow workflow = WorkflowUtil.getWorkflowSchema(this.manager,
                                getSession(), getLogger(), sourceDoc);
                        state = workflow.getInitialState();
                    }
                    setParameter("workflowState", state);
                }
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }

            /*
             * // since we need both state and canInvoke, we could deal with the
             * avalon // component ourselves rather than using WorkflowUtil -
             * saves one // service manager lookup. // problem is that
             * DocumentWorkflowable is not public and Workflowable is abstract :(
             * 
             * WorkflowManager wfManager = null; String workflowState; Boolean
             * canRollback; try { wfManager = (WorkflowManager)
             * this.manager.lookup(WorkflowManager.ROLE); Workflowable
             * workflowable = new DocumentWorkflowable( this.manager,
             * getDocumentFactory().getSession(), sourceDoc, getLogger() );
             * workflowState = workflowable.getLatestVersion().getState();
             * canRollback = new Boolean(wfManager.canInvoke(workflowable,
             * WORKFLOW_EVENT_EDIT)); } catch (ServiceException e) { throw new
             * RuntimeException(e); } finally { if (wfManager != null) {
             * manager.release(wfManager); } } setParameter("workflowState",
             * workflowState); setParameter("canRollback", canRollback);
             */

        }
    }

    protected String getEvent() {
        return getParameterAsString("workflowEvent");
    }
}
