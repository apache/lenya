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
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.util.LinkRewriter;
import org.apache.tools.ant.BuildException;

/**
 * This task is used to rewrite internal links after a cut'n'paste operation, i.e.
 * after a document has changed its document-id. It finds all relevant documents
 * which have a link to the document that changed its document-id and changes this
 * link with the help of an xslt.
 */
public class LinkRewriteTask extends PublicationTask {

    private String area;
    private String oldDocumentId;
    private String newDocumentId;

    /**
     * Get the area
     * @return the area
     */
    public String getArea() {
        return this.area;
    }

    /**
     * Set the area
     * @param _area the area
     */
    public void setArea(String _area) {
        this.area = _area;
    }

    /**
     * Get the new document-id.
     * @return the new document-id
     */
    public String getNewDocumentId() {
        return this.newDocumentId;
    }

    /**
     * Set the new document-id.
     * @param _newDocumentId the new document-id
     */
    public void setNewDocumentId(String _newDocumentId) {
        this.newDocumentId = _newDocumentId;
    }

    /**
     * Get the old document-id.
     * @return the old document-id
     */
    public String getOldDocumentId() {
        return this.oldDocumentId;
    }

    /**
     * Set the old document-id.
     * @param _oldDocumentId the old document-id
     */
    public void setOldDocumentId(String _oldDocumentId) {
        this.oldDocumentId = _oldDocumentId;
    }

    /**
     * Set the stylesheet.
     * @param _stylesheet the stylesheet that transforms the links
     */
    public void setStylesheet(String _stylesheet) {
    }

    /**
     * Set the base dir where in which the link rewrite will take place.
     * @param _baseDir the base dir
     */
    public void setBaseDir(String _baseDir) {
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        try {
            
            Publication pub = getPublication();
            DocumentBuilder builder = pub.getDocumentBuilder();
            
            String oldTargetUrl = builder.buildCanonicalUrl(pub, getArea(), getOldDocumentId());
            Document oldTargetDocument = builder.buildDocument(pub, oldTargetUrl);
            
            String newTargetUrl = builder.buildCanonicalUrl(pub, getArea(), getNewDocumentId());
            Document newTargetDocument = builder.buildDocument(pub, newTargetUrl);
            
            LinkRewriter rewriter = new LinkRewriter();
            rewriter.rewriteLinks(oldTargetDocument, newTargetDocument, getContextPrefix());
            
        } catch (Exception e) {
            throw new BuildException(e);
        }

    }

}
