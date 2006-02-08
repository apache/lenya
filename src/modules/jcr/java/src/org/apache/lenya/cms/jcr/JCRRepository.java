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

import javax.jcr.Repository;
import javax.jcr.Session;

import org.apache.lenya.cms.jcr.metadata.JCRMetaDataRegistry;
import org.apache.lenya.cms.repo.AssetTypeResolver;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.metadata.MetaDataRegistry;

/**
 * Facade to the JCR repository, providing Lenya-specific access.
 */
public abstract class JCRRepository implements org.apache.lenya.cms.repo.Repository {

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

    private AssetTypeResolver typeResolver;

    public AssetTypeResolver getAssetTypeResolver()
            throws org.apache.lenya.cms.repo.RepositoryException {
        return this.typeResolver;
    }

    public void setAssetTypeResolver(AssetTypeResolver resolver) {
        this.typeResolver = resolver;
    }

    private MetaDataRegistry metaDataRegistry;
    protected Session internalSession;

    public MetaDataRegistry getMetaDataRegistry() throws RepositoryException {
        if (this.metaDataRegistry == null) {
            this.metaDataRegistry = new JCRMetaDataRegistry(getInternalSession());
        }
        return this.metaDataRegistry;
    }

    protected abstract Session getSession(String workspaceName) throws RepositoryException;

    protected Session getInternalSession() throws RepositoryException {
        if (this.internalSession == null) {
            this.internalSession = getSession(INTERNAL_WORKSPACE);
        }
        return this.internalSession;
    }
}
