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

package org.apache.lenya.workflow.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.workflow.Action;
import org.apache.lenya.workflow.Condition;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility class to build a workflow schema from a file.
 */
public class WorkflowBuilder extends AbstractLogEnabled {

    /**
     * Ctor.
     * @param logger The logger to use.
     */
    public WorkflowBuilder(Logger logger) {
        ContainerUtil.enableLogging(this, logger);
    }

    /**
     * Builds a workflow schema from a file.
     * @param name The workflow name.
     * @param file The file.
     * @return A workflow schema implementation.
     * @throws WorkflowException if the file does not represent a valid workflow
     *             schema.
     */
    public WorkflowImpl buildWorkflow(String name, File file) throws WorkflowException {
        WorkflowImpl workflow;

        try {
            Document document = DocumentHelper.readDocument(file);
            workflow = buildWorkflow(name, document);
        } catch (Exception e) {
            throw new WorkflowException(e);
        }

        return workflow;
    }

    /**
     * Builds a workflow object from an XML document.
     * @param name The workflow name.
     * @param document The XML document.
     * @return A workflow implementation.
     * @throws ParserConfigurationException when something went wrong.
     * @throws SAXException when something went wrong.
     * @throws IOException when something went wrong.
     * @throws WorkflowException when something went wrong.
     */
    protected WorkflowImpl buildWorkflow(String name, Document document) throws ParserConfigurationException,
            SAXException, IOException, WorkflowException {

        Element root = document.getDocumentElement();
        StateImpl initialState = null;

        Map states = new HashMap();
        Map events = new HashMap();
        Map variables = new HashMap();

        // load states
        NodeList stateElements = root.getElementsByTagNameNS(Workflow.NAMESPACE, STATE_ELEMENT);

        for (int i = 0; i < stateElements.getLength(); i++) {
            Element element = (Element) stateElements.item(i);
            StateImpl state = buildState(element);
            String id = state.getId();
            states.put(id, state);

            if (isInitialStateElement(element)) {
                initialState = state;
            }
        }

        WorkflowImpl workflow = new WorkflowImpl(name, initialState);

        // load variables
        NodeList variableElements = root.getElementsByTagNameNS(Workflow.NAMESPACE,
                VARIABLE_ELEMENT);

        for (int i = 0; i < variableElements.getLength(); i++) {
            Element element = (Element) variableElements.item(i);
            BooleanVariableImpl variable = buildVariable(element);
            variables.put(variable.getName(), variable);
            workflow.addVariable(variable);
        }

        // load events
        NodeList eventElements = root.getElementsByTagNameNS(Workflow.NAMESPACE, EVENT_ELEMENT);

        for (int i = 0; i < eventElements.getLength(); i++) {
            EventImpl event = buildEvent((Element) eventElements.item(i));
            String id = event.getName();
            events.put(id, event);
            workflow.addEvent(event);
        }

        // load transitions
        NodeList transitionElements = root.getElementsByTagNameNS(Workflow.NAMESPACE,
                TRANSITION_ELEMENT);

        for (int i = 0; i < transitionElements.getLength(); i++) {
            TransitionImpl transition = buildTransition((Element) transitionElements.item(i),
                    states,
                    events,
                    variables);
            workflow.addTransition(transition);
        }

        return workflow;
    }

    /**
     * Checks if a state element contains the initial state.
     * @param element An XML element.
     * @return A boolean value.
     */
    protected boolean isInitialStateElement(Element element) {
        String initialAttribute = element.getAttribute(INITIAL_ATTRIBUTE);

        return (initialAttribute != null)
                && (initialAttribute.equals("yes") || initialAttribute.equals("true"));
    }

    protected static final String STATE_ELEMENT = "state";
    protected static final String TRANSITION_ELEMENT = "transition";
    protected static final String EVENT_ELEMENT = "event";
    protected static final String CONDITION_ELEMENT = "condition";
    protected static final String ACTION_ELEMENT = "action";
    protected static final String ID_ATTRIBUTE = "id";
    protected static final String INITIAL_ATTRIBUTE = "initial";
    protected static final String SOURCE_ATTRIBUTE = "source";
    protected static final String DESTINATION_ATTRIBUTE = "destination";
    protected static final String CLASS_ATTRIBUTE = "class";
    protected static final String VARIABLE_ELEMENT = "variable";
    protected static final String ASSIGNMENT_ELEMENT = "assign";
    protected static final String VARIABLE_ATTRIBUTE = "variable";
    protected static final String VALUE_ATTRIBUTE = "value";
    protected static final String NAME_ATTRIBUTE = "name";
    protected static final String SYNCHRONIZED_ATTRIBUTE = "synchronized";

    /**
     * Builds a state from an XML element.
     * @param element An XML element.
     * @return A state.
     */
    protected StateImpl buildState(Element element) {
        String id = element.getAttribute(ID_ATTRIBUTE);
        StateImpl state = new StateImpl(id);

        return state;
    }

    /**
     * Builds a transition from an XML element.
     * @param element An XML element.
     * @param states A map from state IDs to states.
     * @param events A map from event IDs to events.
     * @param variables A map from variable names to variables.
     * @return A transition.
     * @throws WorkflowException when something went wrong.
     */
    protected TransitionImpl buildTransition(Element element, Map states, Map events, Map variables)
            throws WorkflowException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Building transition");
        }

        String sourceId = element.getAttribute(SOURCE_ATTRIBUTE);
        String destinationId = element.getAttribute(DESTINATION_ATTRIBUTE);

        StateImpl source = (StateImpl) states.get(sourceId);
        StateImpl destination = (StateImpl) states.get(destinationId);

        TransitionImpl transition = new TransitionImpl(source, destination);
        ContainerUtil.enableLogging(transition, getLogger());

        // set event
        Element eventElement = (Element) element.getElementsByTagNameNS(Workflow.NAMESPACE,
                EVENT_ELEMENT).item(0);
        String id = eventElement.getAttribute(ID_ATTRIBUTE);
        Event event = (Event) events.get(id);
        transition.setEvent(event);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("    Event: [" + event + "]");
        }

        // load conditions
        NodeList conditionElements = element.getElementsByTagNameNS(Workflow.NAMESPACE,
                CONDITION_ELEMENT);

        for (int i = 0; i < conditionElements.getLength(); i++) {
            Condition condition = buildCondition((Element) conditionElements.item(i));
            transition.addCondition(condition);
        }

        // load assignments
        NodeList assignmentElements = element.getElementsByTagNameNS(Workflow.NAMESPACE,
                ASSIGNMENT_ELEMENT);

        for (int i = 0; i < assignmentElements.getLength(); i++) {
            BooleanVariableAssignmentImpl action = buildAssignment(variables,
                    (Element) assignmentElements.item(i));
            transition.addAction(action);
        }

        // load actions
        NodeList actionElements = element
                .getElementsByTagNameNS(Workflow.NAMESPACE, ACTION_ELEMENT);

        for (int i = 0; i < actionElements.getLength(); i++) {
            Action action = buildAction((Element) actionElements.item(i));
            transition.addAction(action);
        }

        // set synchronization
        if (element.hasAttribute(SYNCHRONIZED_ATTRIBUTE)) {
            Boolean isSynchronized = Boolean.valueOf(element.getAttribute(SYNCHRONIZED_ATTRIBUTE));
            transition.setSynchronized(isSynchronized.booleanValue());
        }

        return transition;
    }

    /**
     * Builds an event from an XML element.
     * @param element An XML element.
     * @return An event.
     */
    protected EventImpl buildEvent(Element element) {
        String id = element.getAttribute(ID_ATTRIBUTE);
        EventImpl event = new EventImpl(id);

        return event;
    }

    private ConditionFactory conditionFactory = null;

    /**
     * Builds a condition from an XML element.
     * @param element An XML element.
     * @return A condition.
     * @throws WorkflowException when something went wrong.
     */
    protected Condition buildCondition(Element element) throws WorkflowException {
        String className = element.getAttribute(CLASS_ATTRIBUTE);
        String expression = DocumentHelper.getSimpleElementText(element);
        if (this.conditionFactory == null) {
            this.conditionFactory = new ConditionFactory(getLogger());
        }
        Condition condition = this.conditionFactory.createCondition(className, expression);

        return condition;
    }

    /**
     * Builds an action from an XML element.
     * @param element An XML element.
     * @return An action.
     */
    protected Action buildAction(Element element) {
        String id = element.getAttribute(ID_ATTRIBUTE);
        Action action = new ActionImpl(id);

        return action;
    }

    /**
     * Builds a boolean variable from an XML element.
     * @param element An XML element.
     * @return A boolean variable.
     */
    protected BooleanVariableImpl buildVariable(Element element) {
        String name = element.getAttribute(NAME_ATTRIBUTE);
        String value = element.getAttribute(VALUE_ATTRIBUTE);

        return new BooleanVariableImpl(name, Boolean.getBoolean(value));
    }

    /**
     * Builds an assignment object from an XML element.
     * @param variables A map from variable names to variables.
     * @param element An XML element.
     * @return An assignment object.
     * @throws WorkflowException when something went wrong.
     */
    protected BooleanVariableAssignmentImpl buildAssignment(Map variables, Element element)
            throws WorkflowException {
        String variableName = element.getAttribute(VARIABLE_ATTRIBUTE);

        String valueString = element.getAttribute(VALUE_ATTRIBUTE);
        boolean value = Boolean.valueOf(valueString).booleanValue();

        BooleanVariableImpl variable = (BooleanVariableImpl) variables.get(variableName);

        return new BooleanVariableAssignmentImpl(variable, value);
    }
}