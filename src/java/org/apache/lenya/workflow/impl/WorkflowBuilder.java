/*
 * WorkflowBuilder.java
 *
 * Created on 8. April 2003, 18:09
 */

package org.apache.lenya.workflow.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.lenya.workflow.Action;
import org.apache.lenya.workflow.Condition;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author  andreas
 */
public class WorkflowBuilder {
	
	protected WorkflowBuilder() {
	}

    public static final String NAMESPACE = "http://apache.org/cocoon/lenya/workflow/1.0";
    public static final String DEFAULT_PREFIX = "wf";

    public static Workflow buildWorkflow(File file) throws WorkflowException {
        Workflow workflow;

        try {
            Document document = DocumentHelper.readDocument(file);
            workflow = buildWorkflow(document);
        } catch (Exception e) {
            throw new WorkflowException(e);
        }

        return workflow;
    }

    public static Workflow buildWorkflow(Document document)
        throws
            ParserConfigurationException,
            SAXException,
            IOException,
            WorkflowException {

        NamespaceHelper helper =
            new NamespaceHelper(NAMESPACE, DEFAULT_PREFIX, document);

        Element root = document.getDocumentElement();
        StateImpl initialState = null;

        Map states = new HashMap();
        Map events = new HashMap();
        Map variables = new HashMap();

        // load states
        NodeList stateElements =
            root.getElementsByTagNameNS(NAMESPACE, STATE_ELEMENT);
        for (int i = 0; i < stateElements.getLength(); i++) {
            Element element = (Element) stateElements.item(i);
            StateImpl state = buildState(element);
            String id = state.getId();
            states.put(id, state);
            if (isInitialStateElement(element)) {
                initialState = state;
            }
        }

        assert initialState != null;
        WorkflowImpl workflow = new WorkflowImpl(initialState);

        // load variables
        NodeList variableElements =
            root.getElementsByTagNameNS(NAMESPACE, VARIABLE_ELEMENT);
        for (int i = 0; i < variableElements.getLength(); i++) {
            Element element = (Element) variableElements.item(i);
            BooleanVariableImpl variable = buildVariable(element);
            variables.put(variable.getName(), variable);
            workflow.addVariable(variable);
        }

        // load events
        NodeList eventElements =
            root.getElementsByTagNameNS(NAMESPACE, EVENT_ELEMENT);
        for (int i = 0; i < eventElements.getLength(); i++) {
            EventImpl event = buildEvent((Element) eventElements.item(i));
            String id = event.getName();
            events.put(id, event);
        }

        // load transitions
        NodeList transitionElements =
            root.getElementsByTagNameNS(NAMESPACE, TRANSITION_ELEMENT);
        for (int i = 0; i < transitionElements.getLength(); i++) {
            TransitionImpl transition =
                buildTransition(
                    (Element) transitionElements.item(i),
                    states,
                    events,
                    variables);
            workflow.addTransition(transition);
        }

        return workflow;
    }

    protected static boolean isInitialStateElement(Element element) {
        assert element.getLocalName().equals(STATE_ELEMENT);
        String initialAttribute = element.getAttribute(INITIAL_ATTRIBUTE);
        return initialAttribute != null
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

    protected static StateImpl buildState(Element element) {
        assert element.getLocalName().equals(STATE_ELEMENT);

        String id = element.getAttribute(ID_ATTRIBUTE);
        StateImpl state = new StateImpl(id);
        return state;
    }

    protected static TransitionImpl buildTransition(
        Element element,
        Map states,
        Map events,
        Map variables)
        throws WorkflowException {
        assert element.getLocalName().equals(TRANSITION_ELEMENT);

        String sourceId = element.getAttribute(SOURCE_ATTRIBUTE);
        String destinationId = element.getAttribute(DESTINATION_ATTRIBUTE);

        assert sourceId != null;
        assert destinationId != null;

        StateImpl source = (StateImpl) states.get(sourceId);
        StateImpl destination = (StateImpl) states.get(destinationId);

        assert source != null;
        assert destination != null;

        TransitionImpl transition = new TransitionImpl(source, destination);

        // set event
        Element eventElement =
            (Element) element.getElementsByTagNameNS(
                NAMESPACE,
                EVENT_ELEMENT).item(
                0);
        String id = eventElement.getAttribute(ID_ATTRIBUTE);
        assert id != null;

        Event event = (Event) events.get(id);
        assert event != null;

        transition.setEvent(event);

        // load conditions
        NodeList conditionElements =
            element.getElementsByTagNameNS(NAMESPACE, CONDITION_ELEMENT);
        for (int i = 0; i < conditionElements.getLength(); i++) {
            Condition condition =
                buildCondition((Element) conditionElements.item(i));
            transition.addCondition(condition);
        }

        // load assignments
        NodeList assignmentElements =
            element.getElementsByTagNameNS(NAMESPACE, ASSIGNMENT_ELEMENT);
        for (int i = 0; i < assignmentElements.getLength(); i++) {
            BooleanVariableAssignmentImpl action = buildAssignment(variables, (Element) assignmentElements.item(i));
            transition.addAction(action);
        }

        // load actions
        NodeList actionElements =
            element.getElementsByTagNameNS(NAMESPACE, ACTION_ELEMENT);
        for (int i = 0; i < actionElements.getLength(); i++) {
            Action action = buildAction((Element) actionElements.item(i));
            transition.addAction(action);
        }

        return transition;
    }

    protected static EventImpl buildEvent(Element element) {
        String id = element.getAttribute(ID_ATTRIBUTE);
        assert id != null;
        EventImpl event = new EventImpl(id);
        return event;
    }

    protected static Condition buildCondition(Element element)
        throws WorkflowException {
        String className = element.getAttribute(CLASS_ATTRIBUTE);
        String expression = DocumentHelper.getSimpleElementText(element);
        Condition condition =
            ConditionFactory.createCondition(className, expression);
        return condition;
    }

    protected static Action buildAction(Element element) {
        String id = element.getAttribute(ID_ATTRIBUTE);
        Action action = new ActionImpl(id);
        return action;
    }
    
    protected static BooleanVariableImpl buildVariable(Element element) {
        String name = element.getAttribute(NAME_ATTRIBUTE);
        String value = element.getAttribute(VALUE_ATTRIBUTE);
        return new BooleanVariableImpl(name, Boolean.getBoolean(value));
    }
    
    protected static BooleanVariableAssignmentImpl buildAssignment(Map variables, Element element) throws WorkflowException {
        String variableName = element.getAttribute(VARIABLE_ATTRIBUTE);
        
        String valueString = element.getAttribute(VALUE_ATTRIBUTE);
        boolean value = Boolean.valueOf(valueString).booleanValue();
        
        BooleanVariableImpl variable = (BooleanVariableImpl) variables.get(variableName);
        return new BooleanVariableAssignmentImpl(variable, value);
    }

}
