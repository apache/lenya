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

/* $Id: DefaultDocument.java,v 1.41 2004/03/01 16:18:16 gregor Exp $  */

package org.apache.lenya.cms.publication;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * A typical CMS document.
 */
public class DefaultDocument implements Document {
    
    private String id;
    private Publication publication;
    private DublinCore dublincore;

    /**
     * Creates a new instance of DefaultDocument.
     * @param publication The publication the document belongs to.
     * @param id The document ID (starting with a slash).
     * @deprecated Use {@link DefaultDocumentBuilder} instead.
     */
    public DefaultDocument(Publication publication, String id) {
        assert id != null;
        assert id.startsWith("/");
        this.id = id;

        assert(publication != null) && !"".equals(publication);
        this.publication = publication;
        this.dublincore = new DublinCoreProxy(this);
    }

    /**
     * Creates a new instance of DefaultDocument.
     * The language of the document is the default language of
     * the publication.
     * @param publication The publication the document belongs to.
     * @param id The document ID (starting with a slash).
     * @param area The area.
     */
    protected DefaultDocument(Publication publication, String id, String area) {
        assert id != null;
        assert id.startsWith("/");
        this.id = id;

        assert(publication != null) && !"".equals(publication);
        this.publication = publication;

        setArea(area);
        setLanguage(publication.getDefaultLanguage());

        this.dublincore = new DublinCoreProxy(this);

    }

    /**
     * Creates a new instance of DefaultDocument.
     * 
     * @param publication The publication the document belongs to.
     * @param id The document ID (starting with a slash).
     * @param area The area.
     * @param language the language
     */
    protected DefaultDocument(Publication publication, String id, String area, String language) {
        assert id != null;
        assert id.startsWith("/");
        this.id = id;

        assert(publication != null) && !"".equals(publication);
        this.publication = publication;
        this.language = language;
        setArea(area);

        this.dublincore = new DublinCoreProxy(this);

    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getId()
     */
    public String getId() {
        return id;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getName()
     */
    public String getName() {
        String[] ids = id.split("/");
        String nodeId = ids[ids.length - 1];

        return nodeId;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getNodeId()
     * @deprecated replaced by getName()
     */
    public String getNodeId() {
        return getName();
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getPublication()
     */
    public Publication getPublication() {
        return publication;
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
        return dublincore;
    }

    /**
     * Returns the file for this document.
     * @return A file object.
     */
    public File getFile() {
        return getPublication().getPathMapper().getFile(
            getPublication(),
            getArea(),
            getId(),
            getLanguage());
    }

    private String language = "";

    /**
     * @see org.apache.lenya.cms.publication.Document#getLanguage()
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getLanguage()
     */
    public String[] getLanguages() throws DocumentException {
        ArrayList languages = new ArrayList();
        SiteTree sitetree;
        try {
            sitetree = getPublication().getSiteTree(getArea());
            if (sitetree != null) {
                SiteTreeNode node = sitetree.getNode(getId());
                if (node != null) {
                    Label[] labels = node.getLabels();
                    for (int i = 0; i < labels.length; i++) {
                        languages.add(labels[i].getLanguage());
                    }
                }
            } else {
                languages.add(getLanguage());
            }
        } catch (SiteTreeException e) {
            throw new DocumentException(e);
        }

        return (String[]) languages.toArray(new String[languages.size()]);
    }

    /**
     * Sets the language of this document.
     * @param language The language.
     */
    public void setLanguage(String language) {
        assert language != null;
        this.language = language;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getLabel()
     */
    public String getLabel() throws DocumentException {
        String label = "";
        try {
            SiteTree siteTree = getPublication().getSiteTree(getArea());
            if (siteTree != null) {
                label = siteTree.getNode(getId()).getLabel(getLanguage()).getLabel();
            }
        } catch (SiteTreeException e) {
            throw new DocumentException(e);
        }
        return label;
    }

    private String area;

    /**
     * @see org.apache.lenya.cms.publication.Document#getArea()
     */
    public String getArea() {
        return area;
    }

    /**
     * @see Document#getCompleteURL(String)
     */
    public String getCompleteURL() {
        return "/" + getPublication().getId() + "/" + getArea() + getDocumentURL();
    }

    /**
     * @see Document#getCompleteInfoURL(String)
     */
    public String getCompleteInfoURL() {
        return "/"
            + getPublication().getId()
            + "/"
            + Publication.INFO_AREA_PREFIX
            + getArea()
            + getDocumentURL();
    }

    /**
     * @see Document#getCompleteURL(String)
     */
    public String getCompleteURLWithoutLanguage() {
        String extensionSuffix = "".equals(getExtension()) ? "" : ("." + getExtension());

        return "/" + getPublication().getId() + "/" + getArea() + getId() + extensionSuffix;
    }

    /**
     * Sets the area.
     * @param area A string.
     */
    protected void setArea(String area) {
        assert AbstractPublication.isValidArea(area);
        this.area = area;
    }

    private String extension = "html";

    /**
     * @see org.apache.lenya.cms.publication.Document#getExtension()
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Sets the extension of the file in the URL.
     * @param extension A string.
     */
    protected void setExtension(String extension) {
        assert extension != null;
        this.extension = extension;
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
     * @see org.apache.lenya.cms.publication.Document#getDocumentURL()
     */
    public String getDocumentURL() {
        return documentURL;
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.publication.Document#exists()
     */
    public boolean exists() throws DocumentException {
        boolean exists;
        try {
            SiteTree sitetree = getPublication().getSiteTree(getArea());
            if (sitetree != null) {
                SiteTreeNode node = sitetree.getNode(getId());
                exists = (node != null) && (node.getLabel(getLanguage()) != null);
            } else {
                exists = getFile().exists();
            }
        } catch (SiteTreeException e) {
            throw new DocumentException(e);
        }
        return exists;
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.publication.Document#existsInAnyLanguage()
     */
    public boolean existsInAnyLanguage() throws DocumentException {
        boolean exists = false;
        try {
            SiteTree sitetree = getPublication().getSiteTree(getArea());
            if (sitetree != null) {
                SiteTreeNode node = sitetree.getNode(getId());
                exists = node != null;
            } else {
                exists = getFile().exists();
            }
        } catch (SiteTreeException e) {
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
            equals =
                getPublication().equals(document.getPublication())
                    && getId().equals(document.getId())
                    && getArea().equals(document.getArea())
                    && getLanguage().equals(document.getLanguage());
        }
        return equals;

    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {

        String key =
            getPublication().getId()
                + ":"
                + getPublication().getServletContext()
                + ":"
                + getArea()
                + ":"
                + getId()
                + ":"
                + getLanguage();

        return key.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getPublication().getId() + ":" + getArea() + ":" + getId() + ":" + getLanguage();
    }

}
