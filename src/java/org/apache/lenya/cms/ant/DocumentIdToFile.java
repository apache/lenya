/*
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2003 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://cocoon.apache.org/lenya/)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact board@apache.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://cocoon.apache.org/lenya/)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
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
 * 
 * @author egli
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
