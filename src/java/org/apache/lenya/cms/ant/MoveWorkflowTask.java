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

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.tools.ant.BuildException;

/**
 * Ant task, which implements the SiteTreeNodeVisitor for the operation move the workflow history
 * files. (Visitor pattern)
 */
public class MoveWorkflowTask extends TwoDocumentsOperationTask {

    /**
     *  
     */
    public MoveWorkflowTask() {
        super();
    }

    /**
     * (non-Javadoc)
     * @see org.apache.lenya.cms.site.tree.SiteTreeNodeVisitor#visitSiteTreeNode(org.apache.lenya.cms.site.tree.SiteTreeNode)
     */
    public void visitSiteTreeNode(SiteTreeNode node) {
        Label[] labels = node.getLabels();
        for (int i = 0; i < labels.length; i++) {
            String language = labels[i].getLanguage();

            String srcDocumentid = node.getAbsoluteId();
            String destDocumentid = srcDocumentid.replaceFirst(this.getFirstdocumentid(), this
                    .getSecdocumentid());

            Document srcDoc;
            Document destDoc;
            WorkflowFactory factory = WorkflowFactory.newInstance();

            log("init workflow history");
            try {
                srcDoc = getIdentityMap().getFactory().get(getFirstarea(), srcDocumentid, language);
                destDoc = getIdentityMap().getFactory().get(getSecarea(), destDocumentid, language);
            } catch (DocumentBuildException e) {
                throw new BuildException(e);
            }

            log("move workflow history of " + srcDoc.getFile().getAbsolutePath() + " to "
                    + destDoc.getFile().getAbsolutePath());
            try {
                if (factory.hasWorkflow(srcDoc)) {
                    log("has workflow");
                    WorkflowInstance sourceInstance = factory.buildExistingInstance(srcDoc);
                    String workflowName = sourceInstance.getWorkflow().getName();
                    
                    WorkflowInstance destInstance = factory.buildNewInstance(destDoc, workflowName);
                    destInstance.getHistory().replaceWith(sourceInstance.getHistory());
                    
                    sourceInstance.getHistory().delete();
                    log("workflow moved");
                }
            } catch (WorkflowException e) {
                throw new BuildException(e);
            }
        }
    }

}