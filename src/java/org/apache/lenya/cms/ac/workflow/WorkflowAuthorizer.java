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
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.workflow.WorkflowResolver;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowEngine;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.impl.WorkflowEngineImpl;

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

        boolean authorized = true;

        String requestUri = request.getRequestURI();
        String context = request.getContextPath();

        if (context == null) {
            context = "";
        }

        String url = requestUri.substring(context.length());

        String event = request.getParameter(EVENT_PARAMETER);
        SourceResolver resolver = null;
        WorkflowResolver workflowResolver = null;

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Authorizing workflow for event [" + event + "]");
        }

        if (event != null) {

            try {
                resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                PublicationFactory pubFactory = PublicationFactory.getInstance(getLogger());
                Publication publication = pubFactory.getPublication(resolver, request);
                DocumentIdentityMap map = new DocumentIdentityMap();
                if (map.getFactory().isDocument(publication, url)) {

                    Document document = map.getFactory().getFromURL(publication, url);
                    workflowResolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);

                    if (workflowResolver.hasWorkflow(document)) {
                        Workflow workflow = workflowResolver.getWorkflowSchema(document);
                        Situation situation = workflowResolver.getSituation();
                        WorkflowEngine engine = new WorkflowEngineImpl();
                        authorized = engine.canInvoke(document, workflow, situation, event);
                    }
                }
            } catch (final ServiceException e) {
                throw new AccessControlException(e);
            } catch (final DocumentBuildException e) {
                throw new AccessControlException(e);
            } catch (final PublicationException e) {
                throw new AccessControlException(e);
            } catch (final WorkflowException e) {
                throw new AccessControlException(e);
            } finally {
                if (resolver != null) {
                    this.manager.release(resolver);
                }
                if (workflowResolver != null) {
                    this.manager.release(workflowResolver);
                }
            }

        }

        return authorized;
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager _manager) throws ServiceException {
        this.manager = _manager;
    }

}