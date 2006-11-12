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

/* $Id$  */

package org.apache.lenya.cms.cocoon.acting;

import java.util.Collections;
import java.util.Map;

import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.util.Stack;

/**
 * AuthorizerAction that delegates the authorizing to an AccessController.
 */
public class DelegatingAuthorizerAction extends AccessControlAction implements Poolable {

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

        return super.act(redirector, resolver, objectModel, src, parameters);
    }

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

        Request request = ObjectModelHelper.getRequest(objectModel);
        
        setHistory(request);

        boolean authorized = getAccessController().authorize(request);

        Map result = null;
        if (authorized) {
            result = Collections.EMPTY_MAP;
        }

        return result;
    }

    /**
     * <code>HISTORY</code> Name of the session attribute that holds the history
     */
    public static final String HISTORY =
        DelegatingAuthorizerAction.class.getPackage().getName() + ".History";

    /**
     * Adds the current URL to the history.
     * @param request The request.
     */
    protected void setHistory(Request request) {
        Session session = request.getSession(true);

        Stack history = (Stack) session.getAttribute(HISTORY);

        if (history == null) {
            history = new Stack(10);
            session.setAttribute(HISTORY, history);
        }
        
        String url = request.getRequestURI();
        String context = request.getContextPath();
        if (context == null) {
            context = "";
        }
        url = url.substring(context.length());

        history.push(url);

    }
    
}
