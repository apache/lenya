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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.ErrorHandler;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.RoleManager;

/**
 * Policy-based authorizer.
 * @version $Id: PolicyAuthorizer.java 473841 2006-11-12 00:46:38Z gregor $
 */
public class PolicyAuthorizer extends AbstractLogEnabled implements Authorizer {
    
    /**
     * Bullet for list of accreditables.
     */
    protected static final String ACCREDITABLE_ERROR_PREFIX = "\u00A0\u2022\u00A0";

    /**
     * Returns the accreditable manager.
     * @return An accreditable manager.
     */
    public AccreditableManager getAccreditableManager() {
        return accreditableManager;
    }

    /**
     * Returns the policy manager.
     * @return A policy manager.
     */
    public PolicyManager getPolicyManager() {
        return policyManager;
    }

    /**
     * Creates a new policy authorizer.
     */
    public PolicyAuthorizer() {
    }

    private PolicyManager policyManager;

    /**
     * Sets the policy manager.
     * @param manager A policy manager.
     */
    public void setPolicyManager(PolicyManager manager) {
        assert manager != null;
        policyManager = manager;
    }

    private AccreditableManager accreditableManager;

    /**
     * Sets the accreditable manager.
     * @param manager An accreditable manager.
     */
    public void setAccreditableManager(AccreditableManager manager) {
        assert manager != null;
        accreditableManager = manager;
    }

    /**
     * @see org.apache.lenya.ac.Authorizer#authorize(Request, ErrorHandler)
     */
    public boolean authorize(Request request, ErrorHandler handler) throws AccessControlException {

        Session session = request.getSession(true);
        Identity identity = (Identity) session.getAttribute(Identity.class.getName());

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Trying to authorize identity: " + identity);
        }

        boolean authorized = authorizePolicy(identity, request, handler);
        getLogger().debug("Authorized: " + authorized);

        return authorized;
    }

    /**
     * Authorizes an request for an identity depending on a policy.
     * @param identity The identity to authorize.
     * @param request The request to authorize.
     * @param handler The error handler.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    protected boolean authorizePolicy(Identity identity, Request request, ErrorHandler handler)
            throws AccessControlException {

        String requestUri = request.getRequestURI();
        String context = request.getContextPath();

        if (context == null) {
            context = "";
        }

        String url = requestUri.substring(context.length());

        Policy policy = getPolicyManager().getPolicy(getAccreditableManager(), url);
        Role[] roles = policy.getRoles(identity);
        saveRoles(request, roles);

        boolean authorized = roles.length > 0;
        
        if (!authorized) {
            Set accreditables = new HashSet();
            RoleManager roleManager = getAccreditableManager().getRoleManager();
            Role[] allRoles = roleManager.getRoles();
            for (int i = 0; i < allRoles.length; i++) {
                Accreditable[] accrs = policy.getAccreditables(allRoles[i]);
                accreditables.addAll(Arrays.asList(accrs));
            }
            if (identity.getUserReference() != null && !accreditables.isEmpty()) {
                handler.error("page-can-only-accessed-by");
                for (Iterator i = accreditables.iterator(); i.hasNext(); ) {
                    Accreditable accreditable = (Accreditable) i.next();
                    handler.error(ACCREDITABLE_ERROR_PREFIX + accreditable.getName());
                }
            }
        }
        
        return authorized;
    }

    protected Set getRoleIds(RoleManager roleManager) {
        Set roleIds = new HashSet();
        Role[] allRoles = roleManager.getRoles();
        for (int i = 0; i < allRoles.length; i++) {
            roleIds.add(allRoles[i].getId());
        }
        return roleIds;
    }

    /**
     * Saves the roles of the current identity to the request.
     * @param request The request.
     * @param roles The roles.
     */
    protected void saveRoles(Request request, Role[] roles) {
        if (getLogger().isDebugEnabled()) {
            StringBuffer rolesStrBuffer = new StringBuffer();
            for (int i = 0; i < roles.length; i++) {
                rolesStrBuffer.append(" ").append(roles[i]);
            }
            getLogger().debug(
                    "Adding roles [" + rolesStrBuffer.toString() + " ] to request [" + request
                            + "]");
        }
        request.setAttribute(AbstractRole.class.getName(), Arrays.asList(roles));
    }

    /**
     * Fetches the stored roles from the request.
     * @param request The request.
     * @return A role array.
     * @throws AccessControlException If the request does not contain the roles
     *         list.
     */
    public static Role[] getRoles(Request request) throws AccessControlException {
        List roleList = (List) request.getAttribute(AbstractRole.class.getName());

        if (roleList == null) {
            StringBuffer message = new StringBuffer("    URI: [").append(request.getRequestURI())
                    .append("]\n");
            for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                message.append("    Parameter: [").append(key).append("] = [").append(
                        request.getParameter(key)).append("]\n");
            }
            throw new AccessControlException("Request [" + request + "] does not contain roles: \n"
                    + message.toString());
        }

        Role[] roles = (Role[]) roleList.toArray(new Role[roleList.size()]);
        return roles;
    }
}
