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

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.cms.linking.LinkRewriter;
import org.apache.lenya.cms.linking.OutgoingLinkRewriter;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;

/**
 * Returns a map if the current request needs a redirect to the <code>https://</code> protocol.
 * This is the case if the policy requires SSL protection and the current request is not secure. The
 * map contains the redirect URI as value for the key <em>redirectUri</em>. Otherwise,
 * <code>null</code> is returned.
 */
public class SslRedirectAction extends AccessControlAction {

    /**
     * The key to obtain the redirect URI from the returned map.
     */
    public static final String KEY_REDIRECT_URI = "redirectUri";

    protected Map doAct(Redirector redirector, SourceResolver resolver, Map objectModel,
            String source, Parameters parameters) throws Exception {

        Request request = ObjectModelHelper.getRequest(objectModel);
        if (!request.isSecure()) {
            AccessController controller = getAccessController();
            PolicyManager policyManager = controller.getPolicyManager();
            String url = ServletHelper.getWebappURI(request);
            Policy policy = policyManager.getPolicy(controller.getAccreditableManager(), url);
            if (policy.isSSLProtected()) {
                Map map = new HashMap();
                Session session = RepositoryUtil.getSession(this.manager, request);
                LinkRewriter rewriter = new OutgoingLinkRewriter(this.manager, session, url, false,
                        true, false);
                String sslUri = rewriter.rewrite(url);
                map.put(KEY_REDIRECT_URI, sslUri);
                return map;
            }
        }

        return null;
    }
}
