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
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.lenya.cms.site.tree.SiteTreeNodeVisitor;
import org.apache.lenya.cms.workflow.WorkflowManager;
import org.apache.tools.ant.BuildException;

/**
 * Ant task which implements the SiteTreeNodeVisitor for the operation move a document. (Visitor
 * pattern)
 */
public class MoveDocumentTask extends PublicationTask implements SiteTreeNodeVisitor {

    private String firstarea;
    private String firstdocumentid;
    private String secarea;
    private String secdocumentid;

    /**
     * Constructor
     */
    public MoveDocumentTask() {
        super();
    }

    /**
     * @return String The area of the source.
     */
    public String getFirstarea() {
        return this.firstarea;
    }

    /**
     * @return String The document-id corresponding to the source.
     */
    public String getFirstdocumentid() {
        return this.firstdocumentid;
    }

    /**
     * @return String The area of the destination.
     */
    public String getSecarea() {
        return this.secarea;
    }

    /**
     * @return String The document-id corresponding to the destination.
     */
    public String getSecdocumentid() {
        return this.secdocumentid;
    }

    /**
     * @param string The area of the source.
     */
    public void setFirstarea(String string) {
        this.firstarea = string;
    }

    /**
     * @param string The document-id corresponding to the source.
     */
    public void setFirstdocumentid(String string) {
        this.firstdocumentid = string;
    }

    /**
     * @param string The area of the destination.
     */
    public void setSecarea(String string) {
        this.secarea = string;
    }

    /**
     * @param string The document-id corresponding to the destination.
     */
    public void setSecdocumentid(String string) {
        this.secdocumentid = string;
    }

    /**
     * move the workflow files
     * 
     * @see org.apache.lenya.cms.site.tree.SiteTreeNodeVisitor#visitSiteTreeNode(org.apache.lenya.cms.site.tree.SiteTreeNode)
     */
    public void visitSiteTreeNode(SiteTreeNode node) {
        Label[] labels = node.getLabels();
        for (int i = 0; i < labels.length; i++) {
            String language = labels[i].getLanguage();

            String srcDocumentid = node.getAbsoluteId();
            String destDocumentid = srcDocumentid.replaceFirst(this.firstdocumentid,
                    this.secdocumentid);

            // TODO: content(fix the build file)
            // TODO: resources (fix the build file)
            // TODO: rcml (fix the build file)
            // TODO: rcbak (fix the build file)

            //move workflow

            Document document;
            Document newDocument;

            log("move workflow history");
            try {
                document = getIdentityMap().get(getPublication(),
                        this.firstarea,
                        srcDocumentid,
                        language);
                newDocument = getIdentityMap().get(getPublication(),
                        this.secarea,
                        destDocumentid,
                        language);
            } catch (DocumentBuildException e) {
                throw new BuildException(e);
            }
            WorkflowManager wfManager = null;
            try {
                wfManager = (WorkflowManager) getServiceManager().lookup(WorkflowManager.ROLE);
                wfManager.moveHistory(document, newDocument);
            } catch (Exception e) {
                throw new BuildException(e);
            } finally {
                if (wfManager != null) {
                    getServiceManager().release(wfManager);
                }
            }
        }
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        try {
            log("document id for the source" + this.getFirstdocumentid());
            log("area for the source" + this.getFirstarea());
            log("document id for the destination" + this.getSecdocumentid());
            log("area for the destination" + this.getSecarea());

            SiteTree tree = getSiteTree(getFirstarea());
            SiteTreeNode node = tree.getNode(getFirstdocumentid());

            node.acceptSubtree(this);
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

}