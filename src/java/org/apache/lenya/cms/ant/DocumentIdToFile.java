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

import org.apache.lenya.cms.publication.DocumentIdToPathMapper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;

/**
 * Ant task to get the file from the document-id.
 * 
 * @see org.apache.lenya.cms.publication.DocumentIdToPathMapper#getFile(Publication publication, String area, String documentId, String language) 
 */
public class DocumentIdToFile extends PublicationTask {
    private String area;
    private String documentId;
    private String language;
    private String propertyname;

    /**
     * Creates a new instance of DocumentIdToPath.
     */
    public DocumentIdToFile() {
        super();
    }

    /**
     * Get the area.
     * @return The directory for the area in the publication.
     */
    public String getArea() {
        return this.area;
    }

    /**
     * Get the document-id.
     * @return The document id 
     */
    protected String getDocumentid() {
        return this.documentId;
    }

    /**
     * Get the property name.
     * @return The name of the property for the path.
     */
    public String getPropertyname() {
        return this.propertyname;
    }

    /**
     * Set the area.
     * @param _area The area.
     */
    public void setArea(String _area) {
        this.area = _area;
    }

    /**
     * Set the property name
     * @param string The name of the property.
     */
    public void setPropertyname(String string) {
        this.propertyname = string;
    }

    /**
     * Set the value of the document id
     * @param _documentId The document id. 
     */
    public void setDocumentid(String _documentId) {
        this.documentId = _documentId;
    }

    /**
     * Get the language
     * @return the language
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * Set the language
     * @param _language the language
     */
    public void setLanguage(String _language) {
        this.language = _language;
    }

    /**
     * Gets the path from the document id and sets this value in the 
     * property of the project with the name propertyname.   
    
     * @param _area The area (e.g. "authoring")
     * @param documentid  The document id.
     * @param _language the language of the document
     * @param _propertyname The name of the property
     */
    public void compute(
        String _area,
        String documentid,
        String _language,
        String _propertyname) {

        Publication publication = getPublication();
        DocumentIdToPathMapper pathMapper = publication.getPathMapper();
        String fileName =
            pathMapper
                .getFile(publication, _area, documentid, _language)
                .getAbsolutePath();

        Target _target = getOwningTarget();
        Project _project = _target.getProject();
        _project.setProperty(_propertyname, fileName);
    }

    /** 
     * @see org.apache.tools.ant.Task#execute()
     **/
    public void execute() throws BuildException {
        try {
            log("document-id " + getDocumentid());
            log("area " + getArea());
            log("language " + getLanguage());
            log("property: " + getPropertyname());
            compute(
                getArea(),
                getDocumentid(),
                getLanguage(),
                getPropertyname());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
