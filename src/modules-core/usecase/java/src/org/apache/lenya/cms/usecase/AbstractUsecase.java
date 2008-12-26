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
package org.apache.lenya.cms.usecase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.transaction.ConcurrentModificationException;
import org.apache.lenya.transaction.LockException;
import org.apache.lenya.transaction.TransactionLock;

/**
 * Abstract usecase implementation.
 * 
 * @version $Id$
 */
public class AbstractUsecase extends AbstractLogEnabled implements Usecase, Configurable,
        Contextualizable, Serviceable, Initializable {

    protected static final String EVENT_CHECK_POSTCONDITIONS = "checkPostconditions";

    protected static final String EVENT_EXECUTE = "execute";

    protected static final String EVENT_CHECK_PRECONDITIONS = "checkPreconditions";

    protected static final String EVENT_CHECK_EXECUTION_CONDITIONS = "checkExecutionConditions";

    protected static final String ERROR_OBJECTS_CHECKED_OUT = "objects-checked-out";

    protected static final StateMachine.Transition[] TRANSITIONS = {
            new StateMachine.Transition("start", "preChecked", EVENT_CHECK_PRECONDITIONS),
            new StateMachine.Transition("preChecked", "preChecked", EVENT_CHECK_PRECONDITIONS),
            new StateMachine.Transition("preChecked", "nodesLocked", "lockInvolvedObjects"),
            new StateMachine.Transition("nodesLocked", "execChecked",
                    EVENT_CHECK_EXECUTION_CONDITIONS),
            new StateMachine.Transition("execChecked", "execChecked",
                    EVENT_CHECK_EXECUTION_CONDITIONS),
            new StateMachine.Transition("nodesLocked", "preChecked", EVENT_CHECK_PRECONDITIONS),
            new StateMachine.Transition("execChecked", "executed", EVENT_EXECUTE),
            new StateMachine.Transition("executed", "postChecked", EVENT_CHECK_POSTCONDITIONS) };

    protected static final StateMachine.Model MODEL = new StateMachine.Model("start", TRANSITIONS);

    protected static final String PARAMETER_STATE_MACHINE = "private.stateMachine";
    protected static final String PARAMETER_SESSION = "private.session";
    protected static final String PARAMETER_FACTORY = "private.factory";
    protected static final String PARAMETER_CHECKOUT_RESTRICTED_TO_SESSION = "checkoutRestrictedToSession";

    protected static final String PARAMETERS_INITIALIZED = "private.parametersInitialized";

    /**
     * Override to initialize parameters.
     */
    protected void initParameters() {
    }

    /**
     * Advance the usecase state machine to the next state. This method has to be called at the end
     * of the corresponding method to ensure that the subsequent methods can only be invoked if
     * nothing went wrong.
     * @param event The vent to invoke.
     */
    protected void advanceState(String event) {
        getStateMachine().invoke(event);
    }

    protected StateMachine getStateMachine() {
        StateMachine machine = (StateMachine) getParameter(PARAMETER_STATE_MACHINE);
        return machine;
    }

    protected void checkEvent(String event) {
        getStateMachine().checkEvent(event);
    }

    protected String SOURCE_URL = "private.sourceUrl";

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getSourceURL() We don't use getParameterAsString()
     *      because this will typically cause stack overflows or NPEs in connection with
     *      initParameters().
     */
    public String getSourceURL() {
        return (String) this.parameters.get(SOURCE_URL);
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
        return Collections.unmodifiableList(new ArrayList(this.errorMessages));
    }

    /**
     * Returns the information messages to show on the confirmation screen.
     * @return An array of strings. Info messages do not prevent the operation from being executed.
     */
    public List getInfoMessages() {
        return Collections.unmodifiableList(new ArrayList(this.infoMessages));
    }

    private List errorMessages = new ArrayList();
    private List infoMessages = new ArrayList();

    /**
     * Adds an error message.
     * @param message The message.
     */
    public void addErrorMessage(String message) {
        this.errorMessages.add(new UsecaseMessage(message));
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
        this.infoMessages.add(new UsecaseMessage(message));
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#checkExecutionConditions()
     */
    public final void checkExecutionConditions() throws UsecaseException {
        checkEvent(EVENT_CHECK_EXECUTION_CONDITIONS);
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
        if (!hasErrors()) {
            advanceState(EVENT_CHECK_EXECUTION_CONDITIONS);
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
        checkEvent(EVENT_CHECK_PRECONDITIONS);
        try {
            clearErrorMessages();
            clearInfoMessages();

            Node[] nodes = getNodesToLock();
            if (!canCheckOut(nodes)) {
                addErrorMessage(ERROR_OBJECTS_CHECKED_OUT);
            }
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
        if (!hasErrors()) {
            advanceState(EVENT_CHECK_PRECONDITIONS);
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
        checkEvent(EVENT_EXECUTE);
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
                if (this.commitEnabled && getErrorMessages().isEmpty() && exception == null) {
                    getSession().commit();
                } else {
                    getSession().rollback();
                }
            } catch (ConcurrentModificationException e) {
                getLogger()
                        .error("Could not commit usecase [" + getName() + "]: " + e.getMessage());
                addErrorMessage(e.getMessage());
            } catch (Exception e1) {
                getLogger().error("Could not commit/rollback usecase [" + getName() + "]: ", e1);
                addErrorMessage("Exception during commit or rollback: " + e1.getMessage()
                        + " (see logfiles for details)");
            }
        }
        if (!hasErrors()) {
            advanceState(EVENT_EXECUTE);
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
        checkEvent(EVENT_CHECK_POSTCONDITIONS);
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
        if (!hasErrors()) {
            advanceState(EVENT_CHECK_POSTCONDITIONS);
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
        // set any exit parameters that are missing values
        if (this.exitUsecaseParameters.containsKey(name)
                && this.exitUsecaseParameters.get(name) == null) {
            setExitParameter(name, value.toString());
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getParameter(java.lang.String)
     */
    public Object getParameter(String name) {
        if (!this.parameters.containsKey(name)) {
            initializeParametersIfNotDone();
        }
        return this.parameters.get(name);
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getParameter(java.lang.String, java.lang.Object)
     */
    public Object getParameter(String name, Object defaultValue) {
        Object value = getParameter(name);
        return value == null ? defaultValue : value;
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getParameterAsString(java.lang.String)
     */
    public String getParameterAsString(String name) {
        Object value = getParameter(name);
        return value == null ? null : value.toString();
    }

    /**
     * Returns a parameter as string. If the parameter does not exist, a default value is returned.
     * @param name The parameter name.
     * @param defaultValue The default value.
     * @return A string.
     */
    public String getParameterAsString(String name, String defaultValue) {
        Object value = getParameter(name);
        return value == null ? defaultValue : value.toString();
    }

    /**
     * Returns a parameter as integer. If the parameter does not exist, a default value is returned.
     * @param name The parameter name.
     * @param defaultValue The default value.
     * @return An integer.
     */
    public int getParameterAsInteger(String name, int defaultValue) {
        Object value = getParameter(name);
        return value == null ? defaultValue : Integer.valueOf(value.toString()).intValue();
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
            if (value instanceof String) {
                valueBoolean = Boolean.valueOf((String) value).booleanValue();
            } else if (value instanceof Boolean) {
                valueBoolean = ((Boolean) value).booleanValue();
            } else {
                throw new IllegalArgumentException("Cannot get boolean value of parameter [" + name
                        + "] (class " + value.getClass().getName() + ")");
            }
        }
        return valueBoolean;
    }

    /**
     * Return a map of all parameters
     * @return the map
     */
    public Map getParameters() {
        initializeParametersIfNotDone();
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

    private String EXIT_URI = "lenya.exitUri";
    private String DEFAULT_TARGET_URL = "private.defaultTargetUrl";

    /**
     * Sets the default target URL which should be used if no explicit target URL is set.
     * @param url A URL string.
     */
    protected void setDefaultTargetURL(String url) {
        setParameter(DEFAULT_TARGET_URL, url);
    }

    /**
     * If {@link #setDefaultTargetURL(String)}was not called, the source document (
     * {@link #getSourceURL()}) is returned.
     * @see org.apache.lenya.cms.usecase.Usecase#getTargetURL(boolean)
     */
    public String getTargetURL(boolean success) {
        String url = getParameterAsString(EXIT_URI);
        if (url == null) {
            url = getParameterAsString(DEFAULT_TARGET_URL);
        }
        if (url == null) {
            url = getSourceURL();
        }
        return url + getExitQueryString();
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setPart(java.lang.String,
     *      org.apache.cocoon.servlet.multipart.Part)
     */
    public void setPart(String name, Part value) {
        if (!Part.class.isInstance(value)) {
            String className = "";
            if (value != null) {
                className = value.getClass().getName();
            }
            throw new RuntimeException("[" + name + "] = (" + className + ")  [" + value
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

    protected DocumentFactory getDocumentFactory() {
        DocumentFactory factory = (DocumentFactory) getParameter(PARAMETER_FACTORY);
        Session session = getSession();
        if (factory == null || factory.getSession() != session) {
            factory = DocumentUtil.createDocumentFactory(this.manager, session);
            setParameter(PARAMETER_FACTORY, factory);
        }
        return factory;
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public final void initialize() throws Exception {
        Request request = ContextHelper.getRequest(this.context);
        Session session = RepositoryUtil.getSession(this.manager, request);
        setSession(session);
        setParameter(PARAMETER_STATE_MACHINE, new StateMachine(MODEL));
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
        initializeParametersIfNotDone();
        Set keys = this.parameters.keySet();
        return (String[]) keys.toArray(new String[keys.size()]);
    }

    protected void initializeParametersIfNotDone() {
        if (this.parameters.get(PARAMETERS_INITIALIZED) == null) {
            this.parameters.put(PARAMETERS_INITIALIZED, Boolean.TRUE);
            initParameters();
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setSourceURL(java.lang.String)
     */
    public void setSourceURL(String url) {
        setParameter(SOURCE_URL, url);
    }

    private UsecaseView view;

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getView()
     */
    public UsecaseView getView() {
        return this.view;
    }

    public void setupView() throws UsecaseException {
        try {
            prepareView();
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            addErrorMessage("Setting up the view for usecase " + getName() + " failed: "
                    + e.getMessage() + " - Please consult the logfiles.");
            if (getLogger().isDebugEnabled()) {
                throw new UsecaseException(e);
            }
        }
    }
    
    /**
     * Override this method to prepare the view (add information messages etc.).
     * @throws Exception If an error occurs.
     */
    protected void prepareView() throws Exception {
    }

    protected static final String ELEMENT_PARAMETER = "parameter";
    protected static final String ATTRIBUTE_NAME = "name";
    protected static final String ATTRIBUTE_VALUE = "value";
    protected static final String ELEMENT_VIEW = "view";
    protected static final String ELEMENT_TRANSACTION = "transaction";
    protected static final String ATTRIBUTE_POLICY = "policy";
    protected static final String VALUE_OPTIMISTIC = "optimistic";
    protected static final String VALUE_PESSIMISTIC = "pessimistic";
    protected static final String ELEMENT_EXIT = "exit";
    protected static final String ATTRIBUTE_USECASE = "usecase";

    private boolean isOptimistic = true;

    /**
     * @return <code>true</code> if the transaction policy is optimistic offline lock,
     *         <code>false</code> if it is pessimistic offline lock.
     */
    public boolean isOptimistic() {
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
            try {
                view.service(this.manager);
            } catch (ServiceException e) {
                throw new ConfigurationException("Couldn't service view: ", e);
            }
            view.configure(viewConfig);
        }

        Configuration transactionConfig = config.getChild(ELEMENT_TRANSACTION, false);
        if (transactionConfig != null) {
            String policy = transactionConfig.getAttribute(ATTRIBUTE_POLICY);
            if (policy.equals(VALUE_PESSIMISTIC)) {
                this.isOptimistic = false;
            }
        }

        Configuration exitConfig = config.getChild(ELEMENT_EXIT, false);
        if (exitConfig != null) {
            this.exitUsecaseName = exitConfig.getAttribute(ATTRIBUTE_USECASE);
            Configuration[] exitParameterConfigs = exitConfig.getChildren(ELEMENT_PARAMETER);
            for (int i = 0; i < exitParameterConfigs.length; i++) {
                String name = exitParameterConfigs[i].getAttribute(ATTRIBUTE_NAME);
                String value = null;
                String[] attributeNames = exitParameterConfigs[i].getAttributeNames();
                for (int j = 0; j < attributeNames.length; j++) {
                    if (attributeNames[j].equals(ATTRIBUTE_VALUE))
                        value = exitParameterConfigs[i].getAttribute(ATTRIBUTE_VALUE);
                }
                setExitParameter(name, value);
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
    protected Node[] getNodesToLock() throws UsecaseException {
        return new Node[0];
    }

    /**
     * <p>
     * This method starts the transaction and locks all involved objects immediately.
     * This way, all changes to the objects in the session occur after the locking,
     * avoiding overriding changes of other sessions.
     * </p>
     * <p>
     * This method is locked via the class lock to avoid inter-usecase synchronization issues.
     * </p>
     * @see org.apache.lenya.cms.usecase.Usecase#lockInvolvedObjects()
     */
    public final void lockInvolvedObjects() throws UsecaseException {
        try {
            startTransaction();
        } catch (RepositoryException e) {
            throw new UsecaseException(e);
        }
        synchronized (TransactionLock.LOCK) {
            lockInvolvedObjects(getNodesToLock());
        }
        advanceState("lockInvolvedObjects");
    }

    /**
     * Start a transaction by using a new, modifiable session.
     * @throws RepositoryException if an error occurs.
     */
    protected void startTransaction() throws RepositoryException {
        if (this.commitEnabled) {
            setSession(RepositoryUtil.createSession(this.manager, getSession().getIdentity(), true));
        }
    }

    /**
     * <p>
     * Lock the objects, for example when you need to change them (for example, delete). If you know
     * when entering the usecase what these objects are, you do not need to call this, the framework
     * will take of it if you implement getObjectsToLock(). If you do not know in advance what the
     * objects are, you can call this method explicitly when appropriate.
     * </p>
     * 
     * @param objects the transactionable objects to lock
     * @throws UsecaseException if an error occurs.
     * @see #lockInvolvedObjects()
     * @see #getNodesToLock()
     */
    public final void lockInvolvedObjects(Node[] objects) throws UsecaseException {
        try {
            for (int i = 0; i < objects.length; i++) {
                if (!objects[i].isLocked()) {
                    objects[i].lock();
                }
                if (!isOptimistic() && !objects[i].isCheckedOutBySession(getSession())) {
                    objects[i].checkout(checkoutRestrictedToSession());
                }
            }
        } catch (RepositoryException e) {
            throw new UsecaseException(e);
        }
    }

    protected boolean canCheckOut(Node[] objects) throws RepositoryException {
        boolean canExecute = true;

        for (int i = 0; i < objects.length; i++) {
            if (objects[i].isCheckedOut() && !objects[i].isCheckedOutBySession(getSession())) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(
                            "AbstractUsecase::lockInvolvedObjects() can not execute, object ["
                                    + objects[i] + "] is already checked out");
                }
                canExecute = false;
            }
        }
        return canExecute;
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#cancel()
     */
    public void cancel() throws UsecaseException {
        if (getSession().isModifiable()) {
            try {
                getSession().rollback();
            } catch (Exception e) {
                throw new UsecaseException(e);
            }
        }
    }

    private String exitUsecaseName = null;
    private Map exitUsecaseParameters = new HashMap();

    /**
     * Sets a parameter to pass to the exit usecase.
     * @param name The parameter name.
     * @param value The parameter value.
     */
    protected void setExitParameter(String name, String value) {
        this.exitUsecaseParameters.put(name, value);
    }

    /**
     * Returns the query string to access the exit usecase of this usecase.
     * @return A query string of the form
     *         <code>?lenya.usecase=...&amp;param1=foo&amp;param2=bar</code>.
     */
    protected String getExitQueryString() {
        StringBuffer queryBuffer = new StringBuffer();
        if (this.exitUsecaseName != null) {
            queryBuffer.append("?lenya.usecase=").append(this.exitUsecaseName);
            for (Iterator i = this.exitUsecaseParameters.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                String value = (String) this.exitUsecaseParameters.get(key);
                queryBuffer.append("&").append(key).append("=").append(value);
            }
        } else {
            String exitUsecase = getParameterAsString("lenya.exitUsecase");
            if (exitUsecase != null && !"".equals(exitUsecase)) {
                queryBuffer.append("?lenya.usecase=").append(exitUsecase);
            }
        }
        return queryBuffer.toString();
    }

    public Session getSession() {
        return (Session) getParameter(PARAMETER_SESSION);
    }

    protected Context context;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException {
        this.context = context;
    }

    protected ServiceManager manager;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    protected void setSession(org.apache.lenya.cms.repository.Session session) {
        setParameter(PARAMETER_SESSION, session);
    }

    private boolean commitEnabled = true;

    public void setTestSession(Session session) {
        this.commitEnabled = false;
        setSession(session);
    }
    
    protected boolean checkoutRestrictedToSession() {
        return getParameterAsBoolean(PARAMETER_CHECKOUT_RESTRICTED_TO_SESSION, true);
    }

}
