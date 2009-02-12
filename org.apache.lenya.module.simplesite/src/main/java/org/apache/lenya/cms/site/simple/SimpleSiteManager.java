/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Session;
import org.apache.lenya.cms.site.AbstractSiteManager;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.transaction.TransactionException;

/**
 * Simple site manager which does not imply structural information. The documents are stored in
 * collections.
 * 
 * @version $Id$
 */
public class SimpleSiteManager extends AbstractSiteManager {

    private DocumentStoreFactory documentStoreFactory;
    private SourceResolver sourceResolver;

    public boolean requires(SiteNode dependingResource, SiteNode requiredResource)
            throws SiteException {
        return false;
    }

    public SiteNode[] getRequiringResources(SiteNode resource) throws SiteException {
        return new SiteNode[0];
    }

    public void add(String path, Document document) throws SiteException {
        getStore(document).add(path, document);
    }

    /**
     * @param document The document.
     * @return The store of the document.
     * @throws SiteException if an error occurs.
     */
    private DocumentStore getStore(Document document) throws SiteException {
        return getStore(document.area());
    }

    /**
     * @param area The area.
     * @return A document store.
     * @throws SiteException if an error occurs.
     */
    protected DocumentStore getStore(Area area) throws SiteException {
        String key = getKey(area);
        DocumentStore store;
        try {
            org.apache.lenya.cms.repository.Session session = (org.apache.lenya.cms.repository.Session) area
                    .getPublication().getSession();
            store = (DocumentStore) session.getRepositoryItem(getDocumentStoreFactory(), key);
        } catch (Exception e) {
            throw new SiteException(e);
        }

        return store;
    }

    protected String getCollectionUuid(Publication pub) {
        String sourceUri = pub.getContentUri(Publication.AUTHORING_AREA) + DOCUMENT_PATH;
        try {

            if (!SourceUtil.exists(sourceUri, getSourceResolver())) {
                throw new RuntimeException("The site configuration [" + sourceUri
                        + "] does not exist!");
            }

            org.w3c.dom.Document xml = SourceUtil.readDOM(sourceUri, getSourceResolver());
            if (!xml.getDocumentElement().hasAttribute("uuid")) {
                throw new RuntimeException("The document element of [" + sourceUri
                        + "] doesn't contain a uuid attribute!");
            }
            return xml.getDocumentElement().getAttribute("uuid");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static final String DOCUMENT_PATH = "/site.xml";

    /**
     * @param publication The publication.
     * @param area The area.
     * @return The key to store sitetree objects in the identity map.
     */
    protected String getKey(Area area) {
        return area.getPublication().getId() + ":" + area.getName() + ":"
                + getCollectionUuid(area.getPublication());
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#contains(org.apache.lenya.cms.publication.Document)
     */
    public boolean contains(Document resource) throws SiteException {

        try {
            DocumentStore store = getStore(resource);
            return store.contains(resource);
        } catch (DocumentException e) {
            throw new SiteException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#containsInAnyLanguage(org.apache.lenya.cms.publication.Document)
     */
    public boolean containsInAnyLanguage(Document resource) throws SiteException {
        try {
            boolean contains = false;

            String[] languages = resource.getLanguages();
            for (int i = 0; i < languages.length; i++) {
                Document doc = resource.getTranslation(languages[i]);
                DocumentStore store = getStore(doc);
                contains = contains || store.contains(doc);
            }

            return contains;
        } catch (Exception e) {
            throw new SiteException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#copy(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copy(Document sourceDocument, Document destinationDocument) throws SiteException {
        DocumentStore destinationStore = getStore(destinationDocument);
        try {
            if (!destinationStore.contains(destinationDocument)) {
                destinationStore.add(destinationDocument);
            }
        } catch (Exception e) {
            throw new SiteException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#setVisibleInNav(org.apache.lenya.cms.publication.Document,
     *      boolean)
     */
    public void setVisibleInNav(Document document, boolean visibleInNav) throws SiteException {
    }

    public Document[] getDocuments(Publication publication, String area) throws SiteException {
        DocumentStore store = getStore(publication.getArea(area));
        try {
            return store.getDocuments();
        } catch (DocumentException e) {
            throw new SiteException(e);
        }
    }

    public SiteStructure getSiteStructure(Publication publication, String area)
            throws SiteException {
        return getStore(publication.getArea(area));
    }

    public DocumentLocator getAvailableLocator(Session session, DocumentLocator document)
            throws SiteException {
        return document;
    }

    public boolean isVisibleInNav(Document document) throws SiteException {
        return true;
    }

    public void set(String path, Document document) throws SiteException {
        try {
            getStore(document).setPath(document, path);
        } catch (TransactionException e) {
            throw new SiteException(e);
        }
    }

    public DocumentLocator[] getRequiredResources(Session session, DocumentLocator locator)
            throws SiteException {
        return new DocumentLocator[0];
    }

    /**
     * TODO: Bean wiring
     */
    public void setDocumentStoreFactory(DocumentStoreFactory documentStoreFactory) {
        this.documentStoreFactory = documentStoreFactory;
    }

    public DocumentStoreFactory getDocumentStoreFactory() {
        return documentStoreFactory;
    }

    /**
     * TODO: Bean wiring
     */
    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

    public SourceResolver getSourceResolver() {
        return sourceResolver;
    }

}
