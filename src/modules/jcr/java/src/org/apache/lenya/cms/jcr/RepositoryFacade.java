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

import java.io.File;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

/**
 * Facade to the JCR repository, providing Lenya-specific access.
 */
public class RepositoryFacade {

    public RepositoryFacade(String webappDirectoryPath, String repositoryFactoryClass)
            throws RepositoryException {
        try {
            Class repoFactoryClass = Class.forName(repositoryFactoryClass);
            RepositoryFactory repoFactory = (RepositoryFactory) repoFactoryClass.newInstance();

            File webappDirectory = new File(webappDirectoryPath);

            String jaasPath = "lenya/modules/jackrabbit/repository/jaas.config";
            System.setProperty("java.security.auth.login.config", new File(webappDirectory,
                    jaasPath).getAbsolutePath());

            this.repository = repoFactory.getRepository(webappDirectoryPath);
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    private Repository repository;

    public Repository getRepository() {
        return this.repository;
    }

    /**
     * @return The repository session.
     */
    public RepositorySession createSession() {
        return new RepositorySession(this);
    }

    protected Node getSubNode(Session session, Node parent, String childName)
            throws RepositoryException {

        Node child = null;
        if (parent.hasNode(childName)) {
            child = parent.getNode(childName);
        } else {
            child = parent.addNode(childName);
        }
        return child;
    }

}
