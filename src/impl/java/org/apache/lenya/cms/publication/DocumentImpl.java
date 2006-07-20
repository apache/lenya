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

package org.apache.lenya.cms.publication;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.RepositorySource;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.LenyaMetaData;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataManager;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.site.SiteManager;

/**
 * A typical CMS document.
 * @version $Id$
 */
public class DocumentImpl extends AbstractLogEnabled implements Document {

    private DocumentIdentifier identifier;
    private DocumentFactory identityMap;
    protected ServiceManager manager;
    private MetaDataManager metaDataManager;

    /**
     * Creates a new instance of DefaultDocument.
     * @param manager The service manager.
     * @param map The identity map the document belongs to.
     * @param identifier The identifier.
     * @param _logger a logger
     */
    protected DocumentImpl(ServiceManager manager, DocumentFactory map,
            DocumentIdentifier identifier, Logger _logger) {

        ContainerUtil.enableLogging(this, _logger);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("DefaultDocument() creating new instance with id ["
                    + identifier.getId() + "], language [" + identifier.getLanguage() + "]");
        }

        this.manager = manager;
        this.identifier = identifier;
        if (identifier.getId() == null) {
            throw new IllegalArgumentException("The document ID must not be null!");
        }
        if (!identifier.getId().startsWith("/")) {
            throw new IllegalArgumentException("The document ID must start with a slash!");
        }

        this.identityMap = map;

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("DefaultDocument() done building instance with _id ["
                    + identifier.getId() + "], _language [" + identifier.getLanguage() + "]");
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getExpires()
     */
    public Date getExpires() throws DocumentException {
        Date expires = null;
        long secs = 0;
        
        MetaData metaData = null;
        String expiresMeta = null;
        try {
            metaData = this.getMetaDataManager().getLenyaMetaData();
            expiresMeta = metaData.getFirstValue("expires");
        } catch (DocumentException e) {
            throw new DocumentException(e);
        }
        if (expiresMeta != null) {
            secs = Long.parseLong(expiresMeta);
        } else {
            secs = -1;
        }

        if (secs != -1) {
            Date date = new Date();
            date.setTime(date.getTime() + secs * 1000l);
            expires = date;
        } else {
            expires = this.getResourceType().getExpires();
        }

        return expires;
    }
    
    /**
     * @see org.apache.lenya.cms.publication.Document#getId()
     */
    public String getId() {
        return this.identifier.getId();
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getName()
     */
    public String getName() {
        String[] ids = getId().split("/");
        String nodeId = ids[ids.length - 1];

        return nodeId;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getPublication()
     */
    public Publication getPublication() {
        return this.identifier.getPublication();
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getLastModified()
     */
    public Date getLastModified() {
        return new Date(getFile().lastModified());
    }

    public File getFile() {
        return getPublication().getPathMapper().getFile(getPublication(),
                getArea(),
                getId(),
                getLanguage());
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getLanguage()
     */
    public String getLanguage() {
        return this.identifier.getLanguage();
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getLanguages()
     */
    public String[] getLanguages() throws DocumentException {

        List documentLanguages = new ArrayList();
        String[] allLanguages = getPublication().getLanguages();

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Number of languages of this publication: " + allLanguages.length);
        }

        for (int i = 0; i < allLanguages.length; i++) {
            Document version;
            try {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Try to create document: " + allLanguages[i] + " " + this);
                }
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

    /**
     * @see org.apache.lenya.cms.publication.Document#getArea()
     */
    public String getArea() {
        return this.identifier.getArea();
    }

    private String extension = null;
    private String defaultExtension = "html";

    /**
     * @see org.apache.lenya.cms.publication.Document#getExtension()
     */
    public String getExtension() {
        if (extension == null) {
            getLogger().warn("Default extension will be used: " + defaultExtension);
            return defaultExtension;
        }
        return this.extension;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getUUID()
     */
    public String getUUID() throws DocumentException {
        String uuid = null;
        String hint = getPublication().getSiteManagerHint();
        if (hint == null) {
            getLogger().error("SiteManagerHint is null!");
        } else {
            SiteManager siteManager = null;
            ServiceSelector selector = null;
            try {
                selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
                siteManager = (SiteManager) selector.select(hint);
                uuid = siteManager.getUUID(this);
                if (uuid == null) {
                    uuid = getCanonicalDocumentURL();
                    getLogger().warn("No UUID found for document [" + this
                            + "]. Use canonical document URL: " + uuid);
                }
                return uuid;
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
        }
        return uuid;
    }

    private String defaultSourceExtension = "xml";

    /**
     * @see org.apache.lenya.cms.publication.Document#getSourceExtension()
     */
    public String getSourceExtension() {
        String sourceExtension;
        try {
            sourceExtension = getMetaDataManager().getLenyaMetaData()
                    .getFirstValue(LenyaMetaData.ELEMENT_EXTENSION);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        if (sourceExtension == null) {
            getLogger().warn("No source extension for document [" + this + "]. The extension \""
                    + defaultSourceExtension + "\" will be used as default!");
            sourceExtension = defaultSourceExtension;
        }
        return sourceExtension;
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
        String hint = getPublication().getSiteManagerHint();
        if (hint == null) {
            try {
                exists = SourceUtil.exists(getSourceURI(), this.manager);
            } catch (Exception e) {
                throw new DocumentException(e);
            }
        } else {
            SiteManager siteManager = null;
            ServiceSelector selector = null;
            try {
                selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
                siteManager = (SiteManager) selector.select(hint);
                exists = siteManager.contains(this);
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
        }
        return exists;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#existsInAnyLanguage()
     */
    public boolean existsInAnyLanguage() throws DocumentException {
        String[] languages = getLanguages();

        if (languages.length > 0) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Document (" + this + ") exists in at least one language: "
                        + languages.length);
            }
            String[] allLanguages = getPublication().getLanguages();
            if (languages.length == allLanguages.length)
                // TODO: This is not entirely true, because the publication could assume the
                // languages EN and DE, but the document could exist for the languages DE and FR!
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Document (" + this
                            + ") exists even in all languages of this publication");
                }
            return true;
        } else {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Document (" + this + ") does NOT exist in any language");
            }
            return false;
        }

    }

    protected DocumentIdentifier getIdentifier() {
        return this.identifier;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object) {
        if (getClass().isInstance(object)) {
            DocumentImpl document = (DocumentImpl) object;
            return document.getIdentifier().equals(getIdentifier());
        }
        return false;
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
    public DocumentFactory getIdentityMap() {
        return this.identityMap;
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

    /**
     * @see org.apache.lenya.cms.metadata.MetaDataOwner#getMetaDataManager()
     */
    public MetaDataManager getMetaDataManager() {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Publication source URI for document [" + this + "]: "
                    + getPublication().getSourceURI());
        }
        if (this.metaDataManager == null) {
            SourceResolver resolver = null;
            RepositorySource source = null;
            try {
                resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                source = (RepositorySource) resolver.resolveURI(getSourceURI());
                this.metaDataManager = source.getNode().getMetaDataManager();
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
        return metaDataManager;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#delete()
     */
    public void delete() throws DocumentException {
        try {
            SourceUtil.delete(getSourceURI(), this.manager);
        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }

    protected static final String IDENTIFIABLE_TYPE = "document";

    /**
     * @see org.apache.lenya.cms.publication.Document#getSourceURI()
     */
    public String getSourceURI() {
        String path = getPublication().getPathMapper().getPath(getId(), getLanguage());
        return getPublication().getSourceURI() + "/content/" + getArea() + "/" + path;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getRepositoryNode()
     */
    public Node getRepositoryNode() {
        Node node = null;
        SourceResolver resolver = null;
        RepositorySource documentSource = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            documentSource = (RepositorySource) resolver.resolveURI(getSourceURI());
            node = documentSource.getNode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (documentSource != null) {
                    resolver.release(documentSource);
                }
                this.manager.release(resolver);
            }
        }
        return node;
    }

    private ResourceType resourceType;

    /**
     * Convenience method to read the document's resource type from the meta-data.
     * @see Document#getResourceType()
     */
    public ResourceType getResourceType() throws DocumentException {
        if (this.resourceType == null) {
            String name = getMetaDataManager().getLenyaMetaData()
                    .getFirstValue(LenyaMetaData.ELEMENT_RESOURCE_TYPE);
            if (name == null) {
                throw new DocumentException("No resource type defined for document [" + this + "]!");
            }
            ServiceSelector selector = null;
            try {
                selector = (ServiceSelector) this.manager.lookup(ResourceType.ROLE + "Selector");
                this.resourceType = (ResourceType) selector.select(name);
            } catch (Exception e) {
                throw new DocumentException(e);
            }
        }
        return this.resourceType;
    }

    public MetaData getMetaData(String namespaceUri) throws RepositoryException {
        return getRepositoryNode().getMetaData(namespaceUri);
    }

    public String[] getMetaDataNamespaceUris() throws RepositoryException {
        return getRepositoryNode().getMetaDataNamespaceUris();
    }

}
