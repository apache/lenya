/*
 * Copyright  1999-2005 The Apache Software Foundation
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.transaction.AbstractOperation;
import org.apache.lenya.transaction.LockException;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.transaction.Transactionable;

/**
 * Abstract usecase implementation.
 * 
 * @version $Id$
 */
public class AbstractUsecase extends AbstractOperation implements Usecase, Configurable {

    /**
     * Ctor.
     */
    public AbstractUsecase() {
        // do nothing
    }

    /**
     * Override to initialize parameters.
     */
    protected void initParameters() {
        // do nothing
    }

    private String SOURCE_URL = "private.sourceUrl";

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getSourceURL()
     */
    public String getSourceURL() {
        return getParameterAsString(SOURCE_URL);
    }

    /**
     * Returns the context.
     * @return A context.
     */
    protected Context getContext() {
        return this.context;
    }

    /**
     * Determine if the usecase has error messages. Provides a way of checking for errors without
     * actually retrieving them.
     * @return true if the usecase resulted in error messages.
     */
    public boolean hasErrors() {
        boolean ret = false;
        if (this.errorMessages != null)
            ret = !this.errorMessages.isEmpty();

        if (getLogger().isDebugEnabled())
            getLogger().debug("AbstractUsecase::hasErrors() called, returning " + ret);

        return ret;
    }

    /**
     * Determine if the usecase has info messages. Provides a way of checking for info messages
     * without actually retrieving them.
     * @return true if the usecase resulted in info messages being generated.
     */
    public boolean hasInfoMessages() {
        boolean ret = false;
        if (this.infoMessages != null)
            ret = !this.infoMessages.isEmpty();
        return ret;
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
        addErrorMessage(message, null);
    }

    /**
     * Adds an error message.
     * @param message The message.
     * @param _params parameters
     */
    public void addErrorMessage(String message, String[] _params) {
        this.errorMessages.add(new UsecaseMessage(message, _params));
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
     * @param _params parameters
     */
    public void addInfoMessage(String message, String[] _params) {
        this.infoMessages.add(new UsecaseMessage(message, _params));
    }

    /**
     * Adds an info message.
     * @param message The message.
     */
    public void addInfoMessage(String message) {
        addInfoMessage(message, null);
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
        // do nothing
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#checkPreconditions()
     */
    public final void checkPreconditions() throws UsecaseException {
        try {
            clearErrorMessages();
            clearInfoMessages();
            doCheckPreconditions();

            List _errorMessages = getErrorMessages();
            for (int i = 0; i < _errorMessages.size(); i++) {
                getLogger().info(_errorMessages.get(i).toString());
            }
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
        // do nothing
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
        Exception exception = null;
        try {
            clearErrorMessages();
            clearInfoMessages();
            doExecute();
            dumpErrorMessages();
        } catch (LockException e) {
            exception = e;
            addErrorMessage("The operation could not be completed because an involved object was changed by another user.");
        } catch (Exception e) {
            exception = e;
            getLogger().error(e.getMessage(), e);
            addErrorMessage(e.getMessage() + " - Please consult the logfiles.");
            throw new UsecaseException(e);
        } finally {
            try {
                if (getErrorMessages().isEmpty() && exception == null) {
                    getUnitOfWork().commit();
                } else {
                    getUnitOfWork().rollback();
                }
            } catch (ServiceException e1) {
                getLogger().error("Service could not be obtained: ", e1);
            } catch (TransactionException e1) {
                getLogger().error("Exception during commit or rollback: ", e1);
                addErrorMessage("Exception during commit or rollback: " + e1.getMessage()
                        + " (see logfiles for details)");
            }
        }
    }

    /**
     * Dumps the error messages to the log.
     */
    protected void dumpErrorMessages() {
        List _errorMessages = getErrorMessages();
        for (int i = 0; i < _errorMessages.size(); i++) {
            getLogger().error(_errorMessages.get(i).toString());
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
        // do nothing
    }

    /**
     * Executes the operation.
     * @throws Exception when something went wrong.
     */
    protected void doExecute() throws Exception {
        // do nothing
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
     * Returns a parameter as string. If the parameter does not exist, a default value is returned.
     * @param name The parameter name.
     * @param defaultValue The default value.
     * @return A string.
     */
    public String getParameterAsString(String name, String defaultValue) {
        String valueString = defaultValue;
        Object value = getParameter(name);
        if (value != null) {
            valueString = value.toString();
        }
        return valueString;
    }

    /**
     * Returns a parameter as integer. If the parameter does not exist, a default value is returned.
     * @param name The parameter name.
     * @param defaultValue The default value.
     * @return An integer.
     */
    public int getParameterAsInteger(String name, int defaultValue) {
        int valueInt = defaultValue;
        Object value = getParameter(name);
        if (value != null) {
            valueInt = Integer.valueOf(value.toString()).intValue();
        }
        return valueInt;
    }

    /**
     * Returns a parameter as boolean. If the parameter does not exist, a default value is returned.
     * @param name The parameter name.
     * @param defaultValue The default value.
     * @return A boolean value..
     */
    public boolean getParameterAsBoolean(String name, boolean defaultValue) {
        boolean valueBoolean = defaultValue;
        Object value = getParameter(name);
        if (value != null) {
            valueBoolean = Boolean.valueOf(value.toString()).booleanValue();
        }

        return valueBoolean;
    }

    /**
     * Return a map of all parameters
     * @return the map
     */
    public Map getParameters() {
        return Collections.unmodifiableMap(this.parameters);
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

    private String TARGET_URL = "private.targetUrl";

    protected void setTargetURL(String url) {
        setParameter(TARGET_URL, url);
    }

    /**
     * If {@link #setTargetURL(String)}was not called, the source document (
     * {@link #getSourceURL()}) is returned.
     * @see org.apache.lenya.cms.usecase.Usecase#getTargetURL(boolean)
     */
    public String getTargetURL(boolean success) {
        String url = getParameterAsString(TARGET_URL);
        if (url == null) {
            url = getSourceURL();
        }
        return url + getExitUsecaseQueryString();
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setPart(java.lang.String,
     *      org.apache.cocoon.servlet.multipart.Part)
     */
    public void setPart(String name, Part value) {
        if (!Part.class.isInstance(value)) {
            throw new RuntimeException("[" + value.getClass() + "] [" + value
                    + "] is not a part object. Maybe you have to enable uploads?");
        }
        setParameter(name, value);
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getPart(java.lang.String)
     */
    public Part getPart(String name) {
        return (Part) getParameter(name);
    }

    protected DocumentIdentityMap getDocumentIdentityMap() {
        try {
            return (DocumentIdentityMap) getUnitOfWork().getIdentityMap();
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public final void initialize() throws Exception {
        super.initialize();
        DocumentIdentityMap map = new DocumentIdentityMap(this.manager, getLogger());
        getUnitOfWork().setIdentityMap(map);
        Request request = ContextHelper.getRequest(this.context);
        Session session = request.getSession(true);
        Identity identity = (Identity) session.getAttribute(Identity.class.getName());
        getUnitOfWork().setIdentity(identity);
    }

    /**
     * Does the actual initialization. Template method.
     */
    protected final void doInitialize() {
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#advance()
     */
    public void advance() throws UsecaseException {
        // do nothing
    }

    /**
     * Deletes a parameter.
     * @param name The parameter name.
     */
    protected void deleteParameter(String name) {
        this.parameters.remove(name);
    }

    private String name;

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getName()
     */
    public String getName() {
        return this.name;
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getParameterNames()
     */
    public String[] getParameterNames() {
        Set keys = this.parameters.keySet();
        return (String[]) keys.toArray(new String[keys.size()]);
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setSourceURL(java.lang.String)
     */
    public void setSourceURL(String url) {
        setParameter(SOURCE_URL, url);
        initParameters();
    }

    private UsecaseView view;

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getView()
     */
    public UsecaseView getView() {
        return this.view;
    }

    protected static final String ELEMENT_PARAMETER = "parameter";
    protected static final String ATTRIBUTE_NAME = "name";
    protected static final String ATTRIBUTE_VALUE = "value";
    protected static final String ELEMENT_VIEW = "view";
    protected static final String ELEMENT_TRANSACTION = "transaction";
    protected static final String ATTRIBUTE_POLICY = "policy";
    protected static final String VALUE_OPTIMISTIC = "optimistic";
    protected static final String VALUE_PESSIMISTIC = "pessimistic";

    private boolean isOptimistic = true;

    /**
     * @return <code>true</code> if the transaction policy is optimistic offline lock,
     *         <code>false</code> if it is pessimistic offline lock.
     */
    protected boolean isOptimistic() {
        return this.isOptimistic;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException {

        Configuration[] parameterConfigs = config.getChildren(ELEMENT_PARAMETER);
        for (int i = 0; i < parameterConfigs.length; i++) {
            String name = parameterConfigs[i].getAttribute(ATTRIBUTE_NAME);
            String value = parameterConfigs[i].getAttribute(ATTRIBUTE_VALUE);
            setParameter(name, value);
        }

        Configuration viewConfig = config.getChild(ELEMENT_VIEW, false);
        if (viewConfig != null) {
            this.view = new UsecaseView();
            view.configure(viewConfig);
        }

        Configuration transactionConfig = config.getChild(ELEMENT_TRANSACTION, false);
        if (transactionConfig != null) {
            String policy = transactionConfig.getAttribute(ATTRIBUTE_POLICY);
            if (policy.equals(VALUE_PESSIMISTIC)) {
                this.isOptimistic = false;
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setView(org.apache.lenya.cms.usecase.UsecaseView)
     */
    public void setView(UsecaseView view) {
        this.view = view;
    }

    /**
     * @return The objects that could be changed during the usecase.
     * @throws UsecaseException if an error occurs.
     */
    protected Transactionable[] getObjectsToLock() throws UsecaseException {
        return new Transactionable[0];
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#lockInvolvedObjects()
     */
    public final void lockInvolvedObjects() throws UsecaseException {
        lockInvolvedObjects(getObjectsToLock());
    }

    /**
     * Lock the objects, for example when you need to change them (for example, delete). If you know
     * when entering the usecase what these objects are, you do not need to call this, the framework
     * will take of it if you implement getObjectsToLock(). If you do not know in advance what the
     * objects are, you can call this method explicitly when appropriate.
     * 
     * @param objects the transactionable objects to lock
     * @see #lockInvolvedObjects()
     * @see #getObjectsToLock()
     */
    public final void lockInvolvedObjects(Transactionable[] objects) throws UsecaseException {

        if (getLogger().isDebugEnabled())
            getLogger()
                    .debug("AbstractUsecase::lockInvolvedObjects() called, are there objects to lock ? "
                            + (objects != null));

        try {
            boolean canExecute = true;

            for (int i = 0; i < objects.length; i++) {
                if (objects[i].isCheckedOut()) {
                    if (getLogger().isDebugEnabled())
                        getLogger()
                                .debug("AbstractUsecase::lockInvolvedObjects() can not execute, object ["
                                        + objects[i] + "] is already checked out");

                    canExecute = false;
                }
            }

            if (canExecute) {
                for (int i = 0; i < objects.length; i++) {
                    if (getLogger().isDebugEnabled())
                        getLogger().debug("AbstractUsecase::lockInvolvedObjects() locking "
                                + objects[i]);

                    objects[i].lock();
                    if (!isOptimistic()) {
                        objects[i].checkout();
                    }
                }
            } else {
                addErrorMessage("The operation cannot be executed because one ore more of the "
                        + "involved objects are checked out.");
            }

        } catch (TransactionException e) {
            throw new UsecaseException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#cancel()
     */
    public void cancel() throws UsecaseException {
        try {
            getUnitOfWork().rollback();
        } catch (Exception e) {
            throw new UsecaseException(e);
        }
    }

    private String exitUsecaseName = null;
    private Map exitUsecaseParameters = new HashMap();

    protected void setExitUsecase(String usecaseName, Map parameters) {
        this.exitUsecaseName = usecaseName;
        if (parameters != null) {
            this.exitUsecaseParameters = parameters;
        }
    }

    protected String getExitUsecaseQueryString() {
        String queryString = "";
        if (this.exitUsecaseName != null) {
            queryString = "?lenya.usecase=" + this.exitUsecaseName;
            for (Iterator i = this.exitUsecaseParameters.keySet().iterator(); i.hasNext(); ) {
                String key = (String) i.next();
                String value = (String) this.exitUsecaseParameters.get(key);
                queryString += "&" + key + "=" + value;
            }
        }
        else {
            String exitUsecase = getParameterAsString("lenya.exitUsecase");
            if (exitUsecase != null && !"".equals(exitUsecase)) {
                queryString = "?lenya.usecase=" + exitUsecase;
            }
        }
        return queryString;
    }
}