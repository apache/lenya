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

import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.tree.SiteTree;
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
     * @return the area of the tree.
     */
    protected String getArea() {
        return this.area;
    }

    /**
     * Set the area of the site tree
     * @param _area the area of the tree.
     */
    public void setArea(String _area) {
        this.area = _area;
    }

    /**
     * Return the document-id corresponding to the node to delete.
     * @return string The document-id.
     */
    protected String getDocumentid() {
        return this.documentid;
    }

    /**
     * Set the value of the document-id corresponding to the node to delete.
     * @param string The document-id.
     */
    public void setDocumentid(String string) {
        this.documentid = string;
    }

    /**
     * Get the name of the label.
     * @return the labelName
     */
    public String getLabelName() {
        return this.labelName;
    }

    /**
     * Set the labelName.
     * @param _labelName the name of the label
     */
    public void setLabelName(String _labelName) {
        this.labelName = _labelName;
    }

    /**
     * Get the language.
     * @return the language
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * Set the language.
     * @param _language the language
     */
    public void setLanguage(String _language) {
        this.language = _language;
    }

    /**
     * Insert a label in an existing node in the tree.
     * 
     * @param _documentid the document-id of the document.
     * @param _labelName the name of the label that is to be inserted.
     * @param _language the language of the label that is to be inserted.
     * @param _area determines in which sitetree the label is to be inserted
     * 
     * @throws SiteException if an error occurs
     */
    public void insertLabel(String _documentid, String _labelName, String _language, String _area)
            throws SiteException {

        SiteTree tree = null;
        Label label = null;
        try {
            tree = getSiteTree(_area);
            label = new Label(_labelName, _language);
            tree.addLabel(_documentid, label);
            tree.save();
        } catch (Exception e) {
            throw new SiteException("Cannot insert label " + label + " into tree " + _area, e);
        }

    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        try {
            log("document-id corresponding to the node: " + getDocumentid());
            log("label name: " + getLabelName());
            log("language: " + getLanguage());
            log("area: " + getArea());
            insertLabel(getDocumentid(), getLabelName(), getLanguage(), getArea());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}