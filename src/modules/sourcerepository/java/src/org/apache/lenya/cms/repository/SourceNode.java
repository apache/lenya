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
import org.apache.lenya.cms.rc.RCMLEntry;
import org.apache.lenya.cms.rc.RevisionController;
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
        if (!isCheckedOut()) {
            throw new RepositoryException("Cannot check in node [" + getSourceURI()
                    + "]: not checked out!");
        }

        try {
            String userName = getUserId();
            boolean newVersion = getSession().isDirty(this);
            getRevisionController().reservedCheckIn(this, userName, exists(), newVersion);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isCheckedOut()
     */
    public boolean isCheckedOut() throws RepositoryException {
        try {
            return getRevisionController().isCheckedOut(this);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isCheckedOutByUser()
     */
    public boolean isCheckedOutByUser() throws RepositoryException {
        try {
            if (getRcml().getEntries().size() > 0) {
                RCMLEntry entry = getRcml().getLatestEntry();
                if (entry.getIdentity().equals(getUserId()) && isCheckedOut()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#checkout()
     */
    public void checkout() throws RepositoryException {

        if (getLogger().isDebugEnabled())
            getLogger().debug("SourceNode::checkout() called, sourceURI [" + getSourceURI() + "]");

        if (!isCheckedOut()) {
            try {
                getRevisionController().reservedCheckOut(this, getUserId());
            } catch (Exception e) {
                throw new RepositoryException(e);
            }
        }
    }

    private RevisionController revisionController;

    protected RevisionController getRevisionController() throws RepositoryException {
        if (this.revisionController == null) {
            this.revisionController = new RevisionController(getLogger());
        }
        return this.revisionController;
    }

    private Lock lock;

    /**
     * @see org.apache.lenya.transaction.Transactionable#hasChanged()
     */
    public boolean hasChanged() throws RepositoryException {
        try {
            int currentVersion = getRevisionController().getLatestVersion(this);
            int lockVersion = getLock().getVersion();
            return currentVersion > lockVersion;
        } catch (Exception e) {
            throw new RepositoryException(e);
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
        if (isCheckedOut() && !isCheckedOutByUser()) {
            throw new RepositoryException("Cannot lock [" + this + "]: node is checked out.");
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Locking [" + this + "]");
        }
        int currentVersion;
        try {
            currentVersion = getRevisionController().getLatestVersion(this);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
        try {
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
        RepositoryEvent event = RepositoryEventFactory.createEvent(this.manager, this,
                getLogger(), descriptor);
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

    public RCML getRcml() {
        if (this.rcml == null) {
            try {
                this.rcml = new SourceNodeRCML(this, this.manager);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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

    public void saveTransactionable() throws TransactionException {
        this.contentSource.saveTransactionable();
        this.metaSource.saveTransactionable();
    }

    public void delete() throws RepositoryException {
        this.contentSource.delete();
        this.metaSource.delete();
        registerRemoved();
    }

}
