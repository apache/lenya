/*
 * $Id
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.cocoon.acting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.ac2.Authorizer;
import org.apache.lenya.cms.ac2.Identity;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DelegatingAuthorizerAction extends AbstractAuthorizerAction {

    protected static final String AUTHORIZER_ELEMENT = "authorizer";
    protected static final String CLASS_ATTRIBUTE = "src";
    
    private List authorizers = new ArrayList();

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);
        
        Configuration authorizerConfigurations[] = conf.getChildren(AUTHORIZER_ELEMENT);
        for (int i = 0; i < authorizerConfigurations.length; i++) {
            String className = authorizerConfigurations[i].getAttribute(CLASS_ATTRIBUTE);
        
            Authorizer authorizer;
            try {
                authorizer = (Authorizer) Class.forName(className).newInstance();
            } catch (Exception e) {
                throw new ConfigurationException("Creating authorizer failed: ", e);
            }
        
            authorizer.configure(authorizerConfigurations[i]);
            authorizers.add(authorizer);
        }
    }

    /**
     * Returns the authorizers of this action.
     * @return An array of authorizers.
     */
    protected Authorizer[] getAuthorizers() {
        return (Authorizer[]) authorizers.toArray(new Authorizer[authorizers.size()]);
    }
    
    /**
     * Returns if this action has authorizers.
     * @return A boolean value.
     */
    protected boolean hasAuthorizers() {
        return !authorizers.isEmpty();
    }

    /**
     * @see org.apache.lenya.cms.cocoon.acting.AbstractAuthorizerAction#authorize(org.apache.cocoon.environment.Request, java.util.Map)
     */
    public boolean authorize(Request request, Map ignore) throws Exception {
        boolean authorized = false;
        
        if (request != null) {
            
            Session session = request.getSession(true);

            Identity identity = (Identity) session.getAttribute(Identity.class.getName());
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Identity: " + identity);
            }
        
            if (identity != null) {
                String url = envelope.getDocumentURL();

                if (hasAuthorizers()) {
                    Authorizer authorizers[] = getAuthorizers();
                    int i = 0;
                    authorized = true;
                    while (i < authorizers.length && authorized) {
                        authorized = authorized && authorizers[i].authorize(identity, getPageEnvelope(), request);
                        i++;
                    }
                }
            }
        }
    
        return authorized;
    }

    /**
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector, org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String src,
        Parameters parameters)
        throws Exception {
        
        envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        return super.act(redirector, resolver, objectModel, src, parameters);
    }
    
    private PageEnvelope envelope;

    /**
     * Returns the envelope of the current page.
     * @return A page envelope.
     */
    protected PageEnvelope getPageEnvelope() {
        return envelope;
    }

}
