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

/* $Id: History.java 126320 2005-01-24 22:16:21Z gregor $  */

package org.apache.lenya.cms.workflow;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.impl.VersionImpl;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * The history of a workflow instance contains a list of all versions of the
 * instance. A version contains
 * </p>
 * <ul>
 * <li>the state,</li>
 * <li>the event that caused the transition (omitted in the first version),
 * </li>
 * <li>all variable assignments.</li>
 * </ul>
 */
public abstract class History {

    /**
     * <code>WORKFLOW_ATTRIBUTE</code> The workflow attribute
     */
    public static final String WORKFLOW_ATTRIBUTE = "workflow";
    /**
     * <code>HISTORY_ELEMENT</code> The history element
     */
    public static final String HISTORY_ELEMENT = "history";
    /**
     * <code>VERSION_ELEMENT</code> The version element
     */
    public static final String VERSION_ELEMENT = "version";
    /**
     * <code>STATE_ATTRIBUTE</code> The state attribute
     */
    public static final String STATE_ATTRIBUTE = "state";
    /**
     * <code>EVENT_ATTRIBUTE</code> The event attribute
     */
    public static final String EVENT_ATTRIBUTE = "event";
    /**
     * <code>VARIABLE_ELEMENT</code> The variable element
     */
    public static final String VARIABLE_ELEMENT = "variable";
    /**
     * <code>NAME_ATTRIBUTE</code> The name attribute
     */
    public static final String NAME_ATTRIBUTE = "name";
    /**
     * <code>VALUE_ATTRIBUTE</code> The value attribute
     */
    public static final String VALUE_ATTRIBUTE = "value";
    /**
     * <code>DATE_ATTRIBUTE</code> The date attribute
     */
    public static final String DATE_ATTRIBUTE = "date";

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

    private File historyFile;
    private List versions;

    /**
     * Creates a new history object for a workflow instance.
     * @param historyFile The history file.
     * @throws WorkflowException if an error occurs.
     */
    public History(File historyFile) throws WorkflowException {
        this.historyFile = historyFile;
    }

    /**
     * @return An array of version wrappers.
     */
    protected VersionWrapper[] getVersionWrappers() {

        if (this.versions == null) {
            this.versions = new ArrayList();
            if (getHistoryFile().exists()) {
                NamespaceHelper helper = getNamespaceHelper();
                Element documentElement = helper.getDocument().getDocumentElement();
                Element[] versionElements = helper.getChildren(documentElement, VERSION_ELEMENT);
                for (int i = 0; i < versionElements.length; i++) {
                    VersionWrapper version = restoreVersion(helper, versionElements[i]);
                    this.versions.add(version);
                }
            }
        }
        return (VersionWrapper[]) this.versions.toArray(new VersionWrapper[this.versions.size()]);
    }

    /**
     * @return An array of versions.
     */
    public Version[] getVersions() {
        VersionWrapper[] wrappers = getVersionWrappers();
        Version[] versions = new Version[wrappers.length];
        for (int i = 0; i < versions.length; i++) {
            versions[i] = wrappers[i].getVersion();
        }
        return versions;
    }

    /**
     * Returns the namespace helper for the history file.
     * @return A namespace helper.
     */
    protected NamespaceHelper getNamespaceHelper() {
        NamespaceHelper helper;
        try {
            if (getHistoryFile().exists()) {
                Document document = DocumentHelper.readDocument(getHistoryFile());
                helper = new NamespaceHelper(Workflow.NAMESPACE, Workflow.DEFAULT_PREFIX, document);
            } else {
                helper = new NamespaceHelper(Workflow.NAMESPACE, Workflow.DEFAULT_PREFIX,
                        HISTORY_ELEMENT);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return helper;
    }

    /**
     * Returns the workflow ID for this history.
     * @return A string.
     * @throws WorkflowException when something went wrong.
     */
    protected String getWorkflowId() throws WorkflowException {
        return getWorkflowId(getNamespaceHelper());
    }

    /**
     * Returns the workflow ID for this history.
     * @param helper The namespace helper for the history document.
     * @return A string.
     * @throws WorkflowException when something went wrong.
     */
    protected String getWorkflowId(NamespaceHelper helper) throws WorkflowException {
        String workflowId = helper.getDocument().getDocumentElement()
                .getAttribute(WORKFLOW_ATTRIBUTE);
        if (workflowId == null) {
            throw new WorkflowException("The attribute '" + WORKFLOW_ATTRIBUTE + "' is missing!");
        }
        if ("".equals(workflowId)) {
            throw new WorkflowException("The workflow ID must not be empty!");
        }
        return workflowId;
    }

    /**
     * Factory method to obtain the history file.
     * @return A file.
     */
    protected File getHistoryFile() {
        return this.historyFile;
    }

    protected VersionWrapper createVersionWrapper() {
        return new VersionWrapper();
    }

    /**
     * Saves the history.
     */
    public void save() {
        try {
            NamespaceHelper helper = new NamespaceHelper(Workflow.NAMESPACE,
                    Workflow.DEFAULT_PREFIX, HISTORY_ELEMENT);

            Element documentElement = helper.getDocument().getDocumentElement();
            VersionWrapper[] versions = getVersionWrappers();
            for (int i = 0; i < versions.length; i++) {
                Element versionElement = versions[i].getVersionElement(helper);
                documentElement.appendChild(versionElement);
            }
System.out.println("Saving file [" + getHistoryFile().getAbsolutePath() + "]");
            DocumentHelper.writeDocument(helper.getDocument(), getHistoryFile());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a new version to the history.
     * @param workflow The workflow.
     * @param version The version.
     * @param situation The situation.
     */
    public void newVersion(Workflow workflow, Version version, Situation situation) {
        VersionWrapper wrapper = createVersionWrapper();
        wrapper.initialize(workflow, version, situation);
        this.versions.add(wrapper);
    }

    /**
     * Restores a version from an XML element.
     * @param helper The namespace helper.
     * @param element An XML element.
     * @return A version.
     */
    protected VersionWrapper restoreVersion(NamespaceHelper helper, Element element) {
        if (!element.getLocalName().equals(VERSION_ELEMENT)) {
            throw new RuntimeException("Invalid history XML!");
        }

        VersionWrapper wrapper = createVersionWrapper();
        wrapper.initialize(helper, element);

        return wrapper;
    }

    /**
     * Deletes the history.
     * @throws WorkflowException when something went wrong.
     */
    public void delete() throws WorkflowException {
        if (getHistoryFile().exists()) {
            boolean deleted = getHistoryFile().delete();
            if (!deleted) {
                throw new WorkflowException("The workflow history could not be deleted!");
            }
        }
    }

    /**
     * Use subclasses of this class to store additional information for a
     * version.
     */
    public class VersionWrapper {

        private Version version;
        private Date date;
        private String[] variableNames;

        /**
         * Ctor.
         */
        public VersionWrapper() {
        }

        /**
         * @param helper The namespace helper to use.
         * @param element An XML element representing a version.
         */
        public void initialize(NamespaceHelper helper, Element element) {
            String event = element.getAttribute(EVENT_ATTRIBUTE);
            String state = element.getAttribute(STATE_ATTRIBUTE);

            this.version = new VersionImpl(event, state);
            restoreVariables(version, element);

            String dateString = element.getAttribute(DATE_ATTRIBUTE);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                this.date = format.parse(dateString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }

        /**
         * Restores the state variables of a workflow instance.
         * @param version The version to restore.
         * @param versionElement The element.
         */
        protected void restoreVariables(Version version, Element versionElement) {

            Element[] variableElements = getNamespaceHelper().getChildren(versionElement,
                    VARIABLE_ELEMENT);
            this.variableNames = new String[variableElements.length];

            for (int i = 0; i < variableElements.length; i++) {
                String name = variableElements[i].getAttribute(NAME_ATTRIBUTE);
                String value = variableElements[i].getAttribute(VALUE_ATTRIBUTE);
                version.setValue(name, Boolean.valueOf(value).booleanValue());
                this.variableNames[i] = name;
            }
        }

        /**
         * @param workflow The workflow.
         * @param version The version.
         * @param situation The situation.
         */
        public void initialize(Workflow workflow, Version version, Situation situation) {
            this.version = version;
            this.date = new GregorianCalendar().getTime();
            this.variableNames = workflow.getVariableNames();
        }

        /**
         * @return The version.
         */
        public Version getVersion() {
            return version;
        }

        /**
         * @param helper The namespace helper to use.
         * @return An XML element.
         */
        public Element getVersionElement(NamespaceHelper helper) {
            Element versionElement = helper.createElement(VERSION_ELEMENT);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = format.format(this.date);
            versionElement.setAttribute(DATE_ATTRIBUTE, dateString);

            versionElement.setAttribute(STATE_ATTRIBUTE, version.getState());
            versionElement.setAttribute(EVENT_ATTRIBUTE, version.getEvent());
            for (int i = 0; i < this.variableNames.length; i++) {
                Element variableElement = helper.createElement(VARIABLE_ELEMENT);
                variableElement.setAttribute(NAME_ATTRIBUTE, this.variableNames[i]);
                variableElement.setAttribute(VALUE_ATTRIBUTE, Boolean.toString(version
                        .getValue(this.variableNames[i])));
                versionElement.appendChild(variableElement);
            }

            return versionElement;
        }

    }

}