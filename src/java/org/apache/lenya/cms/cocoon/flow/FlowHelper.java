/*
 * $Id: FlowHelper.java,v 1.6 2004/02/04 10:36:46 andreas Exp $ <License>
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
 * 
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
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
