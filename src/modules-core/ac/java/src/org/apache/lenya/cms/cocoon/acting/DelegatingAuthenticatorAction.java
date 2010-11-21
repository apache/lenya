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

/* $Id: DelegatingAuthenticatorAction.java 42616 2004-03-03 12:56:33Z gregor $  */

package org.apache.lenya.cms.cocoon.acting;

import java.util.Collections;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;

import org.apache.lenya.ac.AccessControlException;

/**
 * Authenticator action that delegates the authentication to an access controller.
 */
public class DelegatingAuthenticatorAction extends AccessControlAction {

    /**
     * @see org.apache.lenya.cms.cocoon.acting.AccessControlAction#doAct(org.apache.cocoon.environment.Redirector, org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    protected Map doAct(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String source,
        Parameters parameters)
        throws Exception {

        getLogger().debug("Authenticating request");

        Request request = ObjectModelHelper.getRequest(objectModel);
        Map result = null;

	try {
	    if (getAccessController().authenticate(request)) {
		getLogger().debug("Authentication successful.");
		result = Collections.EMPTY_MAP;
	    }
	    else {
		getLogger().debug("Authentication failed.");
	    }
	}
	catch (AccessControlException e) {
	    getLogger().debug("Authentication failed due to AccessControlException: " + e.getMessage());
	}
        return result;
    }

}
