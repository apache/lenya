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

import java.io.File;
import java.io.IOException;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.rc.RevisionController;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * Ant task, to init the rc files of the destination's documents corresponding to a given source
 * subtree. Evry destination file is checked in. (Visitor pattern)
 */
public class InitRCTask extends TwoDocumentsOperationTask {
    private String rcmlDir = "";
    private String rcbakDir = "";
    private String userId = "";
    private RevisionController rc = null;

    /**
     * Constructor
     */
    public InitRCTask() {
        super();
    }

    /**
     * @return String The backups' directory.
     */
    public String getRcbakDir() {
        return this.rcbakDir;
    }

    /**
     * @return String The rcml-files' directory.
     */
    public String getRcmlDir() {
        return this.rcmlDir;
    }

    /**
     * @param string The backup's directory.
     */
    public void setRcbakDir(String string) {
        this.rcbakDir = string;
    }

    /**
     * @param string The rcml-files' directory.
     */
    public void setRcmlDir(String string) {
        this.rcmlDir = string;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNodeVisitor#visitSiteTreeNode(org.apache.lenya.cms.site.tree.SiteTreeNode)
     */
    public void visitSiteTreeNode(SiteTreeNode node) {
        try {
            String publicationPath = this.getPublicationDirectory().getCanonicalPath();

            String srcDocumentid = node.getAbsoluteId();
            String destDocumentid = srcDocumentid.replaceFirst(getFirstdocumentid(),
                    getSecdocumentid());

            Label[] labels = node.getLabels();
            for (int i = 0; i < labels.length; i++) {
                String language = labels[i].getLanguage();
                Document destDoc;
                destDoc = getIdentityMap().get(getPublication(),
                        getSecarea(),
                        destDocumentid,
                        language);
                String filename = destDoc.getFile().getCanonicalPath();
                filename = filename.substring(publicationPath.length());
                this.rc.reservedCheckIn(filename, getUserId(), true);
            }
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        try {
            log("rcml dir" + this.getRcmlDir());
            log("rcbak dir" + this.getRcbakDir());
            log("user" + this.getUserId());
            String publicationPath = this.getPublicationDirectory().getCanonicalPath();
            String rcmlDirectory = new File(publicationPath, this.getRcmlDir()).getCanonicalPath();
            String rcbakDirectory = new File(publicationPath, this.getRcbakDir())
                    .getCanonicalPath();
            this.rc = new RevisionController(rcmlDirectory, rcbakDirectory, publicationPath);
        } catch (IOException e) {
            throw new BuildException(e);
        }
        super.execute();
    }

    /**
     * @return String The user id.
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * @param string The user id.
     */
    public void setUserId(String string) {
        this.userId = string;
    }

}