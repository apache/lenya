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

import javax.jcr.Node;

import org.apache.lenya.cms.repo.RepositoryException;

/**
 * Resolving node wrapper builder.
 */
public interface ResolvingNodeWrapperBuilder extends NodeWrapperBuilder {

    /**
     * @param session The JCR session.
     * @param reference The reference node.
     * @return All existing node keys.
     * @throws RepositoryException if an error occurs.
     */
    String[] getKeys(JCRSession session, Node reference) throws RepositoryException;
    
    /**
     * @param node The node.
     * @return The key for this node.
     * @throws RepositoryException if an error occurs.
     */
    String getKey(Node node) throws RepositoryException;
    
}
