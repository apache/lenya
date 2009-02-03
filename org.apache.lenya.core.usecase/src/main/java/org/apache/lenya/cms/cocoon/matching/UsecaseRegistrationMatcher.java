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

package org.apache.lenya.cms.cocoon.matching;

import java.util.Collections;
import java.util.Map;

import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.matching.Matcher;
import org.apache.cocoon.sitemap.PatternException;
import org.apache.lenya.cms.usecase.UsecaseResolver;
import org.apache.lenya.util.ServletHelper;

/**
 * Matches if the request calls a usecase which is registered for the Usecase Framework.
 */
public class UsecaseRegistrationMatcher extends AbstractLogEnabled implements Matcher, Serviceable, Poolable {

    /**
     * @see org.apache.cocoon.matching.Matcher#match(java.lang.String,
     *      java.util.Map, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map match(String pattern, Map objectModel, Parameters parameters)
            throws PatternException {

        Request request = ObjectModelHelper.getRequest(objectModel);
        String usecaseName = request.getParameter("lenya.usecase");

        Map result = null;

        if (usecaseName == null) {
            if (getLogger().isDebugEnabled()) {
               getLogger().debug("match() called, usecase parameter is empty - returning false");
            }
        }
        else {
            if (getLogger().isDebugEnabled()) {
               getLogger().debug("match() called for request parameter lenya.usecase = [" + usecaseName + "]; note that pattern is not relevant for this matcher");
            }
            // Parameter for usecase is set, see if this is a registered component
            UsecaseResolver resolver = null;
            try {
               resolver = (UsecaseResolver) this.manager.lookup(UsecaseResolver.ROLE);
               String url = ServletHelper.getWebappURI(request);
               if (resolver.isRegistered(url, usecaseName)) {
                   result = Collections.EMPTY_MAP;
               }
               if (getLogger().isDebugEnabled()) {
                   getLogger().debug("Usecase [" + usecaseName + "] exists: [" + !(result == null) + "]");
               }
            } catch (ServiceException e) {
               throw new PatternException(e);
            } finally {
               if (resolver != null) {
                  this.manager.release(resolver);
               }
            }
        }
        return result;
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager _manager) throws ServiceException {
        this.manager = _manager;
    }

}
