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

/* $Id: UserAuthenticator.java,v 1.4 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.ac.impl;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Authenticator;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;

public class UserAuthenticator extends AbstractLogEnabled implements Authenticator {

    /**
     * @see org.apache.lenya.cms.ac2.Authenticator#authenticate(org.apache.cocoon.environment.Request)
     */
    public boolean authenticate(AccreditableManager accreditableManager, Request request)
        throws AccessControlException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Authenticating username [" + username + "] with password [" + password + "]");
        }

        if (username == null || password == null) {
            throw new AccessControlException("Username or password is null!");
        }

        Identity identity =
            (Identity) request.getSession(false).getAttribute(Identity.class.getName());
        boolean authenticated = authenticate(accreditableManager, username, password, identity);
        return authenticated;
    }

    /**
     * Authenticates a user with a given username and password.
     * When the authentication is successful, the user is added to the identity.
     * @param accreditableManager The accreditable manager.
     * @param username The username.
     * @param password The password.
     * @param identity The identity to add the user to.
     * @throws AccessControlException when something went wrong.
     * @return <code>true</code> if the user was authenticated, <code>false</code> otherwise.
     */
    protected boolean authenticate(
        AccreditableManager accreditableManager,
        String username,
        String password,
        Identity identity)
        throws AccessControlException {

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
        }
        else {
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
