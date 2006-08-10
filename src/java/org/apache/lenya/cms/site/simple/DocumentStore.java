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
package org.apache.lenya.cms.site.simple;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentIdentifier;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.util.CollectionImpl;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 * Site structure object which stores a list of documents.
 * 
 * @version $Id$
 */
public class DocumentStore extends CollectionImpl implements SiteStructure {

    /**
     * The identifiable type.
     */
    public static final String IDENTIFIABLE_TYPE = "documentstore";

    /**
     * @param manager The service manager.
     * @param map The identity map.
     * @param pub The publication.
     * @param area The area.
     * @param uuid The UUID.
     * @param _logger The logger.
     * @throws DocumentException if an error occurs.
     */
    public DocumentStore(ServiceManager manager, DocumentFactory map, Publication pub, String area,
            String uuid, Logger _logger) throws DocumentException {
        super(manager,
                map,
                new DocumentIdentifier(pub, area, uuid, pub.getDefaultLanguage()),
                _logger);
    }

    protected static final String NAMESPACE = "http://apache.org/lenya/sitemanagement/simple/1.0";

    protected static final String ATTRIBUTE_LANGUAGE = "xml:lang";
    protected static final String ATTRIBUTE_PATH = "path";

    private Map path2uuid = new HashMap();
    private Map uuid2path = new HashMap();

    /**
     * @see org.apache.lenya.cms.publication.util.CollectionImpl#createDocumentElement(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.xml.NamespaceHelper)
     */
    protected Element createDocumentElement(Document document, NamespaceHelper helper)
            throws DocumentException {
        Element element = super.createDocumentElement(document, helper);
        element.setAttribute(ATTRIBUTE_LANGUAGE, document.getLanguage());
        element.setAttribute(ATTRIBUTE_PATH, getPath(document.getUUID()));
        return element;
    }

    /**
     * @see org.apache.lenya.cms.publication.util.CollectionImpl#loadDocument(org.w3c.dom.Element)
     */
    protected Document loadDocument(Element documentElement) throws DocumentBuildException {
        String uuid = documentElement.getAttribute(ATTRIBUTE_UUID);
        String language = documentElement.getAttribute(ATTRIBUTE_LANGUAGE);
        String path = documentElement.getAttribute(ATTRIBUTE_PATH);
        Document document = getDelegate().getFactory().get(getDelegate().getPublication(),
                getDelegate().getArea(),
                uuid,
                language);
        if (!this.uuid2path.containsKey(uuid)) {
            this.uuid2path.put(uuid, path);
            this.path2uuid.put(path, uuid);
        }
        return document;
    }

    /**
     * @return if the document exists.
     * @throws DocumentException if an error occurs.
     */
    public boolean exists() throws DocumentException {
        try {
            return SourceUtil.exists(getDelegate().getSourceURI(), this.manager);
        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }

    public Node getRepositoryNode() {
        return getDelegate().getRepositoryNode();
    }

    public boolean contains(String path) {
        return path2uuid().containsKey(path);
    }

    public boolean containsUuid(String uuid) {
        return uuid2path().containsKey(uuid);
    }

    protected Document getDocument(String uuid) {
        Document[] docs;
        try {
            docs = getDocuments();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < docs.length; i++) {
            if (docs[i].getUUID().equals(uuid)) {
                return docs[i];
            }
        }
        return null;
    }

    public SiteNode getByUuid(String uuid) throws SiteException {
        String path = getPath(uuid);
        return new SimpleSiteNode(this, path, uuid);
    }

    protected String getPath(String uuid) {
        return (String) uuid2path().get(uuid);
    }

    public SiteNode getNode(String path) throws SiteException {
        if (!contains(path)) {
            throw new SiteException(this + " does not contain the path [" + path + "]");
        }
        String uuid = (String) path2uuid().get(path);
        return new SimpleSiteNode(this, path, uuid);
    }
    
    public String toString() {
        return getPublication().getId() + ":" + getArea();
    }

    public Publication getPublication() {
        return getDelegate().getPublication();
    }

    public String getArea() {
        return getDelegate().getArea();
    }

    public void add(String path, Document document) throws DocumentException {
        super.add(document);
        String uuid = document.getUUID();
        if (!uuid2path().containsKey(uuid)) {
            uuid2path().put(uuid, path);
            path2uuid().put(path, uuid);
        }
    }

    public void setPath(Document document, String path) throws TransactionException {
        uuid2path().put(document.getUUID(), path);
        path2uuid().put(path, document.getUUID());
        save();
    }
    
    protected Map path2uuid() {
        try {
            load();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        return this.path2uuid;
    }
    
    protected Map uuid2path() {
        try {
            load();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        return this.uuid2path;
    }


}