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
package org.apache.lenya.cms.usecase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.cocoon.workflow.WorkflowHelper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.util.ServletHelper;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 * Abstract usecase implementation.
 * 
 * @version $Id$
 */
public class AbstractUsecase extends AbstractOperation implements Usecase, Contextualizable {

    /**
     * Ctor.
     */
    public AbstractUsecase() {
    }

    private Situation situation;

    /**
     * Sets the source URL and the workflow situation of the usecase.
     * @param sourceUrl The URL the usecase was invoked on.
     * @param situation The workflow situation.
     * 
     */
    public void setup(String sourceUrl, Situation situation) {
        this.sourceUrl = sourceUrl;
        this.situation = situation;

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Invoking usecase on URL: [" + sourceUrl + "]");
        }
    }

    /**
     * Override to initialize parameters.
     */
    protected void initParameters() {
    }

    /**
     * Returns the workflow situation.
     * @return A situation.
     */
    protected Situation getSituation() {
        return this.situation;
    }

    private String sourceUrl = null;

    /**
     * Returns the source URL.
     * @return A string.
     */
    protected String getSourceURL() {
        return this.sourceUrl;
    }

    /**
     * Returns the context.
     * @return A context.
     */
    protected Context getContext() {
        return this.context;
    }

    /**
     * Checks if the operation can be executed and returns the error messages. Error messages
     * prevent the operation from being executed.
     * @return A boolean value.
     */
    public List getErrorMessages() {
        return Collections.unmodifiableList(this.errorMessages);
    }

    /**
     * Returns the information messages to show on the confirmation screen.
     * @return An array of strings. Info messages do not prevent the operation from being executed.
     */
    public List getInfoMessages() {
        return Collections.unmodifiableList(this.infoMessages);
    }

    private List errorMessages = new ArrayList();
    private List infoMessages = new ArrayList();

    /**
     * Adds an error message.
     * @param message The message.
     */
    public void addErrorMessage(String message) {
        this.errorMessages.add(message);
    }

    /**
     * Adds an error message.
     * @param messages The messages.
     */
    public void addErrorMessages(String[] messages) {
        for (int i = 0; i < messages.length; i++) {
            addErrorMessage(messages[i]);
        }
    }

    /**
     * Adds an info message.
     * @param message The message.
     */
    public void addInfoMessage(String message) {
        this.infoMessages.add(message);
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#checkExecutionConditions()
     */
    public final void checkExecutionConditions() throws UsecaseException {
        try {
            clearErrorMessages();
            clearInfoMessages();
            doCheckExecutionConditions();
            dumpErrorMessages();
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            addErrorMessage(e.getMessage() + " - Please consult the logfiles.");
            if (getLogger().isDebugEnabled()) {
                throw new UsecaseException(e);
            }
        }
    }

    /**
     * Checks the execution conditions.
     * @throws Exception if an error occurs.
     */
    protected void doCheckExecutionConditions() throws Exception {
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#checkPreconditions()
     */
    public final void checkPreconditions() throws UsecaseException {
        try {
            clearErrorMessages();
            clearInfoMessages();
            doCheckPreconditions();
            dumpErrorMessages();
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            addErrorMessage(e.getMessage() + " - Please consult the logfiles.");
            if (getLogger().isDebugEnabled()) {
                throw new UsecaseException(e);
            }
        }
    }

    /**
     * Checks the preconditions.
     * @throws Exception if an error occurs.
     */
    protected void doCheckPreconditions() throws Exception {
    }

    /**
     * Clears the error messages.
     */
    protected void clearErrorMessages() {
        this.errorMessages.clear();
    }

    /**
     * Clears the info messages.
     */
    protected void clearInfoMessages() {
        this.infoMessages.clear();
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#execute()
     */
    public final void execute() throws UsecaseException {
        try {
            clearErrorMessages();
            clearInfoMessages();
            doExecute();
            dumpErrorMessages();
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            addErrorMessage(e.getMessage() + " - Please consult the logfiles.");
            if (getLogger().isDebugEnabled()) {
                throw new UsecaseException(e);
            }
        }
    }

    /**
     * Dumps the error messages to the log and the command line.
     */
    protected void dumpErrorMessages() {
        List errorMessages = getErrorMessages();
        for (int i = 0; i < errorMessages.size(); i++) {
            if (getLogger().isDebugEnabled()) {
                System.out.println("+++ ERROR +++ " + errorMessages.get(i));
            }
            getLogger().error((String) errorMessages.get(i));
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#checkPostconditions()
     */
    public void checkPostconditions() throws UsecaseException {
        try {
            clearErrorMessages();
            clearInfoMessages();
            doCheckPostconditions();
            dumpErrorMessages();
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            addErrorMessage(e.getMessage() + " - Please consult the logfiles.");
            if (getLogger().isDebugEnabled()) {
                throw new UsecaseException(e);
            }
        }
    }

    /**
     * Checks the post conditions.
     * @throws Exception if an error occured.
     */
    protected void doCheckPostconditions() throws Exception {
    }

    /**
     * Executes the operation.
     * @throws Exception when something went wrong.
     */
    protected void doExecute() throws Exception {
    }

    private Map parameters = new HashMap();

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Setting parameter [" + name + "] = [" + value + "]");
        }
        this.parameters.put(name, value);
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getParameter(java.lang.String)
     */
    public Object getParameter(String name) {
        return this.parameters.get(name);
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getParameterAsString(java.lang.String)
     */
    public String getParameterAsString(String name) {
        String valueString = null;
        Object value = getParameter(name);
        if (value != null) {
            valueString = value.toString();
        }
        return valueString;
    }
    /**
     * Returns one of the strings "true" or "false" depending on whether the corresponding checkbox
     * was checked.
     * @param name The parameter name.
     * @return A string.
     */
    public String getBooleanCheckboxParameter(String name) {
        String value = "false";
        if (getParameter(name) != null && getParameter(name).equals("on")) {
            value = "true";
        }
        return value;
    }

    private String targetUrl = null;

    protected void setTargetURL(String url) {
        this.targetUrl = url;
    }

    /**
     * If {@link #setTargetURL(String)}was not called, the source document (
     * {@link #getSourceURL()}) is returned.
     * @see org.apache.lenya.cms.usecase.Usecase#getTargetURL(boolean)
     */
    public String getTargetURL(boolean success) {
        String url;
        if (this.targetUrl != null) {
            url = this.targetUrl;
        } else {
            url = getSourceURL();
        }
        return url;
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setPart(java.lang.String,
     *      org.apache.cocoon.servlet.multipart.Part)
     */
    public void setPart(String name, Part value) {
    }

    private Context context;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException {
        this.context = context;
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public final void initialize() throws Exception {
        super.initialize();
        doInitialize();
        initParameters();
    }

    /**
     * Does the actual initialization. Template method.
     * @throws Exception if an error occurs.
     */
    protected void doInitialize() throws Exception {
        Map objectModel = ContextHelper.getObjectModel(this.context);
        Situation situation;
        try {
            situation = WorkflowHelper.buildSituation(objectModel);
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
        Request request = ObjectModelHelper.getRequest(objectModel);
        String webappUri = ServletHelper.getWebappURI(request);
        
        setup(webappUri, situation);
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#advance()
     */
    public void advance() {
    }
    
    /**
     * Deletes a parameter.
     * @param name The parameter name.
     */
    protected void deleteParameter(String name) {
        this.parameters.remove(name);
    }

    /**
     * Triggers a workflow event on a document.
     * @param event The event.
     * @param document The document.
     */
    protected void triggerWorkflow(String event, Document document) {
        WorkflowFactory factory = WorkflowFactory.newInstance();
        try {
            WorkflowInstance instance = factory.buildInstance(document);
            Event[] events = instance.getExecutableEvents(getSituation());
            Event executableEvent = null;
            for (int i = 0; i < events.length; i++) {
                if (events[i].getName().equals(event)) {
                    executableEvent = events[i];
                }
            }

            if (executableEvent == null) {
                throw new RuntimeException("The event [" + event
                        + "] is not executable on document [" + document + "]");
            }
            instance.invoke(getSituation(), executableEvent);
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

}