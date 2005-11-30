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
package org.apache.lenya.cms.jcr.mapping;

import javax.jcr.Node;

import org.apache.lenya.cms.repo.RepositoryException;

/**
 * Proxy for JCR nodes. 
 */
public interface NodeProxy {
    
    /**
     * @return The repository this node belongs to.
     * @throws RepositoryException if an error occurs.
     */
    RepositoryFacade getRepository() throws RepositoryException;
    
    /**
     * @return The JCR node.
     * @throws RepositoryException if an error occurs.
     */
    Node getNode() throws RepositoryException;

    /**
     * Initializes the proxy.
     * @param facade The repository.
     * @param node The JCR node.
     * @throws RepositoryException if an error occurs.
     */
    void setup(RepositoryFacade facade, Node node) throws RepositoryException;
    
    /**
     * @return The absolute path of this node.
     * @throws RepositoryException if an error occurs.
     */
    Path getAbsolutePath() throws RepositoryException;
    
}
