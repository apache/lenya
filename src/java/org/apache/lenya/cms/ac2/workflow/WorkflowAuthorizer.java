/*
 * $Id
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.ac2.workflow;

import java.util.Arrays;

import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac2.Identity;
import org.apache.lenya.cms.ac2.Policy;
import org.apache.lenya.cms.ac2.PolicyAuthorizer;
import org.apache.lenya.cms.publication.DefaultDocumentBuilder;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 * If the client requested invoking a workflow event, this authorizer checks if
 * the current document state and identity roles allow this transition.
 * 
 * @author andreas
 */
public class WorkflowAuthorizer extends PolicyAuthorizer {

    protected static final String EVENT_PARAMETER = "lenya.event";

    /**
     * @see org.apache.lenya.cms.ac2.Authorizer#authorize(org.apache.lenya.cms.ac2.Identity, org.apache.lenya.cms.publication.PageEnvelope, org.apache.cocoon.environment.Request)
     */
    public boolean authorize(Identity identity, Publication publication, Request request)
        throws AccessControlException {

        boolean authorized = true;
        
        String requestUri = request.getRequestURI();
        String context = request.getContextPath();
        if (context == null) {
            context = "";
        }
        String url = requestUri.substring(context.length());

        String event = request.getParameter(EVENT_PARAMETER);
        Document document;
        try {
            document = DefaultDocumentBuilder.getInstance().buildDocument(publication, url);
        } catch (DocumentBuildException e) {
            throw new AccessControlException(e);
        }
        
        try {
                
            WorkflowFactory factory = WorkflowFactory.newInstance();
            
            if (factory.hasWorkflow(document)) {
                WorkflowInstance instance = factory.buildInstance(document);
                Policy policy = getAccessController().getPolicy(publication, url);
                Role[] roles = policy.getRoles(identity);
                saveRoles(request, roles);
    
                if (event != null) {
                    authorized = false;
                    Situation situation = factory.buildSituation(roles);
                    Event[] events = instance.getExecutableEvents(situation);
                    int i = 0;
                    while (!authorized && i < events.length) {
                        if (events[i].getName().equals(event)) {
                            authorized = true;
                        }
                        i++;
                    }
                }
            }
            
        } catch (WorkflowException e) {
            throw new AccessControlException(e);
        }
        return authorized;
    }
    
    /**
     * Saves the roles of the current identity to the request.
     * @param request The request.
     * @param roles The roles.
     */
    protected void saveRoles(Request request, Role[] roles) {
        request.setAttribute(Role.class.getName(), Arrays.asList(roles));
    }

}
