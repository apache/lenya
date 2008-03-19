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

package org.apache.lenya.ac;

import org.apache.avalon.framework.component.Component;
import org.apache.cocoon.environment.Request;

/**
 * An authenticator.
 * @version $Id: Authenticator.java 473841 2006-11-12 00:46:38Z gregor $
 */
public interface Authenticator extends Component {

    /**
     * Avalon role.
     */
    String ROLE = Authenticator.class.getName();
    
    /**
     * The default authenticator type.
     */
    String DEFAULT_AUTHENTICATOR = "user";

    /**
     * Authenticates a request.
     * @param accreditableManager The accreditable manager to use.
     * @param request The request.
     * @param handler The error handler.
     * @return <code>true</code> if the request is authenticated,
     *         <code>false</code> otherwise.
     * @throws AccessControlException when something went wrong.
     */
    boolean authenticate(AccreditableManager accreditableManager, Request request,
            ErrorHandler handler) throws AccessControlException;

    /**
     * The login URI for a certain request.
     * @param request The request.
     * @return A string.
     */
    String getLoginUri(Request request);
    
    /**
     * The target URI, i.e. the URI to redirect to after a successful authentication.
     * @param request The request containing the authentication data.
     * @return A string.
     */
    String getTargetUri(Request request);
}
