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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.metadata.dublincore.DublinCoreProxy;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.workflow.CMSHistory;
import org.apache.lenya.cms.workflow.History;
import org.apache.lenya.transaction.Lock;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.transaction.Transactionable;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;

/**
 * A typical CMS document.
 */
public class DefaultDocument extends AbstractLogEnabled implements Document, Transactionable {

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
        this(manager, map, publication, _id, _area, publication.getDefaultLanguage());
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
        this.manager = manager;
        if (_id == null) {
            throw new IllegalArgumentException("The document ID must not be null!");
        }
        if (!_id.startsWith("/")) {
            throw new IllegalArgumentException("The document ID must start with a slash!");
        }
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
                exists = SourceUtil.exists(getSourceURI(), this.manager);
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
                final String path = getPublication().getPathMapper()
                        .getPath(getId(), getLanguage());
                final String uri = "lenya://lenya/pubs/" + getPublication().getId()
                        + "/content/workflow/history/" + path;
                this.history = new CMSHistory(uri, this.manager);
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
        try {
            getDublinCore().save();
        } catch (Exception e) {
            throw new TransactionException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#delete()
     */
    public void delete() throws TransactionException {
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#create()
     */
    public void create() throws TransactionException {
    }

    protected static final String TRANSACTIONABLE_TYPE = "document";

    /**
     * @see org.apache.lenya.transaction.Transactionable#getTransactionableType()
     */
    public String getTransactionableType() {
        return TRANSACTIONABLE_TYPE;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getSourceURI()
     */
    public String getSourceURI() {
        String path = publication.getPathMapper().getPath(getId(), getLanguage());
        return "lenya://lenya/pubs/" + publication.getId() + "/content/" + getArea() + "/" + path;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#checkin()
     */
    public void checkin() throws TransactionException {
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#checkout()
     */
    public void checkout() throws TransactionException {
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isCheckedOut()
     */
    public boolean isCheckedOut() throws TransactionException {
        return false;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#lock()
     */
    public void lock() throws TransactionException {
        SourceUtil.lock(getSourceURI(), this.manager);
        SourceUtil.lock(getHistory().getSourceURI(), this.manager);
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#getLock()
     */
    public Lock getLock() {
        return null;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#unlock()
     */
    public void unlock() throws TransactionException {
        SourceUtil.unlock(getSourceURI(), this.manager);
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isLocked()
     */
    public boolean isLocked() throws TransactionException {
        return false;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#hasChanged()
     */
    public boolean hasChanged() throws TransactionException {
        return false;
    }

}