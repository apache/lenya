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

/* $Id: DocumentCreatorTask.java,v 1.8 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import java.io.File;

import org.apache.lenya.cms.authoring.CreatorException;
import org.apache.lenya.cms.authoring.DocumentCreator;
import org.apache.tools.ant.BuildException;

public class DocumentCreatorTask extends PublicationTask {
    private String parentId;
    private String childId;
    private String childName;
    private String childType;
    private String documentType;
    private String area;
    private String authoringPath;
    private String language;

    /**
     *  (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        DocumentCreator creator = new DocumentCreator();

        try {
            creator.create(
                getPublication(),
                new File(getPublication().getDirectory(), getAuthoringPath()),
                getArea(),
                getParentId(),
                getChildId(),
                getChildName(),
                getChildType(),
                documentType,
                getLanguage());
        } catch (CreatorException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Get the child type
     * 
     * @return the child type
     */
    public String getChildType() {
        assertString(childType);

        return childType;
    }

    /**
     * Get the document type
     * 
     * @return the document type
     */
    public String getDocumentType() {
        assertString(documentType);

        return documentType;
    }

    /**
     * Get the parent id
     * 
     * @return the parent id
     */
    public String getParentId() {
        assertString(parentId);

        return parentId;
    }

    /**
     * Set the child type
     * 
     * @param string the child type
     */
    public void setChildType(String string) {
        assertString(string);
        childType = string;
    }

    /**
     * Set the document type
     * 
     * @param string the document type
     */
    public void setDocumentType(String string) {
        assertString(string);
        documentType = string;
    }

    /**
     * Set the parent id
     * 
     * @param string the parent id
     */
    public void setParentId(String string) {
        assertString(string);
        parentId = string;
    }

    /**
     * Get the child id
     * 
     * @return the child id
     */
    public String getChildId() {
        assertString(childId);

        return childId;
    }

    /**
     * Get the child name
     *  
     * @return the child name
     */
    public String getChildName() {
        assertString(childName);

        return childName;
    }

    /**
     * Set the child id
     * 
     * @param string the child id
     */
    public void setChildId(String string) {
        assertString(string);
        childId = string;
    }

    /**
     * Set the child name
     * 
     * @param string the child name
     */
    public void setChildName(String string) {
        assertString(string);
        childName = string;
    }

    /**
     * Get the authoring path
     * 
     * @return the authoring path
     */
    public String getAuthoringPath() {
        assertString(authoringPath);

        return authoringPath;
    }

    /**
     * Set the authoring path
     * 
     * @param string the authoring path
     */
    public void setAuthoringPath(String string) {
        assertString(string);
        authoringPath = string;
    }

    /**
     * Get the language
     * 
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Set the language
     * 
     * @param language the language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Get the area.
     * 
     * @return the area
     */
    public String getArea() {
        return area;
    }

    /**
     * Set the area.
     * 
     * @param area the area
     */
    public void setArea(String area) {
        this.area = area;
    }

}
