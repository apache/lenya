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

/* $Id: CMSHistory.java,v 1.24 2004/03/01 16:18:16 gregor Exp $  */

package org.apache.lenya.cms.workflow;

import java.io.File;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdToPathMapper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.impl.History;
import org.apache.lenya.workflow.impl.Version;
import org.apache.lenya.workflow.impl.WorkflowInstanceImpl;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

public class CMSHistory extends History {
    public static final String HISTORY_PATH = "content/workflow/history";

    /**
     * Creates a new CMSHistory object.
     * 
     * @param document the document to which the CMSHistory is attached
     */
    protected CMSHistory(Document document) {
        setDocument(document);
    }

    private Document document;

    public static final String IDENTITY_ELEMENT = "identity";
    public static final String USER_ELEMENT = "user";
    public static final String MACHINE_ELEMENT = "machine";
    public static final String ID_ATTRIBUTE = "id";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String IP_ATTRIBUTE = "ip-address";

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.workflow.History#createVersionElement(org.apache.lenya.xml.NamespaceHelper, org.apache.lenya.workflow.Situation)
     */
    protected Element createVersionElement(NamespaceHelper helper, Situation situation) {
        Element element = super.createVersionElement(helper, situation);

        CMSSituation cmsSituation = (CMSSituation) situation;

        Element identityElement = helper.createElement(IDENTITY_ELEMENT);
        element.appendChild(identityElement);

        String userId = cmsSituation.getUserId();
        if (userId != null) {
            identityElement.appendChild(generateUserElement(helper, userId));
        }

        String machineIp = cmsSituation.getMachineIp();
        if (machineIp != null) {
            identityElement.appendChild(generateMachineElement(helper, machineIp));
        }

        return element;
    }

    /**
     * Creates an XML element describing the user.
     * @param helper The namespace helper of the document.
     * @param userId The user ID.
     * @return An XML element.
     */
    protected Element generateUserElement(NamespaceHelper helper, String userId) {
        Element userElement = null;
        userElement = helper.createElement(USER_ELEMENT);
        userElement.setAttribute(ID_ATTRIBUTE, userId);
        return userElement;
    }

    /**
     * Creates an XML element describing the machine.
     * @param helper The namespace helper of the document.
     * @param machineIp The machine IP address.
     * @return An XML element.
     */
    protected Element generateMachineElement(NamespaceHelper helper, String machineIp) {
        Element machineElement = null;
        machineElement = helper.createElement(MACHINE_ELEMENT);
        machineElement.setAttribute(IP_ATTRIBUTE, machineIp);
        return machineElement;
    }

    /**
     * Returns the path of the history file inside the publication directory.
     * @param document A CMS document.
     * @return A string.
     */
    public String getHistoryPath(Document document) {
        DocumentIdToPathMapper pathMapper = document.getPublication().getPathMapper();
        String documentPath = pathMapper.getPath(document.getId(), document.getLanguage());

        String area = document.getArea();
        if (!area.equals(Publication.ARCHIVE_AREA) && !area.equals(Publication.TRASH_AREA)) {
            area = Publication.AUTHORING_AREA;
        }

        String path = HISTORY_PATH + "/" + area + "/" + documentPath;
        path = path.replace('/', File.separatorChar);
        return path;
    }

    /**
     * @see org.apache.lenya.workflow.impl.History#getHistoryFile()
     */
    protected File getHistoryFile() {
        return getHistoryFile(getDocument());
    }

    /**
     * @see org.apache.lenya.workflow.impl.History#getHistoryFile()
     */
    protected File getHistoryFile(Document document) {
        File historyFile =
            new File(document.getPublication().getDirectory(), getHistoryPath(document));
        return historyFile;
    }

    /**
     * @see org.apache.lenya.workflow.impl.History#createInstance()
     */
    protected WorkflowInstanceImpl createInstance() throws WorkflowException {
        return new WorkflowDocument(getDocument());
    }

    /**
     * Get the document
     * 
     * @return the Document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Set the document
     * 
     * @param document the document
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * Initializes the workflow history of another document using the same
     * workflow schema like this history.
     * @param newDocument The document to initialize the history for.
     * @param situation The current situation.
     * @throws WorkflowException when something went wrong.
     */
    protected void initialize(Document newDocument, Situation situation) throws WorkflowException {
        String workflowId = getWorkflowId();
        CMSHistory newHistory = new CMSHistory(newDocument);
        newHistory.initialize(workflowId, situation);
    }

    /**
     * Moves this history to a new document.
     * @param newDocument The new document.
     * @throws WorkflowException when something went wrong.
     */
    protected void move(Document newDocument) throws WorkflowException {
        assert newDocument != null;
        move(getHistoryFile(newDocument));
        setDocument(newDocument);
    }

    /**
     * @see org.apache.lenya.workflow.impl.History#restoreVersion(NamespaceHelper, org.w3c.dom.Element)
     */
    protected Version restoreVersion(NamespaceHelper helper, Element element)
        throws WorkflowException {
        Version version = super.restoreVersion(helper, element);
        CMSVersion cmsVersion = new CMSVersion(version.getEvent(), version.getState());

        Element identityElement = helper.getFirstChild(element, IDENTITY_ELEMENT);
        Element userElement = helper.getFirstChild(identityElement, USER_ELEMENT);
        if (userElement != null) {
            String userId = userElement.getAttribute(ID_ATTRIBUTE);
            cmsVersion.setUserId(userId);
        }

        return cmsVersion;
    }

    /**
     * Returns the history path of this history.
     * @return A string.
     */
    public String getHistoryPath() {
        return getHistoryPath(getDocument());
    }

}
