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

import org.apache.commons.codec.binary.Base64;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Authenticator;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;

/**
 * User authenticator.
 * @version $Id$
 */
public class UserAuthenticator extends AbstractLogEnabled implements Authenticator {

    /**
     * @see org.apache.lenya.ac.Authenticator#authenticate(org.apache.lenya.ac.AccreditableManager,
     *      org.apache.cocoon.environment.Request) Note that this implementation first checks if the
     *      user has authenticated over basic HTTP authentication. If yes, it uses these
     *      credentials.
     */
    public boolean authenticate(AccreditableManager accreditableManager, Request request)
            throws AccessControlException {

        String username = null;
        String password = null;
        
        boolean useHeader = false;
        if (request.getHeader("Authorization") != null) {
            String encoded = request.getHeader("Authorization");

            if (encoded.indexOf("Basic") > -1) {
                encoded = encoded.trim();
                encoded = encoded.substring(encoded.indexOf(' ') + 1);
                String unencoded = new String(Base64.decodeBase64(encoded.getBytes()));

                if (unencoded.indexOf(":") - 1 > -1) {
                    useHeader = true;
                    username = unencoded.substring(0, unencoded.indexOf(":"));
                    password = unencoded.substring(unencoded.indexOf(":") + 1);
                }

            }
        }
        
        if (!useHeader && request.getParameter("username") != null) {
            username = request.getParameter("username").toLowerCase();
            password = request.getParameter("password");
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                    "Authenticating username [" + username + "] with password [" + password + "]");
        }

        if (username == null || password == null) {
            throw new AccessControlException("Username or password is null!");
        }

        Identity identity = (Identity) request.getSession(false).getAttribute(
                Identity.class.getName());

        if (identity == null) {
            throw new AccessControlException("The session does not contain the identity!");
        }

        boolean authenticated = authenticate(accreditableManager, username, password, identity);
        return authenticated;
    }

    /**
     * Authenticates a user with a given username and password. When the authentication is
     * successful, the user is added to the identity.
     * @param accreditableManager The accreditable manager.
     * @param username The username.
     * @param password The password.
     * @param identity The identity to add the user to.
     * @throws AccessControlException when something went wrong.
     * @return <code>true</code> if the user was authenticated, <code>false</code> otherwise.
     */
    protected boolean authenticate(AccreditableManager accreditableManager, String username,
            String password, Identity identity) throws AccessControlException {

        User user = accreditableManager.getUserManager().getUser(username);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Authenticating user: [" + user + "]");
        }

        boolean authenticated = false;
        if (user != null && user.authenticate(password)) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("User [" + user + "] authenticated.");
            }

            if (!identity.contains(user)) {
                User oldUser = identity.getUser();
                if (oldUser != null) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Removing user [" + oldUser + "] from identity.");
                    }
                    identity.removeIdentifiable(oldUser);
                }
                identity.addIdentifiable(user);
            }
            authenticated = true;
        } else {
            if (getLogger().isDebugEnabled()) {
                if (user == null) {
                    getLogger().debug("No such user: [" + username + "]");
                }
                getLogger().debug("User [" + username + "] not authenticated.");
            }
        }

        return authenticated;
    }

}
