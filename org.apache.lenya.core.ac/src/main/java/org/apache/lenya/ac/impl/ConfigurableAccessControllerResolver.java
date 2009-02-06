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

package org.apache.lenya.ac.impl;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;

/**
 * Configurable access controller resolver.
 * @version $Id$
 */
public class ConfigurableAccessControllerResolver extends AbstractAccessControllerResolver {

    /**
     * @see org.apache.lenya.ac.impl.AbstractAccessControllerResolver#doResolveAccessController(java.lang.String)
     */
    public AccessController doResolveAccessController(String webappUrl)
            throws AccessControlException {
        AccessController accessController = null;

        try {
            accessController = (AccessController) WebAppContextUtils
                    .getCurrentWebApplicationContext().getBean(
                            AccessController.ROLE + "/" + this.accessControllerType);

            if (accessController instanceof Configurable) {
                ((Configurable) accessController).configure(this.accessControllerConfiguration);
            }

        } catch (Exception e) {
            throw new AccessControlException(e);
        }

        return accessController;
    }

    protected static final String ACCESS_CONTROLLER_ELEMENT = "access-controller";
    protected static final String TYPE_ATTRIBUTE = "type";
    private String accessControllerType;

    private Configuration accessControllerConfiguration;

    public void setAccessControllerType(String accessControllerType) {
        this.accessControllerType = accessControllerType;
    }

    public void setAccessControllerConfiguration(Configuration accessControllerConfiguration) {
        this.accessControllerConfiguration = accessControllerConfiguration;
    }

}
