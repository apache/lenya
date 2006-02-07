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

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.WorkspaceImpl;
import org.apache.lenya.cms.jcr.metadata.JCRMetaDataRegistry;
import org.apache.lenya.cms.repo.AssetTypeRegistry;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.impl.AssetTypeRegistryImpl;
import org.apache.lenya.cms.repo.metadata.MetaDataRegistry;

/**
 * Facade to the JCR repository, providing Lenya-specific access.
 */
public class JCRRepository implements org.apache.lenya.cms.repo.Repository {

    /**
     * The workspace to store internal data.
     */
    public static final String INTERNAL_WORKSPACE = "internal";

    /**
     * Ctor.
     * @param repository The repository.
     */
    public JCRRepository(Repository repository) {
        this.repository = repository;
    }

    private Repository repository;

    protected Repository getRepository() {
        return this.repository;
    }

    /**
     * @return The repository session.
     */
    public org.apache.lenya.cms.repo.Session createSession() {
        return new JCRSession(this);
    }

    private AssetTypeRegistry typeRegistry;

    public AssetTypeRegistry getAssetTypeRegistry()
            throws org.apache.lenya.cms.repo.RepositoryException {
        if (this.typeRegistry == null) {
            this.typeRegistry = new AssetTypeRegistryImpl();
        }
        return this.typeRegistry;
    }

    private MetaDataRegistry metaDataRegistry;

    public MetaDataRegistry getMetaDataRegistry() throws RepositoryException {
        if (this.metaDataRegistry == null) {
            this.metaDataRegistry = new JCRMetaDataRegistry(getInternalSession());
        }
        return this.metaDataRegistry;
    }

    private Session internalSession;

    protected Session getInternalSession() throws RepositoryException {
        if (this.internalSession == null) {
            try {
                Session defaultWorkspaceSession = getRepository().login(new SimpleCredentials("john",
                        "".toCharArray()));
                WorkspaceImpl defaultWorkspace = (WorkspaceImpl) defaultWorkspaceSession.getWorkspace();
                String[] workspaces = defaultWorkspace.getAccessibleWorkspaceNames();
                if (!Arrays.asList(workspaces).contains(INTERNAL_WORKSPACE)) {
                    defaultWorkspace.createWorkspace(INTERNAL_WORKSPACE);
                    // create = true;
                }

                this.internalSession = getRepository().login(new SimpleCredentials("john",
                        "".toCharArray()),
                        INTERNAL_WORKSPACE);
            } catch (javax.jcr.RepositoryException e) {
                throw new RepositoryException(e);
            }
        }
        return this.internalSession;
    }

    public void shutdown() throws RepositoryException {
        if (this.internalSession != null) {
            this.internalSession.logout();
        }
        ((RepositoryImpl) this.repository).shutdown();
    }

}
