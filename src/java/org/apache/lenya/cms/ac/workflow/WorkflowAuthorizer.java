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

package org.apache.lenya.cms.ac.workflow;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.impl.RepositoryUtil;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.util.ServletHelper;

/**
 * If the client requested invoking a workflow event, this authorizer checks if the current document
 * state and identity roles allow this transition.
 */
public class WorkflowAuthorizer extends AbstractLogEnabled implements Authorizer, Serviceable {

    protected static final String EVENT_PARAMETER = "lenya.event";

    /**
     * @see org.apache.lenya.ac.Authorizer#authorize(org.apache.cocoon.environment.Request)
     */
    public boolean authorize(Request request) throws AccessControlException {
        return authorize(request, ServletHelper.getWebappURI(request));
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager _manager) throws ServiceException {
        this.manager = _manager;
    }

    /**
     * Authorizes a request.
     * @param request The request.
     * @param webappUrl The URL to authorize.
     * @return A boolean value.
     * @throws AccessControlException if an error occurs.
     */
    public boolean authorize(Request request, String webappUrl) throws AccessControlException {
        boolean authorized = true;

        String event = request.getParameter(EVENT_PARAMETER);
        SourceResolver resolver = null;

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Authorizing workflow for event [" + event + "]");
        }

        if (event != null) {

            try {
                resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                Session session = RepositoryUtil.getSession(this.manager, request, getLogger());
                
                Document document = RepositoryUtil.getDocument(session, webappUrl);
                if (document != null) {
                    authorized = WorkflowUtil.canInvoke(this.manager,
                            session,
                            getLogger(),
                            document,
                            event);
                }
            } catch (final Exception e) {
                throw new AccessControlException(e);
            } finally {
                if (resolver != null) {
                    this.manager.release(resolver);
                }
            }

        }

        return authorized;
    }

}