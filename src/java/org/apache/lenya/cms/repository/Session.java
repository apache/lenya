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

import org.apache.lenya.ac.Identity;
import org.apache.lenya.transaction.UnitOfWork;

/**
 * Repository session.
 */
public interface Session extends UnitOfWork {

    /**
     * @return the identity this session belongs to.
     */
    Identity getIdentity();

    /**
     * Commits the transaction.
     * @throws RepositoryException if an error occurs.
     */
    void commit() throws RepositoryException;

    /**
     * Rolls the transaction back.
     * @throws RepositoryException if an error occurs.
     */
    void rollback() throws RepositoryException;

    /**
     * @param factory The factory.
     * @param key The key.
     * @return The item for the specific key.
     * @throws RepositoryException if an error occurs.
     */
    RepositoryItem getRepositoryItem(RepositoryItemFactory factory, String key)
            throws RepositoryException;

}
