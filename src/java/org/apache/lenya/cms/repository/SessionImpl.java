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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.observation.ObservationRegistry;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.observation.RepositoryListener;
import org.apache.lenya.transaction.ConcurrentModificationException;
import org.apache.lenya.transaction.IdentityMap;
import org.apache.lenya.transaction.IdentityMapImpl;
import org.apache.lenya.transaction.Lock;
import org.apache.lenya.transaction.Lockable;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.transaction.TransactionLock;
import org.apache.lenya.transaction.Transactionable;
import org.apache.lenya.transaction.UnitOfWork;
import org.apache.lenya.transaction.UnitOfWorkImpl;
import org.apache.lenya.util.Assert;

import com.google.common.collect.MapMaker;

/**
 * Repository session.
 */
public class SessionImpl extends AbstractLogEnabled implements Session {

    protected static final String UNMODIFIABLE_SESSION_ID = "unmodifiable";
    private ServiceManager manager;
    private Identity identity;
    private boolean modifiable;
    // Cache repository items if session is not modifiable.
    private ConcurrentMap<String, RepositoryItem> itemCache =
        new MapMaker().softValues().makeMap();

    /**
     * Ctor.
     * @param identity The identity.
     * @param modifiable Determines if the repository items in this session can be modified.
     * @param manager The service manager.
     * @param logger The logger.
     */
    public SessionImpl(Identity identity, boolean modifiable, ServiceManager manager, Logger logger) {

        ContainerUtil.enableLogging(this, logger);

        Assert.notNull("service manager", manager);
        this.manager = manager;

        this.identity = identity;
        this.id = modifiable ? createUuid() : UNMODIFIABLE_SESSION_ID;

        ObservationRegistry registry = null;
        try {
            registry = (ObservationRegistry) this.manager.lookup(ObservationRegistry.ROLE);
            addListener(registry);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (registry != null) {
                this.manager.release(registry);
            }
        }
        this.modifiable = modifiable;
        if (modifiable) {
            this.unitOfWork = new UnitOfWorkImpl(new IdentityMapImpl(logger),
                    this.identity, getLogger());
        }
    }

    protected String createUuid() {
        String id;
        UUIDGenerator generator = null;
        try {
            generator = (UUIDGenerator) this.manager.lookup(UUIDGenerator.ROLE);
            id = generator.nextUUID();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (generator == null) {
                this.manager.release(generator);
            }
        }
        return id;
    }

    public Identity getIdentity() {
        return this.identity;
    }

    private UnitOfWorkImpl unitOfWork;

    /**
     * @return The unit of work.
     */
    protected UnitOfWork getUnitOfWork() {
        if (!isModifiable()) {
            throw new RuntimeException("This session [" + getId() + "] is not modifiable!");
        }
        return this.unitOfWork;
    }

    private boolean committing = false;

    /**
     * Commits the transaction.
     * @throws RepositoryException if an error occurs.
     * @throws ConcurrentModificationException if a transactionable has been modified by another
     *         session.
     */
    public synchronized void commit() throws RepositoryException, ConcurrentModificationException {

        savePersistables();
        
        this.committing = true;

        try {
            synchronized (TransactionLock.LOCK) {
                
                getUnitOfWork().commit();
            }
        } catch (ConcurrentModificationException e) {
            throw e;
        } catch (TransactionException e) {
            throw new RepositoryException(e);
        }

        for (RepositoryEvent event : events) {
            for (RepositoryListener listener : listeners) {
                listener.eventFired(event);
            }
        }
        this.events.clear();
        this.committing = false;
    }

    /**
     * Save all persistable objects to their nodes.
     * @throws RepositoryException if an error occurs.
     */
    protected void savePersistables() throws RepositoryException {
        if (!isModifiable())
            throw new RepositoryException("Session not modifiable.");
        Object[] objects = unitOfWork.getIdentityMap().getObjects();
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof Node) {
                Node node = (Node) objects[i];
                Persistable persistable = node.getPersistable();
                if (persistable != null && persistable.isModified()) {
                    persistable.save();
                }
            }
        }
    }

    /**
     * Rolls the transaction back.
     * @throws RepositoryException if an error occurs.
     */
    public void rollback() throws RepositoryException {
        try {
            synchronized (TransactionLock.LOCK) {
                getUnitOfWork().rollback();
            }
        } catch (TransactionException e) {
            throw new RepositoryException(e);
        }
        this.events.clear();
    }

    /**
     * @see org.apache.lenya.cms.repository.Session#getRepositoryItem(org.apache.lenya.cms.repository.RepositoryItemFactory,
     *      java.lang.String)
     */
    public RepositoryItem getRepositoryItem(RepositoryItemFactory factory, String key)
            throws RepositoryException {
        RepositoryItem repositoryItem;
        if (isModifiable()) {
            IdentityMap identityMap = unitOfWork.getIdentityMap();
            RepositoryItemFactoryWrapper wrapper = new RepositoryItemFactoryWrapper(factory, this);
            repositoryItem = (RepositoryItem) identityMap.get(wrapper, key);
        } else {
            repositoryItem = itemCache.get(key);
            if (repositoryItem == null) {
                repositoryItem = factory.buildItem(this, key);
                itemCache.put(key, repositoryItem);
            }
        }
        return repositoryItem;
    }

    @Override
    public void invalidateRepositoryItem(String key) {
        itemCache.remove(key);
    }

    @Override
    public void invalidateAllRepositoryItems() {
        itemCache.clear();
    }

    public void registerNew(Transactionable object) throws TransactionException {
        getUnitOfWork().registerNew(object);
    }

    public void registerDirty(Transactionable object) throws TransactionException {
        getUnitOfWork().registerDirty(object);
    }

    public void registerRemoved(Transactionable object) throws TransactionException {
        getUnitOfWork().registerRemoved(object);
    }

    /**
     * @param identity The identity.
     */
    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public boolean isDirty(Transactionable transactionable) {
        return getUnitOfWork().isDirty(transactionable);
    }

    public Lock createLock(Lockable lockable, int version) throws TransactionException {
        return getUnitOfWork().createLock(lockable, version);
    }

    public void removeLock(Lockable lockable) throws TransactionException {
        getUnitOfWork().removeLock(lockable);
    }

    private Set<RepositoryListener> listeners = new HashSet<RepositoryListener>();

    public void addListener(RepositoryListener listener) throws RepositoryException {
        if (this.listeners.contains(listener)) {
            throw new RepositoryException("The listener [" + listener
                    + "] is already registered for node [" + this + "]!");
        }
        this.listeners.add(listener);
    }

    public boolean isListenerRegistered(RepositoryListener listener) {
        return this.listeners.contains(listener);
    }

    private List<RepositoryEvent> events = new ArrayList<RepositoryEvent>();

    public synchronized void enqueueEvent(RepositoryEvent event) {
        if (!isModifiable()) {
            throw new RuntimeException("Can't enqueue event in unmodifiable session!");
        }
        if (committing) {
            throw new RuntimeException(
                    "No events can be queued while the session is being committed. Event: ["
                            + event.getDescriptor() + "]");
        }
        Assert.isTrue("event belongs to session", event.getSession() == this);
        this.events.add(event);
    }

    public boolean isModifiable() {
        return modifiable;
    }

    private String id;

    public String getId() {
        return this.id;
    }
    
    public String toString() {
        return "Session " + getId();
    }

}
