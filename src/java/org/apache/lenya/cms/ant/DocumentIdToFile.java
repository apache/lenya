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

/* $Id: DocumentIdToFile.java,v 1.2 2004/03/03 12:56:30 gregor Exp $  */

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
     * 
     * @return The directory for the area in the publication.
     */
    public String getArea() {
        return area;
    }

    /**
     * Get the document-id.
     * 
     * @return The document id 
     */
    protected String getDocumentid() {
        return documentId;
    }

    /**
     * Get the property name.
     * 
     * @return The name of the property for the path.
     */
    public String getPropertyname() {
        return propertyname;
    }

    /**
     * Set the area.
     * 
     * @param area The area.
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * Set the property name
     * 
     * @param string The name of the property.
     */
    public void setPropertyname(String string) {
        propertyname = string;
    }

    /**
     * Set the value of the document id
     *   
     * @param documentId The document id. 
     */
    public void setDocumentid(String documentId) {
        this.documentId = documentId;
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
     * Gets the path from the document id and sets this value in the 
     * property of the project with the name propertyname.   
    
     * @param area The area (e.g. "authoring")
     * @param documentid  The document id.
     * @param language the language of the document
     * @param propertyname The name of the property
     */
    public void compute(
        String area,
        String documentid,
        String language,
        String propertyname) {

        Publication publication = getPublication();
        DocumentIdToPathMapper pathMapper = publication.getPathMapper();
        String fileName =
            pathMapper
                .getFile(publication, area, documentid, language)
                .getAbsolutePath();

        Target target = getOwningTarget();
        Project project = target.getProject();
        project.setProperty(propertyname, fileName);
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
