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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.lenya.cms.publication.LockException;
//flo : to suppress when ok import org.apache.lenya.cms.publication.Node;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Repository;
//flo : import org.apache.lenya.cms.publication.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.publication.Session;
//flo : import org.apache.lenya.cms.publication.TransactionLock;
import org.apache.lenya.transaction.TransactionLock;
import org.apache.lenya.utils.URLInformation;
//flo : add identity dependencie
import org.apache.lenya.ac.Identity;


/**
 * Abstract usecase implementation.
 * 
 * @version $Id$
 */
public class AbstractUsecase extends AbstractLogEnabled implements Usecase {

	protected static final String EVENT_CHECK_PRECONDITIONS = "checkPreconditions";
	
	protected static final String EVENT_LOCK_NODES = "lockInvolvedObjects";
	
	protected static final String EVENT_CHECK_EXECUTION_CONDITIONS = "checkExecutionConditions";

	protected static final String EVENT_EXECUTE = "execute";

	protected static final String EVENT_CHECK_POSTCONDITIONS = "checkPostconditions";
    
    protected static final String ERROR_OBJECTS_CHECKED_OUT = "objects-checked-out";
    
    //florent : deal with the retrieve of identity
    protected HttpServletRequest request;

    protected static final StateMachine.Transition[] TRANSITIONS = {
            new StateMachine.Transition("start", "preChecked", EVENT_CHECK_PRECONDITIONS),
            new StateMachine.Transition("preChecked", "preChecked", EVENT_CHECK_PRECONDITIONS),
            new StateMachine.Transition("preChecked", "nodesLocked", EVENT_LOCK_NODES),
            new StateMachine.Transition("nodesLocked", "execChecked",EVENT_CHECK_EXECUTION_CONDITIONS),
            new StateMachine.Transition("execChecked", "execChecked",EVENT_CHECK_EXECUTION_CONDITIONS),
            new StateMachine.Transition("nodesLocked", "preChecked", EVENT_CHECK_PRECONDITIONS),
            new StateMachine.Transition("execChecked", "executed", EVENT_EXECUTE),
            new StateMachine.Transition("executed", "postChecked", EVENT_CHECK_POSTCONDITIONS) };

    protected static final StateMachine.Model MODEL = new StateMachine.Model("start", TRANSITIONS);

    protected static final String PARAMETER_STATE_MACHINE = "private.stateMachine";
    protected static final String PARAMETER_SESSION = "private.session";
    protected static final String PARAMETER_CHECKOUT_RESTRICTED_TO_SESSION = "checkoutRestrictedToSession";

    protected static final String PARAMETERS_INITIALIZED = "private.parametersInitialized";

    protected static final String[] TRANSACTION_POLICIES = { TRANSACTION_POLICY_OPTIMISTIC,
            TRANSACTION_POLICY_PESSIMISTIC, TRANSACTION_POLICY_READONLY };
    protected static final String DEFAULT_TRANSACTION_POLICY = TRANSACTION_POLICY_READONLY;

    protected Repository repository;
    private UsecaseView view;

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
    	String webappUrl = new URLInformation().getWebappUrl();
    	return webappUrl;
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
    public List<UsecaseMessage> getErrorMessages() {
        return Collections.unmodifiableList(new ArrayList<UsecaseMessage>(this.errorMessages));
    }

    /**
     * Returns the information messages to show on the confirmation screen.
     * @return An array of strings. Info messages do not prevent the operation from being executed.
     */
    public List<UsecaseMessage> getInfoMessages() {
        return Collections.unmodifiableList(new ArrayList<UsecaseMessage>(this.infoMessages));
    }

    private List<UsecaseMessage> errorMessages = new ArrayList<UsecaseMessage>();
    private List<UsecaseMessage> infoMessages = new ArrayList<UsecaseMessage>();

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
            dumpErrorMessagesToLog();
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
            
            dumpErrorMessagesToLog();
            
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
            dumpErrorMessagesToLog();
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
            } catch (RepositoryException e) {
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
     * @deprecated use dumpErrorMessagesToLog instead
     */
    protected void dumpErrorMessages() {
        List<UsecaseMessage> _errorMessages = getErrorMessages();
        for (int i = 0; i < _errorMessages.size(); i++) {
            getLogger().error(_errorMessages.get(i).toString());
        }
    }
    
    protected void dumpErrorMessagesToLog() {
    	List<UsecaseMessage> _errorMessages = getErrorMessages();
      for (UsecaseMessage um : _errorMessages){
      	getLogger().info(um.toString());
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
            dumpErrorMessagesToLog();
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

    private Properties parameters = new Properties();

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
    public Map<Object,Object> getParameters() {
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

    /**
     * TODO: Add init-method to bean.
     */
    public final void initialize() {
        ProcessInfoProvider processInfo = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        //florent : deal with identity
        //HttpServletRequest request = processInfo.getRequest();
        this.request = processInfo.getRequest();
        Session session = this.repository.getSession(request);
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
        Set<String> keys = this.parameters.stringPropertyNames();
        return keys.toArray(new String[keys.size()]);
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

    private String transactionPolicy = DEFAULT_TRANSACTION_POLICY;

    public String getTransactionPolicy() {
        return this.transactionPolicy;
    }

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
     * This method starts the transaction and locks all involved objects immediately. This way, all
     * changes to the objects in the session occur after the locking, avoiding overriding changes of
     * other sessions.
     * </p>
     * <p>
     * This method is locked via the class lock to avoid inter-usecase synchronization issues.
     * </p>
     * @see org.apache.lenya.cms.usecase.Usecase#lockInvolvedObjects()
     */
    public final void lockInvolvedObjects() throws UsecaseException {
        startTransaction();
        synchronized (TransactionLock.LOCK) {
            lockInvolvedObjects(getNodesToLock());
        }
        advanceState("lockInvolvedObjects");
    }

    /**
     * Start a transaction by using a new, modifiable session.
     * @throws RepositoryException if an error occurs.
     */
    protected void startTransaction() {
        if (this.commitEnabled && !this.getTransactionPolicy().equals(TRANSACTION_POLICY_READONLY)) {
            //florent : deal with identity 
        		//setSession(this.repository.startSession(getSession().getIdentity(), true));
        	//this under don't work as startSession was suppress from repository api
        	//setSession(this.repository.startSession(Identity.getIdentity(this.request.getSession(false)), true));
        	setSession(this.repository.getSession(this.request));
        	
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
                //florent : remove the .getIdentity
                /*if (!getTransactionPolicy().equals(TRANSACTION_POLICY_OPTIMISTIC)
                        && !objects[i].isCheckedOutBySession(getSession().getId(), getSession()
                                .getIdentity().getUser().getId())) {*/
                
                if (!getTransactionPolicy().equals(TRANSACTION_POLICY_OPTIMISTIC)
                    && !objects[i].isCheckedOutBySession(
                    				getSession().getId(), 
                    				Identity.getIdentity(this.request.getSession(false)).getUser().getId())
                    				) {
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
        	//florent : change for workaround session.getIdentity
            /*if (objects[i].isCheckedOut()
                    && !objects[i].isCheckedOutBySession(getSession().getId(), getSession()
                            .getIdentity().getUser().getId())) {*/
        	if (objects[i].isCheckedOut()
              && !objects[i].isCheckedOutBySession(
              			getSession().getId(), 
              			Identity.getIdentity(this.request.getSession(false)).getUser().getId())
              			) {
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
    private Properties exitUsecaseParameters = new Properties();
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
            for (String key : this.exitUsecaseParameters.stringPropertyNames()){
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

    protected void setSession(Session session) {
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

    protected Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setParameters(Properties params) {
        this.parameters = params;
    }

    public void setTransactionPolicy(String policy) {
        if (!Arrays.asList(TRANSACTION_POLICIES).contains(policy)) {
            throw new IllegalArgumentException("Invalid transaction policy '" + policy
                    + ", must be one of " + TRANSACTION_POLICY_OPTIMISTIC + ", "
                    + TRANSACTION_POLICY_PESSIMISTIC + ", " + TRANSACTION_POLICY_READONLY);
        }
        this.transactionPolicy = policy;
    }

    protected String getExitUsecaseName() {
        return exitUsecaseName;
    }

    public void setExitUsecaseName(String exitUsecaseName) {
        this.exitUsecaseName = exitUsecaseName;
    }

    protected Properties getExitUsecaseParameters() {
        return exitUsecaseParameters;
    }

    public void setExitUsecaseParameters(Properties params) {
        this.exitUsecaseParameters = params;
    }

    protected HttpServletRequest getRequest() {
        ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        HttpServletRequest request = process.getRequest();
        return request;
    }

    private Publication pub;

    /**
     * @return the publication in which the use-case is being executed
     */
    protected Publication getPublication() {
        if (this.pub == null) {	
            String pubId = new URLInformation().getPublicationId();
            this.pub = getSession().getPublication(pubId);
        }
        return this.pub;
    }

}
