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

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;

/**
 * Access controller resolver composed of other access controller resolvers.
 * The member resolvers are called one after the other to resolve the access controllers.
 * 
 * @version $Id$
 */
public class ComposableAccessControllerResolver
    extends AbstractAccessControllerResolver
    implements Configurable, Disposable {

    /**
     * @see org.apache.lenya.ac.impl.AbstractAccessControllerResolver#doResolveAccessController(java.lang.String)
     */
    public AccessController doResolveAccessController(String url) throws AccessControlException {

        AccessController controller = null;

        try {
            
            if (this.selector == null) {
                this.selector =
                    (ServiceSelector) getManager().lookup(AccessControllerResolver.ROLE + "Selector");
            }

            String[] types = getResolverTypes();
            int i = 0;
            while (controller == null && i < types.length) {

                getLogger().debug("Trying to resolve AC resolver for type [" + types[i] + "]");
                AccessControllerResolver resolver =
                    (AccessControllerResolver) this.selector.select(types[i]);
                controller = resolver.resolveAccessController(url);
                setResolver(controller, resolver);
                getLogger().debug("Resolved access controller [" + controller + "]");
                i++;
            }

        } catch (ServiceException e) {
            throw new AccessControlException(e);
        }

        return controller;
    }

    private Map controllerToResolver = new HashMap();

    /**
     * @see org.apache.lenya.ac.AccessControllerResolver#release(org.apache.lenya.ac.AccessController)
     */
    public void release(AccessController controller) {
        assert controller != null;
        AccessControllerResolver resolver = getResolver(controller);
        resolver.release(controller);
        this.selector.release(resolver);
    }

    /**
     * Returns the access controller resolver that was used to resolve a
     * specific access controller.
     * @param controller The access controller.
     * @return An AC resolver.
     */
    protected AccessControllerResolver getResolver(AccessController controller) {
        AccessControllerResolver resolver =
            (AccessControllerResolver) this.controllerToResolver.get(controller);
        return resolver;
    }
    
    /**
     * Sets the access controller resolver that was used to resolve a
     * specific access controller.
     * @param controller The access controller.
     * @param resolver An AC resolver.
     */
    protected void setResolver(AccessController controller, AccessControllerResolver resolver) {
        this.controllerToResolver.put(controller, resolver);
    }

    protected static final String RESOLVER_ELEMENT = "resolver";
    protected static final String TYPE_ATTRIBUTE = "type";

    private String[] resolverTypes;
    private ServiceSelector selector;

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException {
        Configuration[] accessControllerConfigs = configuration.getChildren(RESOLVER_ELEMENT);
        this.resolverTypes = new String[accessControllerConfigs.length];
        for (int i = 0; i < accessControllerConfigs.length; i++) {
            this.resolverTypes[i] = accessControllerConfigs[i].getAttribute(TYPE_ATTRIBUTE);
        }
    }

    /**
     * Returns the access controller types.
     * @return A string array.
     */
    protected String[] getResolverTypes() {
        return this.resolverTypes;
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        if (this.selector != null) {
            getManager().release(this.selector);
        }
    }

}
