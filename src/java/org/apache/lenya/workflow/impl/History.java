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

/* $Id: History.java,v 1.25 2004/08/16 13:26:34 andreas Exp $  */

package org.apache.lenya.workflow.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.lenya.workflow.BooleanVariable;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.State;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.lenya.workflow.WorkflowListener;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * The history of a workflow instance contains a list of all versions of the instance. A version
 * contains
 * </p>
 * <ul>
 * <li>the state,</li>
 * <li>the event that caused the transition (omitted in the first version).</li>
 * </ul>
 */
public abstract class History implements WorkflowListener {
    public static final String WORKFLOW_ATTRIBUTE = "workflow";
    public static final String HISTORY_ELEMENT = "history";
    public static final String VERSION_ELEMENT = "version";
    public static final String STATE_ATTRIBUTE = "state";
    public static final String EVENT_ATTRIBUTE = "event";
    public static final String VARIABLE_ELEMENT = "variable";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String DATE_ATTRIBUTE = "date";

    /**
     * Creates a new history object. A new history file is created and initialized.
     * @param workflowId The workflow ID.
     * @param situation The current situation.
     * @throws WorkflowException when something went wrong.
     */
    public void initialize(String workflowId, Situation situation) throws WorkflowException {
        try {
            File file = getHistoryFile();
            file.getParentFile().mkdirs();
            file.createNewFile();

            NamespaceHelper helper = new NamespaceHelper(Workflow.NAMESPACE,
                    Workflow.DEFAULT_PREFIX, HISTORY_ELEMENT);

            Element historyElement = helper.getDocument().getDocumentElement();
            historyElement.setAttribute(WORKFLOW_ATTRIBUTE, workflowId);

            Element initialVersionElement = createInitialVersionElement(helper, situation,
                    workflowId);
            historyElement.appendChild(initialVersionElement);

            DocumentHelper.writeDocument(helper.getDocument(), file);
        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    /**
     * Creates a new history object for a workflow instance.
     */
    protected History() {
    }

    private WorkflowInstanceImpl instance = null;

    /**
     * Returns the namespace helper for the history file.
     * @return A namespace helper.
     * @throws WorkflowException It the helper could not be obtained.
     */
    protected NamespaceHelper getNamespaceHelper() throws WorkflowException {
        NamespaceHelper helper;
        try {
            Document document = DocumentHelper.readDocument(getHistoryFile());
            helper = new NamespaceHelper(Workflow.NAMESPACE, Workflow.DEFAULT_PREFIX, document);
        } catch (Exception e) {
            throw new WorkflowException(e);
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
        String workflowId = helper.getDocument().getDocumentElement().getAttribute(
                WORKFLOW_ATTRIBUTE);
        if (workflowId == null) {
            throw new WorkflowException("The attribute '" + WORKFLOW_ATTRIBUTE + "' is missing!");
        }
        if ("".equals(workflowId)) {
            throw new WorkflowException("The workflow ID must not be empty!");
        }
        return workflowId;
    }

    /**
     * Restores the workflow, state and variables of a workflow instance from this history.
     * @return The workflow instance to restore.
     * @throws WorkflowException if something goes wrong.
     */
    public WorkflowInstanceImpl getInstance() throws WorkflowException {
        if (this.instance == null) {
            if (!isInitialized()) {
                throw new WorkflowException(
                        "The workflow history has not been initialized: The file ["
                                + getHistoryFile() + "] is missing.");
            }

            WorkflowInstanceImpl instance = createInstance();
            NamespaceHelper helper = getNamespaceHelper();

            String workflowId = getWorkflowId(helper);
            if (null == workflowId) {
                throw new WorkflowException("No workflow attribute set in history document!");
            }

            instance.setWorkflow(workflowId);

            restoreState(instance, helper);
            restoreVariables(instance, helper);

            instance.addWorkflowListener(this);
            setInstance(instance);
        }

        return instance;
    }

    /**
     * Returns if the history has been initialized.
     * @return A boolean value.
     */
    public boolean isInitialized() {
        return getHistoryFile().exists();
    }

    /**
     * Factory method to obtain the history file.
     * @return A file.
     */
    protected abstract File getHistoryFile();

    /**
     * Factory method to create a workflow instance object.
     * @return A workflow instance object.
     * @throws WorkflowException if something goes wrong.
     */
    protected abstract WorkflowInstanceImpl createInstance() throws WorkflowException;

    /**
     * Creates a new version element. This method is called after a tansition invocation.
     * @param helper The namespace helper of the history document.
     * @param situation The current situation.
     * @param workflowId The workflow ID.
     * @return An XML element.
     * @throws WorkflowException when something went wrong.
     */
    protected Element createInitialVersionElement(NamespaceHelper helper, Situation situation,
            String workflowId) throws WorkflowException {
        Element versionElement = createVersionElement(helper, situation);

        WorkflowInstanceImpl instance = createInstance();
        instance.setWorkflow(workflowId);

        StateImpl initialState = (StateImpl) instance.getWorkflow().getInitialState();
        versionElement.setAttribute(STATE_ATTRIBUTE, initialState.getId());

        return versionElement;
    }

    /**
     * Creates a new version element. This method is called after a tansition invocation.
     * @param helper The namespace helper of the history document.
     * @param state The state of the new version.
     * @param situation The current situation.
     * @param event The event that was invoked.
     * @return An XML element.
     */
    protected Element createVersionElement(NamespaceHelper helper, StateImpl state,
            Situation situation, Event event) {
        Element versionElement = createVersionElement(helper, situation);
        versionElement.setAttribute(STATE_ATTRIBUTE, state.getId());
        versionElement.setAttribute(EVENT_ATTRIBUTE, event.getName());
        return versionElement;
    }

    /**
     * Creates a version element based on a situation.
     * @param helper The namespace helper of the history document.
     * @param situation The current situation.
     * @return An XML element.
     */
    protected Element createVersionElement(NamespaceHelper helper, Situation situation) {
        Element versionElement = helper.createElement(VERSION_ELEMENT);
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(now);
        versionElement.setAttribute(DATE_ATTRIBUTE, dateString);
        return versionElement;
    }

    /**
     * Advances the workflow history when a tranistion was invoked.
     * 
     * @param instance The workflow instance.
     * @param situation The situation before the transition.
     * @param event The invoked event.
     * 
     * @throws WorkflowException when something went wrong.
     */
    public void transitionFired(WorkflowInstance instance, Situation situation, Event event)
            throws WorkflowException {
        try {
            org.w3c.dom.Document xmlDocument = DocumentHelper.readDocument(getHistoryFile());
            Element root = xmlDocument.getDocumentElement();

            NamespaceHelper helper = new NamespaceHelper(Workflow.NAMESPACE,
                    Workflow.DEFAULT_PREFIX, xmlDocument);

            Element versionElement = createVersionElement(helper, (StateImpl) instance
                    .getCurrentState(), situation, event);

            root.appendChild(versionElement);

            saveVariables(helper);

            DocumentHelper.writeDocument(xmlDocument, getHistoryFile());
        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    /**
     * Sets the workflow instance.
     * @param impl A workflow instance implementation.
     */
    public void setInstance(WorkflowInstanceImpl impl) {
        instance = impl;
    }

    /**
     * Saves the state variables as children of the document element.
     * @param helper The helper that holds the document.
     * @throws WorkflowException when something went wrong.
     */
    protected void saveVariables(NamespaceHelper helper) throws WorkflowException {
        Element parent = helper.getDocument().getDocumentElement();
        BooleanVariable[] variables = getInstance().getWorkflowImpl().getVariables();

        for (int i = 0; i < variables.length; i++) {
            String name = variables[i].getName();
            boolean value = getInstance().getValue(name);

            try {
                Element element = (Element) XPathAPI.selectSingleNode(parent, "*[local-name() = '"
                        + VARIABLE_ELEMENT + "']" + "[@" + NAME_ATTRIBUTE + " = '" + name + "']");

                if (element == null) {
                    element = helper.createElement(VARIABLE_ELEMENT);
                    element.setAttribute(NAME_ATTRIBUTE, name);
                    parent.appendChild(element);
                }

                element.setAttribute(VALUE_ATTRIBUTE, Boolean.toString(value));
            } catch (TransformerException e) {
                throw new WorkflowException(e);
            }
        }
    }

    /**
     * Restores the state variables of a workflow instance.
     * @param instance The instance to restore.
     * @param helper The helper that wraps the history document.
     * @throws WorkflowException when something went wrong.
     */
    protected void restoreVariables(WorkflowInstanceImpl instance, NamespaceHelper helper)
            throws WorkflowException {
        Element parent = helper.getDocument().getDocumentElement();

        Element[] variableElements = helper.getChildren(parent, VARIABLE_ELEMENT);

        for (int i = 0; i < variableElements.length; i++) {
            String name = variableElements[i].getAttribute(NAME_ATTRIBUTE);
            String value = variableElements[i].getAttribute(VALUE_ATTRIBUTE);
            instance.setValue(name, Boolean.valueOf(value).booleanValue());
        }
    }

    /**
     * Restores the state of a workflow instance.
     * @param instance The instance to restore.
     * @param helper The helper that wraps the history document.
     * @throws WorkflowException when something went wrong.
     */
    protected void restoreState(WorkflowInstanceImpl instance, NamespaceHelper helper)
            throws WorkflowException {
        State state;
        Element[] versionElements = helper.getChildren(helper.getDocument().getDocumentElement(),
                VERSION_ELEMENT);

        if (versionElements.length > 0) {
            Element lastElement = versionElements[versionElements.length - 1];
            String stateId = lastElement.getAttribute(STATE_ATTRIBUTE);
            state = instance.getWorkflowImpl().getState(stateId);
        } else {
            state = instance.getWorkflow().getInitialState();
        }

        instance.setCurrentState(state);
    }

    /**
     * Moves this history to a new file.
     * @param newFile The new file.
     * @throws WorkflowException when something went wrong.
     */
    protected void move(File newFile) throws WorkflowException {

        FileInputStream sourceStream = null;
        FileOutputStream destinationStream = null;
        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;
        try {
            newFile.getParentFile().mkdirs();
            newFile.createNewFile();
            File historyFile = getHistoryFile();

            sourceStream = new FileInputStream(historyFile);
            sourceChannel = sourceStream.getChannel();

            destinationStream = new FileOutputStream(newFile);
            destinationChannel = destinationStream.getChannel();

            destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            File directory = historyFile.getParentFile();
            boolean deleted = historyFile.delete();
            if (!deleted) {
                throw new WorkflowException("The old history file could not be deleted!");
            }
            if (directory.exists() && directory.isDirectory() && directory.listFiles().length == 0) {
                directory.delete();
            }
        } catch (IOException e) {
            throw new WorkflowException(e);
        } finally {
            try {
                if (sourceStream != null)
                    sourceStream.close();
                if (destinationStream != null)
                    destinationStream.close();
            } catch (IOException e) {
                throw new WorkflowException(e);
            }
        }
    }

    /**
     * Restores a version from an XML element.
     * @param helper The namespace helper.
     * @param element An XML element.
     * @return A version.
     * @throws WorkflowException when something went wrong.
     */
    protected Version restoreVersion(NamespaceHelper helper, Element element)
            throws WorkflowException {
        if (!element.getLocalName().equals(VERSION_ELEMENT)) {
            throw new WorkflowException("Invalid history XML!");
        }

        Event event = null;
        String eventId = element.getAttribute(EVENT_ATTRIBUTE);
        if (eventId != null && !"".equals(eventId)) {
            event = getInstance().getWorkflowImpl().getEvent(eventId);
        }

        String stateId = element.getAttribute(STATE_ATTRIBUTE);
        State state = getInstance().getWorkflowImpl().getState(stateId);

        Version version = new Version(event, state);
        return version;
    }

    /**
     * Returns the versions of this history.
     * @return An array of versions.
     * @throws WorkflowException when something went wrong.
     */
    public Version[] getVersions() throws WorkflowException {
        List versions = new ArrayList();

        NamespaceHelper helper = getNamespaceHelper();
        Element documentElement = helper.getDocument().getDocumentElement();
        Element[] versionElements = getNamespaceHelper().getChildren(documentElement,
                VERSION_ELEMENT);

        for (int i = 0; i < versionElements.length; i++) {
            Version version = restoreVersion(helper, versionElements[i]);
            versions.add(version);
        }
        return (Version[]) versions.toArray(new Version[versions.size()]);
    }

    /**
     * Deletes the history.
     * @throws WorkflowException when something went wrong.
     */
    public void delete() throws WorkflowException {
        System.out.println("Deleting file [" + getHistoryFile() + "]");
        if (!isInitialized()) {
            throw new WorkflowException("The workflow history is not initialized!");
        }

        boolean deleted = getHistoryFile().delete();

        if (!deleted) {
            throw new WorkflowException("The workflow history could not be deleted!");
        }

    }

}