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

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 * The CMS history
 */
public class CMSHistory extends History {
    private Document document;

    /**
     * <code>HISTORY_PATH</code> The path to the workflow history
     */
    public static final String HISTORY_PATH = "content/workflow/history";
    
    /**
     * Ctor.
     * @param document The document.
     * @param file The history file.
     * @throws WorkflowException if an error occurs.
     */
    public CMSHistory(Document document, File file) throws WorkflowException {
        super(file);
        this.document = document;
    }

    /**
     * Additionally to deleting the workflow history, the parent directories are
     * deleted up to the workflow history directory.
     * @see org.apache.lenya.cms.workflow.History#delete()
     */
    public void delete() throws WorkflowException {
        super.delete();

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

    /**
     * CMS-specific version wrapper.
     */
    public class CMSVersionWrapper extends VersionWrapper {

        private String userId;
        private String machineIp;

        /**
         * @see org.apache.lenya.cms.workflow.History.VersionWrapper#getVersionElement(org.apache.lenya.xml.NamespaceHelper)
         */
        public Element getVersionElement(NamespaceHelper helper) {
            Element element = super.getVersionElement(helper);

            Element identityElement = helper.createElement(IDENTITY_ELEMENT);
            if (this.userId != null) {
                identityElement.appendChild(generateUserElement(helper, this.userId));
            }

            if (this.machineIp != null) {
                identityElement.appendChild(generateMachineElement(helper, this.machineIp));
            }
            element.appendChild(identityElement);
            return element;
        }

        /**
         * @see org.apache.lenya.cms.workflow.History.VersionWrapper#initialize(org.apache.lenya.xml.NamespaceHelper,
         *      org.w3c.dom.Element)
         */
        public void initialize(NamespaceHelper helper, Element element) {
            super.initialize(helper, element);

            Element identityElement = helper.getFirstChild(element, IDENTITY_ELEMENT);
            Element userElement = helper.getFirstChild(identityElement, USER_ELEMENT);
            if (userElement != null) {
                this.userId = userElement.getAttribute(ID_ATTRIBUTE);
            }
            Element machineElement = helper.getFirstChild(identityElement, MACHINE_ELEMENT);
            if (machineElement != null) {
                this.machineIp = machineElement.getAttribute(IP_ATTRIBUTE);
            }
        }

        /**
         * @see org.apache.lenya.cms.workflow.History.VersionWrapper#initialize(org.apache.lenya.workflow.Workflow,
         *      org.apache.lenya.workflow.Version,
         *      org.apache.lenya.workflow.Situation)
         */
        public void initialize(Workflow workflow, Version version, Situation situation) {
            super.initialize(workflow, version, situation);
            CMSSituation cmsSituation = (CMSSituation) situation;
            this.userId = cmsSituation.getUserId();
            this.machineIp = cmsSituation.getMachineIp();
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

    }

}