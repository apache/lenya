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

import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.workflow.Situation;

/**
 * Abstract usecase implementation.
 *
 * @version $Id:$ 
 */
public class AbstractUsecase extends AbstractOperation implements Usecase {

    /**
     * Ctor.
     */
    public AbstractUsecase() {
    }

    private Situation situation;
    private Document sourceDocument;

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setup(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.workflow.Situation)
     */
    public void setup(Document sourceDocument, Situation situation) {
        this.sourceDocument = sourceDocument;
        this.situation = situation;

        initParameters();

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Invoking usecase on document: [" + sourceDocument + "]");
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

    /**
     * Returns the source document.
     * @return A document.
     */
    protected Document getSourceDocument() {
        return sourceDocument;
    }

    /**
     * Checks if the operation can be executed and returns the error messages. Error messages
     * prevent the operation from being executed.
     * @return A boolean value.
     */
    public List getErrorMessages() {
        return Collections.unmodifiableList(errorMessages);
    }

    /**
     * Returns the information messages to show on the confirmation screen.
     * @return An array of strings. Info messages do not prevent the operation from being executed.
     */
    public List getInfoMessages() {
        return Collections.unmodifiableList(infoMessages);
    }

    private List errorMessages = new ArrayList();
    private List infoMessages = new ArrayList();

    /**
     * Adds an error message.
     * @param message The message.
     */
    protected void addErrorMessage(String message) {
        errorMessages.add(message);
    }

    /**
     * Adds an error message.
     * @param messages The messages.
     */
    protected void addErrorMessages(String[] messages) {
        for (int i = 0; i < messages.length; i++) {
            addErrorMessage(messages[i]);
        }
    }

    /**
     * Adds an info message.
     * @param message The message.
     */
    protected void addInfoMessage(String message) {
        infoMessages.add(message);
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
        errorMessages.clear();
    }

    /**
     * Clears the info messages.
     */
    protected void clearInfoMessages() {
        infoMessages.clear();
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
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.String)
     */
    public void setParameter(String name, String value) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Setting parameter [" + name + "] = [" + value + "]");
        }
        this.parameters.put(name, value);
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getParameter(java.lang.String)
     */
    public String getParameter(String name) {
        return (String) parameters.get(name);
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

    private Document targetDocument = null;

    /**
     * Sets the target document.
     * @param document A document.
     */
    protected void setTargetDocument(Document document) {
        this.targetDocument = document;
    }

    /**
     * If {@link #setTargetDocument(Document)}was not called, the source document (
     * {@link #getSourceDocument()}) is returned.
     * @see org.apache.lenya.cms.usecase.Usecase#getTargetDocument(boolean)
     */
    public Document getTargetDocument(boolean success) {
        Document document;
        if (this.targetDocument != null) {
            document = this.targetDocument;
        } else {
            document = getSourceDocument();
        }
        return document;
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setPart(java.lang.String,
     *      org.apache.cocoon.servlet.multipart.Part)
     */
    public void setPart(String name, Part value) {
    }

}