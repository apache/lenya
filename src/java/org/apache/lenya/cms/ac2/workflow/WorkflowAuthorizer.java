/*
$Id: WorkflowAuthorizer.java,v 1.17 2003/08/15 13:10:19 andreas Exp $
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
package org.apache.lenya.cms.ac2.workflow;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.SourceResolver;

import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac2.Authorizer;
import org.apache.lenya.cms.publication.DefaultDocumentBuilder;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 * If the client requested invoking a workflow event, this authorizer checks if
 * the current document state and identity roles allow this transition.
 *
 * @author andreas
 */
public class WorkflowAuthorizer extends AbstractLogEnabled implements Authorizer, Serviceable {

    protected static final String EVENT_PARAMETER = "lenya.event";

    /**
     * @see org.apache.lenya.cms.ac2.Authorizer#authorize(org.apache.lenya.cms.ac2.Identity, org.apache.cocoon.environment.Request)
     */
    public boolean authorize(Request request)
        throws AccessControlException {

        getLogger().debug("Authorizing workflow");

        boolean authorized = true;

        String requestUri = request.getRequestURI();
        String context = request.getContextPath();

        if (context == null) {
            context = "";
        }

        String url = requestUri.substring(context.length());

        String event = request.getParameter(EVENT_PARAMETER);
        SourceResolver resolver = null;

        if (event != null) {
            try {
                resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
                Publication publication = PublicationFactory.getPublication(resolver, request);

                if (DefaultDocumentBuilder.getInstance().isDocument(publication, url)) {

                    Document document =
                        DefaultDocumentBuilder.getInstance().buildDocument(publication, url);
                    WorkflowFactory factory = WorkflowFactory.newInstance();

                    if (factory.hasWorkflow(document)) {
                        WorkflowInstance instance = factory.buildInstance(document);

                        authorized = false;

                        Situation situation = factory.buildSituation(request);
                        Event[] events = instance.getExecutableEvents(situation);
                        int i = 0;

                        while (!authorized && (i < events.length)) {
                            if (events[i].getName().equals(event)) {
                                authorized = true;
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
