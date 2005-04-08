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

import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to insert a label into an existing node in a tree.
 * @deprecated will be replaced during the 1.4 cycle by
 *             org.apache.lenya.cms.metadata.Metadata
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
        return this.creator;
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Get the publisher
     * 
     * @return the publisher
     */
    public String getPublisher() {
        return this.publisher;
    }

    /**
     * Get the rights
     * 
     * @return the rights
     */
    public String getRights() {
        return this.rights;
    }

    /**
     * Get the subject
     * 
     * @return the subject
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * Get the title
     * 
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Set the creator.
     * 
     * @param string the creator
     */
    public void setCreator(String string) {
        this.creator = string;
    }

    /**
     * Set the description
     * 
     * @param string the description
     */
    public void setDescription(String string) {
        this.description = string;
    }

    /**
     * Set the publisher.
     * 
     * @param string the publisher
     */
    public void setPublisher(String string) {
        this.publisher = string;
    }

    /**
     * Set the rights
     * 
     * @param string the rights
     */
    public void setRights(String string) {
        this.rights = string;
    }

    /**
     * Set the subject
     * 
     * @param string the subject
     */
    public void setSubject(String string) {
        this.subject = string;
    }

    /**
     * Set the title
     * 
     * @param string the title
     */
    public void setTitle(String string) {
        this.title = string;
    }

    /**
     * Get the area
     * 
     * @return the area
     */
    public String getArea() {
        return this.area;
    }

    /**
     * Set the area
     * 
     * @param string the area
     */
    public void setArea(String string) {
        this.area = string;
    }

    /**
     * Get the document-id
     * 
     * @return the document-id
     */
    public String getDocumentId() {
        return this.documentId;
    }

    /**
     * Set the document-id
     * 
     * @param string the document-id
     */
    public void setDocumentId(String string) {
        this.documentId = string;
    }

    /**
     * Get the language
     * 
     * @return the language
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * Set the language.
     * 
     * @param string the language
     */
    public void setLanguage(String string) {
        this.language = string;
    }

    /**
     * Write the dublin core params.
     * 
     * @param _documentId the document-id
     * @param _area the area
     * @param lang the language
     * @param _creator the creator.
     * @param _title the title
     * @param _description the description
     * @param _subject the subject
     * @param _publisher the publisher
     * @param _rights the rights
     * 
     * @throws BuildException if an error occurs
     * @throws DocumentBuildException if an error occurs
     * @throws DocumentException if an error occurs
     */
    public void writeDublinCoreParameters(String _documentId, String _area, String lang,
            String _creator, String _title, String _description, String _subject,
            String _publisher, String _rights) throws BuildException, DocumentBuildException,
            DocumentException {

        Document doc = getIdentityMap()
                .get(getPublication(), _area, _documentId, lang);
        DublinCore dc = doc.getDublinCore();
        dc.setValue(DublinCore.ELEMENT_CREATOR, _creator);
        dc.setValue(DublinCore.ELEMENT_TITLE, _title);
        dc.setValue(DublinCore.ELEMENT_DESCRIPTION, _description);
        dc.setValue(DublinCore.ELEMENT_SUBJECT, _subject);
        dc.setValue(DublinCore.ELEMENT_PUBLISHER, _publisher);
        dc.setValue(DublinCore.ELEMENT_RIGHTS, _rights);
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        try {
            writeDublinCoreParameters(getDocumentId(),
                    getArea(),
                    getLanguage(),
                    getCreator(),
                    getTitle(),
                    getDescription(),
                    getSubject(),
                    getPublisher(),
                    getRights());
        } catch (final BuildException e) {
            throw new BuildException(e);
        } catch (final DocumentBuildException e) {
            throw new BuildException(e);
        } catch (final DocumentException e) {
            throw new BuildException(e);
        }
    }

}