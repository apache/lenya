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

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.observation.RepositoryListener;
import org.apache.lenya.transaction.IdentityMap;
import org.apache.lenya.transaction.IdentityMapImpl;
import org.apache.lenya.transaction.Lock;
import org.apache.lenya.transaction.Lockable;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.transaction.Transactionable;

/**
 * Shared item store implementation.
 */
public class SharedItemStoreImpl extends AbstractLogEnabled implements SharedItemStore, ThreadSafe {

    private IdentityMap map;

    protected synchronized IdentityMap getIdentityMap() {
        if (this.map == null) {
            this.map = new IdentityMapImpl(getLogger());
        }
        return this.map;
    }

    public void addListener(RepositoryListener listener) throws RepositoryException {
        throw new IllegalStateException("Operation not permitted.");
    }

    public void commit() throws RepositoryException {
        throw new IllegalStateException("Operation not permitted.");
    }

    public void enqueueEvent(RepositoryEvent event) {
        throw new IllegalStateException("Operation not permitted.");
    }

    public Identity getIdentity() {
        throw new IllegalStateException("Operation not permitted.");
    }

    public RepositoryItem getRepositoryItem(RepositoryItemFactory factory, String key)
            throws RepositoryException {
        RepositoryItemFactoryWrapper wrapper = new RepositoryItemFactoryWrapper(factory, this);
        return (RepositoryItem) getIdentityMap().get(wrapper, key);
    }

    public boolean isListenerRegistered(RepositoryListener listener) {
        return false;
    }

    public boolean isModifiable() {
        return false;
    }

    public void rollback() throws RepositoryException {
        throw new IllegalStateException("Operation not permitted.");
    }

    public void setIdentity(Identity identity) {
        throw new IllegalStateException("Operation not permitted.");
    }

    public Lock createLock(Lockable lockable, int version) throws TransactionException {
        throw new IllegalStateException("Operation not permitted.");
    }

    public boolean isDirty(Transactionable transactionable) {
        return false;
    }

    public void registerDirty(Transactionable object) throws TransactionException {
        throw new IllegalStateException("Operation not permitted.");
    }

    public void registerNew(Transactionable object) throws TransactionException {
        throw new IllegalStateException("Operation not permitted.");
    }

    public void registerRemoved(Transactionable object) throws TransactionException {
        throw new IllegalStateException("Operation not permitted.");
    }

    public void removeLock(Lockable lockable) throws TransactionException {
        throw new IllegalStateException("Operation not permitted.");
    }

    public synchronized void clear() {
        this.map = null;
    }
    
}
