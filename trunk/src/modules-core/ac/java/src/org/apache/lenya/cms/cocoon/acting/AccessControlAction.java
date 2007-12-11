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

package org.apache.lenya.cms.cocoon.acting;

import java.util.Collections;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.acting.ConfigurableServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.util.ServletHelper;

/**
 * Super class for access control actions.
 * 
 * @version $Id$
 */
public abstract class AccessControlAction extends ConfigurableServiceableAction {

    private AccessController accessController;

    /**
     * <p>
     * Invokes the access control functionality.
     * If no access controller was found for the requested URL, an empty map is returned.
     * </p>
     * <p>
     * This is a template method. Implement doAct() to add your functionality.
     * </p>
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector, org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(
        Redirector redirector,
        SourceResolver sourceResolver,
        Map objectModel,
        String source,
        Parameters parameters)
        throws Exception {

        ServiceSelector selector = null;
        AccessControllerResolver resolver = null;
        this.accessController = null;

        Request request = ObjectModelHelper.getRequest(objectModel);

        Map result = null;

        try {
            selector =
                (ServiceSelector) this.manager.lookup(AccessControllerResolver.ROLE + "Selector");
                
            getLogger().debug("Resolving AC resolver for type [" + AccessControllerResolver.DEFAULT_RESOLVER + "]");
            resolver =
                (AccessControllerResolver) selector.select(
                    AccessControllerResolver.DEFAULT_RESOLVER);
            getLogger().debug("Resolved AC resolver [" + resolver + "]");

            String webappUrl = ServletHelper.getWebappURI(request);
            this.accessController = resolver.resolveAccessController(webappUrl);

            if (this.accessController == null) {
                result = Collections.EMPTY_MAP;
            } else {
                this.accessController.setupIdentity(request);
                result = doAct(redirector, sourceResolver, objectModel, source, parameters);
            }

        } finally {
            if (selector != null) {
                if (resolver != null) {
                    selector.release(resolver);
                }
                this.manager.release(selector);
            }
        }
        return result;
    }

    /**
     * The actual act method.
     * @param redirector  The <code>Redirector</code> in charge
     * @param resolver    The <code>SourceResolver</code> in charge
     * @param objectModel The <code>Map</code> with object of the
     *                    calling environment which can be used
     *                    to select values this controller may need
     *                    (ie Request, Response).
     * @param source      A source <code>String</code> to the Action
     * @param parameters  The <code>Parameters</code> for this invocation
     * @return Map        The returned <code>Map</code> object with
     *                    sitemap substitution values which can be used
     *                    in subsequent elements attributes like src=
     *                    using a xpath like expression: src="mydir/{myval}/foo"
     *                    If the return value is null the processing inside
     *                    the <map:act> element of the sitemap will
     *                    be skipped.
     * @exception Exception Indicates something is totally wrong
     */
    protected abstract Map doAct(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String source,
        Parameters parameters)
        throws Exception;

    /**
     * Returns the access controller.
     * @return An access controller.
     */
    public AccessController getAccessController() {
        return this.accessController;
    }

}
