/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

/**
 * Matches if the request calls a usecase which is registered for the Usecase Framework.
 */
public class UsecaseRegistrationMatcher extends AbstractLogEnabled implements Matcher, Serviceable {

    /**
     * @see org.apache.cocoon.matching.Matcher#match(java.lang.String,
     *      java.util.Map, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map match(String pattern, Map objectModel, Parameters parameters)
            throws PatternException {
        Request request = ObjectModelHelper.getRequest(objectModel);
        String usecaseName = request.getParameter("lenya.usecase");

        Map result = null;

        UsecaseResolver resolver = null;
        try {
            resolver = (UsecaseResolver) this.manager.lookup(UsecaseResolver.ROLE);
            if (resolver.isRegistered(usecaseName)) {
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