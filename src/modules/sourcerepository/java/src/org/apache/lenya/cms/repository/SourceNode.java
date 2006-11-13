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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.TraversableSource;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
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

    private String sourceURI;
    protected ServiceManager manager;
    protected static final String FILE_PREFIX = "file:/";
    protected static final String CONTEXT_PREFIX = "context://";
    protected static final String LENYA_META_SUFFIX = "meta";

    /**
     * Ctor.
     * 
     * @param session
     * @param sourceURI
     * @param manager
     * @param logger
     */
    public SourceNode(Session session, String sourceURI, ServiceManager manager, Logger logger) {
        this.sourceURI = sourceURI;
        this.manager = manager;
        enableLogging(logger);
        this.session = session;
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
     * Returns the URI of the actual source which is used.
     * 
     * @return A string.
     */
    protected String getRealSourceURI() {
        String contentDir = null;
        String publicationId = null;
        try {
            String pubBase = Node.LENYA_PROTOCOL + Publication.PUBLICATION_PREFIX_URI + "/";
            String publicationsPath = this.sourceURI.substring(pubBase.length());
            publicationId = publicationsPath.split("/")[0];
            DocumentFactory factory = DocumentUtil
                    .createDocumentFactory(this.manager, getSession());
            Publication pub = factory.getPublication(publicationId);
            contentDir = pub.getContentDir();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String contentBaseUri = null;
        String urlID = this.sourceURI.substring(Node.LENYA_PROTOCOL.length());

        // Substitute e.g. "lenya://lenya/pubs/PUB_ID/content" by "contentDir"
        String filePrefix = urlID.substring(0, urlID.indexOf(publicationId)) + publicationId;
        String tempString = urlID.substring(filePrefix.length() + 1);
        String fileMiddle = tempString.substring(0, tempString.indexOf("/"));
        String fileSuffix = tempString.substring(fileMiddle.length() + 1, tempString.length());
        String uriSuffix;
        if (new File(contentDir).isAbsolute()) {
            // Absolute
            contentBaseUri = FILE_PREFIX + contentDir;
            uriSuffix = File.separator + fileSuffix;
        } else {
            // Relative
            contentBaseUri = CONTEXT_PREFIX + contentDir;
            uriSuffix = "/" + fileSuffix;
        }

        try {
            if (!SourceUtil.exists(contentBaseUri, this.manager)) {
                getLogger().info(
                        "The content directory [" + contentBaseUri + "] does not exist. "
                                + "It will be created as soon as documents are added.");
            }
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String realSourceUri = contentBaseUri + uriSuffix;

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Real Source URI: " + realSourceUri);
        }

        return realSourceUri;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#checkin()
     */
    public void checkin() throws RepositoryException {
        if (!isCheckedOut()) {
            throw new RepositoryException("Cannot check in node [" + this.sourceURI
                    + "]: not checked out!");
        }

        try {
            String userName = getUserId();
            boolean newVersion = getSession().isDirty(this);
            getRevisionController().reservedCheckIn(this, userName, true, newVersion);
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
            RCMLEntry entry = getRcml().getLatestEntry();
            if (entry.getIdentity().equals(getUserId()) && isCheckedOut())
                return true;
            else
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
            getLogger().debug("SourceNode::checkout() called, sourceURI [" + sourceURI + "]");

        if (!isCheckedOut()) {
            try {
                getRevisionController().reservedCheckOut(this, getUserId());
            } catch (Exception e) {
                throw new RepositoryException(e);
            }
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#deleteTransactionable()
     */
    public void deleteTransactionable() throws RepositoryException {
        try {
            if (!isCheckedOut()) {
                throw new RuntimeException("Cannot delete source [" + this.sourceURI
                        + "]: not checked out!");
            } else {
                SourceUtil.delete(getRealSourceURI(), this.manager);
            }
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public void removed() {
        Node node;
        try {
            node = getDocumentNode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (Iterator i = this.listeners.iterator(); i.hasNext();) {
            NodeListener listener = (NodeListener) i.next();
            listener.nodeRemoved(node, getSession().getIdentity());
        }
    }

    private RevisionController revisionController;

    protected RevisionController getRevisionController() throws RepositoryException {
        if (this.revisionController == null) {
            this.revisionController = new RevisionController();
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
     * @see org.apache.lenya.transaction.Transactionable#saveTransactionable()
     */
    public synchronized void saveTransactionable() throws RepositoryException {
        if (!isCheckedOut()) {
            throw new RepositoryException("Cannot save node [" + this.sourceURI
                    + "]: not checked out!");
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Saving [" + this + "] to source [" + getRealSourceURI() + "]");
        }

        if (this.data != null) {
            SourceResolver resolver = null;
            ModifiableSource source = null;
            InputStream in = null;
            OutputStream out = null;
            try {

                resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
                source = (ModifiableSource) resolver.resolveURI(getRealSourceURI());

                out = source.getOutputStream();

                byte[] buf = new byte[4096];
                in = new ByteArrayInputStream(this.data);
                int read = in.read(buf);

                while (read > 0) {
                    out.write(buf, 0, read);
                    read = in.read(buf);
                }
            } catch (Exception e) {
                throw new RepositoryException(e);
            } finally {

                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                } catch (Throwable t) {
                    throw new RuntimeException("Could not close streams: ", t);
                }

                if (resolver != null) {
                    if (source != null) {
                        resolver.release(source);
                    }
                    manager.release(resolver);
                }
            }
        }
    }

    public void changed() {
        Node node;
        try {
            node = getDocumentNode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (Iterator i = this.listeners.iterator(); i.hasNext();) {
            NodeListener listener = (NodeListener) i.next();
            listener.nodeChanged(node, getSession().getIdentity());
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

        if (!getSourceURI().endsWith("." + LENYA_META_SUFFIX)) {
            lockMetaData();
        }
    }

    protected void lockMetaData() throws RepositoryException {
        SourceUtil.lock(getMetaSourceUri(), this.manager);
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
     * @see org.apache.lenya.cms.repository.Node#exists()
     */
    public boolean exists() throws RepositoryException {
        loadData();
        return this.data != null;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "node " + this.sourceURI;
    }

    byte[] data = null;

    /**
     * @see org.apache.lenya.cms.repository.Node#getInputStream()
     */
    public synchronized InputStream getInputStream() throws RepositoryException {
        if (!exists()) {
            throw new RuntimeException(this + " does not exist!");
        }
        return new ByteArrayInputStream(this.data);
    }

    /**
     * 
     */
    public Collection getChildren() throws RepositoryException {
        SourceResolver resolver = null;
        TraversableSource source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = (TraversableSource) resolver.resolveURI(getRealSourceURI());
            Collection children = source.getChildren();
            java.util.Iterator iterator = children.iterator();
            java.util.Vector newChildren = new java.util.Vector();
            while (iterator.hasNext()) {
                TraversableSource child = (TraversableSource) iterator.next();
                newChildren.add(new SourceNode(getSession(), sourceURI + "/" + child.getName(),
                        this.manager, getLogger()));
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
            source = (TraversableSource) resolver.resolveURI(getRealSourceURI());
            return source.isCollection();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Loads the data from the real source.
     * 
     * @throws RepositoryException if an error occurs.
     */
    protected synchronized void loadData() throws RepositoryException {

        if (this.data != null) {
            return;
        }

        ByteArrayOutputStream out = null;
        InputStream in = null;
        SourceResolver resolver = null;
        TraversableSource source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = (TraversableSource) resolver.resolveURI(getRealSourceURI());

            if (source.exists() && !source.isCollection()) {
                byte[] buf = new byte[4096];
                out = new ByteArrayOutputStream();
                in = source.getInputStream();
                int read = in.read(buf);

                while (read > 0) {
                    out.write(buf, 0, read);
                    read = in.read(buf);
                }

                this.data = out.toByteArray();
            }
        } catch (Exception e) {
            throw new RepositoryException(e);
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (Exception e) {
                throw new RepositoryException(e);
            }

            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.repository.Node#getOutputStream()
     */
    public synchronized OutputStream getOutputStream() throws RepositoryException {
        if (getLogger().isDebugEnabled())
            getLogger().debug("Get OutputStream for " + getSourceURI());
        try {
            registerDirty();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new NodeOutputStream();
    }

    /**
     * Output stream.
     */
    private class NodeOutputStream extends ByteArrayOutputStream {
        /**
         * @see java.io.OutputStream#close()
         */
        public synchronized void close() throws IOException {
            SourceNode.this.data = super.toByteArray();
            SourceNode.this.lastModified = new Date().getTime();
            super.close();
        }
    }

    /**
     * @see org.apache.lenya.cms.repository.Node#getContentLength()
     */
    public long getContentLength() throws RepositoryException {
        loadData();
        return this.data.length;
    }

    private long lastModified = -1;

    /**
     * @see org.apache.lenya.cms.repository.Node#getLastModified()
     */
    public long getLastModified() throws RepositoryException {
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(getRealSourceURI());

            long sourceLastModified;

            if (source.exists()) {
                sourceLastModified = source.getLastModified();
                if (sourceLastModified > this.lastModified) {
                    this.lastModified = sourceLastModified;
                }
            } else if (this.lastModified == -1) {
                throw new RepositoryException("The source [" + getRealSourceURI()
                        + "] does not exist!");
            }

            return this.lastModified;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.repository.Node#getMimeType()
     */
    public String getMimeType() throws RepositoryException {
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(getRealSourceURI());
            if (source.exists()) {
                return source.getMimeType();
            } else {
                throw new SourceNotFoundException("The source [" + getRealSourceURI()
                        + "] does not exist!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.repository.Node#getSourceURI()
     */
    public String getSourceURI() {
        return sourceURI;
    }

    /**
     * @return The source URI of the meta data node. TODO: This is a hack and
     *         can be removed when UUIDs are used.
     */
    protected String getMetaSourceUri() {
        String sourceUri = getSourceURI();
        return sourceUri + "." + LENYA_META_SUFFIX;
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
            getSession().registerDirty(this);
        } catch (TransactionException e) {
            throw new RepositoryException(e);
        }
    }

    public void registerRemoved() throws RepositoryException {
        try {
            getSession().registerRemoved(this);
            SourceUtil.delete(getMetaSourceUri(), this.manager);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    private Set listeners = new HashSet();

    public void addListener(NodeListener listener) throws RepositoryException {
        if (this.listeners.contains(listener)) {
            throw new RepositoryException("The listener [" + listener
                    + "] is already registered for node [" + this + "]!");
        }
        this.listeners.add(listener);
    }

    public boolean isListenerRegistered(NodeListener listener) {
        return this.listeners.contains(listener);
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
        return new SourceNodeHistory(this, this.manager);
    }

    public MetaData getMetaData(String namespaceUri) throws MetaDataException {
        return getMetaDataHandler().getMetaData(namespaceUri);
    }

    private SourceNodeMetaDataHandler metaDataHandler = null;
    
    protected SourceNodeMetaDataHandler getMetaDataHandler() {
        if (this.metaDataHandler == null) {
            this.metaDataHandler = new SourceNodeMetaDataHandler(this.manager, getMetaSourceUri());
        }
        return this.metaDataHandler;
    }

    public String[] getMetaDataNamespaceUris() throws MetaDataException {
        return getMetaDataHandler().getMetaDataNamespaceUris();
    }

}
