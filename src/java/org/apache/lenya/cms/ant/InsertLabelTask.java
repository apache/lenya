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

/* $Id: InsertLabelTask.java,v 1.4 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to insert a label into an existing node in a tree.
 */
public class InsertLabelTask extends PublicationTask {
    private String documentid;
    private String labelName;
    private String area;
    private String language;

    /**
     * Creates a new instance of InsertLabelTask
     */
    public InsertLabelTask() {
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
     * Set the area of the site tree
     * 
     * @param area the area of the tree.
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
     * Insert a label in an existing node in the tree.
     * 
     * @param documentid the document-id of the document.
     * @param labelName the name of the label that is to be inserted.
     * @param language the language of the label that is to be inserted.
     * @param area determines in which sitetree the label is to be inserted
     * 
     * @throws SiteTreeException if an error occurs
     */
    public void insertLabel(
        String documentid,
        String labelName,
        String language,
        String area)
        throws SiteTreeException {

        DefaultSiteTree tree = null;
        Label label = null;
        try {
            tree = getPublication().getSiteTree(area);
            label = new Label(labelName, language);
            tree.addLabel(documentid, label);
            tree.save();
        } catch (Exception e) {
            throw new SiteTreeException(
                "Cannot insert label "
                    + label
                    + " into tree "
                    + area,
                e);
        }

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
            insertLabel(
                getDocumentid(),
                getLabelName(),
                getLanguage(),
                getArea());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
