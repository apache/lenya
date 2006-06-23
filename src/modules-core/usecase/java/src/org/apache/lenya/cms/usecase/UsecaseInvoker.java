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

import java.util.List;
import java.util.Map;

/**
 * Invoke a usecase.
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

}
