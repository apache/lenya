/*
 * Created on 06.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.repository;

import java.io.File;
import java.io.IOException;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.Source;
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
import org.w3c.dom.Document;

/**
 * A repository node.
 * 
 * @version $Id:$
 */
public class SourceNode extends AbstractLogEnabled implements Node {

    private Document document;
    private String sourceUri;
    private ServiceManager manager;
    private IdentityMap identityMap;

    /**
     * Ctor.
     * @param map
     * @param sourceUri
     * @param manager
     * @param logger
     */
    public SourceNode(IdentityMap map, String sourceUri, ServiceManager manager, Logger logger) {
        this.sourceUri = sourceUri;
        this.manager = manager;
        enableLogging(logger);
        this.identityMap = map;
    }

    /**
     * @see org.apache.lenya.cms.repository.Node#getDocument()
     */
    public Document getDocument() {
        try {
            if (this.document == null && SourceUtil.exists(getRealSourceURI(), this.manager)) {
                this.document = SourceUtil.readDOM(getRealSourceURI(), this.manager);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this.document;
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
        return "context://" + this.sourceUri.substring("lenya://".length());
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#checkin()
     */
    public void checkin() throws TransactionException {
        if (!isCheckedOut()) {
            throw new TransactionException("Cannot check in node [" + this.sourceUri
                    + "]: not checked out!");
        }

        try {
            String userName = getUserId();
            boolean newVersion = this.identityMap.getUnitOfWork().isDirty(this);
            getRevisionController().reservedCheckIn(getRCPath(), userName, true, newVersion);
            this.isCheckedOut = false;
        } catch (Exception e) {
            throw new TransactionException(e);
        }
    }

    private boolean isCheckedOut = false;

    /**
     * @see org.apache.lenya.transaction.Transactionable#isCheckedOut()
     */
    public boolean isCheckedOut() throws TransactionException {
        return isCheckedOut;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#checkout()
     */
    public void checkout() throws TransactionException {
        if (!isCheckedOut()) {
            try {
                getRevisionController().reservedCheckOut(getRCPath(), getUserId());
                this.isCheckedOut = true;
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
        String publicationsPath = this.sourceUri.substring("lenya://lenya/pubs/".length());
        String publicationId = publicationsPath.split("/")[0];
        String path = publicationsPath + "/" + publicationId + "/content/";
        return this.sourceUri.substring(path.length());
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#delete()
     */
    public void delete() throws TransactionException {
        try {
            if (!isCheckedOut()) {
                throw new RuntimeException("Cannot delete source [" + this.sourceUri
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
                String publicationsPath = this.sourceUri.substring("lenya://lenya/pubs/".length());
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
     * @see org.apache.lenya.transaction.Transactionable#save()
     */
    public void save() throws TransactionException {
        if (!isCheckedOut()) {
            throw new TransactionException("Cannot save node [" + this.sourceUri
                    + "]: not checked out!");
        }
        try {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Saving document [" + document.getDocumentElement().getNodeName()
                        + "] to source [" + getRealSourceURI() + "]");
            }
            SourceUtil.writeDOM(this.document, getRealSourceURI(), this.manager);
        } catch (Exception e) {
            throw new TransactionException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#create()
     */
    public void create() throws TransactionException {
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#lock()
     */
    public void lock() throws TransactionException {
        System.out.println("Locking " + this);
        new Exception().printStackTrace();
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
     * @see org.apache.lenya.transaction.Transactionable#getTransactionableType()
     */
    public String getTransactionableType() {
        return Node.TRANSACTIONABLE_TYPE;
    }

    /**
     * @see org.apache.lenya.cms.repository.Node#setDocument(org.w3c.dom.Document)
     */
    public void setDocument(Document document) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Setting document [" + document.getDocumentElement().getNodeName()
                    + "]");
        }
        this.document = document;
    }

    /**
     * @see org.apache.lenya.cms.repository.Node#exists()
     */
    public boolean exists() throws TransactionException {
        return getDocument() != null;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "node " + this.sourceUri;
    }
    
}