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

import java.util.List;

import org.apache.cocoon.servlet.multipart.Part;

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
     * Returns the current value of a parameter as a string.
     * @param name The parameter name.
     * @return A string or <code>null</code> if the parameter was not set.
     */
    String getParameterAsString(String name);

    /**
     * Sets a parameter from the form. This method is called for parts in
     * multipart requests.
     * @param name The parameter name.
     * @param value The parameter value.
     */
    void setPart(String name, Part value);

    /**
     * Advances the usecase to the next step. This method is called when all
     * parameters are set.
     */
    void advance();

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
     * Returns the error messages from the previous operation. Error messages
     * prevent the operation from being executed.
     * @return A list of strings.
     */
    List getErrorMessages();

    /**
     * Returns the info messages from the previous operation. Info messages do
     * not prevent the operation from being executed.
     * @return A list of strings.
     */
    List getInfoMessages();

    /**
     * Executes the usecase. During this method error and info messages are
     * filled in. If getErrorMessages() returns an empty array, the operation
     * succeeded. Otherwise, the operation failed.
     * @throws UsecaseException if an error occured that causes an unstable
     *             system.
     */
    void execute() throws UsecaseException;

    /**
     * Returns the webapp URL which should be redirected to after the usecase is
     * completed.
     * @param success If the usecase was completed successfully.
     * @return A web application URL.
     */
    String getTargetURL(boolean success);

    /**
     * @return If the usecase is interactive, i.e. a confirmation screen should
     *         be shown.
     */
    boolean isInteractive();

}