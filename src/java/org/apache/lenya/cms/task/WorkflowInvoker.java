/*
$Id: WorkflowInvoker.java,v 1.3 2003/10/02 15:28:33 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.task;

import java.util.Map;

import org.apache.lenya.cms.ac.Machine;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.ac2.Identity;
import org.apache.lenya.cms.publication.DefaultDocumentBuilder;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.util.NamespaceMap;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.lenya.workflow.impl.SynchronizedWorkflowInstances;
import org.apache.log4j.Category;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class WorkflowInvoker extends ParameterWrapper {

    private static Category log = Category.getInstance(WorkflowInvoker.class);

    public static final String ROLES = "roles";
    public static final String USER_ID = "user-id";
    public static final String MACHINE = "machine";
    public static final String EVENT = "event";
    public static final String EVENT_REQUEST_PARAMETER = "lenya.event";

    public static final String PREFIX = "workflow";

    /**
     * Ctor.
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
     * @param parameters A map containing the prefixed parameters.
     */
    public WorkflowInvoker(Map parameters) {
        super(parameters);
    }

    /**
     * Returns the role names.
     * @return A string array.
     */
    protected String[] getRoleIDs() {
        String rolesString = get(ROLES);
        String[] roleIDs = rolesString.split(",");
        return roleIDs;
    }

    /**
     * Sets the roles.
     * @param parameters A workflow invoker namespace map.
     * @param roles A role array.
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
                document =
                    DefaultDocumentBuilder.getInstance().buildDocument(publication, webappUrl);
            } catch (DocumentBuildException e) {
                throw new ExecutionException(e);
            }
            doTransition = factory.hasWorkflow(document);
        }
    }

    /**
     * Invokes the transition.
     * @throws ExecutionException when something went wrong.
     */
    public void invokeTransition() throws ExecutionException {
        if (doTransition) {
            
            log.debug("Invoking transition.");

            try {
                WorkflowFactory factory = WorkflowFactory.newInstance();
                SynchronizedWorkflowInstances instance = factory.buildSynchronizedInstance(document);
                Situation situation =
                    factory.buildSituation(getRoleIDs(), getUserId(), getMachineIp());

                Event event = null;
                Event[] events = instance.getExecutableEvents(situation);

                for (int i = 0; i < events.length; i++) {
                    if (events[i].getName().equals(getEventName())) {
                        event = events[i];
                    }
                }

                assert event != null;
                instance.invoke(situation, event);

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
        String[] keys = { };
        return keys;
    }

}
