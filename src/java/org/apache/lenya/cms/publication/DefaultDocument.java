/*
 * Copyright  1999-2004 The Apache Software Foundation
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

/* $Id$  */

package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.metadata.dublincore.DublinCoreProxy;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.rc.RCEnvironment;
import org.apache.lenya.cms.rc.RevisionController;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.workflow.CMSHistory;
import org.apache.lenya.cms.workflow.History;
import org.apache.lenya.transaction.Lock;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;

/**
 * A typical CMS document.
 */
public class DefaultDocument extends AbstractLogEnabled implements Document {

    private String id;
    private DublinCore dublincore;
    private DocumentIdentityMap identityMap;
    private ResourcesManager resourcesManager;
    protected ServiceManager manager;

    /**
     * Creates a new instance of DefaultDocument. The language of the document is the default
     * language of the publication.
     * @param manager The service manager.
     * @param map The identity map the document belongs to.
     * @param publication The publication.
     * @param _id The document ID (starting with a slash).
     * @param _area The area.
     */
    protected DefaultDocument(ServiceManager manager, DocumentIdentityMap map,
            Publication publication, String _id, String _area) {
        if (_id == null) {
            throw new IllegalArgumentException("The document ID must not be null!");
        }
        if (!_id.startsWith("/")) {
            throw new IllegalArgumentException("The document ID must start with a slash!");
        }
        this.manager = manager;
        this.id = _id;
        this.publication = publication;

        this.identityMap = map;

        setArea(_area);
        setLanguage(getPublication().getDefaultLanguage());

        this.dublincore = new DublinCoreProxy(this, this.manager);
    }

    /**
     * Creates a new instance of DefaultDocument.
     * @param manager The service manager.
     * @param map The identity map the document belongs to.
     * @param publication The publication.
     * @param _id The document ID (starting with a slash).
     * @param _area The area.
     * @param _language the language
     */
    protected DefaultDocument(ServiceManager manager, DocumentIdentityMap map,
            Publication publication, String _id, String _area, String _language) {
        if (_id == null) {
            throw new IllegalArgumentException("The document ID must not be null!");
        }
        if (!_id.startsWith("/")) {
            throw new IllegalArgumentException("The document ID must start with a slash!");
        }
        this.manager = manager;
        this.id = _id;
        this.publication = publication;

        this.identityMap = map;
        this.language = _language;
        setArea(_area);

        this.dublincore = new DublinCoreProxy(this, this.manager);
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getId()
     */
    public String getId() {
        return this.id;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getName()
     */
    public String getName() {
        String[] ids = this.id.split("/");
        String nodeId = ids[ids.length - 1];

        return nodeId;
    }

    private Publication publication;

    /**
     * @see org.apache.lenya.cms.publication.Document#getPublication()
     */
    public Publication getPublication() {
        return this.publication;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getLastModified()
     */
    public Date getLastModified() {
        return new Date(getFile().lastModified());
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getDublinCore()
     */
    public DublinCore getDublinCore() {
        return this.dublincore;
    }

    /**
     * Returns the file for this document.
     * @return A file object.
     */
    public File getFile() {
        return getPublication().getPathMapper().getFile(getPublication(),
                getArea(),
                getId(),
                getLanguage());
    }

    private String language = "";

    /**
     * @see org.apache.lenya.cms.publication.Document#getLanguage()
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getLanguages()
     */
    public String[] getLanguages() throws DocumentException {

        List documentLanguages = new ArrayList();
        String[] allLanguages = getPublication().getLanguages();

        for (int i = 0; i < allLanguages.length; i++) {
            Document version;
            try {
                version = getIdentityMap().getLanguageVersion(this, allLanguages[i]);
            } catch (DocumentBuildException e) {
                throw new DocumentException(e);
            }
            if (version.exists()) {
                documentLanguages.add(allLanguages[i]);
            }
        }

        return (String[]) documentLanguages.toArray(new String[documentLanguages.size()]);
    }

    /**
     * Sets the language of this document.
     * @param _language The language.
     */
    public void setLanguage(String _language) {
        assert _language != null;
        this.language = _language;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getLabel()
     */
    public String getLabel() throws DocumentException {
        String labelString = "";
        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(getPublication().getSiteManagerHint());
            if (siteManager != null) {
                labelString = siteManager.getLabel(this);
            }
        } catch (Exception e) {
            throw new DocumentException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }
        return labelString;
    }

    private String area;

    /**
     * @see org.apache.lenya.cms.publication.Document#getArea()
     */
    public String getArea() {
        return this.area;
    }

    /**
     * @see Document#getCompleteInfoURL()
     */
    public String getCompleteInfoURL() {
        return "/" + getPublication().getId() + "/" + Publication.INFO_AREA_PREFIX + getArea()
                + getCanonicalDocumentURL();
    }

    /**
     * Sets the area.
     * @param _area A string.
     */
    protected void setArea(String _area) {
        if (!PublicationImpl.isValidArea(_area)) {
            throw new IllegalArgumentException("The area [" + _area + "] is not valid!");
        }
        this.area = _area;
    }

    private String extension = "html";

    /**
     * @see org.apache.lenya.cms.publication.Document#getExtension()
     */
    public String getExtension() {
        return this.extension;
    }

    /**
     * Sets the extension of the file in the URL.
     * @param _extension A string.
     */
    protected void setExtension(String _extension) {
        assert _extension != null;
        this.extension = _extension;
    }

    private String documentURL;

    /**
     * Sets the document URL.
     * @param url The document URL (without publication ID and area).
     */
    public void setDocumentURL(String url) {
        assert url != null;
        this.documentURL = url;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#exists()
     */
    public boolean exists() throws DocumentException {
        boolean exists;
        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(getPublication().getSiteManagerHint());
            if (siteManager != null) {
                exists = siteManager.contains(this);
            } else {
                exists = getFile().exists();
            }
        } catch (Exception e) {
            throw new DocumentException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }
        return exists;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#existsInAnyLanguage()
     */
    public boolean existsInAnyLanguage() throws DocumentException {
        boolean exists = false;

        try {
            String[] languages = getLanguages();
            for (int i = 0; i < languages.length; i++) {
                Document languageVersion = getIdentityMap().getLanguageVersion(this, languages[i]);
                exists = exists || languageVersion.exists();
            }
        } catch (DocumentBuildException e) {
            throw new DocumentException(e);
        }
        return exists;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object) {
        boolean equals = false;
        if (getClass().isInstance(object)) {
            Document document = (Document) object;
            equals = getPublication().equals(document.getPublication())
                    && getId().equals(document.getId()) && getArea().equals(document.getArea())
                    && getLanguage().equals(document.getLanguage());
        }
        return equals;

    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {

        String key = getPublication().getId() + ":" + getPublication().getServletContext() + ":"
                + getArea() + ":" + getId() + ":" + getLanguage();

        return key.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getPublication().getId() + ":" + getArea() + ":" + getId() + ":" + getLanguage();
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getIdentityMap()
     */
    public DocumentIdentityMap getIdentityMap() {
        return this.identityMap;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getResourcesManager()
     */
    public ResourcesManager getResourcesManager() {
        if (this.resourcesManager == null) {
            this.resourcesManager = new DefaultResourcesManager(this, this.manager);
            ContainerUtil.enableLogging(this.resourcesManager, getLogger());
        }
        return this.resourcesManager;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getCanonicalWebappURL()
     */
    public String getCanonicalWebappURL() {
        return "/" + getPublication().getId() + "/" + getArea() + getCanonicalDocumentURL();
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getCanonicalDocumentURL()
     */
    public String getCanonicalDocumentURL() {
        return this.documentURL;
    }

    /**
     * If the document is involved in a unit of work, a temporary source is created to be used in
     * the transaction.
     * @see org.apache.lenya.cms.publication.Document#getSourceURI()
     */
    public String getSourceURI() {
        String uri = getRealSourceURI();

        if (getIdentityMap().getUnitOfWork() != null) {
            String workUri = getWorkSourceURI();

            SourceResolver resolver = null;
            ModifiableSource realSource = null;
            ModifiableSource workSource = null;
            try {
                resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                realSource = (ModifiableSource) resolver.resolveURI(uri);
                workSource = (ModifiableSource) resolver.resolveURI(workUri);
                if (realSource.exists() && !workSource.exists()) {
                    SourceUtil.copy(realSource, workSource, true);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (resolver != null) {
                    if (realSource != null) {
                        resolver.release(realSource);
                    }
                    if (workSource != null) {
                        resolver.release(workSource);
                    }
                    this.manager.release(resolver);
                }
            }

            uri = workUri;
        }

        return uri;
    }

    /**
     * @return The real source URI.
     */
    protected String getRealSourceURI() {
        try {
            return "file:/" + getFile().getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return A source URI to be used in a transaction.
     */
    protected String getWorkSourceURI() {
        String workUri = "file:/" + getPublication().getDirectory().getAbsolutePath() + "/work/";
        workUri += getUserId();

        String path = getPublication().getPathMapper().getPath(getId(), getLanguage());
        workUri += "/" + getArea() + "/" + path;

        return workUri;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#accept(org.apache.lenya.cms.publication.util.DocumentVisitor)
     */
    public void accept(DocumentVisitor visitor) throws PublicationException {
        visitor.visitDocument(this);
    }

    private History history;

    /**
     * @return The workflow history.
     */
    public History getHistory() {
        if (this.history == null) {
            try {
                this.history = new CMSHistory(this, getHistoryFile());
            } catch (WorkflowException e) {
                throw new RuntimeException(e);
            }
        }
        return this.history;
    }

    /**
     * @see org.apache.lenya.workflow.Workflowable#getVersions()
     */
    public Version[] getVersions() {
        return getHistory().getVersions();
    }

    /**
     * @see org.apache.lenya.workflow.Workflowable#getLatestVersion()
     */
    public Version getLatestVersion() {
        Version[] versions = getVersions();
        Version lastVersion = null;
        if (versions.length > 0) {
            lastVersion = versions[versions.length - 1];
        }
        return lastVersion;
    }

    /**
     * Returns the history file inside the publication directory.
     * @return A string.
     */
    public File getHistoryFile() {
        if (getIdentityMap().getUnitOfWork() == null) {
            return getRealHistoryFile();
        } else {
            return getWorkHistoryFile();
        }
    }

    protected File getRealHistoryFile() {
        DocumentIdToPathMapper pathMapper = getPublication().getPathMapper();
        String documentPath = pathMapper.getPath(getId(), getLanguage());

        String area = getArea();
        if (!area.equals(Publication.ARCHIVE_AREA) && !area.equals(Publication.TRASH_AREA)) {
            area = Publication.AUTHORING_AREA;
        }

        String path = CMSHistory.HISTORY_PATH + "/" + area + "/" + documentPath;
        path = path.replace('/', File.separatorChar);
        return new File(getPublication().getDirectory(), path);
    }

    protected File getWorkHistoryFile() {
        DocumentIdToPathMapper pathMapper = getPublication().getPathMapper();
        String documentPath = pathMapper.getPath(getId(), getLanguage());

        String area = getArea();
        if (!area.equals(Publication.ARCHIVE_AREA) && !area.equals(Publication.TRASH_AREA)) {
            area = Publication.AUTHORING_AREA;
        }

        String path = "work/" + getUserId() + "/" + CMSHistory.HISTORY_PATH + "/" + area + "/"
                + documentPath;
        path = path.replace('/', File.separatorChar);
        return new File(getPublication().getDirectory(), path);
    }

    /**
     * @return The source URI of the history file.
     */
    public String getHistorySourceURI() {
        return getHistoryFile().toURI().toString();
    }

    /**
     * @see org.apache.lenya.workflow.Workflowable#newVersion(org.apache.lenya.workflow.Workflow,
     *      org.apache.lenya.workflow.Version, org.apache.lenya.workflow.Situation)
     */
    public void newVersion(Workflow workflow, Version version, Situation situation) {
        getHistory().newVersion(workflow, version, situation);
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#save()
     */
    public void save() throws TransactionException {

        SourceResolver sourceResolver = null;
        ModifiableSource realSource = null;
        ModifiableSource workSource = null;
        try {
            getDublinCore().save();
            getHistory().save();

            if (getWorkHistoryFile().exists()) {
                FileUtil.copyFile(getWorkHistoryFile(), getRealHistoryFile());
                getWorkHistoryFile().delete();
            }

            sourceResolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            realSource = (ModifiableSource) sourceResolver.resolveURI(getRealSourceURI());
            workSource = (ModifiableSource) sourceResolver.resolveURI(getWorkSourceURI());
            if (workSource.exists()) {
                SourceUtil.copy(workSource, realSource, true);
                workSource.delete();
            }

        } catch (Exception e) {
            throw new TransactionException(e);
        } finally {
            if (sourceResolver != null) {
                if (realSource != null) {
                    sourceResolver.release(realSource);
                }
                if (workSource != null) {
                    sourceResolver.release(workSource);
                }
                this.manager.release(sourceResolver);
            }
        }

    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#checkin()
     */
    public void checkin() throws TransactionException {
        checkin(true);
    }

    private boolean isCheckedOut = false;

    protected void checkin(boolean backup) throws TransactionException {
        try {
            String userName = getUserId();
            boolean newVersion = getIdentityMap().getUnitOfWork().isDirty(this);
            getRevisionController().reservedCheckIn(getRCPath(), userName, backup, newVersion);
            this.isCheckedOut = false;
        } catch (Exception e) {
            throw new TransactionException(e);
        }
    }

    /**
     * @return The username of the unit of work's identity.
     */
    protected String getUserId() {
        String userName = null;
        User user = getIdentityMap().getUnitOfWork().getIdentity().getUser();
        if (user != null) {
            userName = user.getId();
        }
        return userName;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#checkout()
     */
    public void checkout() throws TransactionException {
        try {
            String userName = getUserId();
            getRevisionController().reservedCheckOut(getRCPath(), userName);
            this.isCheckedOut = true;
        } catch (Exception e) {
            throw new TransactionException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isCheckedOut()
     */
    public boolean isCheckedOut() throws TransactionException {
        return this.isCheckedOut;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#lock()
     */
    public void lock() throws TransactionException {
        this.lock = new Lock(getVersion());
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
        return Document.TRANSACTIONABLE_TYPE;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#delete()
     */
    public void delete() throws TransactionException {
        SourceResolver sourceResolver = null;
        Source realSource = null;
        Source workSource = null;
        try {
            sourceResolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            realSource = sourceResolver.resolveURI(getSourceURI());
            if (realSource.exists()) {
                ((ModifiableSource) realSource).delete();
            }
            workSource = sourceResolver.resolveURI(getWorkSourceURI());
            if (workSource.exists()) {
                ((ModifiableSource) workSource).delete();
            }
        } catch (Exception e) {
            throw new TransactionException(e);
        } finally {
            if (sourceResolver != null) {
                if (realSource != null) {
                    sourceResolver.release(realSource);
                }
                if (workSource != null) {
                    sourceResolver.release(workSource);
                }
                this.manager.release(sourceResolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#create()
     */
    public void create() throws TransactionException {
    }

    private RevisionController revisionController;

    protected RevisionController getRevisionController() throws TransactionException {
        if (this.revisionController == null) {
            try {
                String publicationPath = getPublication().getDirectory().getCanonicalPath();
                RCEnvironment rcEnvironment = RCEnvironment.getInstance(getPublication()
                        .getServletContext().getCanonicalPath());
                String rcmlDirectory = publicationPath + File.separator
                        + rcEnvironment.getRCMLDirectory();
                String backupDirectory = publicationPath + File.separator
                        + rcEnvironment.getBackupDirectory();

                this.revisionController = new RevisionController(rcmlDirectory, backupDirectory,
                        publicationPath);
            } catch (IOException e) {
                throw new TransactionException(e);
            }
        }
        return this.revisionController;
    }

    private Lock lock;

    /**
     * @see org.apache.lenya.transaction.Transactionable#getLock()
     */
    public Lock getLock() {
        return this.lock;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#getVersion()
     */
    public int getVersion() throws TransactionException {
        try {
            return getRevisionController().getLatestVersion(getRCPath());
        } catch (Exception e) {
            throw new TransactionException(e);
        }
    }

    /**
     * @return The path to use for the revision controller.
     * @throws IOException if an error occurs.
     */
    protected String getRCPath() throws IOException {
        return getArea() + "/" + getPublication().getPathMapper().getPath(getId(), getLanguage());
    }

}