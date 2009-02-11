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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;

/**
 * Access controller resolver composed of other access controller resolvers. The member resolvers
 * are called one after the other to resolve the access controllers.
 * 
 * @version $Id$
 */
public class ComposableAccessControllerResolver extends AbstractAccessControllerResolver {

    /**
     * @see org.apache.lenya.ac.impl.AbstractAccessControllerResolver#doResolveAccessController(java.lang.String)
     */
    public AccessController doResolveAccessController(String url) throws AccessControlException {

        AccessController controller = null;

        Iterator i = this.resolvers.iterator();
        while (controller == null && i.hasNext()) {
            AccessControllerResolver resolver = (AccessControllerResolver) i.next();
            controller = resolver.resolveAccessController(url);
            setResolver(controller, resolver);
            getLogger().debug("Resolved access controller [" + controller + "]");
        }

        return controller;
    }

    private Map controllerToResolver = new HashMap();

    /**
     * Returns the access controller resolver that was used to resolve a specific access controller.
     * @param controller The access controller.
     * @return An AC resolver.
     */
    protected AccessControllerResolver getResolver(AccessController controller) {
        AccessControllerResolver resolver = (AccessControllerResolver) this.controllerToResolver
                .get(controller);
        return resolver;
    }

    /**
     * Sets the access controller resolver that was used to resolve a specific access controller.
     * @param controller The access controller.
     * @param resolver An AC resolver.
     */
    protected void setResolver(AccessController controller, AccessControllerResolver resolver) {
        this.controllerToResolver.put(controller, resolver);
    }

    protected static final String RESOLVER_ELEMENT = "resolver";
    protected static final String TYPE_ATTRIBUTE = "type";

    private List resolvers = new ArrayList();
    
    public void setResolvers(List resolvers) {
        this.resolvers = resolvers;
    }

}
