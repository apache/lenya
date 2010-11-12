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

/* $Id$  */

package org.apache.lenya.ac;

import org.apache.avalon.framework.component.Component;

/**
 * An access controller resolver resolves the appropriate access controller
 * for a given Cocoon object model.
 */
public interface AccessControllerResolver extends Component {
    
    /**
     * The Avalon role.
     */
    String ROLE = AccessControllerResolver.class.getName();

    /**
     * Resolves an access controller for a certain URL.
     * @param webappUrl The URL within the web application (without context prefix).
     * @return An access controller or <code>null</code> if no controller could be resolved.
     * @throws AccessControlException when something went wrong.
     */
    AccessController resolveAccessController(String webappUrl) throws AccessControlException;
    
}
