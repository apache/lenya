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

/* $Id: ConfigurableAccessControllerResolver.java,v 1.2 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.ac.impl;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;

public class ConfigurableAccessControllerResolver
    extends AbstractAccessControllerResolver
    implements Configurable {

    /**
     * @see org.apache.lenya.cms.ac2.AbstractAccessControllerResolver#doResolveAccessController(java.lang.String)
     */
    public AccessController doResolveAccessController(String webappUrl)
        throws AccessControlException {
        AccessController accessController = null;

        try {
            accessController =
                (AccessController) getManager().lookup(
                    AccessController.ROLE + "/" + accessControllerType);

            if (accessController instanceof Configurable) {
                ((Configurable) accessController).configure(accessControllerConfiguration);
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

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException {
        accessControllerConfiguration = configuration.getChild(ACCESS_CONTROLLER_ELEMENT);
        accessControllerType = accessControllerConfiguration.getAttribute(TYPE_ATTRIBUTE);
    }

}
