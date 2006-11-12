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

/* $Id: RenameLabelTask.java 160149 2005-04-05 09:51:54Z michi $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to change the href attribute of a label element in the sitetree.
 */
public class ChangeHrefTask extends PublicationTask {
    private String documentid;
    private String href;
    private String area;
    private String language;

    /**
     * Creates a new instance of ChangeHrefTask
     */
    public ChangeHrefTask() {
        super();
    }

    /**
     * Get the area of the site tree.
     * 
     * @return  the area of the tree.
     */
    protected String getArea() {
        return area;
    }

    /**
     * Set the area.
     * 
     * @param area the area.
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * Return the document-id corresponding to the node to delete.
     * 
     * @return string The document-id.
     */
    protected String getDocumentid() {
        return documentid;
    }

    /**
     * Set the value of the document-id corresponding to the node to delete.
     * 
     * @param string The document-id.
     */
    public void setDocumentid(String string) {
        documentid = string;
    }

    /**
     * Get the href.
     * 
     * @return the href
     */
    public String getHref() {
        return href;
    }

    /**
     * Set the href.
     * 
     * @param value of the href
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Get the language.
     * 
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Set the language.
     * 
     * @param language the language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Change the href attribute of an existing node in the tree.
     * 
     * @param documentid the document-id of the document.
     * @param language the language of the label that is to be renamed.
     * @param area determines in which sitetree the label is to be renamed
     * @param href the new href value
     * 
     * @throws SiteTreeException if an error occurs.
     */
    public void changeHref (
        String documentid,
        String language,
        String area,
        String href)
        throws SiteTreeException, DocumentException {

        SiteTree tree = getPublication().getTree(area);
        SiteTreeNode node = tree.getNode(documentid);
        if (node == null) {
            throw new DocumentException(
                "Document-id " + documentid + " not found.");
        }
        Label label = node.getLabel(language);
        if (label == null) {
            throw new DocumentException(
                "Label for language " + language + " not found.");
        }
        if (href.equals("")) href = null;
        	// FIXME: This is somewhat of a hack. See also RenameLabelTask.java
        tree.removeLabel(documentid, label);
        label.setHref(href);
        tree.addLabel(documentid, label);
        tree.save();
    }

    /** (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        try {
            log("document-id corresponding to the node: " + getDocumentid());
            log("language: " + getLanguage());
            log("area: " + getArea());
            log("href: " + getHref());
            changeHref(
                getDocumentid(),
                getLanguage(),
                getArea(),
                getHref());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
