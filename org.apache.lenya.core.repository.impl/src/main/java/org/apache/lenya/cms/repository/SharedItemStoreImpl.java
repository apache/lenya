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

import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.observation.RepositoryListener;
//florent remove this identity as duplication of the core-ac one 
//import org.apache.lenya.transaction.Identity;
//import org.apache.lenya.ac.Identity;
import org.apache.lenya.transaction.Lock;
import org.apache.lenya.transaction.Lockable;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.transaction.Transactionable;

/**
 * Shared item store implementation.
 */
public class SharedItemStoreImpl extends AbstractLogEnabled implements SharedItemStore {

    //florent : remove session private Session session;
    private RepositoryManager repositoryManager;

    public RepositoryManager getRepositoryManager() {
        return repositoryManager;
    }

    public void setRepositoryManager(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

//    public synchronized Session getSession() {
//        if (this.session == null) {
//            try {
//                this.session = getRepositoryManager().createSession(null, false);
//            } catch (RepositoryException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return this.session;
//    }

    public void addListener(RepositoryListener listener) throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    public void commit() throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    public void enqueueEvent(RepositoryEvent event) {
        throw new UnsupportedOperationException();
    }

    /*public Identity getIdentity() {
        throw new UnsupportedOperationException();
    }*/

    public RepositoryItem getRepositoryItem(RepositoryItemFactory factory, String key)
            throws RepositoryException {
        //florent remove session return (RepositoryItem) getSession().getRepositoryItem(factory, key);
    	throw new UnsupportedOperationException();
    }

    public boolean isListenerRegistered(RepositoryListener listener) {
        return false;
    }

    public boolean isModifiable() {
        return false;
    }

    public void rollback() throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    /*public void setIdentity(Identity identity) {
        throw new UnsupportedOperationException();
    }*/

    public Lock createLock(Lockable lockable, int version) throws TransactionException {
        throw new UnsupportedOperationException();
    }

    public boolean isDirty(Transactionable transactionable) {
        return false;
    }

    public void registerDirty(Transactionable object) throws TransactionException {
        throw new UnsupportedOperationException();
    }

    public void registerNew(Transactionable object) throws TransactionException {
        throw new UnsupportedOperationException();
    }

    public void registerRemoved(Transactionable object) throws TransactionException {
        throw new UnsupportedOperationException();
    }

    public void removeLock(Lockable lockable) throws TransactionException {
        throw new UnsupportedOperationException();
    }

//    public synchronized void clear() {
//        this.session = null;
//    }

    public String getId() {
        return getClass().getName();
    }

    /*public SessionHolder getHolder() {
        throw new UnsupportedOperationException();
    }

    public void setHolder(SessionHolder holder) {
        throw new UnsupportedOperationException();
    }*/

}
