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
 * An access controller allows authenticating and authorizing identities.
 * @version $Id$
 */
public interface AccessController extends Component {

    /**
     * The access control namespace URI.
     */
    String NAMESPACE = "http://apache.org/cocoon/lenya/ac/1.0";

    /**
     * The default prefix for the access control namespace.
     */
    String DEFAULT_PREFIX = "ac";

    /**
     * The Avalon role.
     */
    String ROLE = AccessController.class.getName();

    /**
     * Authenticates a request.
     * @param request A request.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    boolean authenticate(Request request) throws AccessControlException;

    /**
     * Authorizes a request inside a publication.
     * @param request A request.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    boolean authorize(Request request) throws AccessControlException;

    /**
     * Initializes the identity for this access controller.
     * @param request The request that contains the identity information.
     * @throws AccessControlException when something went wrong.
     */
    void setupIdentity(Request request) throws AccessControlException;

    /**
     * Returns the accreditable manager.
     * @return An accreditable manager.
     */
    AccreditableManager getAccreditableManager();

    /**
     * Returns the policy manager.
     * @return A policy manager.
     */
    PolicyManager getPolicyManager();
    
    /**
     * @return The authorizers.
     */
    Authorizer[] getAuthorizers();
    
}