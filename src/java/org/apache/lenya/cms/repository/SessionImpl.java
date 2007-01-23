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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.observation.ObservationRegistry;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.observation.RepositoryListener;
import org.apache.lenya.transaction.IdentityMap;
import org.apache.lenya.transaction.Lock;
import org.apache.lenya.transaction.Lockable;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.transaction.Transactionable;
import org.apache.lenya.transaction.UnitOfWork;
import org.apache.lenya.transaction.UnitOfWorkImpl;
import org.apache.lenya.util.Assert;

/**
 * Repository session.
 */
public class SessionImpl extends AbstractLogEnabled implements Session {

    private ServiceManager manager;
    
    /**
     * Ctor.
     * @param map The identity map.
     * @param identity The identity.
     * @param manager The service manager.
     * @param logger The logger.
     */
    public SessionImpl(IdentityMap map, Identity identity, ServiceManager manager, Logger logger) {
        
        Assert.notNull("identity map", map);
        
        this.manager = manager;
        this.unitOfWork = new UnitOfWorkImpl(map, identity, logger);
        this.unitOfWork.setIdentity(identity);
        ContainerUtil.enableLogging(this, logger);
        
        ObservationRegistry registry = null;
        try {
            registry = (ObservationRegistry) this.manager.lookup(ObservationRegistry.ROLE);
            addListener(registry);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (registry != null) {
                this.manager.release(registry);
            }
        }
    }

    public Identity getIdentity() {
        return getUnitOfWork().getIdentity();
    }

    private UnitOfWork unitOfWork;

    /**
     * @return The unit of work.
     */
    protected UnitOfWork getUnitOfWork() {
        return this.unitOfWork;
    }

    /**
     * Commits the transaction.
     * @throws RepositoryException if an error occurs.
     */
    public void commit() throws RepositoryException {
        
        try {
            getUnitOfWork().commit();
        } catch (TransactionException e) {
            throw new RepositoryException(e);
        }
        
        for (Iterator i = this.events.iterator(); i.hasNext(); ) {
            RepositoryEvent event = (RepositoryEvent) i.next();
            for (Iterator l = this.listeners.iterator(); l.hasNext(); ) {
                RepositoryListener listener = (RepositoryListener) l.next();
                listener.eventFired(event);
            }
        }
        this.events.clear();
    }

    /**
     * Rolls the transaction back.
     * @throws RepositoryException if an error occurs.
     */
    public void rollback() throws RepositoryException {
        try {
            getUnitOfWork().rollback();
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
        RepositoryItemFactoryWrapper wrapper = new RepositoryItemFactoryWrapper(factory, this);
        return (RepositoryItem) ((UnitOfWorkImpl) getUnitOfWork()).getIdentityMap().get(wrapper,
                key);
    }
    
    private Set newObjects = new HashSet();
    private Set modifiedObjects = new HashSet();
    private Set removedObjects = new HashSet();

    public void registerNew(Transactionable object) throws TransactionException {
        getUnitOfWork().registerNew(object);
        this.newObjects.add(object);
    }

    public void registerDirty(Transactionable object) throws TransactionException {
        getUnitOfWork().registerDirty(object);
        this.modifiedObjects.add(object);
    }

    public void registerRemoved(Transactionable object) throws TransactionException {
        getUnitOfWork().registerRemoved(object);
        this.removedObjects.add(object);
    }

    public void setIdentity(Identity identity) {
        getUnitOfWork().setIdentity(identity);
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

    private Set listeners = new HashSet();

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

    private List events = new ArrayList();
    
    public void enqueueEvent(RepositoryEvent event) {
        Assert.isTrue("event belongs to session", event.getSession() == this);
        this.events.add(event);
    }

}
