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
package org.apache.lenya.modules.monitoring;

import java.util.Collections;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;

/**
 * The action returns an empty map if the current request has no session yet and the session limit
 * is exceeded and <code>null</code> otherwise. The session limit is specified using the
 * <em>limit</em> parameter.
 */
public class SessionLimitAction extends AbstractAction {

    protected static final String PARAM_LIMIT = "limit";

    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
            Parameters parameters) throws Exception {
        Request request = ObjectModelHelper.getRequest(objectModel);
        if (request.getSession(false) == null) {
            long limit = parameters.getParameterAsLong(PARAM_LIMIT);
            if (SessionCountLogger.getSessionCount() >= limit) {
                getLogger().info("Session limit [" + limit + "] exceeded.");
                return Collections.EMPTY_MAP;
            }
        }
        return null;
        
    }

}
