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

import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.commons.logging.Log;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.TraversableSource;
import org.apache.lenya.cms.observation.RepositoryEventDescriptor;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.observation.RepositoryEventImpl;
import org.apache.lenya.cms.rc.CheckInEntry;
import org.apache.lenya.cms.rc.RCML;
import org.apache.lenya.cms.rc.RCMLEntry;
import org.apache.lenya.cms.rc.RevisionControlException;
//florent :
/*import org.apache.lenya.cms.repository.metadata.MetaData;
import org.apache.lenya.cms.repository.metadata.MetaDataException;*/
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.transaction.Lock;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.transaction.Transactionable;
import org.apache.lenya.ac.Identity;

/**
 * A repository node.
 * 
 */
public class SourceNode extends AbstractLogEnabled implements Node, Transactionable {

    private ContentSourceWrapper contentSource;
    private MetaSourceWrapper metaSource;
    private NodeFactory nodeFactory;
    private SourceResolver sourceResolver;
    private SourceNodeRcmlFactory rcmlFactory;

    /**
     * Ctor.
     * @param sourceNodeRepository
     * 
     * @param session
     * @param sourceUri
     * @param logger
     */
    public SourceNode(Session session, String sourceUri, SourceResolver resolver, Log logger) {
        this.session = session;
        this.sourceResolver = resolver;
        this.contentSource = new ContentSourceWrapper(this, sourceUri, resolver, logger);
        this.metaSource = new MetaSourceWrapper(this, sourceUri, resolver, logger);
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
        return getRepositorySession().getIdentity().getUserId();
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#checkin()
     */
    public void checkin() throws RepositoryException {
        RCML rcml = getRcml();
        synchronized (rcml) {
            try {
                if (!rcml.isCheckedOutBySession(getRepositorySession().getId(),
                        getRepositorySession().getIdentity().getUserId())) {
                    throw new RepositoryException("Cannot check in node [" + getSourceURI()
                            + "]: not checked out by this session!");
                }
                rcml.checkIn(this, exists(), getRepositorySession().isDirty(this));
            } catch (Exception e) {
                throw new RepositoryException(e);
            }
        }
    }

    public void forceCheckIn() throws RepositoryException {
        RCML rcml = getRcml();
        synchronized (rcml) {
            try {
                if (!rcml.isCheckedOut()) {
                    throw new RepositoryException("Cannot check in node [" + getSourceURI()
                            + "]: not checked out!");
                }
                rcml.checkIn(this, false, false);
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

    public boolean isCheckedOutBySession(String sessionId, String userId)
            throws RepositoryException {
        try {
            return getRcml().isCheckedOutBySession(sessionId, userId);
        } catch (RevisionControlException e) {
            throw new RepositoryException(e);
        }
    }

    public void checkout(boolean restrictedToSession) throws RepositoryException {

        if (getLogger().isDebugEnabled())
            getLogger().debug("SourceNode::checkout() called, sourceURI [" + getSourceURI() + "]");

        RCML rcml = getRcml();
        synchronized (rcml) {
            try {
                if (rcml.isCheckedOut()
                        && !rcml.isCheckedOutBySession(getRepositorySession().getId(),
                                getRepositorySession().getIdentity().getUserId())) {
                    throw new RepositoryException("The node [" + this
                            + "] is already checked out by another session!");
                }
                if (!rcml.isCheckedOut()) {
                    rcml.checkOut(this, restrictedToSession);
                }
            } catch (RevisionControlException e) {
                throw new RepositoryException(e);
            }
        }
    }

    public void checkout() throws RepositoryException {
        checkout(true);
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

    protected int getCurrentRevisionNumber() throws RepositoryException {
        CheckInEntry entry;
        try {
            entry = (CheckInEntry)getRcml().getLatestCheckInEntry();
        } catch (RevisionControlException e) {
            throw new RepositoryException(e);
        }
        if (entry == null) {
            return 0;
        } else {
            return entry.getVersion();
        }
    }

    /**
     * @return The document node, if this is a meta data node, or the node itself otherwise.
     * @throws ServiceException
     * @throws RepositoryException
     */
    protected Node getDocumentNode() throws RepositoryException {
        Node node;
        String sourceUri = getSourceURI();
        if (sourceUri.endsWith(".meta")) {
            String documentSourceUri = sourceUri
                    .substring(0, sourceUri.length() - ".meta".length());
            node = (Node) getNodeFactory().buildItem(getRepositorySession(), documentSourceUri);
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
    public synchronized void lock() throws RepositoryException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Locking [" + this + "]");
        }
        try {
            int currentRev = getCurrentRevisionNumber();
            int contentLoadRev = getContentSource().getLoadRevision();
            int contentRev = contentLoadRev == -1 ? currentRev : contentLoadRev;

            int metaLoadRev = getMetaSource().getLoadRevision();
            int metaRev = metaLoadRev == -1 ? currentRev : metaLoadRev;

            int lockRev = Math.min(contentRev, metaRev);
            this.lock = getRepositorySession().createLock(this, lockRev);
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
            getRepositorySession().removeLock(this);
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
        return "node " + getSourceURI() + " (" + getContentSource().getRealSourceUri() + ")";
    }

    /**
     * 
     */
    public Collection getChildren() throws RepositoryException {
        TraversableSource source = null;
        try {
            source = (TraversableSource) this.sourceResolver.resolveURI(
                    this.contentSource.getRealSourceUri());
            Collection children = source.getChildren();
            java.util.Iterator iterator = children.iterator();
            java.util.Vector newChildren = new java.util.Vector();
            while (iterator.hasNext()) {
                TraversableSource child = (TraversableSource) iterator.next();
                SourceNode node = new SourceNode(getRepositorySession(), getSourceURI() + "/"
                        + child.getName(), this.sourceResolver, getLogger());
                node.setRcmlFactory(this.rcmlFactory);
                newChildren.add(node);
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
        TraversableSource source = null;
        try {
            source = (TraversableSource) this.sourceResolver.resolveURI(
                    this.contentSource.getRealSourceUri());
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
     * @see org.apache.lenya.cms.repository.Node#getRepositorySession()
     */
    public Session getRepositorySession() {
        return this.session;
    }

    public void registerDirty() throws RepositoryException {
        try {
            if (!getRepositorySession().isDirty(this)) {
                getRepositorySession().registerDirty(this);
                enqueueEvent(RepositoryEventDescriptor.CHANGED);
            }
        } catch (TransactionException e) {
            throw new RepositoryException(e);
        }
    }

    protected void enqueueEvent(Object descriptor) {
        RepositoryEvent event = new RepositoryEventImpl(getRepositorySession(), this, descriptor);
        getRepositorySession().enqueueEvent(event);
    }

    public void registerRemoved() throws RepositoryException {
        try {
            getRepositorySession().registerRemoved(this);
            enqueueEvent(RepositoryEventDescriptor.REMOVED);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    private RCML rcml;
    private Persistable persistable;

    protected synchronized RCML getRcml() {
        if (this.rcml == null) {
            this.rcml = this.rcmlFactory.getRcml(this);
        }
        return this.rcml;
    }

    public History getHistory() {
        return new SourceNodeHistory(this, this.sourceResolver, getLogger());
    }

    public MetaData getMetaData(String namespaceUri) throws MetaDataException {
        return this.metaSource.getMetaData(namespaceUri);
    }

    public boolean exists() throws RepositoryException {
        try {
            RCML rcml = getRcml();
            RCMLEntry entry = rcml.getLatestEntry();
            if (entry == null) {
                return false;
            } else if (entry.getType() == RCML.ci) {
                return true;
            } else {
                if (rcml.getLatestCheckInEntry() != null) {
                    return true;
                } else {
                    // before first check-in, the node exists only in the session that created it
                    return entry.getSessionId().equals(getRepositorySession().getId());
                }
            }
        } catch (RevisionControlException e) {
            throw new RepositoryException(e);
        }
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
        try {
            CheckInEntry entry = (CheckInEntry)getRcml().getLatestCheckInEntry();
            if (entry != null) {
                return entry.getTime();
            } else {
                throw new RepositoryException("The node [" + this + "] hasn't been checked in yet.");
            }
        } catch (RepositoryException e) {
            throw e;
        } catch (RevisionControlException e) {
            throw new RepositoryException(e);
        }
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
            boolean wasLocked = isLocked();
            if (wasLocked) {
                unlock();
            }
            getRcml().copyFrom(this, source);
            if (wasLocked) {
                // this is a hack: update the lock revision to the latest copied revision to avoid
                // the "node has changed" error
                this.lock = getRepositorySession().createLock(this, getCurrentRevisionNumber());
            }
        } catch (RevisionControlException e) {
            throw new RepositoryException(e);
        } catch (TransactionException e) {
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

    public boolean isCheckedOutBySession() throws TransactionException {
        return isCheckedOutBySession(getRepositorySession().getId(), getRepositorySession()
                .getIdentity().getUserId());
    }

    public void setPersistable(Persistable item) throws RepositoryException {
        this.persistable = item;
    }

    public Persistable getPersistable() {
        return this.persistable;
    }

    public String getCacheKey() {
        return getSourceURI();
    }

    protected NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    protected void setNodeFactory(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public void setRcmlFactory(SourceNodeRcmlFactory rcmlFactory) {
        this.rcmlFactory = rcmlFactory;
    }

}
