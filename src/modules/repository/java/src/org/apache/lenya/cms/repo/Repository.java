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
package org.apache.lenya.cms.repo;

import org.apache.lenya.cms.repo.metadata.MetaDataRegistry;

/**
 * Repository.
 */
public interface Repository {

    /**
     * @return A session.
     * @throws RepositoryException if an error occurs.
     */
    Session createSession() throws RepositoryException;

    /**
     * @return The meta data registry.
     * @throws RepositoryException if an error occurs.
     */
    MetaDataRegistry getMetaDataRegistry() throws RepositoryException;

    /**
     * Shuts down the repository.
     * @throws RepositoryException if an error occurs.
     */
    void shutdown() throws RepositoryException;

    /**
     * Sets the asset type resolver to use. This method has to be invoked before the content is
     * accessed.
     * @param resolver A resolver.
     * @throws RepositoryException if an error occurs.
     */
    void setAssetTypeResolver(AssetTypeResolver resolver) throws RepositoryException;
    
    /**
     * @return The asset type resolver.
     * @throws RepositoryException if no asset type resolver is registered.
     */
    AssetTypeResolver getAssetTypeResolver() throws RepositoryException;
}
