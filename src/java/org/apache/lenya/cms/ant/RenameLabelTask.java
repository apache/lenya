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

/* $Id: RenameLabelTask.java,v 1.3 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to rename a label in an existing node in a tree.
 */
public class RenameLabelTask extends PublicationTask {
    private String documentid;
    private String labelName;
    private String area;
    private String language;

    /**
     * Creates a new instance of InsertLabelTask
     */
    public RenameLabelTask() {
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
     * Set the value of the area of the tree.
     * 
     * @param area the area of the tree.
     */
    public void setAbsolutetreepath(String area) {
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
     * Get the name of the label.
     * 
     * @return the labelName
     */
    public String getLabelName() {
        return labelName;
    }

    /**
     * Set the labelName.
     * 
     * @param labelName the name of the label
     */
    public void setLabelName(String labelName) {
        this.labelName = labelName;
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
     * Rename a label in an existing node in the tree.
     * 
     * @param documentid the document-id of the document.
     * @param labelName the new name of the label.
     * @param language the language of the label that is to be renamed.
     * @param area determines in which sitetree the label is to be renamed
     * 
     * @throws SiteTreeException if an error occurs.
     */
    public void renameLabel(
        String documentid,
        String labelName,
        String language,
        String area)
        throws SiteTreeException, DocumentException {

        DefaultSiteTree tree = null;
        tree = getPublication().getSiteTree(area);
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
	// FIXME: This is somewhat of a hack. The change of the label
	// name should not be done by removing the label and readding
	// it. Instead the node should probably have a setLabel method
	// which could be invoked by the Label.setLabel() method.
        tree.removeLabel(documentid, label);
        label.setLabel(labelName);
        tree.addLabel(documentid, label);
        tree.save();
    }

    /** (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        try {
            log("document-id corresponding to the node: " + getDocumentid());
            log("label name: " + getLabelName());
            log("language: " + getLanguage());
            log("area: " + getArea());
            renameLabel(
                getDocumentid(),
                getLabelName(),
                getLanguage(),
                getArea());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
