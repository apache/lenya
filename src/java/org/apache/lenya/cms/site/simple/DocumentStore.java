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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

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
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.util.Assert;
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

    protected static final Object SITE_PATH = "/sitestructure";

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
        this.doc2path.put(getKey(uuid, pub.getDefaultLanguage()), SITE_PATH);
    }

    protected static final String NAMESPACE = "http://apache.org/lenya/sitemanagement/simple/1.0";

    protected static final String ATTRIBUTE_LANGUAGE = "xml:lang";
    protected static final String ATTRIBUTE_PATH = "path";

    private Map doc2path = new HashMap();

    protected String getKey(String uuid, String language) {
        return uuid + ":" + language;
    }

    protected String getLanguage(String key) {
        return key.split(":")[1];
    }

    protected String getUuid(String key) {
        return key.split(":")[0];
    }

    /**
     * @see org.apache.lenya.cms.publication.util.CollectionImpl#createDocumentElement(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.xml.NamespaceHelper)
     */
    protected Element createDocumentElement(Document document, NamespaceHelper helper)
            throws DocumentException {
        Element element = super.createDocumentElement(document, helper);
        element.setAttribute(ATTRIBUTE_LANGUAGE, document.getLanguage());
        String path = getPath(document.getUUID(), document.getLanguage());
        element.setAttribute(ATTRIBUTE_PATH, path);
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
        String key = getKey(uuid, language);
        if (!this.doc2path.containsKey(key)) {
            this.doc2path.put(key, path);
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
        return doc2path().values().contains(path);
    }

    public boolean containsByUuid(String uuid, String language) {
        return doc2path().containsKey(getKey(uuid, language));
    }

    public boolean containsInAnyLanguage(String uuid) {
        return doc2path().containsKey(uuid);
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

    public Link getByUuid(String uuid, String language) throws SiteException {
        String path = getPath(uuid, language);
        SiteNode node = new SimpleSiteNode(this, path, uuid, getLogger());
        return node.getLink(language);
    }

    protected String getPath(String uuid, String language) {
        String key = getKey(uuid, language);
        Assert.isTrue("contains [" + key + "]", containsByUuid(uuid, language));
        return (String) doc2path().get(key);
    }

    private Map path2node = new WeakHashMap();

    public SiteNode getNode(String path) throws SiteException {

        SiteNode node = (SiteNode) this.path2node.get(path);
        if (node == null) {
            Set keys = doc2path().keySet();
            for (Iterator i = keys.iterator(); i.hasNext();) {
                String key = (String) i.next();
                String value = (String) doc2path().get(key);
                if (value.equals(path)) {
                    String uuid = getUuid(key);
                    node = new SimpleSiteNode(this, path, uuid, getLogger());
                    this.path2node.put(path, node);
                }
            }
        }
        if (node != null) {
            return node;
        }
        throw new SiteException("[" + this + "] does not contain the path [" + path + "]");
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

    public Link add(String path, Document document) throws SiteException {
        Assert.notNull("path", path);
        Assert.notNull("document", document);

        try {
            Assert.isTrue("document [" + document + "] is already contained!", !contains(document));
            String key = getKey(document.getUUID(), document.getLanguage());
            if (!doc2path().containsKey(key)) {
                doc2path().put(key, path);
            }
            super.add(document);
        } catch (DocumentException e) {
            throw new SiteException(e);
        }

        return getNode(path).getLink(document.getLanguage());
    }

    /**
     * Sets the path for a document.
     * @param document
     * @param path
     * @throws TransactionException
     */
    public void setPath(Document document, String path) throws TransactionException {
        Assert.notNull("path", path);
        Assert.notNull("document", document);
        String key = getKey(document.getUUID(), document.getLanguage());
        doc2path().put(key, path);
        save();
    }

    protected Map doc2path() {
        try {
            load();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        return this.doc2path;
    }

    public SiteNode[] getNodes() {
        try {
            Document[] docs = getDocuments();
            Set paths = new HashSet();
            for (int i = 0; i < docs.length; i++) {
                paths.add(getPath(docs[i].getUUID(), docs[i].getLanguage()));
            }
            Set nodes = new HashSet();
            for (Iterator i = paths.iterator(); i.hasNext();) {
                String path = (String) i.next();
                nodes.add(getNode(path));
            }
            return (SiteNode[]) nodes.toArray(new SiteNode[nodes.size()]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}