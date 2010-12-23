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

package org.apache.lenya.cms.ac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserManager;

/**
 * Policy utility class.
 */
public final class PolicyUtil {

    /**
     * Fetches the stored roles from the request.
     * @param request The request.
     * @return A role array.
     * @throws AccessControlException If the request does not contain the roles
     *         list.
     */
    public static final Role[] getRoles(Request request) throws AccessControlException {
        List roleList = (List) request.getAttribute(Role.class.getName());

        if (roleList == null) {
            StringBuffer buf = new StringBuffer();
            buf.append("    URI: [" + request.getRequestURI() + "]\n");
            for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                buf.append("    Parameter: [" + key + "] = [" + request.getParameter(key) + "]\n");
            }

            throw new AccessControlException("Request [" + request + "] does not contain roles: \n"
                    + buf.toString());
        }

        Role[] roles = (Role[]) roleList.toArray(new Role[roleList.size()]);
        return roles;
    }
    
    /**
     * @param manager The service manager.
     * @param webappUrl The web application URL.
     * @param userId The user ID.
     * @param logger The logger.
     * @return A user.
     * @throws AccessControlException if an error occurs.
     */
    public static final User getUser(ServiceManager manager, String webappUrl,
            String userId, Logger logger) throws AccessControlException {
        ServiceSelector selector = null;
        AccessControllerResolver resolver = null;
        AccessController controller = null;
        try {
            selector = (ServiceSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");
            resolver = (AccessControllerResolver) selector
                    .select(AccessControllerResolver.DEFAULT_RESOLVER);
            controller = resolver.resolveAccessController(webappUrl);

            AccreditableManager accreditableManager = controller.getAccreditableManager();
            UserManager userManager = accreditableManager.getUserManager();
            
            return userManager.getUser(userId);
        } catch (ServiceException e) {
            throw new AccessControlException(e);
        } finally {
            if (selector != null) {
                if (resolver != null) {
                    if (controller != null) {
                        resolver.release(controller);
                    }
                    selector.release(resolver);
                }
                manager.release(selector);
            }
        }

    }

    /**
     * @param manager The service manager.
     * @param webappUrl The web application URL.
     * @param role The ID of the role.
     * @param logger The logger to use.
     * @return All users which have the role on this URL.
     * @throws AccessControlException if an error occurs.
     */
    public static final User[] getUsersWithRole(ServiceManager manager, String webappUrl,
            String role, Logger logger) throws AccessControlException {
        ServiceSelector selector = null;
        AccessControllerResolver resolver = null;
        AccessController controller = null;
        try {
            selector = (ServiceSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");
            resolver = (AccessControllerResolver) selector
                    .select(AccessControllerResolver.DEFAULT_RESOLVER);
            controller = resolver.resolveAccessController(webappUrl);

            AccreditableManager accreditableManager = controller.getAccreditableManager();
            UserManager userManager = accreditableManager.getUserManager();
            User[] users = userManager.getUsers();
            List usersWithRole = new ArrayList();
            PolicyManager policyManager = controller.getPolicyManager();

            Role roleObject = accreditableManager.getRoleManager().getRole(role);

            for (int i = 0; i < users.length; i++) {
                Identity identity = new Identity();
                identity.addIdentifiable(users[i]);
                Role[] roles = policyManager.getGrantedRoles(accreditableManager, identity,
                        webappUrl);
                if (Arrays.asList(roles).contains(roleObject)) {
                    usersWithRole.add(users[i]);
                }
            }

            return (User[]) usersWithRole.toArray(new User[usersWithRole.size()]);
        } catch (ServiceException e) {
            throw new AccessControlException(e);
        } finally {
            if (selector != null) {
                if (resolver != null) {
                    if (controller != null) {
                        resolver.release(controller);
                    }
                    selector.release(resolver);
                }
                manager.release(selector);
            }
        }
    }

}
