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

import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * anttask to set the document-id in the dc:identifier of all existing files
 * corresponding to this document-id
 */
public class SetIdentifier extends PublicationTask {

    private String area;
    private String documentid;

    /**
     *  
     */
    public SetIdentifier() {
        super();
    }

    /**
     * @return String The area.
     */
    public String getArea() {
        return this.area;
    }

    /**
     * @return String The document-id.
     */
    public String getDocumentid() {
        return this.documentid;
    }

    /**
     * @param string The area.
     */
    public void setArea(String string) {
        this.area = string;
    }

    /**
     * @param string The document-id.
     */
    public void setDocumentid(String string) {
        this.documentid = string;
    }

    /**
     * write the document id in the DC Identifier of a document corresponding to
     * this url
     * @param document The document.
     * @throws DocumentException when something went wrong when getting the
     *             DublinCore.
     */
    public void writeDCIdentifier(Document document) throws DocumentException {
        DublinCore dublincore = document.getDublinCore();
        dublincore.setValue("identifier", this.documentid);
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        log("document-id " + this.getDocumentid());
        log("area " + this.getArea());

        String language = null;
        SiteTree tree;

        tree = getSiteTree(this.area);
        SiteTreeNode node = tree.getNode(this.documentid);
        Label[] labels = node.getLabels();

        try {
            if (labels.length < 1) {
                log("no languages found for the node with id : " + node.getId());
                writeDCIdentifier(getIdentityMap().get(getPublication(),
                        this.area,
                        this.documentid));
            } else {
                for (int i = 0; i < labels.length; i++) {
                    language = labels[i].getLanguage();
                    writeDCIdentifier(getIdentityMap().get(getPublication(),
                            this.area,
                            this.documentid,
                            language));
                }
            }
        } catch (DocumentException e1) {
            throw new BuildException(e1);
        } catch (DocumentBuildException e2) {
            throw new BuildException(e2);
        }
    }

}