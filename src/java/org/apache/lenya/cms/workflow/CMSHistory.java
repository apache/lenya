/*
$Id: CMSHistory.java,v 1.14 2003/09/01 17:01:48 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.workflow;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdToPathMapper;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.impl.History;
import org.apache.lenya.workflow.impl.Version;
import org.apache.lenya.workflow.impl.WorkflowInstanceImpl;
import org.apache.lenya.xml.NamespaceHelper;

import org.w3c.dom.Element;

import java.io.File;


/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CMSHistory extends History {
    public static final String HISTORY_PATH = "content/workflow/history".replace('/', File.separatorChar);

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
	 *  (non-Javadoc)
	 * @see org.apache.lenya.workflow.impl.History#getHistoryFile()
	 */
    protected File getHistoryFile() {
        return getHistoryFile(getDocument());
    }

    /**
     *  (non-Javadoc)
     * @see org.apache.lenya.workflow.impl.History#getHistoryFile()
     */
    protected File getHistoryFile(Document document) {
        File historyDirectory = new File(document.getPublication().getDirectory(), HISTORY_PATH);
        DocumentIdToPathMapper pathMapper = document.getPublication().getPathMapper();
        String documentPath = pathMapper.getPath(document.getId(), document.getLanguage()); 
        File historyFile = new File(historyDirectory, document.getArea() + File.separator + documentPath);
        return historyFile;
    }

    /** (non-Javadoc)
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
    protected Version restoreVersion(NamespaceHelper helper, Element element) throws WorkflowException {
        Version version = super.restoreVersion(helper, element);
        CMSVersion cmsVersion = new CMSVersion(version.getEvent(), version.getState());
        
        Element identityElement = helper.getFirstChild(element, IDENTITY_ELEMENT);
        Element userElement = helper.getFirstChild(identityElement, USER_ELEMENT);
        String userId = userElement.getAttribute(ID_ATTRIBUTE);
        cmsVersion.setUserId(userId);
        
        return cmsVersion;
    }

}
