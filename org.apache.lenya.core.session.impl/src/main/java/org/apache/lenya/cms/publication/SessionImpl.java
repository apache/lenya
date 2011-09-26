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

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.observation.RepositoryEventFactory;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.SessionHolder;
import org.apache.lenya.transaction.UnitOfWork;

/**
 * @deprecated have to solve the concurrency beetween lenya-core-repository/o.a.l.cms.repository.SessionImpl and lenya-publication-impl/o.a.l.cms.publication.SEssionImpl
 */
public class SessionImpl implements Session, SessionHolder {

    private static final Log logger = LogFactory.getLog(SessionImpl.class);

    private org.apache.lenya.cms.repository.Session repositorySession;
    //florent private RepositoryImpl repository;
    private Repository repository;
    private DocumentFactory documentFactory;
    private DocumentFactoryBuilder documentFactoryBuilder;

    public SessionImpl(Repository repository,
            org.apache.lenya.cms.repository.Session repoSession) {
        Validate.notNull(repository, "repository");
        Validate.notNull(repoSession, "repository session");
        this.repository = repository;
        this.repositorySession = repoSession;
        this.repositorySession.setHolder(this);
    }

    public org.apache.lenya.cms.repository.Session getRepositorySession() {
        return this.repositorySession;
    }

    public Publication getPublication(String id) throws ResourceNotFoundException {
        try {
            return getDocumentFactory().getPublication(id);
        } catch (PublicationException e) {
            throw new ResourceNotFoundException(e);
        }
    }

    public Repository getRepository() {
        return this.repository;
    }

    protected DocumentFactory getDocumentFactory() {
        if (this.documentFactory == null) {
            this.documentFactory = this.documentFactoryBuilder.createDocumentFactory(this);
        }
        return this.documentFactory;
    }

    public boolean existsPublication(String id) {
        return getDocumentFactory().existsPublication(id);
    }

    public String[] getPublicationIds() {
        return getDocumentFactory().getPublicationIds();
    }

    public Identity getIdentity() {
        //return ((IdentityWrapper) getRepositorySession().getIdentity()).getIdentity();
    	return getRepositorySession().getIdentity();
    }

    /**
     * @return The unit of work.
     */
    protected UnitOfWork getUnitOfWork() {
      if (repositorySession == null){
        throw new RuntimeException("This session [" + getId() + "] is not modifiable!");
      }  
      return repositorySession;
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
        //florent : now we only use one identity class
    	//getRepositorySession().setIdentity(new IdentityWrapper(identity));
    	getRepositorySession().setIdentity(identity);
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

    public void setDocumentFactoryBuilder(DocumentFactoryBuilder documentFactoryBuilder) {
        this.documentFactoryBuilder = documentFactoryBuilder;
    }

    public Publication addPublication(String id) throws RepositoryException {
        if (existsPublication(id)) {
            throw new RepositoryException("The publication '" + id + "' already exists.");
        }
        //florent : remove document-impl dependencie 
        //DocumentFactoryImpl factory = (DocumentFactoryImpl) getDocumentFactory();
        DocumentFactory factory = (DocumentFactory) getDocumentFactory();
        factory.getPublicationManager().addPublication(id);
        return getPublication(id);
    }

}
