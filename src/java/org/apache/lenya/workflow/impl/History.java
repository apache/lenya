/*
 * $Id
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.workflow.impl;

import java.io.File;

import javax.xml.transform.TransformerException;

import org.apache.lenya.cms.workflow.CMSSituation;
import org.apache.lenya.workflow.BooleanVariable;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.State;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.lenya.workflow.WorkflowListener;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class History implements WorkflowListener {

    public static final String WORKFLOW_ATTRIBUTE = "workflow";
    public static final String HISTORY_ELEMENT = "history";
    public static final String VERSION_ELEMENT = "version";
    public static final String STATE_ATTRIBUTE = "state";
    public static final String USER_ATTRIBUTE = "user";
    public static final String EVENT_ATTRIBUTE = "event";
    public static final String VARIABLE_ELEMENT = "variable";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String VALUE_ATTRIBUTE = "value";

    /**
     * Creates a new history object. A new history file is created and initialized. 
     * @param file The history file.
     * @param workflowFileName The workflow reference.
     */
    public void initialize(String workflowId) throws WorkflowException {
        try {
            File file = getHistoryFile();
            file.getParentFile().mkdirs();
            file.createNewFile();
            
            NamespaceHelper helper =
                new NamespaceHelper(
                    WorkflowBuilder.NAMESPACE,
                    WorkflowBuilder.DEFAULT_PREFIX,
                    HISTORY_ELEMENT);

            Element historyElement = helper.getDocument().getDocumentElement();
            historyElement.setAttribute(WORKFLOW_ATTRIBUTE, workflowId);
            createVariableElements(helper);
            saveVariables(helper);

            DocumentHelper.writeDocument(helper.getDocument(), file);
        } catch (Exception e) {
            throw new WorkflowException(e);
        }

    }

    /**
     * Creates a new history object for a workflow instance. 
     * @param instance
     * @param file
     * @param x
     * @throws WorkflowException
     */
    protected History() {
    }
    
    private WorkflowInstanceImpl instance = null;
    
    
    
    /**
     * Restores the workflow, state and variables of a workflow instance from this history.
     * @param instance The workflow instance to restore.
     * @throws WorkflowException if something goes wrong.
     */
    public WorkflowInstanceImpl getInstance() throws WorkflowException {
        
        if (this.instance == null) {
            
            if (!isInitialized()) {
                throw new WorkflowException(
                    "The workflow history has not been initialized!");
            }
            
            WorkflowInstanceImpl instance = createInstance(); 
            NamespaceHelper helper;
            String workflowId;
            try {
                Document document = DocumentHelper.readDocument(getHistoryFile());
                helper =
                    new NamespaceHelper(
                        WorkflowBuilder.NAMESPACE,
                        WorkflowBuilder.DEFAULT_PREFIX,
                        document);
            } catch (Exception e) {
                throw new WorkflowException(e);
            }

            workflowId = helper.getDocument().getDocumentElement().getAttribute(WORKFLOW_ATTRIBUTE);
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
     */
    protected abstract WorkflowInstanceImpl createInstance() throws WorkflowException;

    protected Element createVersionElement(
        NamespaceHelper helper,
        StateImpl state,
        Situation situation,
        Event event) {
            Element versionElement = helper.createElement(VERSION_ELEMENT);
        versionElement.setAttribute(STATE_ATTRIBUTE, state.getId());
        versionElement.setAttribute(EVENT_ATTRIBUTE, event.getName());
        return versionElement;
    }

    public void transitionFired(WorkflowInstance instance, Situation situation, Event event)
        throws WorkflowException {

        try {
            org.w3c.dom.Document xmlDocument = DocumentHelper.readDocument(getHistoryFile());
            Element root = xmlDocument.getDocumentElement();

            NamespaceHelper helper =
                new NamespaceHelper(
                    WorkflowBuilder.NAMESPACE,
                    WorkflowBuilder.DEFAULT_PREFIX,
                    xmlDocument);

            CMSSituation cmsSituation = (CMSSituation) situation;
            Element versionElement =
                createVersionElement(helper, (StateImpl) instance.getCurrentState(), situation, event);

            root.appendChild(versionElement);
            
            saveVariables(helper);
            
            DocumentHelper.writeDocument(xmlDocument, getHistoryFile());

        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    /**
     * @param impl
     */
    public void setInstance(WorkflowInstanceImpl impl) {
        instance = impl;
    }
    
    /**
     * Saves the state variables as children of the document element.
     * @param helper The helper that holds the document.
     */
    protected void saveVariables(NamespaceHelper helper) throws WorkflowException {
        Element parent = helper.getDocument().getDocumentElement();
        BooleanVariable variables[] = getInstance().getWorkflowImpl().getVariables();
        for (int i = 0; i < variables.length; i++) {
            String name = variables[i].getName();
            boolean value = getInstance().getValue(name);
            try {
                Element element = (Element) XPathAPI.selectSingleNode(parent,
                    "*[local-name() = '" + VARIABLE_ELEMENT + "']" +
                    "[@" + NAME_ATTRIBUTE + " = '" + name + "']");
                if (element == null) {
                    throw new WorkflowException("Variable element for variable '" + name + "' not found!");
                }
                element.setAttribute(VALUE_ATTRIBUTE, Boolean.toString(value));
                
            } catch (TransformerException e) {
                throw new WorkflowException(e);
            }
        }
    }
    
    protected void createVariableElements(NamespaceHelper helper) throws WorkflowException {
        Element parent = helper.getDocument().getDocumentElement();
        BooleanVariable variables[] = getInstance().getWorkflowImpl().getVariables();
        for (int i = 0; i < variables.length; i++) {
            Element element = helper.createElement(VARIABLE_ELEMENT);
            element.setAttribute(NAME_ATTRIBUTE, variables[i].getName());
            parent.appendChild(element);
        }
    }
    
    /**
     * Restores the state variables of a workflow instance.
     * @param instance The instance to restore.
     * @param helper The helper that wraps the history document.
     * @throws WorkflowException
     */
    protected void restoreVariables(WorkflowInstanceImpl instance, NamespaceHelper helper) throws WorkflowException {
        Element parent = helper.getDocument().getDocumentElement();
        
        Element variableElements[] = helper.getChildren(parent, VARIABLE_ELEMENT);
        for (int i = 0; i < variableElements.length; i++) {
            String name = variableElements[i].getAttribute(NAME_ATTRIBUTE);
            String value = variableElements[i].getAttribute(VALUE_ATTRIBUTE);
            instance.setValue(name, new Boolean(value).booleanValue());
        }
    }
    
    /**
     * Restores the state of a workflow instance.
     * @param instance The instance to restore.
     * @param helper The helper that wraps the history document.
     * @throws WorkflowException
     */
    protected void restoreState(WorkflowInstanceImpl instance, NamespaceHelper helper) throws WorkflowException {
        State state;
        Element versionElements[] =
            helper.getChildren(helper.getDocument().getDocumentElement(), VERSION_ELEMENT);
        if (versionElements.length > 0) {
            Element lastElement = versionElements[versionElements.length - 1];
            String stateId = lastElement.getAttribute(STATE_ATTRIBUTE);
            state = instance.getWorkflowImpl().getState(stateId);
        }
        else {
            state = instance.getWorkflow().getInitialState();
        }
        instance.setCurrentState(state);
    }
    
}
