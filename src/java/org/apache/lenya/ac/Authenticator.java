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
 * @version $Id$
 */
public interface Authenticator extends Component {

    /**
     * Avalon role.
     */
    String ROLE = Authenticator.class.getName();

    /**
     * Authenticates a request.
     * @param accreditableManager The accreditable manager to use.
     * @param request The request.
     * @return <code>true</code> if the request is authenticated, <code>false</code> otherwise.
     * @throws AccessControlException when something went wrong.
     */
    boolean authenticate(AccreditableManager accreditableManager, Request request)
        throws AccessControlException;
}
