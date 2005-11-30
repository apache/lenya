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
 * The site structure of an area.
 */
public interface Site {

    /**
     * @return The top level nodes in this area.
     * @throws RepositoryException if an error occurs.
     */
    SiteNode[] getChildren() throws RepositoryException;
    
    /**
     * @return The nodes of this site in preorder.
     * @throws RepositoryException if an error occurs.
     */
    SiteNode[] preOrder() throws RepositoryException;
    
    /**
     * @param name The name.
     * @param contentNode The content node which the site node refers to.
     * @return a site node.
     * @throws RepositoryException if a child with this name already exists.
     */
    SiteNode addChild(String name, ContentNode contentNode) throws RepositoryException;
    
    /**
     * @param name The name.
     * @return a site node.
     * @throws RepositoryException if the child does not exist.
     */
    SiteNode getChild(String name) throws RepositoryException;
    
    /**
     * @param path The path of the node.
     * @return A site node.
     * @throws RepositoryException if the node does not exist.
     */
    SiteNode getNode(String path) throws RepositoryException;
    
    /**
     * @return The area this site belongs to.
     * @throws RepositoryException if an error occurs.
     */
    Area getArea() throws RepositoryException;
    
}
