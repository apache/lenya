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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

import org.apache.lenya.cms.repo.RepositoryException;

/**
 * A list of node wrappers.
 */
public class NodeWrapperManager {

    /**
     * Ctor.
     * @param session The session.
     * @param builder The builder.
     */
    public NodeWrapperManager(JCRSession session, NodeWrapperBuilder builder) {
        this.session = session;
        this.builder = builder;
    }

    private JCRSession session;
    private NodeWrapperBuilder builder;

    private Map key2node = new HashMap();

    protected NodeWrapper getNode(String key, BuilderParameters parameters)
            throws org.apache.lenya.cms.repo.RepositoryException {

        NodeWrapper wrapper = (NodeWrapper) this.key2node.get(key);
        if (wrapper == null) {
            wrapper = builder.getNode(this.session, parameters);
            this.key2node.put(key, wrapper);
        }

        return wrapper;
    }

    protected NodeWrapper getNode(String key, BuilderParameters parameters, boolean create)
            throws org.apache.lenya.cms.repo.RepositoryException {
        
        if (builder.existsNode(this.session, parameters)) {
            return builder.getNode(this.session, parameters);
        }
        else if (create) {
            return builder.addNode(this.session, parameters);
        }
        else {
            throw new org.apache.lenya.cms.repo.RepositoryException("The node does not exist!");
        }
    }

    protected NodeWrapper addNode(String key, BuilderParameters parameters)
            throws org.apache.lenya.cms.repo.RepositoryException {

        NodeWrapper wrapper = (NodeWrapper) this.key2node.get(key);
        if (wrapper == null) {
            wrapper = builder.addNode(this.session, parameters);
            this.key2node.put(key, wrapper);
        }

        return wrapper;
    }

    private List keys;

    protected String[] getKeys(Node reference) throws org.apache.lenya.cms.repo.RepositoryException {
        loadKeys(reference);
        return (String[]) this.keys.toArray(new String[this.keys.size()]);
    }

    protected void loadKeys(Node reference) throws RepositoryException {
        if (this.keys == null) {
            this.keys = new ArrayList();
            if (!(this.builder instanceof ResolvingNodeWrapperBuilder)) {
                throw new RuntimeException("The builder must be a ResolvingNodeWrapperBuilder.");
            }
            ResolvingNodeWrapperBuilder resolvingBuilder = (ResolvingNodeWrapperBuilder) builder;

            String[] nodeKeys = resolvingBuilder.getKeys(session, reference);
            this.keys.addAll(Arrays.asList(nodeKeys));
        }
    }

    protected boolean contains(Node reference, String key) throws RepositoryException {
        loadKeys(reference);
        return this.keys.contains(key);
    }

}
