
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

/* $Id: WorkflowHelper.java,v 1.2 2004/03/01 16:18:28 gregor Exp $  */

package org.apache.lenya.cms.cocoon.workflow;

import java.util.Map;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.impl.PolicyAuthorizer;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;

/**
 * Workflow helper.
 */
public class WorkflowHelper {

    /**
     * Creates a situation for a Cocoon object model.
     * @param objectModel The object model.
     * @return A workflow situation.
     * @throws WorkflowException when something went wrong.
     */
    public static Situation buildSituation(Map objectModel) throws WorkflowException {
        Request request = ObjectModelHelper.getRequest(objectModel);
        return buildSituation(request);
    }
    
    /**
     * Creates a situation for a request.
     * @param request The request.
     * @return A workflow situation.
     * @throws WorkflowException when something went wrong.
     */
    public static Situation buildSituation(Request request) throws WorkflowException {
        Role[] roles;
        try {
            roles = PolicyAuthorizer.getRoles(request);
        } catch (AccessControlException e) {
            throw new WorkflowException(e);
        }
        Session session = request.getSession(false);
        if (session == null) {
            throw new WorkflowException("Session not initialized!");
        }
        Identity identity = Identity.getIdentity(session);
        
        return WorkflowFactory.newInstance().buildSituation(roles, identity);
    }

}
