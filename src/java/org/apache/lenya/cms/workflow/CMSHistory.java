/*
 * Copyright 1999-2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *  
 */

/* $Id$ */

package org.apache.lenya.cms.workflow;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdToPathMapper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.workflow.History;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.impl.HistoryImpl;
import org.apache.lenya.workflow.impl.Version;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 * The CMS history
 */
public class CMSHistory extends HistoryImpl {
    /**
     * <code>HISTORY_PATH</code> The path to the workflow history
     */
    public static final String HISTORY_PATH = "content/workflow/history";
    /**
     * <code>IDENTITY_ELEMENT</code> The identity element
     */
    public static final String IDENTITY_ELEMENT = "identity";
    /**
     * <code>USER_ELEMENT</code> The user element
     */
    public static final String USER_ELEMENT = "user";
    /**
     * <code>MACHINE_ELEMENT</code> The machine element
     */
    public static final String MACHINE_ELEMENT = "machine";
    /**
     * <code>ID_ATTRIBUTE</code> The id attribute
     */
    public static final String ID_ATTRIBUTE = "id";
    /**
     * <code>IP_ATTRIBUTE</code> The IP attribute
     */
    public static final String IP_ATTRIBUTE = "ip-address";


    /**
     * Creates a new CMSHistory object.
     * 
     * @param workflowDocument the document to which the CMSHistory is attached
     * @throws WorkflowException if an error occurs.
     */
    protected CMSHistory(WorkflowDocument workflowDocument) throws WorkflowException {
        super(workflowDocument);
    }

    /**
     * @see org.apache.lenya.workflow.impl.HistoryImpl#createVersionElement(org.apache.lenya.xml.NamespaceHelper,
     *      org.apache.lenya.workflow.Situation)
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
     * @return A string.
     */
    public String getHistoryPath() {
        
        Document document = ((WorkflowDocument) getInstance()).getDocument();
        
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
     * @see org.apache.lenya.workflow.impl.HistoryImpl#getHistoryFile()
     */
    protected File getHistoryFile() {
        WorkflowDocument document = (WorkflowDocument) getInstance();
        File historyFile = new File(document.getDocument().getPublication().getDirectory(),
                getHistoryPath());
        return historyFile;
    }

    /**
     * Initializes the workflow history of another document using the same
     * workflow schema like this history.
     * @param newDocument The document to initialize the history for.
     * @param situation The current situation.
     * @throws WorkflowException when something went wrong.
     */
    protected void initialize(WorkflowDocument newDocument, Situation situation)
            throws WorkflowException {
        CMSHistory newHistory = new CMSHistory(newDocument);
        newHistory.initialize(situation);
    }

    /**
     * @see org.apache.lenya.workflow.History#replaceWith(org.apache.lenya.workflow.History)
     */
    public void replaceWith(History otherHistory) throws WorkflowException {
        CMSHistory history = (CMSHistory) otherHistory;
        history.copy(getHistoryFile());
    }

    /**
     * @see org.apache.lenya.workflow.impl.HistoryImpl#restoreVersion(NamespaceHelper,
     *      org.w3c.dom.Element)
     */
    protected Version restoreVersion(NamespaceHelper helper, Element element)
            throws WorkflowException {
        Version version = super.restoreVersion(helper, element);
        CMSVersion cmsVersion = new CMSVersion(version.getEvent(), version.getState());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = df.parse(element.getAttribute(DATE_ATTRIBUTE));
        } catch (final ParseException e) {
            throw new WorkflowException("Could not parse workflow date: " +e.toString());
        }
        cmsVersion.setDate(date);

        Element identityElement = helper.getFirstChild(element, IDENTITY_ELEMENT);
        Element userElement = helper.getFirstChild(identityElement, USER_ELEMENT);
        if (userElement != null) {
            String userId = userElement.getAttribute(ID_ATTRIBUTE);
            cmsVersion.setUserId(userId);
        }

        return cmsVersion;
    }

    /**
     * Additionally to deleting the workflow history, the parent directories are
     * deleted up to the workflow history directory.
     * @see org.apache.lenya.workflow.impl.HistoryImpl#delete()
     */
    public void delete() throws WorkflowException {
        super.delete();

        Document document = ((WorkflowDocument) getInstance()).getDocument();
        File stopDirectory = new File(document.getPublication().getDirectory(), HISTORY_PATH);
        if (!stopDirectory.isDirectory())
            throw new WorkflowException("Stop dir '" + stopDirectory.getAbsolutePath()
                    + "' is not a directory");
        if (!getHistoryFile().getAbsolutePath().startsWith(stopDirectory.getAbsolutePath()))
            throw new WorkflowException("Start dir '" + getHistoryFile().getAbsolutePath()
                    + "' is not a descending sibling of stop directory '"
                    + stopDirectory.getAbsolutePath() + "'.");

        File parent = getHistoryFile().getParentFile();

        while (!parent.equals(stopDirectory) && parent.delete())
            parent = parent.getParentFile();
    }
}