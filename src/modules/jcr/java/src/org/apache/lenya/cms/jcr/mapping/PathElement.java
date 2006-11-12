/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
 * A path element.
 */
public interface PathElement {
    
    /**
     * @param parent The parent node.
     * @return The node identified by this path element, relative to the parent.
     * @throws RepositoryException if not exactly one node is identified.
     */
    Node getNode(Node parent) throws RepositoryException;
    
    /**
     * @param parent The parent node.
     * @return All nodes identified by this path element, relative to the parent.
     * @throws RepositoryException if an error occurs.
     */
    Node[] getNodes(Node parent) throws RepositoryException;
    
    /**
     * @param parent The parent node.
     * @return if this path element identifies a node, relative to the parent.
     * @throws RepositoryException if an error occurs.
     */
    boolean existsNode(Node parent) throws RepositoryException;

}
