/*
$Id
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
package org.apache.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;

import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.Identity;
import org.apache.lenya.cms.ac.ItemManager;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.ac2.AccessController;
import org.apache.lenya.cms.ac2.DefaultAccessController;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

import org.w3c.dom.Document;

import java.io.File;

import java.util.Map;


/**
 * @author egli
 *
 *
 */
public class UserAuthenticatorAction extends IMLAuthenticatorAction {
    private Publication publication;

    /*
     * This is an implementation of an authenticator which uses
     * the User classes and delegates the authentication to them.
     * An LDAPUser authenticates itself differently than a FileUser
     *
     * @param username
     * @param password
     * @param request
     * @param map is ignored
     *
     * @return true if authentication succeded
     */
    public boolean authenticate(String username, String password, Request request, Map map)
        throws Exception {
        File configurationDirectory = new File(publication.getDirectory(), ItemManager.PATH);
        
        User user = getUser(username);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Authenticating user: " + user);
        }

        if (user.authenticate(password)) {
            String context = request.getContextPath();
            int port = request.getServerPort();
            Document idoc = getIdentityDoc(username, port, context);

            Session session = request.getSession(true);

            if (session == null) {
                return false;
            }

            Identity identity = new Identity(idoc);

            session.setAttribute("org.apache.lenya.cms.ac.Identity", identity);

            return true;
        }

        return false;
    }

    /**
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector, org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src,
        Parameters parameters) throws Exception {
        publication = PublicationFactory.getPublication(objectModel);

        return super.act(redirector, resolver, objectModel, src, parameters);
    }

    /**
     * Returns the publication.
     * @return The publication.
     */
    public Publication getPublication() {
        return publication;
    }

    protected static final String ACCESS_CONTROLLER_ELEMENT = "access-controller";
    private String accessControllerId = null;
    
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);
        Configuration accessControllerConfiguration = conf.getChild(ACCESS_CONTROLLER_ELEMENT);
        if (accessControllerConfiguration != null) {
            accessControllerId = accessControllerConfiguration.getValue();
            getLogger().debug("Access controller ID: [" + accessControllerId + "]");
        }
        else {
            getLogger().debug("No access controller ID provided, using default access controller.");
        }
    }

    protected String getAccessControllerId() {
        return accessControllerId;
    }
    
    /**
     * Returns a user for a username using the AccreditableManager of this action.
     * @param username A string.
     * @return A user.
     */
    protected User getUser(String username) throws AccessControlException, ComponentException {
        User user;
        AccessController controller = null;
        String id = getAccessControllerId();
        String suffix = id == null ? "" : "/" + id;
            
        try {
            controller = (AccessController) manager.lookup(AccessController.ROLE + suffix);
            user = ((DefaultAccessController) controller).getAccreditableManager().getUserManager().getUser(username);
        }
        finally {
            if (controller != null) {
                manager.release(controller);
            }
        }
        
        return user;
    }

}
