/*
 * Copyright  1999-2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
import java.util.Collection;
import java.util.Date;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.TraversableSource;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.MetaDataManager;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.rc.RCEnvironment;
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

    /**
     * Ctor.
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
        Identity identity = getSession().getUnitOfWork().getIdentity();
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
     * @return A string.
     */
    protected String getRealSourceURI() {
/*
        if (getLogger().isDebugEnabled()) {
            getLogger().error("test ...");
        }
*/
        //String realSourceURI = "file://home/michi/" + this.sourceURI.substring(Node.LENYA_PROTOCOL.length());
        String realSourceURI = "context://" + this.sourceURI.substring(Node.LENYA_PROTOCOL.length());
/*
        try {
            SourceResolver resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            getLogger().error("Real Source URI: " + realSourceURI);
            Source source = (Source) resolver.resolveURI(realSourceURI);
            getLogger().error("Source: " + source);
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
*/
        return realSourceURI;
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
            boolean newVersion = getSession().getUnitOfWork().isDirty(this);
            getRevisionController().reservedCheckIn(getRCPath(), userName, true, newVersion);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isCheckedOut()
     */
    public boolean isCheckedOut() throws RepositoryException {
        try {
            return getRevisionController().isCheckedOut(getRCPath());
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isCheckedOutByUser()
     */
    public boolean isCheckedOutByUser() throws RepositoryException {
        try {
            RCMLEntry entry = getRevisionController().getRCML(getRCPath()).getLatestEntry();
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
                getRevisionController().reservedCheckOut(getRCPath(), getUserId());
            } catch (Exception e) {
                throw new RepositoryException(e);
            }
        }
    }

    /**
     * @return The path to use for the revision controller.
     * @throws IOException if an error occurs.
     */
    protected String getRCPath() throws IOException {
        String pubBase = Node.LENYA_PROTOCOL + Publication.PUBLICATION_PREFIX_URI + "/";
        String publicationsPath = this.sourceURI.substring(pubBase.length());
        String publicationId = publicationsPath.split("/")[0];
        String path = pubBase + publicationId + "/";
        return this.sourceURI.substring(path.length());
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

    private RevisionController revisionController;

    protected RevisionController getRevisionController() throws RepositoryException {
        if (this.revisionController == null) {
            try {
                String pubBase = Node.LENYA_PROTOCOL + Publication.PUBLICATION_PREFIX_URI + "/";
                String publicationsPath = this.sourceURI.substring(pubBase.length());
                String publicationId = publicationsPath.split("/")[0];

                Publication pub = PublicationUtil.getPublication(this.manager, publicationId);

                String publicationPath = pub.getDirectory().getCanonicalPath();
                RCEnvironment rcEnvironment = RCEnvironment.getInstance(pub.getServletContext()
                        .getCanonicalPath());
                String rcmlDirectory = publicationPath + File.separator
                        + rcEnvironment.getRCMLDirectory();
                String backupDirectory = publicationPath + File.separator
                        + rcEnvironment.getBackupDirectory();
                this.revisionController = new RevisionController(rcmlDirectory,
                        backupDirectory,
                        publicationPath);

            } catch (Exception e) {
                throw new RepositoryException(e);
            }
        }
        return this.revisionController;
    }

    private Lock lock;

    /**
     * @see org.apache.lenya.transaction.Transactionable#hasChanged()
     */
    public boolean hasChanged() throws RepositoryException {
        try {
            int currentVersion = getRevisionController().getLatestVersion(getRCPath());
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
            currentVersion = getRevisionController().getLatestVersion(getRCPath());
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
        this.lock = new Lock(currentVersion);

        if (!getSourceURI().endsWith(".meta")) {
            lockMetaData();
        }
    }

    protected void lockMetaData() throws RepositoryException {
        SourceUtil.lock(getMetaSourceURI(), this.manager);
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
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isLocked()
     */
    public boolean isLocked() throws RepositoryException {
        return this.lock != null;
    }

    /**
     * @see org.apache.lenya.transaction.Identifiable#getIdentifiableType()
     */
    public String getIdentifiableType() {
        return Node.IDENTIFIABLE_TYPE;
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
                newChildren.add(new SourceNode(getSession(),
                        sourceURI + "/" + child.getName(),
                        this.manager,
                        getLogger()));
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
            if (!isLocked()) {
                throw new RuntimeException("Cannot write to source [" + getSourceURI()
                        + "]: not locked!");
            }
            if (getSession().getUnitOfWork() == null) {
                throw new RuntimeException("Cannot write to source outside of a transaction (UnitOfWork is null)!");
            }
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
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(getRealSourceURI());
            if (source.exists()) {
                return source.getContentLength();
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
            }
            else if (this.lastModified == -1) {
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

    protected String getMetaSourceURI() {
        return getSourceURI() + ".meta";
    }

    private MetaDataManager metaDataManager;

    /**
     * @see org.apache.lenya.cms.metadata.MetaDataOwner#getMetaDataManager()
     */
    public MetaDataManager getMetaDataManager() throws DocumentException {
        if (this.metaDataManager == null) {
            this.metaDataManager = new MetaDataManager(getMetaSourceURI(),
                    this.manager,
                    getLogger());
        }
        return this.metaDataManager;
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
            getSession().getUnitOfWork().registerDirty(this);
        } catch (TransactionException e) {
            throw new RepositoryException(e);
        }
    }

    public void registerRemoved() throws RepositoryException {
        try {
            getSession().getUnitOfWork().registerRemoved(this);
            SourceUtil.delete(getMetaSourceURI(), this.manager);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

}
