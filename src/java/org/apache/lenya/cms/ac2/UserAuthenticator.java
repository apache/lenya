/*
$Id: UserAuthenticator.java,v 1.6 2003/09/02 18:22:37 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/

package org.apache.lenya.cms.ac2;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.User;

/**
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
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
            getLogger().info("User [" + user + "] authenticated.");
            
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
            if (user == null) {
                getLogger().warn("No such user: [" + username + "]");
            }
            getLogger().warn("User [" + username + "] not authenticated.");
        }

        return authenticated;
    }

}
