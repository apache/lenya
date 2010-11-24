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
package org.apache.lenya.cms.publication;

import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.publication.LockException;

/**
 * Common interface for documents and site structures. This is a hack, we should try to find a
 * better approach.
 * 
 * @deprecated doublon with o.a.l.cms.repository.Node
 */
public interface Node {

    boolean isCheckedOutBySession(String sessionId, String userId) throws RepositoryException;

    void checkin() throws RepositoryException;

    Session getSession();

    boolean isCheckedOut() throws RepositoryException;

    String getCheckoutUserId() throws RepositoryException;

    void checkout() throws RepositoryException;

    void lock() throws LockException, RepositoryException;

    void unlock() throws RepositoryException;

    void registerDirty() throws RepositoryException;

    boolean isLocked() throws RepositoryException;

    void forceCheckIn() throws RepositoryException;

    void rollback(int revision) throws RepositoryException;

    void checkout(boolean checkoutRestrictedToSession) throws RepositoryException;

    String getSourceURI();
}
