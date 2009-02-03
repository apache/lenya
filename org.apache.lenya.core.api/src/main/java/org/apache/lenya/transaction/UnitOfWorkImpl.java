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
package org.apache.lenya.transaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.util.Assert;

/**
 * Default implementation of a unit of work.
 * 
 * @version $Id$
 */
public class UnitOfWorkImpl extends AbstractLogEnabled implements UnitOfWork {

    /**
     * Ctor.
     * @param map The identity map to use.
     * @param identity The identity.
     * @param logger The logger.
     */
    public UnitOfWorkImpl(IdentityMap map, Identity identity, Logger logger) {
        ContainerUtil.enableLogging(this, logger);

        Assert.notNull(map);
        this.identityMap = map;
        this.identityMap.setUnitOfWork(this);

        this.identity = identity;
    }

    private IdentityMap identityMap;

    /**
     * @return The identity map.
     */
    public IdentityMap getIdentityMap() {
        return this.identityMap;
    }

    private Set newObjects = new HashSet();
    private Set modifiedObjects = new HashSet();
    private Set removedObjects = new HashSet();

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#registerNew(org.apache.lenya.transaction.Transactionable)
     */
    public void registerNew(Transactionable object) throws TransactionException {
        this.newObjects.add(object);
    }

    /**
     * @throws TransactionException
     * @throws LockException
     * @see org.apache.lenya.transaction.UnitOfWork#registerDirty(org.apache.lenya.transaction.Transactionable)
     */
    public void registerDirty(Transactionable object) throws TransactionException {
        this.modifiedObjects.add(object);
    }

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#registerRemoved(org.apache.lenya.transaction.Transactionable)
     */
    public void registerRemoved(Transactionable object) throws TransactionException {
        this.removedObjects.add(object);
    }

    /**
     * Commit the transaction. We lock this method for the whole class to avoid synchronization
     * problems.
     * @see org.apache.lenya.transaction.UnitOfWork#commit()
     */
    public void commit() throws TransactionException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("UnitOfWorkImpl::commit() called");
        }

        Set lockedObjects = this.locks.keySet();

        for (Iterator i = lockedObjects.iterator(); i.hasNext();) {
            Transactionable t = (Transactionable) i.next();
            if (t.hasChanged()) {
                throw new ConcurrentModificationException(t);
            }
        }

        Set involvedObjects = new HashSet();
        involvedObjects.addAll(this.newObjects);
        involvedObjects.addAll(this.modifiedObjects);
        involvedObjects.addAll(this.removedObjects);

        try {
            for (Iterator i = involvedObjects.iterator(); i.hasNext();) {
                Transactionable t = (Transactionable) i.next();
                t.checkout();
            }

            for (Iterator i = this.newObjects.iterator(); i.hasNext();) {
                Transactionable t = (Transactionable) i.next();
                t.createTransactionable();
                t.saveTransactionable();
            }
            for (Iterator i = this.modifiedObjects.iterator(); i.hasNext();) {
                Transactionable t = (Transactionable) i.next();
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("UnitOfWorkImpl::commit() calling save on [" + t + "]");
                }
                t.saveTransactionable();
            }
            for (Iterator i = this.removedObjects.iterator(); i.hasNext();) {
                Transactionable t = (Transactionable) i.next();
                t.deleteTransactionable();
            }

        } finally {
            if (getIdentityMap() != null) {
                Object[] objects = getIdentityMap().getObjects();
                for (int i = 0; i < objects.length; i++) {
                    if (objects[i] instanceof Transactionable) {
                        Transactionable t = (Transactionable) objects[i];
                        if (t.isCheckedOutBySession() && !this.removedObjects.contains(t)) {
                            t.checkin();
                        }
                        if (t.isLocked()) {
                            t.unlock();
                        }
                    }
                }
            }
        }

        resetTransaction();

    }

    protected void resetTransaction() {
        this.modifiedObjects.clear();
        this.newObjects.clear();
        this.removedObjects.clear();
    }

    private Identity identity;

    protected Identity getIdentity() {
        return this.identity;
    }

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#isDirty(org.apache.lenya.transaction.Transactionable)
     */
    public boolean isDirty(Transactionable transactionable) {
        return this.modifiedObjects.contains(transactionable)
                || this.newObjects.contains(transactionable)
                || this.removedObjects.contains(transactionable);
    }

    /**
     * Rollback the transaction. We lock this method for the whole class to avoid synchronization
     * problems.
     * @see org.apache.lenya.transaction.UnitOfWork#rollback()
     */
    public synchronized void rollback() throws TransactionException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("UnitOfWorkImpl::rollback() called");
        }
        if (getIdentityMap() != null) {
            Object[] objects = getIdentityMap().getObjects();
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof Transactionable) {
                    Transactionable t = (Transactionable) objects[i];
                    if (t.isCheckedOutBySession()) {
                        t.checkin();
                    }
                    if (t.isLocked()) {
                        t.unlock();
                    }
                }
            }
            resetTransaction();
        }
    }

    private Map locks = new HashMap();

    public Lock createLock(Lockable lockable, int version) throws TransactionException {
        if (this.locks.containsKey(lockable)) {
            throw new LockException("A lock is already placed on [" + lockable
                    + "]. A new lock could lead to inconsistent data.");
        }
        Lock lock = new Lock(version);
        this.locks.put(lockable, lock);
        return lock;
    }

    public void removeLock(Lockable lockable) throws TransactionException {
        if (!this.locks.containsKey(lockable)) {
            throw new LockException("No lock is already placed on [" + lockable + "]!");
        }
        this.locks.remove(lockable);
    }

}
