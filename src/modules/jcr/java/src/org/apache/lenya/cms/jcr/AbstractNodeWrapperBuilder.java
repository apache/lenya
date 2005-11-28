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

import org.apache.lenya.cms.repo.RepositoryException;

/**
 * Abstract node wrapper builder.
 */
public abstract class AbstractNodeWrapperBuilder implements NodeWrapperBuilder {

    protected abstract NodeWrapper getNodeInternal(JCRSession session, BuilderParameters parameters)
            throws RepositoryException;

    public NodeWrapper getNode(JCRSession session, BuilderParameters parameters)
            throws RepositoryException {
        NodeWrapper node = getNodeInternal(session, parameters);
        if (node == null) {
            throw new RepositoryException("The node does not exist.");
        }
        return node;
    }
    
    public boolean existsNode(JCRSession session, BuilderParameters parameters) throws RepositoryException {
        return getNodeInternal(session, parameters) != null;
    }

}
