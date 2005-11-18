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
package org.apache.lenya.cms.repo;

/**
 * A site node, which represents a site (subsite) itself.
 */
public interface SiteNode {

    /**
     * @return The name, which corresponds to the URL snippet.
     * @throws RepositoryException if an error occurs.
     */
    String getName() throws RepositoryException;
    
    /**
     * @return The top level nodes in this area.
     * @throws RepositoryException if an error occurs.
     */
    SiteNode[] getChildren() throws RepositoryException;
    
    /**
     * @param name The name.
     * @return A child node.
     * @throws RepositoryException if an error occurs.
     */
    SiteNode getChild(String name) throws RepositoryException;

    /**
     * @param name The name.
     * @param contentNode The content node which the site node refers to.
     * @return a site node.
     * @throws RepositoryException if an error occurs.
     */
    SiteNode addChild(String name, ContentNode contentNode) throws RepositoryException;
    
    /**
     * @return The path of the node, relative to the area.
     * @throws RepositoryException if an error occurs.
     */
    String getPath() throws RepositoryException;
    
    /**
     * @return The referenced content node.
     * @throws RepositoryException if an error occurs.
     */
    ContentNode getContentNode() throws RepositoryException;
    
    /**
     * @return The parent node or <code>null</code> if this is a top-level node.
     * @throws RepositoryException if an error occurs.
     */
    SiteNode getParent() throws RepositoryException;
}
