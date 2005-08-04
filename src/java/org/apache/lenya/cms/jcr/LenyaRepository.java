/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.jcr;

import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.RepositoryException;

import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.jcr.JackrabbitRepository;

/**
 * Lenya-specific repository implementation.
 */
public class LenyaRepository extends JackrabbitRepository {
    
    protected static final String SESSION_ATTRIBUTE = javax.jcr.Session.class.getName();
    
    /**
     * @see javax.jcr.Repository#login()
     */
    public javax.jcr.Session login() throws LoginException, NoSuchWorkspaceException, RepositoryException {
        
        javax.jcr.Session jcrSession = null;
        
        Map objectModel = ContextHelper.getObjectModel(this.context);
        Request request = ObjectModelHelper.getRequest(objectModel);
        Session session = request.getSession(false);
        if (session != null) {
            jcrSession = (javax.jcr.Session) session.getAttribute(SESSION_ATTRIBUTE);
            if (jcrSession == null) {
                jcrSession = super.login();
                session.setAttribute(SESSION_ATTRIBUTE, jcrSession);
            }
        }
        
        return jcrSession;
    }

}
