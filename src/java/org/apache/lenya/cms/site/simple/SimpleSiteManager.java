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

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.RepositoryItemFactory;
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
public class SimpleSiteManager extends AbstractSiteManager implements Serviceable {

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#requires(org.apache.lenya.cms.publication.DocumentFactory,
     *      org.apache.lenya.cms.site.SiteNode, org.apache.lenya.cms.site.SiteNode)
     */
    public boolean requires(DocumentFactory map, SiteNode dependingResource,
            SiteNode requiredResource) throws SiteException {
        return false;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getRequiredResources(org.apache.lenya.cms.publication.DocumentFactory,
     *      org.apache.lenya.cms.site.SiteNode)
     */
    public SiteNode[] getRequiredResources(DocumentFactory map, SiteNode resource)
            throws SiteException {
        return new SiteNode[0];
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getRequiringResources(org.apache.lenya.cms.publication.DocumentFactory,
     *      org.apache.lenya.cms.site.SiteNode)
     */
    public SiteNode[] getRequiringResources(DocumentFactory map, SiteNode resource)
            throws SiteException {
        return new SiteNode[0];
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#add(org.apache.lenya.cms.publication.Document)
     */
    public void add(String path, Document document) throws SiteException {

        DocumentStore store = getStore(document);
        try {
            store.add(path, document);
        } catch (DocumentException e) {
            throw new SiteException(e);
        }
    }

    /**
     * @param document The document.
     * @return The store of the document.
     * @throws SiteException if an error occurs.
     */
    private DocumentStore getStore(Document document) throws SiteException {
        Publication publication = document.getPublication();
        String area = document.getArea();
        DocumentFactory map = document.getFactory();
        return getStore(map, publication, area);
    }

    /**
     * @param map The identity map.
     * @param publication The publication.
     * @param area The area.
     * @return A document store.
     * @throws SiteException if an error occurs.
     */
    protected DocumentStore getStore(DocumentFactory map, Publication publication, String area)
            throws SiteException {
        String key = getKey(publication, area);
        DocumentStore store;
        RepositoryItemFactory factory = new DocumentStoreFactory(this.manager, getLogger());
        try {
            store = (DocumentStore) map.getSession().getRepositoryItem(factory, key);
        } catch (Exception e) {
            throw new SiteException(e);
        }

        return store;
    }

    protected String getCollectionUuid(Publication pub, String area) {
        String sourceUri = pub.getContentURI(area) + DOCUMENT_PATH;
        try {
            org.w3c.dom.Document xml = SourceUtil.readDOM(sourceUri, manager);
            if (!xml.getDocumentElement().hasAttribute("uuid")) {
                throw new RuntimeException("The document element of [" + sourceUri + "] doesn't contain a uuid attribute!");
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
    protected String getKey(Publication publication, String area) {
        return publication.getId() + ":" + area + ":" + getCollectionUuid(publication, area);
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#contains(org.apache.lenya.cms.publication.Document)
     */
    public boolean contains(Document resource) throws SiteException {

        try {
            DocumentStore store = getStore(resource);
            if (resource.equals(store)) {
                return true;
            }
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
     * @see org.apache.lenya.cms.site.SiteManager#delete(org.apache.lenya.cms.publication.Document)
     */
    public void delete(Document document) throws SiteException {
        DocumentStore store = getStore(document);
        try {
            if (store.contains(document)) {
                store.remove(document);
            }
        } catch (Exception e) {
            throw new SiteException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getLabel(org.apache.lenya.cms.publication.Document)
     */
    public String getLabel(Document document) throws SiteException {
        return "";
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#setLabel(org.apache.lenya.cms.publication.Document,
     *      java.lang.String)
     */
    public void setLabel(Document document, String label) throws SiteException {
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#setVisibleInNav(org.apache.lenya.cms.publication.Document,
     *      boolean)
     */
    public void setVisibleInNav(Document document, boolean visibleInNav) throws SiteException {
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getDocuments(org.apache.lenya.cms.publication.DocumentFactory,
     *      org.apache.lenya.cms.publication.Publication, java.lang.String)
     */
    public Document[] getDocuments(DocumentFactory identityMap, Publication publication, String area)
            throws SiteException {
        DocumentStore store = getStore(identityMap, publication, area);
        try {
            return store.getDocuments();
        } catch (DocumentException e) {
            throw new SiteException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getSiteStructure(org.apache.lenya.cms.publication.DocumentFactory,
     *      org.apache.lenya.cms.publication.Publication, java.lang.String)
     */
    public SiteStructure getSiteStructure(DocumentFactory map, Publication publication, String area)
            throws SiteException {
        return getStore(map, publication, area);
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getAvailableLocator(DocumentFactory,
     *      DocumentLocator)
     */
    public DocumentLocator getAvailableLocator(DocumentFactory factory, DocumentLocator document)
            throws SiteException {
        return document;
    }

    public boolean isVisibleInNav(Document document) throws SiteException {
        return true;
    }

    public boolean contains(DocumentFactory factory, Publication pub, String area, String path)
            throws SiteException {
        return getStore(factory, pub, area).contains(path);
    }

    public void set(String path, Document document) throws SiteException {
        try {
            getStore(document).setPath(document, path);
        } catch (TransactionException e) {
            throw new SiteException(e);
        }
    }

}
