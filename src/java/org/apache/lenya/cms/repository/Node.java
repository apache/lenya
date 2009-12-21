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
package org.apache.lenya.cms.repository;

import java.io.OutputStream;
import java.util.Collection;

/**
 * A repository node is used to persist a {@link Persistable}.
 * 
 * @version $Id$
 */
public interface Node extends RepositoryItem, ContentHolder {

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
     * @return The output stream.
     * @throws RepositoryException if the node does not exist.
     */
    OutputStream getOutputStream() throws RepositoryException;

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
     * Checks out the node with restriction to the current session..
     * @throws RepositoryException if an error occurs.
     */
    void checkout() throws RepositoryException;

    /**
     * Checks out the node with the possibility to allow other sessions to check it in.
     * This is a workaround for the current WYSIWYG editor infrastructure, which can't
     * use the same session for opening and saving a node.
     * @param restrictedToSession if the check-out is restricted to the current session.
     * @throws RepositoryException if an error occurs.
     */
    void checkout(boolean restrictedToSession) throws RepositoryException;

    /**
     * Checks in the node.
     * @throws RepositoryException if the node is not checked out or is checked
     *         out by a different session.
     */
    void checkin() throws RepositoryException;

    /**
     * Checks in the node even if it is checked out by a different session.
     * @throws RepositoryException if the node is not checked out.
     */
    void forceCheckIn() throws RepositoryException;

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
     * @return The ID of the user who has checked out this node.
     * @throws RepositoryException if the node is not checked out.
     */
    String getCheckoutUserId() throws RepositoryException;

    /**
     * Checks if the node is checked out by a certain session. We pass the session
     * as a parameter to allow the check for nodes from the shared item store.
     * @param session The session.
     * @return if the node is checked out by a specific session.
     * @throws RepositoryException if an error occurs.
     */
    boolean isCheckedOutBySession(Session session) throws RepositoryException;

    /**
     * @param source The node to copy the revisions from.
     * @throws RepositoryException if an error occurs.
     */
    void copyRevisionsFrom(Node source) throws RepositoryException;

    /**
     * @param revisionNumber The revision number to roll back.
     * @throws RepositoryException if this revision doesn't exist.
     */
    void rollback(int revisionNumber) throws RepositoryException;

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

    /**
     * @return The revision history.
     */
    History getHistory();

    /**
     * Delete this node.
     * @throws RepositoryException if an error occurs.
     */
    void delete() throws RepositoryException;
    
    /**
     * @param persistable The object which is persisted using the node.
     * @throws RepositoryException if the node already has an item.
     */
    void setPersistable(Persistable persistable) throws RepositoryException;
    
    /**
     * @return The object which is persisted using this node or <code>null</code>
     * if no object is registered.
     */
    Persistable getPersistable();

}
