/*
$Id: DelegatingAuthorizerAction.java,v 1.6 2003/07/09 18:53:51 andreas Exp $
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

import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;

import org.apache.lenya.cms.ac2.AccessController;
import org.apache.lenya.cms.ac2.Authorizer;
import org.apache.lenya.cms.ac2.Identity;
import org.apache.lenya.cms.ac2.file.FilePolicyManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DelegatingAuthorizerAction extends AbstractAuthorizerAction {
    protected static final String AUTHORIZER_ELEMENT = "authorizer";
    protected static final String TYPE_ATTRIBUTE = "type";
    protected static final String ACCESS_CONTROLLER_ELEMENT = "access-controller";
    private List authorizerTypes = new ArrayList();
    private String accessControllerId;

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);

        Configuration accessControllerConfiguration = conf.getChild(ACCESS_CONTROLLER_ELEMENT);
        accessControllerId = accessControllerConfiguration.getValue();
        getLogger().debug("Access controller ID: [" + accessControllerId + "]");

        Configuration[] authorizerConfigurations = conf.getChildren(AUTHORIZER_ELEMENT);

        for (int i = 0; i < authorizerConfigurations.length; i++) {
            String type = authorizerConfigurations[i].getAttribute(TYPE_ATTRIBUTE);
            authorizerTypes.add(type);
            getLogger().debug("Adding authorizer [" + type + "]");
        }
    }

    /**
     * Returns the authorizers of this action.
     * @return An array of authorizers.
     */
    protected String[] getAuthorizerTypes() {
        return (String[]) authorizerTypes.toArray(new String[authorizerTypes.size()]);
    }

    /**
     * Returns if this action has authorizers.
     * @return A boolean value.
     */
    protected boolean hasAuthorizers() {
        return !authorizerTypes.isEmpty();
    }

    /**
     * @see org.apache.lenya.cms.cocoon.acting.AbstractAuthorizerAction#authorize(org.apache.cocoon.environment.Request, java.util.Map)
     */
    public boolean authorize(Request request, Map ignore)
        throws Exception {
        boolean authorized = false;

        if (request != null) {
            AccessController controller = null;
            ComponentSelector selector = null;

            try {
                controller = (AccessController) manager.lookup(AccessController.ROLE + "/" +
                        accessControllerId);
                selector = (ComponentSelector) manager.lookup(Authorizer.class.getName() + "Selector");

                Session session = request.getSession(true);
                Identity identity = (Identity) session.getAttribute(Identity.class.getName());

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Trying to authorize identity: " + identity);
                }

                if (identity != null) {
                    if (hasAuthorizers()) {
                        String[] authorizerTypes = getAuthorizerTypes();
                        int i = 0;
                        authorized = true;
                        

                        while ((i < authorizerTypes.length) && authorized) {
                            Authorizer authorizer = (Authorizer) selector.select(authorizerTypes[i]);

                            try {
                                authorized = authorized &&
                                    authorizer.authorize(controller, new FilePolicyManager(),
                                        identity, getPublication(), request);

                                if (getLogger().isDebugEnabled()) {
                                    getLogger().debug("Authorizer [" + authorizer + "] returned [" +
                                        authorized + "]");
                                }
                            } finally {
                                if (selector != null) {
                                    selector.release(authorizer);
                                } 
                            }

                            i++;
                        }
                        
                    }
                }
            } finally {
                manager.release(controller);
                manager.release(selector);
            }
        }

        return authorized;
    }

    /**
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector, org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src,
        Parameters parameters) throws Exception {
        publication = PublicationFactory.getPublication(objectModel);

        return super.act(redirector, resolver, objectModel, src, parameters);
    }

    private Publication publication;

    /**
     * Returns the envelope of the current page.
     * @return A page envelope.
     */
    protected Publication getPublication() {
        return publication;
    }
}
