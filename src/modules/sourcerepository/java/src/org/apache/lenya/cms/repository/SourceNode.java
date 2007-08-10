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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.TraversableSource;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.observation.DocumentEvent;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.observation.RepositoryEventFactory;
import org.apache.lenya.cms.rc.RCML;
import org.apache.lenya.cms.rc.RevisionControlException;
import org.apache.lenya.transaction.Lock;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.transaction.Transactionable;

/**
 * A repository node.
 * 
 * @version $Id$
 */
public class SourceNode extends AbstractLogEnabled implements Node, Transactionable {

    protected ServiceManager manager;

    private ContentSourceWrapper contentSource;
    private MetaSourceWrapper metaSource;

    /**
     * Ctor.
     * 
     * @param session
     * @param sourceUri
     * @param manager
     * @param logger
     */
    public SourceNode(Session session, String sourceUri, ServiceManager manager, Logger logger) {
        this.manager = manager;
        enableLogging(logger);
        this.session = session;

        this.contentSource = new ContentSourceWrapper(this, sourceUri, manager, logger);
        this.metaSource = new MetaSourceWrapper(this, sourceUri, manager, logger);
    }

    protected ContentSourceWrapper getContentSource() {
        return this.contentSource;
    }

    protected MetaSourceWrapper getMetaSource() {
        return this.metaSource;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#deleteTransactionable()
     */
    public void deleteTransactionable() throws RepositoryException {
        this.contentSource.deleteTransactionable();
        this.metaSource.deleteTransactionable();
        getRcml().delete();
    }

    protected String getUserId() {
        String userId = null;
        Identity identity = getSession().getIdentity();
        if (identity != null) {
            User user = identity.getUser();
            if (user != null) {
                userId = user.getId();
            }
        }
        return userId;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#checkin()
     */
    public void checkin() throws RepositoryException {
        RCML rcml = getRcml();
        synchronized (rcml) {
            try {
                if (!rcml.isCheckedOut()) {
                    throw new RepositoryException("Cannot check in node [" + getSourceURI()
                            + "]: not checked out!");
                }
                boolean newVersion = getSession().isDirty(this);
                rcml.checkIn(this, exists(), newVersion);
            } catch (Exception e) {
                throw new RepositoryException(e);
            }
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isCheckedOut()
     */
    public boolean isCheckedOut() throws RepositoryException {
        try {
            return getRcml().isCheckedOut();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isCheckedOutBySession()
     */
    public boolean isCheckedOutBySession() throws RepositoryException {
        try {
            return getRcml().isCheckedOutBySession(getSession());
        } catch (RevisionControlException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#checkout()
     */
    public void checkout() throws RepositoryException {

        if (getLogger().isDebugEnabled())
            getLogger().debug("SourceNode::checkout() called, sourceURI [" + getSourceURI() + "]");

        RCML rcml = getRcml();
        synchronized (rcml) {
            try {
                if (rcml.isCheckedOut() && !rcml.isCheckedOutBySession(getSession())) {
                    throw new RepositoryException("The node [" + this
                            + "] is already checked out by another session!");
                }
                if (!rcml.isCheckedOut()) {
                    rcml.checkOut(this);
                }
            } catch (RevisionControlException e) {
                throw new RepositoryException(e);
            }
        }
    }

    private Lock lock;

    /**
     * @see org.apache.lenya.transaction.Transactionable#hasChanged()
     */
    public boolean hasChanged() throws RepositoryException {
        try {
            int currentVersion = getCurrentRevisionNumber();
            int lockVersion = getLock().getVersion();
            return currentVersion > lockVersion;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    protected int getCurrentRevisionNumber() {
        if (getHistory().getRevisionNumbers().length > 0) {
            return getHistory().getLatestRevision().getNumber();
        }
        else {
            return 0;
        }
    }

    /**
     * @return The document node, if this is a meta data node, or the node
     *         itself otherwise.
     * @throws ServiceException
     * @throws RepositoryException
     */
    protected Node getDocumentNode() throws ServiceException, RepositoryException {
        Node node;
        String sourceUri = getSourceURI();
        if (sourceUri.endsWith(".meta")) {
            String documentSourceUri = sourceUri
                    .substring(0, sourceUri.length() - ".meta".length());
            NodeFactory factory = null;
            try {
                factory = (NodeFactory) this.manager.lookup(NodeFactory.ROLE);
                node = (Node) factory.buildItem(getSession(), documentSourceUri);
            } finally {
                if (factory != null) {
                    this.manager.release(factory);
                }
            }
        } else {
            node = this;
        }
        return node;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#createTransactionable()
     */
    public void createTransactionable() throws RepositoryException {
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#lock()
     */
    public void lock() throws RepositoryException {
        if (isCheckedOut() && !isCheckedOutBySession()) {
            throw new RepositoryException("Cannot lock [" + this
                    + "]: node is checked out by this session.");
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Locking [" + this + "]");
        }
        try {
            int currentVersion = getCurrentRevisionNumber();
            this.lock = getSession().createLock(this, currentVersion);
        } catch (TransactionException e) {
            throw new RepositoryException(e);
        }

    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#getLock()
     */
    public Lock getLock() {
        return this.lock;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#unlock()
     */
    public void unlock() throws RepositoryException {
        this.lock = null;
        try {
            getSession().removeLock(this);
        } catch (TransactionException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isLocked()
     */
    public boolean isLocked() throws RepositoryException {
        return this.lock != null;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "node " + getSourceURI();
    }

    /**
     * 
     */
    public Collection getChildren() throws RepositoryException {
        SourceResolver resolver = null;
        TraversableSource source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = (TraversableSource) resolver.resolveURI(this.contentSource.getRealSourceUri());
            Collection children = source.getChildren();
            java.util.Iterator iterator = children.iterator();
            java.util.Vector newChildren = new java.util.Vector();
            while (iterator.hasNext()) {
                TraversableSource child = (TraversableSource) iterator.next();
                newChildren.add(new SourceNode(getSession(),
                        getSourceURI() + "/" + child.getName(), this.manager, getLogger()));
            }
            return newChildren;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * 
     */
    public boolean isCollection() throws RepositoryException {
        SourceResolver resolver = null;
        TraversableSource source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = (TraversableSource) resolver.resolveURI(this.contentSource.getRealSourceUri());
            return source.isCollection();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.repository.Node#getSourceURI()
     */
    public String getSourceURI() {
        return this.contentSource.getSourceUri();
    }

    private Session session;

    /**
     * @see org.apache.lenya.cms.repository.Node#getSession()
     */
    public Session getSession() {
        return this.session;
    }

    public void registerDirty() throws RepositoryException {
        try {
            if (!getSession().isDirty(this)) {
                getSession().registerDirty(this);
                enqueueEvent(DocumentEvent.CHANGED);
            }
        } catch (TransactionException e) {
            throw new RepositoryException(e);
        }
    }

    protected void enqueueEvent(Object descriptor) {
        RepositoryEvent event = RepositoryEventFactory.createEvent(this.manager, this, getLogger(),
                descriptor);
        getSession().enqueueEvent(event);
    }

    public void registerRemoved() throws RepositoryException {
        try {
            getSession().registerRemoved(this);
            enqueueEvent(DocumentEvent.REMOVED);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    private RCML rcml;

    protected synchronized RCML getRcml() {
        if (this.rcml == null) {
            SourceNodeRcmlFactory factory = SourceNodeRcmlFactory.getInstance();
            this.rcml = factory.getRcml(this, this.manager);
        }
        return this.rcml;
    }

    public History getHistory() {
        return new SourceNodeHistory(this, this.manager, getLogger());
    }

    public MetaData getMetaData(String namespaceUri) throws MetaDataException {
        return this.metaSource.getMetaData(namespaceUri);
    }

    public boolean exists() throws RepositoryException {
        return this.contentSource.exists();
    }

    public OutputStream getOutputStream() throws RepositoryException {
        return this.contentSource.getOutputStream();
    }

    public long getContentLength() throws RepositoryException {
        return this.contentSource.getContentLength();
    }

    public InputStream getInputStream() throws RepositoryException {
        return this.contentSource.getInputStream();
    }

    public long getLastModified() throws RepositoryException {

        if (!exists()) {
            throw new RepositoryException("The node [" + this + "] does not exist!");
        }

        long contentLastModified = this.contentSource.getLastModified();
        long metaLastModified = 0;
        if (this.metaSource.exists()) {
            metaLastModified = this.metaSource.getLastModified();
        }

        return Math.max(contentLastModified, metaLastModified);
    }

    public String getMimeType() throws RepositoryException {
        return this.contentSource.getMimeType();
    }

    public String[] getMetaDataNamespaceUris() throws MetaDataException {
        return this.metaSource.getMetaDataNamespaceUris();
    }

    public synchronized void saveTransactionable() throws TransactionException {
        if (!isCheckedOut()) {
            throw new RepositoryException("Cannot save node [" + getSourceURI()
                    + "]: not checked out!");
        }
        this.contentSource.saveTransactionable();
        this.metaSource.saveTransactionable();
    }

    public void delete() throws RepositoryException {
        this.contentSource.delete();
        this.metaSource.delete();
        registerRemoved();
    }

    public String getCheckoutUserId() throws RepositoryException {
        RCML rcml = getRcml();
        synchronized (rcml) {
            try {
                if (!rcml.isCheckedOut()) {
                    throw new RepositoryException("The node [" + this + "] is not checked out!");
                }
                return rcml.getLatestEntry().getIdentity();
            } catch (RevisionControlException e) {
                throw new RepositoryException(e);
            }
        }
    }

    public void copyRevisionsFrom(Node source) throws RepositoryException {
        try {
            getRcml().copyFrom(this, source);
        } catch (RevisionControlException e) {
            throw new RepositoryException(e);
        }
    }

    public void rollback(int revisionNumber) throws RepositoryException {
        try {
            long time = getHistory().getRevision(revisionNumber).getTime();
            getRcml().restoreBackup(this, time);
        } catch (RevisionControlException e) {
            throw new RepositoryException(e);
        }
    }

}
