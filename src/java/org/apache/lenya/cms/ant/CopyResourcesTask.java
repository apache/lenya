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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * Ant task, which implements the SiteTreeNodeVisitor for the operation copy the resources. (Visitor
 * pattern)
 */
public class CopyResourcesTask extends TwoDocumentsOperationTask {

    /**
     *  
     */
    public CopyResourcesTask() {
        super();
    }

    /**
     * Copy the resources files belongs to the documents corresponding to this node
     * 
     * @see org.apache.lenya.cms.site.tree.SiteTreeNodeVisitor#visitSiteTreeNode(org.apache.lenya.cms.publication.SiteTreeNode)
     */
    public void visitSiteTreeNode(SiteTreeNode node) {
        String srcDocumentid = node.getAbsoluteId();
        String destDocumentid = srcDocumentid
                .replaceFirst(getFirstdocumentid(), getSecdocumentid());

        Label[] labels = node.getLabels();

        // FIXME: if the resources differ for different languages, so iterate
        // on all languages

        String language = labels[0].getLanguage();
        Document srcDoc;
        try {
            srcDoc = getIdentityMap().getFactory().get(getFirstarea(), srcDocumentid, language);
        } catch (DocumentBuildException e) {
            throw new BuildException(e);
        }
        ResourcesManager resourcesMgr = srcDoc.getResourcesManager();
        List resources = new ArrayList(Arrays.asList(resourcesMgr.getResources()));
        resources.addAll(Arrays.asList(resourcesMgr.getMetaFiles()));
        File[] srcFiles = (File[]) resources.toArray(new File[resources.size()]);

        if (srcFiles == null) {
            log("There are no resources for the document " + getFirstdocumentid());
            return;
        }

        Document destDoc;
        try {
            destDoc = getIdentityMap().getFactory().get(getSecarea(), destDocumentid, language);
        } catch (DocumentBuildException e) {
            throw new BuildException(e);
        }
        resourcesMgr = destDoc.getResourcesManager();
        for (int i = 0; i < srcFiles.length; i++) {

            try {
                log("copy file " + srcFiles[i].getAbsolutePath() + "to file "
                        + resourcesMgr.getPath().getCanonicalPath());
                FileUtil.copyFileToDirectory(srcFiles[i], resourcesMgr.getPath());
            } catch (IOException e) {
                throw new BuildException(e);
            }
        }

    }
}