/*
 * Copyright 1999-2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *  
 */

/* $Id$ */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeBuildException;
import org.apache.lenya.cms.publication.DocumentTypeBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.tools.ant.BuildException;

/**
 * A task to initialize the workflow history
 */
public class InitWorkflowHistoryTask extends PublicationTask {
    private String documentId;
    private String documentType;
    private String language;

    /**
     * Returns the machine IP address from which the history was initialized.
     * @return A string.
     */
    public String getMachineIp() {
        return this.machineIp;
    }

    /**
     * Sets the machine IP address from which the history was initialized.
     * @param _machineIp A string.
     */
    public void setMachineIp(String _machineIp) {
        this.machineIp = _machineIp;
    }

    /**
     * Returns the ID of the user who initialized the history.
     * @return A string.
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Sets the ID of the user who initialized the history.
     * @param _userId A string.
     */
    public void setUserId(String _userId) {
        this.userId = _userId;
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        String _language = getLanguage();

        if (_language == null) {
            _language = getPublication().getDefaultLanguage();
        }
        Document document;
        try {
            document = getIdentityMap().getFactory().get(getPublication(),
                    Publication.AUTHORING_AREA,
                    getDocumentId(),
                    _language);
            log(".execute(): " + document.getLanguage());
        } catch (final DocumentBuildException e) {
            throw new BuildException(e);
        }

        try {
            String[] roles = new String[0];
            Situation situation = WorkflowFactory.newInstance().buildSituation(roles,
                    getUserId(),
                    getMachineIp());
            DocumentType type = DocumentTypeBuilder.buildDocumentType(getDocumentType(),
                    getPublication());
            WorkflowFactory factory = WorkflowFactory.newInstance();
            /*
             * WorkflowInstance instance = factory.buildNewInstance(document,
             * type.getWorkflowFileName());
             * instance.getHistory().initialize(situation);
             */
        } catch (final BuildException e) {
            throw new BuildException(e);
        } catch (final DocumentTypeBuildException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Get the document-id.
     * @return the document-id
     */
    public String getDocumentId() {
        assertString(this.documentId);
        return this.documentId;
    }

    /**
     * Set the document-id.
     * @param aDocumentId the document-id
     */
    public void setDocumentId(String aDocumentId) {
        assertString(aDocumentId);
        this.documentId = aDocumentId;
    }

    /**
     * Get the document type.
     * @return the document type
     */
    public String getDocumentType() {
        assertString(this.documentType);
        return this.documentType;
    }

    /**
     * Set the document type.
     * @param aDocumentType the document type
     */
    public void setDocumentType(String aDocumentType) {
        assertString(aDocumentType);
        this.documentType = aDocumentType;
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
     * @param string the language
     */
    public void setLanguage(String string) {
        this.language = string;
    }

    private String userId = "";
    private String machineIp = "";

}