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

/* $Id: FlowHelper.java,v 1.8 2004/03/01 16:18:27 gregor Exp $  */

package org.apache.lenya.cms.cocoon.flow;

import java.util.Enumeration;

import org.apache.cocoon.components.flow.javascript.fom.FOM_Cocoon;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.impl.PolicyAuthorizer;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Situation;
import org.apache.log4j.Category;

/**
 * Flowscript utility class.
 * The FOM_Cocoon object is not passed in the constructor to avoid
 * errors. This way, not the initial, but the current FOM_Cocoon
 * object is used by the methods.
 */
public class FlowHelper {

    private static final Category log = Category.getInstance(FlowHelper.class);

    /**
     * Ctor.
     */
    public FlowHelper() {
    }

    /**
     * Returns the current workflow situation.
     * @param cocoon The FOM_Cocoon object.
     * @return A situation.
     * @throws AccessControlException when something went wrong.
     */
    public Situation getSituation(FOM_Cocoon cocoon) throws AccessControlException {
        Request request = ObjectModelHelper.getRequest(cocoon.getObjectModel());
        Session session = request.getSession();
        Identity identity = (Identity) session.getAttribute(Identity.class.getName());

        String userId = "";
        String ipAddress = "";

        User user = identity.getUser();
        if (user != null) {
            userId = user.getId();
        }

        Machine machine = identity.getMachine();
        if (machine != null) {
            ipAddress = machine.getIp();
        }

        Role[] roles = PolicyAuthorizer.getRoles(request);
        String[] roleIds = new String[roles.length];
        for (int i = 0; i < roles.length; i++) {
            roleIds[i] = roles[i].getId();
        }

        WorkflowFactory factory = WorkflowFactory.newInstance();
        Situation situation = factory.buildSituation(roleIds, userId, ipAddress);
        return situation;
    }

    /**
     * Returns the current page envelope.
     * @param cocoon The FOM_Cocoon object.
     * @return A page envelope.
     * @throws PageEnvelopeException when something went wrong.
     */
    public PageEnvelope getPageEnvelope(FOM_Cocoon cocoon) throws PageEnvelopeException {
        PageEnvelopeFactory factory = PageEnvelopeFactory.getInstance();
        return factory.getPageEnvelope(cocoon.getObjectModel());
    }

    /**
     * Returns the request URI of the current request.
     * @param cocoon The FOM_Cocoon object.
     * @return A string.
     */
    public String getRequestURI(FOM_Cocoon cocoon) {
        return cocoon.getRequest().getRequestURI();
    }
    
    /**
     * Returns the request object of the current request.
     * @param cocoon The FOM_Cocoon object.
     * @return A request object.
     */
    public Request getRequest(FOM_Cocoon cocoon) {
        return cocoon.getRequest();
    }

    public static final String SEPARATOR = ":";

    /**
     * Resolves the request parameter value for a specific name.
     * The parameter names are encoded as <code>{name}:{value}.{axis}</code>.
     * This is a workaround for the &lt;input type="image"/&gt;
     * bug in Internet Explorer.
     * @param cocoon The FOM_Cocoon object.
     * @param parameterName The request parameter name.
     * @return A string.
     */
    public String getImageParameterValue(FOM_Cocoon cocoon, String parameterName) {

        log.debug("Resolving parameter value for name [" + parameterName + "]");

        Request request = cocoon.getRequest();
        String value = request.getParameter(parameterName);

        if (value == null) {
            String prefix = parameterName + SEPARATOR;
            Enumeration e = request.getParameterNames();
            while (e.hasMoreElements() && value == null) {
                String name = (String) e.nextElement();
                if (name.startsWith(prefix)) {
                    log.debug("Complete parameter name: [" + name + "]");
                    value = name.substring(prefix.length(), name.length() - 2);
                    log.debug("Resolved value: [" + value + "]");
                }
            }
        }

        return value;
    }

}
