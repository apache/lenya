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

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.rc.RCEnvironment;
import org.apache.lenya.cms.rc.RevisionController;
import org.apache.lenya.transaction.IdentityMap;
import org.apache.lenya.transaction.Lock;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.cms.rc.RCMLEntry;

/**
 * A repository node.
 * 
 * @version $Id$
 */
public class SourceNode extends AbstractLogEnabled implements Node {

    private String sourceURI;
    private ServiceManager manager;
    private IdentityMap identityMap;

    /**
     * Ctor.
     * @param map
     * @param sourceURI
     * @param manager
     * @param logger
     */
    public SourceNode(IdentityMap map, String sourceURI, ServiceManager manager, Logger logger) {
        this.sourceURI = sourceURI;
        this.manager = manager;
        enableLogging(logger);
        this.identityMap = map;
    }

    protected String getUserId() {
        String userId = null;
        Identity identity = this.identityMap.getUnitOfWork().getIdentity();
        if (identity != null) {
            User user = identity.getUser();
            if (user != null) {
                userId = user.getId();
            }
        }
        return userId;
    }

    protected String getRealSourceURI() {
        return "context://" + this.sourceURI.substring(Node.LENYA_PROTOCOL.length());
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#checkin()
     */
    public void checkin() throws TransactionException {
        if (!isCheckedOut()) {
            throw new TransactionException("Cannot check in node [" + this.sourceURI
                    + "]: not checked out!");
        }

        try {
            String userName = getUserId();
            boolean newVersion = this.identityMap.getUnitOfWork().isDirty(this);
            getRevisionController().reservedCheckIn(getRCPath(), userName, true, newVersion);
        } catch (Exception e) {
            throw new TransactionException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isCheckedOut()
     */
    public boolean isCheckedOut() throws TransactionException {
        try {
            return getRevisionController().isCheckedOut(getRCPath());
        } catch (TransactionException e) {
            throw e;
        } catch (Exception e) {
            throw new TransactionException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isCheckedOutByUser()
     */
    public boolean isCheckedOutByUser() throws TransactionException {
        try {
            RCMLEntry entry = getRevisionController().getRCML(getRCPath()).getLatestEntry();
            if(entry.getIdentity().equals(getUserId())) 
                return true;
            else
                return false;
        } catch (TransactionException e) {
            throw e;
        } catch (Exception e) {
            throw new TransactionException(e);
        }
    }    
    
    /**
     * @see org.apache.lenya.transaction.Transactionable#checkout()
     */
    public void checkout() throws TransactionException {

        if (getLogger().isDebugEnabled())
            getLogger().debug("SourceNode::checkout() called, sourceURI [" + sourceURI + "]");

        if (!isCheckedOut()) {
            try {
                getRevisionController().reservedCheckOut(getRCPath(), getUserId());
            } catch (Exception e) {
                throw new TransactionException(e);
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
    public void deleteTransactionable() throws TransactionException {
        try {
            if (!isCheckedOut()) {
                throw new RuntimeException("Cannot delete source [" + this.sourceURI
                        + "]: not checked out!");
            } else {
                SourceUtil.delete(getRealSourceURI(), this.manager);
            }
        } catch (Exception e) {
            throw new TransactionException(e);
        }
    }

    private RevisionController revisionController;

    protected RevisionController getRevisionController() throws TransactionException {
        if (this.revisionController == null) {
            try {
                String pubBase = Node.LENYA_PROTOCOL + Publication.PUBLICATION_PREFIX_URI + "/";
                String publicationsPath = this.sourceURI.substring(pubBase.length());
                String publicationId = publicationsPath.split("/")[0];

                Source contextSource = null;
                SourceResolver resolver = null;
                try {
                    resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                    contextSource = resolver.resolveURI("context://");
                    File context = org.apache.excalibur.source.SourceUtil.getFile(contextSource);
                    PublicationFactory factory = PublicationFactory.getInstance(getLogger());
                    Publication pub = factory.getPublication(publicationId, context
                            .getAbsolutePath());

                    String publicationPath = pub.getDirectory().getCanonicalPath();
                    RCEnvironment rcEnvironment = RCEnvironment.getInstance(pub.getServletContext()
                            .getCanonicalPath());
                    String rcmlDirectory = publicationPath + File.separator
                            + rcEnvironment.getRCMLDirectory();
                    String backupDirectory = publicationPath + File.separator
                            + rcEnvironment.getBackupDirectory();
                    this.revisionController = new RevisionController(rcmlDirectory,
                            backupDirectory, publicationPath);
                } finally {
                    if (resolver != null) {
                        if (contextSource != null) {
                            resolver.release(contextSource);
                        }
                        this.manager.release(resolver);
                    }
                }

            } catch (Exception e) {
                throw new TransactionException(e);
            }
        }
        return this.revisionController;
    }

    private Lock lock;

    /**
     * @see org.apache.lenya.transaction.Transactionable#hasChanged()
     */
    public boolean hasChanged() throws TransactionException {
        try {
            int currentVersion = getRevisionController().getLatestVersion(getRCPath());
            int lockVersion = getLock().getVersion();
            return currentVersion > lockVersion;
        } catch (Exception e) {
            throw new TransactionException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#saveTransactionable()
     */
    public synchronized void saveTransactionable() throws TransactionException {
        if (!isCheckedOut()) {
            throw new TransactionException("Cannot save node [" + this.sourceURI
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
                throw new TransactionException(e);
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
    public void createTransactionable() throws TransactionException {
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#lock()
     */
    public void lock() throws TransactionException {
        if (isCheckedOut() && !isCheckedOutByUser()) {
    	    throw new TransactionException("Cannot lock [" + this + "]: node is checked out.");
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Locking [" + this + "]");
        }
        int currentVersion;
        try {
            currentVersion = getRevisionController().getLatestVersion(getRCPath());
        } catch (TransactionException e) {
            throw e;
        } catch (Exception e) {
            throw new TransactionException(e);
        }
        this.lock = new Lock(currentVersion);
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
    public void unlock() throws TransactionException {
        this.lock = null;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isLocked()
     */
    public boolean isLocked() throws TransactionException {
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
    public boolean exists() throws TransactionException {
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
    public synchronized InputStream getInputStream() throws TransactionException {
        if (!exists()) {
            throw new RuntimeException(this + " does not exist!");
        }
        return new ByteArrayInputStream(this.data);
    }

    /**
     * Loads the data from the real source.
     * @throws TransactionException if an error occurs.
     */
    protected synchronized void loadData() throws TransactionException {

        if (this.data != null) {
            return;
        }

        ByteArrayOutputStream out = null;
        InputStream in = null;
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(getRealSourceURI());

            if (source.exists()) {
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
            throw new TransactionException(e);
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (Exception e) {
                throw new TransactionException(e);
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
    public synchronized OutputStream getOutputStream() throws TransactionException {
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
            super.close();
        }
    }

    /**
     * @see org.apache.lenya.cms.repository.Node#getContentLength()
     */
    public long getContentLength() throws TransactionException {
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

    /**
     * @see org.apache.lenya.cms.repository.Node#getLastModified()
     */
    public long getLastModified() throws TransactionException {
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(getRealSourceURI());
            if (source.exists()) {
                return source.getLastModified();
            } else {
                throw new TransactionException("The source [" + getRealSourceURI()
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
     * @see org.apache.lenya.cms.repository.Node#getMimeType()
     */
    public String getMimeType() throws TransactionException {
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


}
