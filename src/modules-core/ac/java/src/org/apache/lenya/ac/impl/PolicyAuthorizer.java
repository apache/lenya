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

import java.util.Arrays;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.Role;
import org.apache.lenya.util.ServletHelper;

/**
 * Policy-based authorizer.
 * @version $Id$
 */
public class PolicyAuthorizer extends AbstractLogEnabled implements Authorizer {

    private PolicyManager policyManager;
    private AccreditableManager accreditableManager;

    /**
     * Creates a new policy authorizer.
     */
    public PolicyAuthorizer() {
        // do nothing
    }

    /**
     * @see org.apache.lenya.ac.Authorizer#authorize(org.apache.cocoon.environment.Request)
     */
    public boolean authorize(Request request)
        throws AccessControlException {
        return authorize(request, ServletHelper.getWebappURI(request));
    }

    protected boolean authorize(Request request, String webappUrl) throws AccessControlException {
        Session session = request.getSession(true);
        Identity identity = (Identity) session.getAttribute(Identity.class.getName());

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Trying to authorize identity: " + identity);
        }

        boolean authorized;

        if (identity.belongsTo(getAccreditableManager())) {
            authorized = authorizePolicy(identity, request, webappUrl);
        } else {
            getLogger().debug(
                "Identity ["
                    + identity
                    + "] not authorized - belongs to wrong accreditable manager.");
            authorized = false;
        }

        getLogger().debug("Authorized: " + authorized);

        return authorized;
    }

   /**
     * Sets the policy manager.
     * @param manager A policy manager.
     */
    public void setPolicyManager(PolicyManager manager) {
        assert manager != null;
        this.policyManager = manager;
    }

    /**
     * Returns the policy manager.
     * @return A policy manager.
     */
    public PolicyManager getPolicyManager() {
        return this.policyManager;
    }

    /**
     * Sets the accreditable manager.
     * @param manager An accreditable manager.
     */
    public void setAccreditableManager(AccreditableManager manager) {
        assert manager != null;
        this.accreditableManager = manager;
    }

    /**
     * Returns the accreditable manager.
     * @return An accreditable manager.
     */
    public AccreditableManager getAccreditableManager() {
        return this.accreditableManager;
    }

     /**
     * Authorizes an request for an identity depending on a policy.
     * @param identity The identity to authorize.
     * @param request The request to authorize.
     * @param webappUrl The web application URL.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    protected boolean authorizePolicy(
        Identity identity,
        Request request,
        String webappUrl)
        throws AccessControlException {

        Role[] roles = getPolicyManager().getGrantedRoles(getAccreditableManager(), identity, webappUrl);
        saveRoles(request, roles);
        return roles.length > 0;
    }

    /**
     * Saves the roles of the current identity to the request.
     * @param request The request.
     * @param roles The roles.
     */
    protected void saveRoles(Request request, Role[] roles) {
        String rolesString = "";
        for (int i = 0; i < roles.length; i++) {
            rolesString += " " + roles[i];
        }
        getLogger().debug("Adding roles [" + rolesString + " ] to request [" + request + "]");
        request.setAttribute(Role.class.getName(), Arrays.asList(roles));
    }

}
