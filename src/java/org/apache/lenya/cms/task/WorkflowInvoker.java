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

/* $Id: WorkflowInvoker.java,v 1.10 2004/03/01 16:18:20 gregor Exp $  */

package org.apache.lenya.cms.task;

import java.util.Map;

import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.util.NamespaceMap;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.SynchronizedWorkflowInstances;
import org.apache.log4j.Category;

public class WorkflowInvoker extends ParameterWrapper {

	private static Category log = Category.getInstance(WorkflowInvoker.class);

	public static final String ROLES = "roles";
	public static final String USER_ID = "user-id";
	public static final String MACHINE = "machine";
	public static final String EVENT = "event";

	public static final String PREFIX = "workflow";

	public static final String EVENT_REQUEST_PARAMETER = "workflow.event";
	public static final String LENYA_EVENT_REQUEST_PARAMETER = "lenya.event";

	/**
	 * Ctor.
	 * 
	 * @param eventName
	 *            The event name.
	 * @param identity
	 *            The identity.
	 * @param roles
	 *            The roles.
	 * @return A namespace map containing the parameters.
	 */
	public static NamespaceMap extractParameters(
		String eventName,
		Identity identity,
		Role[] roles) {
		NamespaceMap parameters = new NamespaceMap(PREFIX);
		log.debug("Extractign workflow invoker parameters.");
		log.debug("    Event: [" + eventName + "]");
		parameters.put(EVENT, eventName);
		setRoles(parameters, roles);
		setIdentity(parameters, identity);
		return parameters;
	}

	/**
	 * Ctor.
	 * 
	 * @param parameters
	 *            A map containing the prefixed parameters.
	 */
	public WorkflowInvoker(Map parameters) {
		super(parameters);
	}

	/**
	 * Returns the role names.
	 * 
	 * @return A string array.
	 */
	protected String[] getRoleIDs() {
		String rolesString = get(ROLES);
		String[] roleIDs = rolesString.split(",");
		return roleIDs;
	}

	/**
	 * Sets the roles.
	 * 
	 * @param parameters
	 *            A workflow invoker namespace map.
	 * @param roles
	 *            A role array.
	 */
	public static void setRoles(NamespaceMap parameters, Role[] roles) {

		String roleString = "";
		for (int i = 0; i < roles.length; i++) {
			if (i > 0) {
				roleString += ",";
			}
			roleString += roles[i].getId();
		}
		parameters.put(ROLES, roleString);
	}

	/**
	 * Sets the identity.
	 * 
	 * @param parameters
	 *            A workflow invoker namespace map.
	 * @param identity
	 *            An identity.
	 */
	public static void setIdentity(NamespaceMap parameters, Identity identity) {

		String userId = "";
		User user = identity.getUser();
		if (user != null) {
			userId = user.getId();
		}
		parameters.put(USER_ID, userId);

		String machineIp = "";
		Machine machine = identity.getMachine();
		if (machine != null) {
			machineIp = machine.getIp();
		}
		parameters.put(MACHINE, machineIp);
	}

	/**
	 * Returns the workflow event name.
	 * 
	 * @return A string.
	 */
	public String getEventName() {
		return get(EVENT);
	}

	/**
	 * Returns the user ID.
	 * 
	 * @return A string.
	 */
	public String getUserId() {
		return get(USER_ID);
	}

	/**
	 * Returns the machine IP address.
	 * 
	 * @return A string.
	 */
	public String getMachineIp() {
		return get(MACHINE);
	}

	private Document document;
	private boolean doTransition = false;

	/**
	 * Initializes the workflow invoker.
	 * 
	 * @param publication
	 *            The publication.
	 * @param webappUrl
	 *            The webapp URL.
	 * @throws ExecutionException
	 *             when something went wrong.
	 */
	public void setup(Publication publication, String webappUrl) throws ExecutionException {
		String eventName = getEventName();
		if (eventName == null) {
			log.debug("No workflow event.");
		} else {
			log.debug("Workflow event: [" + eventName + "]");
			// check for workflow instance first (task can initialize the workflow history)
			WorkflowFactory factory = WorkflowFactory.newInstance();
			try {
				document = publication.getDocumentBuilder().buildDocument(publication, webappUrl);
			} catch (DocumentBuildException e) {
				throw new ExecutionException(e);
			}
			doTransition = factory.hasWorkflow(document);
		}
	}

	/**
	 * Invokes the transition.
	 * 
	 * @throws ExecutionException
	 *             when something went wrong.
	 */
	public void invokeTransition() throws ExecutionException {
		if (doTransition) {

			try {
				WorkflowFactory factory = WorkflowFactory.newInstance();
				SynchronizedWorkflowInstances instance =
					factory.buildSynchronizedInstance(document);
				Situation situation =
					factory.buildSituation(getRoleIDs(), getUserId(), getMachineIp());

				Event event = null;
				Event[] events = instance.getExecutableEvents(situation);

				log.debug("Resolved executable events.");
				
				for (int i = 0; i < events.length; i++) {
					if (events[i].getName().equals(getEventName())) {
						event = events[i];
					}
				}

				assert event != null;
				
				log.debug("Invoking transition.");
				instance.invoke(situation, event);
				log.debug("Invoking transition completed.");

			} catch (Exception e) {
				throw new ExecutionException(e);
			}
		}

	}

	/**
	 * @see org.apache.lenya.cms.task.ParameterWrapper#getPrefix()
	 */
	public String getPrefix() {
		return PREFIX;
	}

	/**
	 * @see org.apache.lenya.cms.task.ParameterWrapper#getRequiredKeys()
	 */
	protected String[] getRequiredKeys() {
		String[] keys = {
		};
		return keys;
	}

}
