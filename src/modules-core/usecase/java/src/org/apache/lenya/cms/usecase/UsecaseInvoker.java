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
import java.util.Map;

import org.apache.lenya.cms.repository.Session;

/**
 * <p>
 * This service allows to invoke a usecase in a convenient way. A typical usage
 * scenario is the composition of usecases - you can invoke one or multiple
 * "child" usecases from another usecase.
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 *     UsecaseInvoker invoker = null;
 *     try {
 *         invoker = (UsecaseInvoker) this.manager.lookup(UsecaseInvoker.ROLE);
 *         Map params = new HashMap();
 *         params.put(..., ...);
 *         invoker.invoke(getSourceUrl(), childUsecaseName, params);
 *     
 *         if (invoker.getResult() != UsecaseInvoker.SUCCESS) {
 *             List messages = invoker.getErrorMessages();
 *             for (Iterator i = messages.iterator(); i.hasNext();) {
 *                 UsecaseMessage message = (UsecaseMessage) i.next();
 *                 addErrorMessage(message.getMessage(), message.getParameters());
 *             }
 *         }
 *     } finally {
 *         if (invoker != null) {
 *             this.manager.release(invoker);
 *         }
 *     }
 *     
 * </pre>
 * 
 * @version $Id$
 */
public interface UsecaseInvoker {

    /**
     * The Avalon role.
     */
    String ROLE = UsecaseInvoker.class.getName();

    /**
     * Invokes a usecase.
     * @param webappUrl The URL to invoke the usecase on.
     * @param usecaseName The name of the usecase.
     * @param parameters The parameters.
     * @throws UsecaseException if an error occurs.
     */
    void invoke(String webappUrl, String usecaseName, Map parameters) throws UsecaseException;

    /**
     * @return The result of the invocation.
     */
    int getResult();

    /**
     * The invocation was successful.
     */
    int SUCCESS = 0;

    /**
     * The precondition check failed.
     */
    int PRECONDITIONS_FAILED = 1;

    /**
     * The execution condition check failed.
     */
    int EXECUTION_CONDITIONS_FAILED = 2;

    /**
     * The execution itself failed.
     */
    int EXECUTION_FAILED = 3;

    /**
     * The postcondition check failed.
     */
    int POSTCONDITIONS_FAILED = 4;

    /**
     * The authorization failed.
     */
    int AUTHORIZATION_FAILED = 5;

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
     * @return The target URL of the usecase, based on the success. This method
     *         throws a RuntimeException if the usecase hasn't been executed
     *         yet.
     */
    String getTargetUrl();

    /**
     * @param session The test session to use.
     */
    void setTestSession(Session session);

}
