/*
$Id: CocoonTaskWrapper.java,v 1.4 2003/08/29 12:52:46 andreas Exp $
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
package org.apache.lenya.cms.cocoon.task;

import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac2.Identity;
import org.apache.lenya.cms.ac2.PolicyAuthorizer;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.task.DefaultTaskWrapper;
import org.apache.lenya.cms.task.ExecutionException;
import org.apache.lenya.cms.task.Notifier;
import org.apache.lenya.cms.task.TaskWrapperParameters;
import org.apache.lenya.cms.task.WorkflowInvoker;
import org.apache.lenya.util.NamespaceMap;
import org.apache.lenya.util.ServletHelper;
import org.apache.log4j.Category;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CocoonTaskWrapper extends DefaultTaskWrapper {

    private static Category log = Category.getInstance(CocoonTaskWrapper.class);

    /**
     * Ctor to be called from a Cocoon component.
     * @param objectModel A Cocoon object model.
     * @param parameters A parameters object.
     * @throws ExecutionException when something went wrong.
     */
    public CocoonTaskWrapper(Map objectModel, Parameters parameters) throws ExecutionException {
        
        log.debug("Creating CocoonTaskWrapper");

        Publication publication;
        try {
            publication = PublicationFactory.getPublication(objectModel);
        } catch (PublicationException e) {
            throw new ExecutionException(e);
        }
        Request request = ObjectModelHelper.getRequest(objectModel);

        setNotifying(request);

        Parameters taskParameters = extractTaskParameters(parameters, publication, request);
        getTaskParameters().parameterize(taskParameters);

        String taskId = request.getParameter(TaskWrapperParameters.TASK_ID);
        taskId = parameters.getParameter(TaskWrapperParameters.TASK_ID, taskId);

        String webappUrl = ServletHelper.getWebappURI(request);
        initialize(taskId, publication, webappUrl, taskParameters);

        String eventName = request.getParameter(WorkflowInvoker.EVENT_REQUEST_PARAMETER);
        if (eventName != null) {
            Identity identity = Identity.getIdentity(request.getSession(false));
            Role[] roles;
            try {
                roles = PolicyAuthorizer.getRoles(request);
            } catch (AccessControlException e) {
                throw new ExecutionException(e);
            }
            setWorkflowAware(eventName, identity, roles);
        }

    }

    /**
     * Enables notification if the corresponding request parameters exist.
     * @param request The request.
     */
    protected void setNotifying(Request request) {

        log.debug("Trying to initialize notification ...");

        Map requestParameters = ServletHelper.getParameterMap(request);

        log.debug("    Request parameters:");
        for (Iterator i = requestParameters.keySet().iterator(); i.hasNext(); ) {
            Object key = i.next();
            log.debug("        [" + key + "] = [" + requestParameters.get(key) + "]");
        }

        NamespaceMap notificationMap =
            new NamespaceMap(requestParameters, Notifier.PREFIX);

        log.debug("    Notification parameters:");
        for (Iterator i = notificationMap.getMap().keySet().iterator(); i.hasNext(); ) {
            Object key = i.next();
            log.debug("        [" + key + "] = [" + notificationMap.getMap().get(key) + "]");
        }

        if (notificationMap.getMap().isEmpty()) {
            log.debug("    No notification parameters found.");
        }
        else {
            log.debug("    Initializing notification");

            String toKey =
                NamespaceMap.getFullName(Notifier.PREFIX, Notifier.PARAMETER_TO);
            String toString = "";
            String[] toValues = request.getParameterValues(toKey);

            if (toValues == null) {
                throw new IllegalStateException("You must specify at least one [notification.tolist] request parameter!");
            }

            for (int i = 0; i < toValues.length; i++) {
                if (i > 0 && !"".equals(toString)) {
                    toString += ",";
                }
                log.debug("    Adding notification address [" + toValues[i].trim() + "]");
                toString += toValues[i].trim();
            }

            notificationMap.put(Notifier.PARAMETER_TO, toString);
            setNotifying(notificationMap);
        }
    }

}
