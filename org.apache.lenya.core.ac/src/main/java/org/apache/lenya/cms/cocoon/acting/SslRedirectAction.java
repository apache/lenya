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
package org.apache.lenya.cms.cocoon.acting;

import java.util.Collections;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ConfigurableServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.cms.linking.LinkRewriter;
import org.apache.lenya.cms.linking.OutgoingLinkRewriter;
import org.apache.lenya.cms.publication.Repository;
import org.apache.lenya.cms.publication.Session;
//import org.apache.lenya.util.ServletHelper;
import org.apache.lenya.utils.ServletHelper;
import org.apache.lenya.utils.URLInformation;
/**
 * Returns a map if the current request needs a redirect to the <code>https://</code> protocol. This
 * is the case if the policy requires SSL protection and the current request is not secure. The map
 * contains the redirect URI as value for the key <em>redirectUri</em>. Otherwise, <code>null</code>
 * is returned.
 */
public class SslRedirectAction extends ConfigurableServiceableAction {

    /**
     * The key to obtain the redirect URI from the returned map.
     */
    public static final String KEY_REDIRECT_URI = "redirectUri";

    private Repository repository;

    public Map act(Redirector redirector, SourceResolver sourceResolver, Map objectModel,
            String source, Parameters parameters) throws Exception {

        AccessControllerResolver resolver = null;
        AccessController accessController = null;

        Request request = ObjectModelHelper.getRequest(objectModel);

        if (!request.isSecure()) {

            resolver = (AccessControllerResolver) WebAppContextUtils
                    .getCurrentWebApplicationContext().getBean(AccessControllerResolver.ROLE);
          //TODO : florent : remove comment when ok 
            //String url = ServletHelper.getWebappURI(request);
            String url = new URLInformation().getWebappUrl();
            
            accessController = resolver.resolveAccessController(url);

            if (accessController != null) {
                PolicyManager policyManager = accessController.getPolicyManager();
                Policy policy = policyManager.getPolicy(accessController.getAccreditableManager(),
                        url);
                if (policy.isSSLProtected()) {
                    Session session = this.repository.getSession(request);
                    LinkRewriter rewriter = new OutgoingLinkRewriter(session, url, false, true,
                            false);
                    String sslUri = rewriter.rewrite(url);
                    return Collections.singletonMap(KEY_REDIRECT_URI, sslUri);
                }
            }

        }
        return null;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
