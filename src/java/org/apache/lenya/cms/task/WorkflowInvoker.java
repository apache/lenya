/*
 * Copyright 1999-2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *  
 */

/* $Id$ */

package org.apache.lenya.cms.task;

import java.util.Map;

import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.util.NamespaceMap;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowEngine;
import org.apache.lenya.workflow.impl.WorkflowEngineImpl;
import org.apache.log4j.Logger;

/**
 * The workflow invoker
 */
public class WorkflowInvoker extends ParameterWrapper {

    private static Logger log = Logger.getLogger(WorkflowInvoker.class);

    /**
     * <code>ROLES</code> The roles
     */
    public static final String ROLES = "roles";
    /**
     * <code>USER_ID</code> The user id
     */
    public static final String USER_ID = "user-id";
    /**
     * <code>MACHINE</code> The machine
     */
    public static final String MACHINE = "machine";
    /**
     * <code>EVENT</code> The event
     */
    public static final String EVENT = "event";
    /**
     * <code>PREFIX</code> The workflow namespace prefix
     */
    public static final String PREFIX = "workflow";
    /**
     * <code>EVENT_REQUEST_PARAMETER</code> The workflow event request parameter
     */
    public static final String EVENT_REQUEST_PARAMETER = "workflow.event";
    /**
     * <code>LENYA_EVENT_REQUEST_PARAMETER</code> The Lenya event request parameter
     */
    public static final String LENYA_EVENT_REQUEST_PARAMETER = "lenya.event";

    /**
     * Ctor.
     * 
     * @param eventName The event name.
     * @param identity The identity.
     * @param roles The roles.
     * @return A namespace map containing the parameters.
     */
    public static NamespaceMap extractParameters(String eventName, Identity identity, Role[] roles) {
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
     * @param parameters A map containing the prefixed parameters.
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
     * @param parameters A workflow invoker namespace map.
     * @param roles A role array.
     */
    public static void setRoles(NamespaceMap parameters, Role[] roles) {

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < roles.length; i++) {
            if (i > 0) {
                buf.append(",");
            }
            buf.append(roles[i].getId());
        }
        String roleString = buf.toString();
        parameters.put(ROLES, roleString);
    }

    /**
     * Sets the identity.
     * 
     * @param parameters A workflow invoker namespace map.
     * @param identity An identity.
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
     * @return A string.
     */
    public String getEventName() {
        return get(EVENT);
    }

    /**
     * Returns the user ID.
     * @return A string.
     */
    public String getUserId() {
        return get(USER_ID);
    }

    /**
     * Returns the machine IP address.
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
     * @param publication The publication.
     * @param webappUrl The webapp URL.
     * @throws ExecutionException when something went wrong.
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
                DocumentIdentityMap map = new DocumentIdentityMap(publication);
                this.document = map.getFactory().getFromURL(webappUrl);
            } catch (DocumentBuildException e) {
                throw new ExecutionException(e);
            }
            this.doTransition = factory.hasWorkflow(this.document);
        }
    }

    /**
     * Invokes the transition.
     * @throws ExecutionException when something went wrong.
     */
    public void invokeTransition() throws ExecutionException {
        if (this.doTransition) {

            try {
                WorkflowFactory factory = WorkflowFactory.newInstance();
                WorkflowEngine engine = new WorkflowEngineImpl();
                Situation situation = factory.buildSituation(getRoleIDs(), getUserId(),
                        getMachineIp());
                Workflow workflow = factory.getWorkflow(this.document);

                log.debug("Invoking transition.");
                engine.invoke(this.document, workflow, situation, getEventName());
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
        String[] keys = {};
        return keys;
    }

}