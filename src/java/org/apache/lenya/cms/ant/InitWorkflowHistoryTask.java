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

/* $Id: InitWorkflowHistoryTask.java,v 1.11 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Situation;
import org.apache.tools.ant.BuildException;

public class InitWorkflowHistoryTask extends PublicationTask {
    private String documentId;
    private String documentType;
    private String language;

    /**
     * Returns the machine IP address from which the history was initialized.
     * @return A string.
     */
    public String getMachineIp() {
        return machineIp;
    }

    /**
     * Sets the machine IP address from which the history was initialized.
     * @param machineIp A string.
     */
    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    /**
     * Returns the ID of the user who initialized the history.
     * @return A string.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who initialized the history.
     * @param userId A string.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        String language = getLanguage();

        if (language == null) {
            language = getPublication().getDefaultLanguage();
        }
        DocumentBuilder builder = getPublication().getDocumentBuilder();
        String url = builder.buildCanonicalUrl(getPublication(), Publication.AUTHORING_AREA, getDocumentId(), language);
        Document document;
        try {
            document = builder.buildDocument(getPublication(), url);
            log(".execute(): " + document.getLanguage());
        } catch (DocumentBuildException e) {
            throw new BuildException(e);
        }

        try {
            String[] roles = new String[0];
            Situation situation =
                WorkflowFactory.newInstance().buildSituation(roles, getUserId(), getMachineIp());
            DocumentType type =
                DocumentTypeBuilder.buildDocumentType(getDocumentType(), getPublication());
            WorkflowFactory.initHistory(document, type.getWorkflowFileName(), situation);
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    /**
     * Get the document-id.
     * 
     * @return the document-id
     */
    public String getDocumentId() {
        assertString(documentId);

        return documentId;
    }

    /**
     * Set the document-id.
     * 
     * @param aDocumentId the document-id
     */
    public void setDocumentId(String aDocumentId) {
        assertString(aDocumentId);
        documentId = aDocumentId;
    }

    /**
     * Get the document type.
     * 
     * @return the document type
     */
    public String getDocumentType() {
        assertString(documentType);

        return documentType;
    }

    /**
     * Set the document type.
     * 
     * @param aDocumentType the document type
     */
    public void setDocumentType(String aDocumentType) {
        assertString(aDocumentType);
        documentType = aDocumentType;
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
     * @param string the language
     */
    public void setLanguage(String string) {
        language = string;
    }

    private String userId = "";
    private String machineIp = "";

}
