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

/* $Id: WorkflowAuthorizer.java,v 1.3 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.cms.ac.workflow;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.cms.cocoon.workflow.WorkflowHelper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.SynchronizedWorkflowInstances;

/**
 * If the client requested invoking a workflow event, this authorizer checks if the current
 * document state and identity roles allow this transition.
 */
public class WorkflowAuthorizer extends AbstractLogEnabled implements Authorizer, Serviceable {

	protected static final String EVENT_PARAMETER = "lenya.event";

	/**
	 * @see org.apache.lenya.cms.ac2.Authorizer#authorize(org.apache.lenya.cms.ac2.Identity,
	 *      org.apache.cocoon.environment.Request)
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

		if (getLogger().isDebugEnabled()) {
			getLogger().debug("Authorizing workflow for event [" + event + "]");
		}

		if (event != null) {

			try {
				resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
				Publication publication = PublicationFactory.getPublication(resolver, request);

				DocumentBuilder builder = publication.getDocumentBuilder();
				if (builder.isDocument(publication, url)) {

					Document document = builder.buildDocument(publication, url);
					WorkflowFactory factory = WorkflowFactory.newInstance();

					if (factory.hasWorkflow(document)) {
						SynchronizedWorkflowInstances instance =
							factory.buildSynchronizedInstance(document);

						authorized = false;

						Situation situation = WorkflowHelper.buildSituation(request);
						Event[] events = instance.getExecutableEvents(situation);
						int i = 0;

						while (!authorized && (i < events.length)) {
							if (events[i].getName().equals(event)) {
								authorized = true;
							}
							if (getLogger().isDebugEnabled()) {
								getLogger().debug("    Event [" + events[i] + "] is executable.");
							}

							i++;
						}
					}
				}

			} catch (Exception e) {
				throw new AccessControlException(e);
			} finally {
				if (resolver != null) {
					manager.release(resolver);
				}
			}
		}

		return authorized;
	}

	private ServiceManager manager;

	/**
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 */
	public void service(ServiceManager manager) throws ServiceException {
		this.manager = manager;
	}

}
