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

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.metadata.dublincore.DublinCoreProxy;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;

/**
 * A typical CMS document.
 */
public class DefaultDocument extends AbstractLogEnabled implements Document {

    private String id;
    private DublinCore dublincore;
    private DocumentIdentityMap identityMap;
    private ResourcesManager resourcesManager;

    /**
     * Creates a new instance of DefaultDocument. The language of the document
     * is the default language of the publication.
     * @param map The identity map the document belongs to.
     * @param _id The document ID (starting with a slash).
     * @param _area The area.
     */
    protected DefaultDocument(DocumentIdentityMap map, String _id, String _area) {
        if (_id == null) {
            throw new IllegalArgumentException("The document ID must not be null!");
        }
        if (!_id.startsWith("/")) {
            throw new IllegalArgumentException("The document ID must start with a slash!");
        }
        this.id = _id;

        this.identityMap = map;

        setArea(_area);
        setLanguage(this.identityMap.getPublication().getDefaultLanguage());

        this.dublincore = new DublinCoreProxy(this);
    }

    /**
     * Creates a new instance of DefaultDocument.
     * 
     * @param map The identity map the document belongs to.
     * @param _id The document ID (starting with a slash).
     * @param _area The area.
     * @param _language the language
     */
    protected DefaultDocument(DocumentIdentityMap map, String _id, String _area, String _language) {
        if (_id == null) {
            throw new IllegalArgumentException("The document ID must not be null!");
        }
        if (!_id.startsWith("/")) {
            throw new IllegalArgumentException("The document ID must start with a slash!");
        }
        this.id = _id;

        this.identityMap = map;
        this.language = _language;
        setArea(_area);

        this.dublincore = new DublinCoreProxy(this);
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

    /**
     * @see org.apache.lenya.cms.publication.Document#getPublication()
     */
    public Publication getPublication() {
        return getIdentityMap().getPublication();
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
                version = getIdentityMap().getFactory().getLanguageVersion(this, allLanguages[i]);
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
        try {
            SiteManager siteManager = getPublication().getSiteManager(getIdentityMap());
            if (siteManager != null) {
                labelString = siteManager.getLabel(this);
            }
        } catch (SiteException e) {
            throw new DocumentException(e);
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
        try {
            SiteManager manager = getPublication().getSiteManager(getIdentityMap());
            if (manager != null) {
                exists = manager.contains(this);
            } else {
                exists = getFile().exists();
            }
        } catch (SiteException e) {
            throw new DocumentException(e);
        }
        return exists;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#existsInAnyLanguage()
     */
    public boolean existsInAnyLanguage() throws DocumentException {
        boolean exists;
        try {
            SiteManager manager = getPublication().getSiteManager(getIdentityMap());
            if (manager != null) {
                exists = manager.containsInAnyLanguage(this);
            } else {
                exists = getFile().exists();
            }
        } catch (SiteException e) {
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
            this.resourcesManager = new DefaultResourcesManager(this);
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
     * @see org.apache.lenya.cms.publication.Document#getSourceURI()
     */
    public String getSourceURI() {
        try {
            return "file:/" + getFile().getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#accept(org.apache.lenya.cms.publication.util.DocumentVisitor)
     */
    public void accept(DocumentVisitor visitor) throws PublicationException {
        visitor.visitDocument(this);
    }

}