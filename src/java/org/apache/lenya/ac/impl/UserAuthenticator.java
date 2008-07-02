/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *  
 */

package org.apache.lenya.ac.impl;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Authenticator;
import org.apache.lenya.ac.ErrorHandler;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.ManagedUser;
import org.apache.lenya.ac.ManagedUserReference;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.ac.UserReference;
import org.apache.lenya.ac.attr.AttributeSet;
import org.apache.lenya.ac.attr.impl.EmptyAttributeSet;
import org.apache.lenya.cms.publication.util.OutgoingLinkRewriter;
import org.apache.lenya.util.ServletHelper;

/**
 * User authenticator.
 * @version $Id: UserAuthenticator.java 473842 2006-11-12 01:15:20Z gregor $
 */
public class UserAuthenticator extends AbstractLogEnabled implements Authenticator, Serviceable {

    protected ServiceManager manager;

    /**
     * @see org.apache.lenya.ac.Authenticator#authenticate(org.apache.lenya.ac.AccreditableManager,
     *      org.apache.cocoon.environment.Request, ErrorHandler)
     */
    public boolean authenticate(AccreditableManager accreditableManager, Request request,
            ErrorHandler handler) throws AccessControlException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                    "Authenticating username [" + username + "] with password [" + password + "]");
        }

        if (username == null || password == null) {
            throw new AccessControlException("Username or password is null!");
        }

        Identity identity = (Identity) request.getSession(false).getAttribute(
                Identity.class.getName());
        boolean authenticated = authenticate(accreditableManager, username, password, identity,
                handler);
        if (!authenticated) {
            handler.error("Authentication failed");
        }
        return authenticated;
    }

    /**
     * Authenticates a user with a given username and password. When the authentication is
     * successful, the user is added to the identity.
     * @param accreditableManager The accreditable manager.
     * @param username The username.
     * @param password The password.
     * @param identity The identity to add the user to.
     * @param handler The error handler.
     * @throws AccessControlException when something went wrong.
     * @return <code>true</code> if the user was authenticated, <code>false</code> otherwise.
     */
    protected boolean authenticate(AccreditableManager accreditableManager, String username,
            String password, Identity identity, ErrorHandler handler) throws AccessControlException {

        boolean authenticated = false;

        if (username.trim().equals("")) {
            handler.error("Please enter a username.");
        } else {

            UserManager userManager = accreditableManager.getUserManager();
            User user = userManager.getUser(username);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Authenticating user: [" + user + "]");
            }

            if (user != null && ((ManagedUser) user).authenticate(password)) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("User [" + user + "] authenticated.");
                }

                UserReference oldUser = identity.getUserReference();
                if (oldUser != null) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Removing user [" + oldUser + "] from identity.");
                    }
                    identity.removeIdentifiable(oldUser);
                }
                identity.addIdentifiable(new ManagedUserReference(username, userManager.getId()));
                authenticated = true;
            } else {
                if (getLogger().isDebugEnabled()) {
                    if (user == null) {
                        getLogger().debug("No such user: [" + username + "]");
                    }
                    getLogger().debug("User [" + username + "] not authenticated.");
                }
            }
        }

        return authenticated;
    }

    public String getLoginUri(Request request) {
        String webappUrl = ServletHelper.getWebappURI(request);
        OutgoingLinkRewriter rewriter = new OutgoingLinkRewriter(this.manager, getLogger());
        String outgoingUrl = rewriter.rewrite(webappUrl);
        return outgoingUrl + "?lenya.usecase=login&lenya.step=showscreen";
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public String getTargetUri(Request request) {
        return request.getRequestURI();
    }

    private AttributeSet attrs = new EmptyAttributeSet();

    public AttributeSet getAttributeSet() {
        return this.attrs;
    }
}
