/*
$Id: DefaultTaskWrapper.java,v 1.8 2003/10/22 16:32:55 egli Exp $
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
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac2.Identity;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.util.NamespaceMap;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.log4j.Category;
import org.w3c.dom.Element;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DefaultTaskWrapper implements TaskWrapper {

    private static Category log = Category.getInstance(DefaultTaskWrapper.class);

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

        List keys = new ArrayList();
        for (Iterator i = parameters.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            keys.add(key);
        }

        Collections.sort(keys);

        for (Iterator i = keys.iterator(); i.hasNext();) {
            String key = (String) i.next();
            String value = null;
            Object valueObject = parameters.get(key);
            if (valueObject instanceof String) {
                value = ((String) valueObject).trim();
            } else if (valueObject instanceof String[]) {
                String[] values = (String[]) valueObject;
                value = "";
                for (int j = 0; j < values.length; j++) {
                    if (j > 0 && !"".equals(value)) {
                        value += ",";
                    }
                    value += values[j].trim();
                }
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

        log.debug("-----------------------------------");
        log.debug(" Executing task [" + taskId + "]");
        log.debug("-----------------------------------");

        if (!wrapperParameters.isComplete()) {

            String[] missingKeys = getWrapperParameters().getMissingKeys();
            String keyString = "";
            for (int i = 0; i < missingKeys.length; i++) {
                if (i > 0) {
                    keyString += ", ";
                }
                keyString += missingKeys[i];
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

        //FIXME The new workflow is set before the end of the transition because the document id
        // and so the document are sometimes changing during the transition (ex archiving , ...) 
        workflowInvoker.invokeTransition();

        task.execute(publication.getServletContext().getAbsolutePath());

        Notifier notifier = new Notifier(manager, getParameters());
        notifier.sendNotification(getTaskParameters());

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

        for (Iterator i = getParameters().keySet().iterator(); i.hasNext();) {
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
