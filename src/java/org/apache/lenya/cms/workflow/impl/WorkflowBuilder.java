/*
 * WorkflowBuilder.java
 *
 * Created on 8. April 2003, 18:09
 */

package org.apache.lenya.cms.workflow.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.lenya.cms.workflow.Action;
import org.apache.lenya.cms.workflow.Condition;
import org.apache.lenya.cms.workflow.Event;
import org.apache.lenya.cms.workflow.State;
import org.apache.lenya.cms.workflow.Workflow;
import org.apache.lenya.cms.workflow.WorkflowBuildException;
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

    /** Creates a new instance of WorkflowBuilder */
    public WorkflowBuilder() {
    }

    public static final String NAMESPACE = "http://apache.org/cocoon/lenya/workflow/1.0";
    public static final String DEFAULT_PREFIX = "wf";

    public Workflow buildWorkflow(File file) throws WorkflowBuildException {
        Workflow workflow;

        try {
            Document document = DocumentHelper.readDocument(file);
            workflow = buildWorkflow(document);
        } catch (Exception e) {
            throw new WorkflowBuildException(e);
        }

        return workflow;
    }

    public Workflow buildWorkflow(Document document)
        throws
            ParserConfigurationException,
            SAXException,
            IOException,
            WorkflowBuildException {

        NamespaceHelper helper =
            new NamespaceHelper(NAMESPACE, DEFAULT_PREFIX, document);

        Element root = document.getDocumentElement();
        State initialState = null;

        Map states = new HashMap();
        Map events = new HashMap();

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

        // load events
        NodeList eventElements =
            root.getElementsByTagNameNS(NAMESPACE, EVENT_ELEMENT);
        for (int i = 0; i < eventElements.getLength(); i++) {
            EventImpl event = buildEvent((Element) eventElements.item(i));
            String id = event.getId();
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
                    events);
            workflow.addTransition(transition);
        }

        return workflow;
    }

    protected boolean isInitialStateElement(Element element) {
        assert element.getLocalName().equals(STATE_ELEMENT);
        String initialAttribute = element.getAttribute(INITIAL_ATTRIBUTE);
        return initialAttribute != null
            && (initialAttribute.equals("yes") || initialAttribute.equals("true"));
    }

    public static final String STATE_ELEMENT = "state";
    public static final String TRANSITION_ELEMENT = "transition";
    public static final String EVENT_ELEMENT = "event";
    public static final String CONDITION_ELEMENT = "condition";
    public static final String ACTION_ELEMENT = "action";
    public static final String ID_ATTRIBUTE = "id";
    public static final String INITIAL_ATTRIBUTE = "initial";
    public static final String SOURCE_ATTRIBUTE = "source";
    public static final String DESTINATION_ATTRIBUTE = "destination";
    public static final String CLASS_ATTRIBUTE = "class";

    protected StateImpl buildState(Element element) {
        assert element.getLocalName().equals(STATE_ELEMENT);

        String id = element.getAttribute(ID_ATTRIBUTE);
        StateImpl state = new StateImpl(id);
        return state;
    }

    protected TransitionImpl buildTransition(
        Element element,
        Map states,
        Map events)
        throws WorkflowBuildException {
        assert element.getLocalName().equals(TRANSITION_ELEMENT);

        String sourceId = element.getAttribute(SOURCE_ATTRIBUTE);
        String destinationId = element.getAttribute(DESTINATION_ATTRIBUTE);

        assert sourceId != null;
        assert destinationId != null;

        State source = (State) states.get(sourceId);
        State destination = (State) states.get(destinationId);

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

        // load actions
        NodeList actionElements =
            element.getElementsByTagNameNS(NAMESPACE, ACTION_ELEMENT);
        for (int i = 0; i < actionElements.getLength(); i++) {
            Action action = buildAction((Element) actionElements.item(i));
            transition.addAction(action);
        }

        return transition;
    }

    protected EventImpl buildEvent(Element element) {
        String id = element.getAttribute(ID_ATTRIBUTE);
        assert id != null;
        EventImpl event = new EventImpl(id);
        return event;
    }

    protected Condition buildCondition(Element element)
        throws WorkflowBuildException {
        String className = element.getAttribute(CLASS_ATTRIBUTE);
        String expression = DocumentHelper.getSimpleElementText(element);
        Condition condition =
            ConditionFactory.createCondition(className, expression);
        return condition;
    }

    protected Action buildAction(Element element) {
        String id = element.getAttribute(ID_ATTRIBUTE);
        Action action = new ActionImpl(id);
        return action;
    }

}
