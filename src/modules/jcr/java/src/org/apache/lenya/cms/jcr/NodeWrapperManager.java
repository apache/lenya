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

import java.util.HashMap;
import java.util.Map;

/**
 * A list of node wrappers.
 */
public class NodeWrapperManager {

    /**
     * Ctor.
     * @param session The session.
     */
    public NodeWrapperManager(JCRSession session) {
        this.session = session;
    }

    private JCRSession session;

    private Map key2node = new HashMap();

    protected NodeWrapper getNode(String key, NodeWrapperBuilder builder, boolean create)
            throws org.apache.lenya.cms.repo.RepositoryException {

        NodeWrapper wrapper = (NodeWrapper) this.key2node.get(key);
        if (wrapper == null) {
            wrapper = builder.buildNode(this.session, create);
            this.key2node.put(key, wrapper);
        }

        return wrapper;
    }
    
}
