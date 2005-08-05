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
import org.apache.lenya.cms.metadata.MetaDataManager;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.SiteManager;

/**
 * A typical CMS document.
 * @version $Id$
 */
public class DefaultDocument extends AbstractLogEnabled implements Document {

    private String id;
    private String sourceURI;
    private DocumentIdentityMap identityMap;
    protected ServiceManager manager;
    private MetaDataManager metaDataManager;

    /**
     * Creates a new instance of DefaultDocument. The language of the document is the default
     * language of the publication.
     * @param manager The service manager.
     * @param map The identity map the document belongs to.
     * @param publication The publication.
     * @param _id The document ID (starting with a slash).
     * @param _area The area.
     * @param _logger a logger
     */
    protected DefaultDocument(ServiceManager manager, DocumentIdentityMap map,
            Publication publication, String _id, String _area, Logger _logger) {
        this(manager, map, publication, _id, _area, publication.getDefaultLanguage(), _logger);
    }

    /**
     * Creates a new instance of DefaultDocument.
     * @param manager The service manager.
     * @param map The identity map the document belongs to.
     * @param publication The publication.
     * @param _id The document ID (starting with a slash).
     * @param _area The area.
     * @param _language the language
     * @param _logger a logger
     */
    protected DefaultDocument(ServiceManager manager, DocumentIdentityMap map,
            Publication publication, String _id, String _area, String _language, Logger _logger) {

        ContainerUtil.enableLogging(this, _logger);
        if (getLogger().isDebugEnabled())
            getLogger().debug("DefaultDocument() creating new instance with _id [" + _id
                    + "], _language [" + _language + "]");

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

        if (getLogger().isDebugEnabled())
            getLogger().debug("DefaultDocument() done building instance with _id [" + _id
                    + "], _language [" + _language + "]");

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
            SourceUtil.delete(getMetaSourceURI(), this.manager);
        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }

    protected static final String IDENTIFIABLE_TYPE = "document";

    /**
     * @see org.apache.lenya.transaction.Identifiable#getIdentifiableType()
     */
    public String getIdentifiableType() {
        return IDENTIFIABLE_TYPE;
    }

    /**
     * When source URI has not been set by whoever created the document, provides a default
     * mechanism for constructing the document's URI.
     * @return A URI.
     */
    private String getDefaultSourceURI() {
        String path = publication.getPathMapper().getPath(getId(), getLanguage());
        return publication.getSourceURI() + "/content/" + getArea() + "/" + path;

    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getSourceURI()
     */
    public String getSourceURI() {
        if (sourceURI == null)
            sourceURI = getDefaultSourceURI();
        return sourceURI;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#setSourceURI(String)
     */
    public void setSourceURI(String _uri) {
        sourceURI = _uri;
    }

    /**
     * @return The meta source URI.
     */
    public String getMetaSourceURI() {
        return getSourceURI() + Document.DOCUMENT_META_SUFFIX;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getRepositoryNodes()
     */
    public Node[] getRepositoryNodes() {
        Node[] nodes = new Node[2];
        SourceResolver resolver = null;
        RepositorySource documentSource = null;
        RepositorySource metaSource = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            documentSource = (RepositorySource) resolver.resolveURI(getSourceURI());
            metaSource = (RepositorySource) resolver.resolveURI(getMetaSourceURI());
            nodes[0] = documentSource.getNode();
            nodes[1] = metaSource.getNode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (documentSource != null) {
                    resolver.release(documentSource);
                }
                if (metaSource != null) {
                    resolver.release(metaSource);
                }
                this.manager.release(resolver);
            }
        }
        return nodes;
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

}
