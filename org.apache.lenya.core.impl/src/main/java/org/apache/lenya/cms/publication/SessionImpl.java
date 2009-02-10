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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.observation.ObservationRegistry;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.observation.RepositoryEventFactory;
import org.apache.lenya.cms.observation.RepositoryListener;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.Persistable;
import org.apache.lenya.cms.repository.RepositoryItem;
import org.apache.lenya.cms.repository.RepositoryItemFactory;
import org.apache.lenya.cms.repository.RepositoryItemFactoryWrapper;
import org.apache.lenya.cms.repository.SharedItemStore;
import org.apache.lenya.cms.repository.UUIDGenerator;
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

public class SessionImpl implements Session {

    private static final Log logger = LogFactory.getLog(SessionImpl.class);
    
    private org.apache.lenya.cms.repository.Session repositorySession;
    private RepositoryImpl repository;
    private DocumentFactory documentFactory;
    private DocumentFactoryBuilder documentFactoryBuilder;

    
    public SessionImpl(RepositoryImpl repository, org.apache.lenya.cms.repository.Session repoSession) {
        Validate.notNull(repository, "repository");
        Validate.notNull(repoSession, "repository session");
        this.repository = repository;
        this.repositorySession = repoSession;
    }

    protected org.apache.lenya.cms.repository.Session getRepositorySession() {
        return this.repositorySession;
    }

    public Publication getPublication(String id) {
        try {
            return getDocumentFactory().getPublication(id);
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }
    }

    public Repository getRepository() {
        return this.repository;
    }

    public DocumentFactory getDocumentFactory() {
        if (this.documentFactory == null) {
            this.documentFactory = this.documentFactoryBuilder.createDocumentFactory(this);
        }
        return this.documentFactory;
    }

    public boolean existsPublication(String id) {
        return getDocumentFactory().existsPublication(id);
    }

    public Publication[] getPublications() {
        return getDocumentFactory().getPublications();
    }

    public Identity getIdentity() {
        return ((IdentityWrapper) getRepositorySession()).getIdentity();
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

    public String getId() {
        return getRepositorySession().getId();
    }

    public synchronized void commit() throws RepositoryException {
        try {
            getRepositorySession().commit();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public void rollback() throws RepositoryException {
        try {
            getRepositorySession().rollback();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @param identity The identity.
     */
    public void setIdentity(Identity identity) {
        getRepositorySession().setIdentity(new IdentityWrapper(identity));
    }

    private UriHandler uriHandler;

    public UriHandler getUriHandler() {
        if (this.uriHandler == null) {
            this.uriHandler = new UriHandlerImpl(getDocumentFactory());
        }
        return this.uriHandler;
    }

    public void enqueueEvent(Document document, Object descriptor) {
        RepositoryEvent event = RepositoryEventFactory.createEvent(document, descriptor);
        getRepositorySession().enqueueEvent(event);
    }

    public boolean isModifiable() {
        return getRepositorySession().isModifiable();
    }

}
