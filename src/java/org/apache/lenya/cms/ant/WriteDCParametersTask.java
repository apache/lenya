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

/* $Id: WriteDCParametersTask.java,v 1.6 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DublinCore;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to insert a label into an existing node in a tree.
 */
public class WriteDCParametersTask extends PublicationTask {
    private String documentId = null;
    private String area = null;
    private String creator = null;
    private String title = null;
    private String description = null;
    private String subject = null;
    private String language = null;
    private String publisher = null;
    private String rights = null;
 
    /**
     * Creates a new instance of InsertLabelTask
     */
    public WriteDCParametersTask() {
        super();
    }

    /**
     * Get the creator
     * 
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the publisher
     * 
     * @return the publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Get the rights
     * 
     * @return the rights
     */
    public String getRights() {
        return rights;
    }

    /**
     * Get the subject
     * 
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Get the title
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the creator.
     * 
     * @param string the creator
     */
    public void setCreator(String string) {
        creator = string;
    }

    /**
     * Set the description
     * 
     * @param string the description
     */
    public void setDescription(String string) {
        description = string;
    }

    /**
     * Set the publisher.
     * 
     * @param string the publisher
     */
    public void setPublisher(String string) {
        publisher = string;
    }

    /**
     * Set the rights
     * 
     * @param string the rights
     */
    public void setRights(String string) {
        rights = string;
    }

    /**
     * Set the subject
     * 
     * @param string the subject
     */
    public void setSubject(String string) {
        subject = string;
    }

    /**
     * Set the title
     * 
     * @param string the title
     */
    public void setTitle(String string) {
        title = string;
    }

    /**
     * Get the area
     * 
     * @return the area
     */
    public String getArea() {
        return area;
    }

    /**
     * Set the area
     * 
     * @param string the area
     */
    public void setArea(String string) {
        area = string;
    }

    /**
     * Get the document-id
     * 
     * @return the document-id
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Set the document-id
     * 
     * @param string the document-id
     */
    public void setDocumentId(String string) {
        documentId = string;
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
     * Set the language.
     * 
     * @param string the language
     */
    public void setLanguage(String string) {
        language = string;
    }

    /**
     * Write the dublin core params.
     * 
     * @param documentId the document-id
     * @param area the area
     * @param lang the language
     * @param creator the creator.
     * @param title the title
     * @param description the description
     * @param subject the subject
     * @param publisher the publisher
     * @param rights the rights
     * 
     * @throws BuildException if an error occurs
     * @throws DocumentBuildException if an error occurs
     * @throws DocumentException if an error occurs
     */
    public void writeDublinCoreParameters(
        String documentId,
        String area,
        String lang,
        String creator,
        String title,
        String description,
        String subject,
        String publisher,
        String rights)
        throws BuildException, DocumentBuildException, DocumentException {

        DocumentBuilder builder = getPublication().getDocumentBuilder();
        String url = builder.buildCanonicalUrl(getPublication(), area, documentId, lang);
        Document doc = builder.buildDocument(getPublication(), url);
        DublinCore dc = doc.getDublinCore();
        dc.setValue(DublinCore.ELEMENT_CREATOR, creator);
        dc.setValue(DublinCore.ELEMENT_TITLE, title);
        dc.setValue(DublinCore.ELEMENT_DESCRIPTION, description);
        dc.setValue(DublinCore.ELEMENT_SUBJECT, subject);
        dc.setValue(DublinCore.ELEMENT_PUBLISHER, publisher);
        dc.setValue(DublinCore.ELEMENT_RIGHTS, rights);
        dc.save();
    }

    /** (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        try {
            writeDublinCoreParameters(
                getDocumentId(),
                getArea(),
                getLanguage(),
                getCreator(),
                getTitle(),
                getDescription(),
                getSubject(),
                getPublisher(),
                getRights());
            } catch (
                Exception e) {
            throw new BuildException(e);
        }
    }

}
