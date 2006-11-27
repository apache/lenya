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

import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.rc.RCML;

/**
 * Repository node.
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
    
    
    /**
     * @return The RCML to use for this node.
     */
    RCML getRcml();
    
    /**
     * @return The revision history.
     */
    History getHistory();
    
    /**
     * @return The event to use when this node is added/changed/removed.
     */
    RepositoryEvent getEvent();
}
