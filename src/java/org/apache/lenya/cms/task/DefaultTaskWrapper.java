/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

package org.apache.lenya.cms.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.Request;
import org.apache.commons.collections.IteratorUtils;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Role;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.util.NamespaceMap;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

public class DefaultTaskWrapper implements TaskWrapper {

    private static Logger log = Logger.getLogger(DefaultTaskWrapper.class);

    private Map parameters = new HashMap();
    private TaskWrapperParameters wrapperParameters =
        new TaskWrapperParameters(getParameterObject());
    private TaskParameters taskParameters = new TaskParameters(getParameterObject());

    /**
     * Default ctor for subclasses.
     */
    protected DefaultTaskWrapper() {
    }

    /**
     * Ctor to be called when all task wrapper parameters are known.
     * All keys and values must be strings or string arrays.
     * @param parameters The prefixed parameters.
     */
    public DefaultTaskWrapper(Map parameters) {
        log.debug("Creating");

        List keys = IteratorUtils.toList(parameters.keySet().iterator(), parameters.size()); 
        Collections.sort(keys);

        for (Iterator i = keys.iterator(); i.hasNext();) {
            String key = (String) i.next();
            String value = null;
            Object valueObject = parameters.get(key);
            if (valueObject instanceof String) {
                value = ((String) valueObject).trim();
            } else if (valueObject instanceof String[]) {
                String[] values = (String[]) valueObject;
                StringBuffer buf = new StringBuffer();
                for (int j = 0; j < values.length; j++) {
                    if (j > 0 && buf.length() > 0) {
                        buf.append(",");
                    }
                    buf.append(values[j].trim());
                }
                value = buf.toString();
            } else {
                log.debug("Not a string value: [" + key + "] = [" + valueObject + "]");
            }

            if (value != null) {
                log.debug("Setting parameter: [" + key + "] = [" + value + "]");
                this.parameters.put(key, value);
            }
        }
    }

    /**
     * Ctor.
     * Restores the wrapper parameters from an XML element.
     * @param parent The parent of the task wrapper element.
     * @param helper The namespace helper of the document.
     */
    public DefaultTaskWrapper(NamespaceHelper helper, Element parent) {
        log.debug("Creating");
        restore(helper, parent);
    }

    /**
     * Initializes the task wrapper.
     * @param taskId The task ID.
     * @param publication The publication.
     * @param webappUrl The webapp URL.
     * @param parameters The task parameters.
     * @throws ExecutionException when the task ID is null.
     */
    protected void initialize(
        String taskId,
        Publication publication,
        String webappUrl,
        Parameters parameters)
        throws ExecutionException {
        log.debug("Initializing");

        getTaskParameters().setPublication(publication);
        getWrapperParameters().setWebappUrl(webappUrl);

        getWrapperParameters().setTaskId(taskId);
        getTaskParameters().parameterize(parameters);
    }

    /**
     * Extracts the task parameters from the given objects.
     * @param parameters A parameters object.
     * @param publication A publication.
     * @param request A request.
     * @return A parameters object.
     */
    protected Parameters extractTaskParameters(
        Parameters parameters,
        Publication publication,
        Request request) {
        Parameters taskParameters = new Parameters();
        taskParameters.setParameter(
            Task.PARAMETER_SERVLET_CONTEXT,
            publication.getServletContext().getAbsolutePath());
        taskParameters.setParameter(Task.PARAMETER_CONTEXT_PREFIX, request.getContextPath());
        taskParameters.setParameter(
            Task.PARAMETER_SERVER_PORT,
            Integer.toString(request.getServerPort()));
        taskParameters.setParameter(Task.PARAMETER_SERVER_URI, "http://" + request.getServerName());
        taskParameters.setParameter(Task.PARAMETER_PUBLICATION_ID, publication.getId());

        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            String value = request.getParameter(key);
            if (value != null) {
                taskParameters.setParameter(key, value);
            }
        }

        String[] names = parameters.getNames();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            String value = parameters.getParameter(name, "");
            if (value != null) {
                taskParameters.setParameter(name, value);
            }
        }
        return taskParameters;
    }

    /**
     * Enables workflow transition invocation.
     * @param eventName The event name.
     * @param identity The identity that executes the task.
     * @param roles The roles of the identity.
     */
    public void setWorkflowAware(String eventName, Identity identity, Role[] roles) {
        NamespaceMap workflowParameters =
            WorkflowInvoker.extractParameters(eventName, identity, roles);
        getParameterObject().putAll(workflowParameters.getPrefixedMap());
    }

    /**
     * Executes the task.
     * @throws ExecutionException when something went wrong.
     */
    public void execute() throws ExecutionException {

        String taskId = getWrapperParameters().getTaskId();

        if (taskId == null) {
            throw new ExecutionException("No task id provided!");
        }

        log.info("===================================");
        log.info(" Executing task [" + taskId + "]");
        log.info("-----------------------------------");

        if (!wrapperParameters.isComplete()) {

            String[] missingKeys = getWrapperParameters().getMissingKeys();
            StringBuffer keyString = new StringBuffer();
            for (int i = 0; i < missingKeys.length; i++) {
                if (i > 0) {
                    keyString.append(", ");
                }
                keyString.append(missingKeys[i]);
            }
            throw new ExecutionException("Parameters missing: [" + keyString + "]");
        }

        TaskManager manager;

        Publication publication = getTaskParameters().getPublication();

        WorkflowInvoker workflowInvoker = new WorkflowInvoker(getParameters());
        workflowInvoker.setup(publication, getWrapperParameters().getWebappUrl());

        Task task;
        try {
            manager = new TaskManager(publication.getDirectory().getAbsolutePath());
            task = manager.getTask(taskId);

            Properties properties = new Properties();
            properties.putAll(getTaskParameters().getMap());
            Parameters parameters = Parameters.fromProperties(properties);

            task.parameterize(parameters);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }

		log.debug("-----------------------------------");
		log.debug(" Triggering workflow");
		log.debug("-----------------------------------");
		
        //FIXME The new workflow is set before the end of the transition because the document id
        // and so the document are sometimes changing during the transition (ex archiving , ...) 
        workflowInvoker.invokeTransition();

		log.debug("-----------------------------------");
		log.debug(" Triggering task");
		log.debug("-----------------------------------");
		
        task.execute(publication.getServletContext().getAbsolutePath());

		log.debug("-----------------------------------");
		log.debug(" Triggering notification");
		log.debug("-----------------------------------");
        Notifier notifier = new Notifier(manager, getParameters());
        notifier.sendNotification(getTaskParameters());

		log.debug("-----------------------------------");
		log.debug(" Executing task finished.");
		log.debug("===================================\n\n");
    }

    /**
     * Returns the task wrapper parameters.
     * @return A task wrapper parameters object.
     */
    public TaskWrapperParameters getWrapperParameters() {
        return wrapperParameters;
    }

    /**
     * Returns the task parameters.
     * @return A task parameters object.
     */
    public TaskParameters getTaskParameters() {
        return taskParameters;
    }

    protected static final String ELEMENT_TASK = "task";
    protected static final String ELEMENT_PARAMETER = "parameter";
    protected static final String ATTRIBUTE_NAME = "name";
    protected static final String ATTRIBUTE_VALUE = "value";

    /**
     * Saves the wrapper parameters to an XML element.
     * @param helper The namespace helper of the document.
     * @return An XML element.
     */
    public Element save(NamespaceHelper helper) {
        org.w3c.dom.Document document = helper.getDocument();
        NamespaceHelper taskHelper =
            new NamespaceHelper(Task.NAMESPACE, Task.DEFAULT_PREFIX, document);
        Element element = taskHelper.createElement(ELEMENT_TASK);
        
        List keys = new ArrayList(getParameters().keySet());
        Collections.sort(keys);

        for (Iterator i = keys.iterator(); i.hasNext();) {
            String key = (String) i.next();
            Element parameterElement = taskHelper.createElement(ELEMENT_PARAMETER);
            parameterElement.setAttribute(ATTRIBUTE_NAME, key);
            parameterElement.setAttribute(ATTRIBUTE_VALUE, (String) getParameters().get(key));
            element.appendChild(parameterElement);
        }

        return element;
    }

    /**
     * Restores the wrapper parameters from an XML element.
     * @param parent The parent of the task wrapper element.
     * @param helper The namespace helper of the document.
     */
    public void restore(NamespaceHelper helper, Element parent) {
        org.w3c.dom.Document document = helper.getDocument();
        NamespaceHelper taskHelper =
            new NamespaceHelper(Task.NAMESPACE, Task.DEFAULT_PREFIX, document);
        Element taskElement = taskHelper.getFirstChild(parent, ELEMENT_TASK);
        Element[] parameterElements = taskHelper.getChildren(taskElement, ELEMENT_PARAMETER);
        for (int i = 0; i < parameterElements.length; i++) {
            String key = parameterElements[i].getAttribute(ATTRIBUTE_NAME);
            String value = parameterElements[i].getAttribute(ATTRIBUTE_VALUE);
            getParameterObject().put(key, value);
        }
    }

    /**
     * Returns all prefixed parameters.
     * @return A map.
     */
    public Map getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    /**
     * Returns all prefixed parameters.
     * @return A map.
     */
    protected Map getParameterObject() {
        return parameters;
    }

    /**
     * Sets the notification parameters.
     * @param notificationParameters The notification parameters.
     */
    protected void setNotifying(NamespaceMap notificationParameters) {
        log.info("Enabling notification");
        getParameterObject().putAll(notificationParameters.getPrefixedMap());
    }

}
