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
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lenya.cms.observation.ObservationRegistry;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.observation.RepositoryListener;
import org.apache.lenya.cms.publication.DocumentFactoryBuilder;
import org.apache.lenya.transaction.ConcurrentModificationException;
//florent : change this clase with the core-ac one in order to remove duplications
//import org.apache.lenya.transaction.Identity;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.transaction.IdentityMap;
//import org.apache.lenya.transaction.IdentityMapImpl;
import org.apache.lenya.transaction.Lock;
import org.apache.lenya.transaction.Lockable;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.transaction.TransactionLock;
import org.apache.lenya.transaction.Transactionable;
import org.apache.lenya.transaction.UnitOfWork;
import org.apache.lenya.transaction.UnitOfWorkImpl;

/**
 * Repository session.
 * 
 * @deprecated have to solve the concurrency beetween lenya-core-repository/o.a.l.cms.repository.SessionImpl and lenya-publication-impl/o.a.l.cms.publication.SEssionImpl 
 */
public class SessionImpl implements Session {
    
    private static final Log logger = LogFactory.getLog(SessionImpl.class);

    protected static final String UNMODIFIABLE_SESSION_ID = "unmodifiable";
    private Identity identity;
    private ObservationRegistry observationRegistry;
    private String id;
    private String userId;

    protected ObservationRegistry getObservationRegistry() {
        return observationRegistry;
    }

    protected void setObservationRegistry(ObservationRegistry observationRegistry)
            throws RepositoryException {
        if (this.observationRegistry != null) {
            throw new IllegalStateException("Observation registry already set.");
        }
        this.observationRegistry = observationRegistry;
        addListener(observationRegistry);
    }

    /**
     * Ctor.
     * @param identity The identity.
     * @param modifiable Determines if the repository items in this session can be modified.
     */
    //florent protected SessionImpl(Identity identity, boolean modifiable) {
    public SessionImpl(Identity identity, boolean modifiable) {

        this.identityMap = new IdentityMapImpl();
        this.identity = identity;
        this.id = modifiable ? createUuid() : UNMODIFIABLE_SESSION_ID;

        if (modifiable) {
            this.unitOfWork = new UnitOfWorkImpl(this.identityMap, this.identity);
        }
    }

    protected String createUuid() {
        return UUID.randomUUID().toString();
    }

    public Identity getIdentity() {
        return this.identity;
    }

    private UnitOfWork unitOfWork;
    private SharedItemStore sharedItemStore;

    /**
     * @return The unit of work.
     */
    protected UnitOfWork getUnitOfWork() {
        if (this.unitOfWork == null) {
            throw new RuntimeException("This session [" + getId() + "] is not modifiable!");
        }
        return this.unitOfWork;
    }

    private boolean committing = false;

    /**
     * Commits the transaction.
     * @throws RepositoryException if an error occurs.
     * @throws ConcurrentModificationException if a transactionable has been modified by another
     *             session.
     */
    public synchronized void commit() throws RepositoryException, ConcurrentModificationException {

        savePersistables();

        this.committing = true;

        try {
            synchronized (TransactionLock.LOCK) {

                getUnitOfWork().commit();
                //florent : remove session getSharedItemStore().clear();
            }
        } catch (ConcurrentModificationException e) {
            throw e;
        } catch (TransactionException e) {
            throw new RepositoryException(e);
        }

        for (Iterator i = this.events.iterator(); i.hasNext();) {
            RepositoryEvent event = (RepositoryEvent) i.next();
            for (Iterator l = this.listeners.iterator(); l.hasNext();) {
                RepositoryListener listener = (RepositoryListener) l.next();
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
        Object[] objects = getIdentityMap().getObjects();
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

    protected SharedItemStore getSharedItemStore() {
        return this.sharedItemStore;
    }

    protected void setSharedItemStore(SharedItemStore sharedItemStore) {
        this.sharedItemStore = sharedItemStore;
    }

    /**
     * @see org.apache.lenya.cms.repository.Session#getRepositoryItem(org.apache.lenya.cms.repository.RepositoryItemFactory,
     *      java.lang.String)
     */
    public RepositoryItem getRepositoryItem(RepositoryItemFactory factory, String key)
            throws RepositoryException {
        //florent remove session RepositoryItemFactoryWrapper wrapper = new RepositoryItemFactoryWrapper(factory, this);
    	RepositoryItemFactoryWrapper wrapper = new RepositoryItemFactoryWrapper(factory);
        return (RepositoryItem) getIdentityMap().get(wrapper, key);
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
    private IdentityMap identityMap;

    private SessionHolder holder;

    public synchronized void enqueueEvent(RepositoryEvent event) {
        //florent : remove session Validate.isTrue(event.getSession() == this, "event belongs to session");
        if (!isModifiable()) {
            throw new RuntimeException("Can't enqueue event in unmodifiable session!");
        }
        if (committing) {
            throw new RuntimeException(
                    "No events can be queued while the session is being committed. Event: ["
                            + event.getDescriptor() + "]");
        }
        this.events.add(event);
    }

    protected IdentityMap getIdentityMap() {
        return this.identityMap;
    }

    public boolean isModifiable() {
        return this.unitOfWork != null;
    }

    public String getId() {
        return this.id;
    }

    public String toString() {
        return "Session " + getId();
    }

    public SessionHolder getHolder() {
        return this.holder;
    }
    
    public void setHolder(SessionHolder holder) {
        this.holder = holder;
    }

    //florent : add for use the repository session inside the repository module...
    private DocumentFactoryBuilder documentFactoryBuilder;

    public void setDocumentFactoryBuilder(DocumentFactoryBuilder documentFactoryBuilder) {
      this.documentFactoryBuilder = documentFactoryBuilder;
  }
}
