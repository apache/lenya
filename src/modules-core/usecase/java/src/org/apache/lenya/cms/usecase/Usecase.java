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

import java.util.List;

import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.repository.Session;

/**
 * Usecase interface.
 * 
 * @version $Id$
 */
public interface Usecase {

    /**
     * The <code>Usecase</code> role.
     */
    String ROLE = Usecase.class.getName();
    
    /**
     * The <em>itemState</em> parameter is used to ask the usecase if the corresponding menu item
     * is checked. The parameter value is supposed to be a {@link java.lang.Boolean} object. If the
     * value is null, the <em>checked</em> attribute is not added.
     */
    public static final String PARAMETER_ITEM_STATE = "itemState";
    
    /**
     * @param url The URL the usecase is invoked on.
     */
    void setSourceURL(String url);

    /**
     * Sets a parameter from the form.
     * @param name The parameter name.
     * @param value The parameter value.
     */
    void setParameter(String name, Object value);

    /**
     * Returns the current value of a parameter.
     * @param name The parameter name.
     * @return An object.
     */
    Object getParameter(String name);

    /**
     * Returns the current value of a parameter.
     * @param name The parameter name.
     * @param defaultValue The default value to return if the parameter is not set.
     * @return An object.
     */
    Object getParameter(String name, Object defaultValue);

    /**
     * Returns the current value of a parameter as a string.
     * @param name The parameter name.
     * @return A string or <code>null</code> if the parameter was not set.
     */
    String getParameterAsString(String name);
    
    /**
     * @return The parameter names.
     */
    String[] getParameterNames();

    /**
     * Sets a parameter from the form. This method is called for parts in
     * multipart requests.
     * @param name The parameter name.
     * @param value The parameter value.
     */
    void setPart(String name, Part value);

    /**
     * Returns the current value of a part parameter as a string.
     * @param name The part parameter name.
     * @return A part or <code>null</code> if the part was not set.
     */
    Part getPart(String name);

    /**
     * Advances the usecase to the next step. This method is called when all
     * parameters are set.
     * @throws UsecaseException if an error occurs.
     */
    void advance() throws UsecaseException;

    /**
     * Checks the conditions before a form is displayed.
     * @throws UsecaseException if an error occurs that causes an unstable
     *             system.
     */
    void checkPreconditions() throws UsecaseException;

    /**
     * Checks the conditions after the usecase was executed.
     * @throws UsecaseException if an error occurs that causes an unstable
     *             system.
     */
    void checkPostconditions() throws UsecaseException;

    /**
     * Checks the conditions right before the operation is executed.
     * @throws UsecaseException if an error occurs that causes an unstable
     *             system.
     */
    void checkExecutionConditions() throws UsecaseException;
    
    /**
     * Locks all objects that are involved in the transaction.
     * @throws UsecaseException if an error occurs.
     */
    void lockInvolvedObjects() throws UsecaseException;

    /**
     * Returns the error messages from the previous operation. Error messages
     * prevent the operation from being executed.
     * @return A list of {@link UsecaseMessage} objects.
     */
    List getErrorMessages();

    /**
     * Returns the info messages from the previous operation. Info messages do
     * not prevent the operation from being executed.
     * @return A list of {@link UsecaseMessage} objects.
     */
    List getInfoMessages();

    /**
     * Determine if the usecase has error messages.
     * Provides a way of checking for errors without actually retrieving them.
     * @return true if the usecase resulted in error messages.
     */
    public boolean hasErrors();

    /**
     * Determine if the usecase has info messages.
     * Provides a way of checking for info messages without actually retrieving them.
     * @return true if the usecase resulted in info messages being generated.
     */
    public boolean hasInfoMessages();

    /**
     * Executes the usecase. During this method error and info messages are
     * filled in. If getErrorMessages() returns an empty array, the operation
     * succeeded. Otherwise, the operation failed.
     * @throws UsecaseException if an error occured that causes an unstable
     *             system.
     */
    void execute() throws UsecaseException;
    
    /**
     * Cancels the usecase.
     * @throws UsecaseException if an error occurs.
     */
    void cancel() throws UsecaseException;
    
    /**
     * @return The web application URL the usecase was invoked on.
     */
    String getSourceURL();

    /**
     * Returns the webapp URL which should be redirected to after the usecase is
     * completed.
     * @param success If the usecase was completed successfully.
     * @return A web application URL.
     */
    String getTargetURL(boolean success);

    /**
     * @param name The name of this usecase.
     */
    void setName(String name);
    
    /**
     * @return The name of this usecase.
     */
    String getName();
    
    /**
     * @return The view of the usecase.
     */
    UsecaseView getView();

    /**
     * @param view The view of the usecase.
     */
    void setView(UsecaseView view);

    /**
     * @return The repository session.
     */
    Session getSession();
    
    /**
     * If you invoke this method, the usecase won't use its own isolated session,
     * but the passed test session. The session will not be committed when the usecase
     * is invoked, so you can check it for modifications without modifying the repository.
     * @param session The test session.
     */
    void setTestSession(Session session);
    
    /**
     * @return if the usecase uses optimistic offline lock.
     */
    boolean isOptimistic();
    
    /**
     * Initialize the view.
     * @throws UsecaseException if an error occurs.
     */
    void setupView() throws UsecaseException;
}
