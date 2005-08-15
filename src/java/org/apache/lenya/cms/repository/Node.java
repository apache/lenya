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
package org.apache.lenya.cms.repository;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.lenya.cms.metadata.MetaDataOwner;

/**
 * Repository node.
 * 
 * @version $Id$
 */
public interface Node extends MetaDataOwner {
    
    /**
     * @return The session this node belongs to.
     */
    Session getSession();

    /**
     * The protocol with which to find Lenya nodes
     */
    String LENYA_PROTOCOL = "lenya://";

    /**
     * The identifiable type.
     */
    String IDENTIFIABLE_TYPE = "node";

    /**
     * @return if the node exists.
     * @throws RepositoryException if an error occurs.
     */
    boolean exists() throws RepositoryException;

    /**
     * @return if the node is a collection.
     * @throws RepositoryException if an error occurs.
     */
    boolean isCollection() throws RepositoryException;

    /**
     * @return children
     * @throws RepositoryException if an error occurs.
     */
    public Collection getChildren() throws RepositoryException;

    /**
     * @return The input stream.
     * @throws RepositoryException if the node does not exist.
     */
    InputStream getInputStream() throws RepositoryException;

    /**
     * @return The output stream.
     * @throws RepositoryException if the node does not exist.
     */
    OutputStream getOutputStream() throws RepositoryException;
    
    /**
     * @return The last modification date.
     * @throws RepositoryException if the node does not exist.
     */
    long getLastModified() throws RepositoryException;
    
    /**
     * @return The content length.
     * @throws RepositoryException if the node does not exist.
     */
    long getContentLength() throws RepositoryException;
    
    /**
     * @return The MIME type.
     * @throws RepositoryException if the node does not exist.
     */
    String getMimeType() throws RepositoryException;

    /**
     * Accessor for the source URI of this node
     * @return the source URI
     */
    String getSourceURI();

    /**
     * Locks the node.
     * @throws RepositoryException if an error occurs.
     */
    void lock() throws RepositoryException;

    /**
     * Unlocks the node.
     * @throws RepositoryException if an error occurs.
     */
    void unlock() throws RepositoryException;

    /**
     * Checks out the node.
     * @throws RepositoryException if an error occurs. 
     */
    void checkout() throws RepositoryException;

    /**
     * Checks in the node.
     * @throws RepositoryException if an error occurs. 
     */
    void checkin() throws RepositoryException;

    /**
     * Registers the node as dirty.
     * @throws RepositoryException if an error occurs.
     */
    void registerDirty() throws RepositoryException;

    /**
     * @return if the node is checked out.
     * @throws RepositoryException if an error occurs.
     */
    boolean isCheckedOut() throws RepositoryException;

    /**
     * @return if the node is checked out by the current user.
     * @throws RepositoryException if an error occurs.
     */
    boolean isCheckedOutByUser() throws RepositoryException;

    /**
     * @return if the node is locked.
     * @throws RepositoryException if an error occurs.
     */
    boolean isLocked() throws RepositoryException;

    /**
     * Registers the node as removed.
     * @throws RepositoryException if an error occurs.
     */
    void registerRemoved() throws RepositoryException;
    
}
