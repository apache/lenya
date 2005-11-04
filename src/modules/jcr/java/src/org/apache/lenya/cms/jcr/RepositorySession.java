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
package org.apache.lenya.cms.jcr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.core.WorkspaceImpl;

/**
 * Repository session.
 */
public class RepositorySession {

    /**
     * Ctor.
     * @param repository The repository facade.
     */
    public RepositorySession(RepositoryFacade repository) {
        this.repository = repository;
    }

    private RepositoryFacade repository;

    protected RepositoryFacade getRepository() {
        return this.repository;
    }

    private Map area2session = new HashMap();

    public Session getSession(String area) throws RepositoryException {
        Session session = (Session) this.area2session.get(area);
        if (session == null) {

            Session defaultWorkspaceSession = getRepository().getRepository()
                    .login(new SimpleCredentials("john", "".toCharArray()));

            WorkspaceImpl defaultWorkspace = (WorkspaceImpl) defaultWorkspaceSession.getWorkspace();
            String[] workspaces = defaultWorkspace.getAccessibleWorkspaceNames();
            if (!Arrays.asList(workspaces).contains(area)) {
                defaultWorkspace.createWorkspace(area);
            }

            session = getRepository().getRepository().login(new SimpleCredentials("john",
                    "".toCharArray()),
                    area);

            NamespaceRegistry registry = session.getWorkspace().getNamespaceRegistry();
            String uri = "http://apache.org/cocoon/lenya/jcr/1.0";
            if (!Arrays.asList(registry.getURIs()).contains(uri)) {
                registry.registerNamespace("lenya", uri);
            }
            this.area2session.put(area, session);
        }

        return session;
    }

}
