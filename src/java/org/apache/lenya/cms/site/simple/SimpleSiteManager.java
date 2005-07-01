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

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.AbstractSiteManager;
import org.apache.lenya.cms.site.Node;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.transaction.IdentifiableFactory;

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
     * @see org.apache.lenya.cms.site.SiteManager#requires(org.apache.lenya.cms.publication.DocumentIdentityMap,
     *      org.apache.lenya.cms.site.Node, org.apache.lenya.cms.site.Node)
     */
    public boolean requires(DocumentIdentityMap map, Node dependingResource, Node requiredResource)
            throws SiteException {
        return false;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getRequiredResources(org.apache.lenya.cms.publication.DocumentIdentityMap,
     *      org.apache.lenya.cms.site.Node)
     */
    public Node[] getRequiredResources(DocumentIdentityMap map, Node resource) throws SiteException {
        return new Node[0];
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getRequiringResources(org.apache.lenya.cms.publication.DocumentIdentityMap,
     *      org.apache.lenya.cms.site.Node)
     */
    public Node[] getRequiringResources(DocumentIdentityMap map, Node resource)
            throws SiteException {
        return new Node[0];
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#add(org.apache.lenya.cms.publication.Document)
     */
    public void add(Document document) throws SiteException {

        DocumentStore store = getStore(document);
        try {
            store.add(document);
        } catch (DocumentException e) {
            throw new SiteException(e);
        }
    }

    /**
     * @param document The document.
     * @return The store of the document.
     */
    private DocumentStore getStore(Document document) {
        Publication publication = document.getPublication();
        String area = document.getArea();
        DocumentIdentityMap map = document.getIdentityMap();
        return getStore(map, publication, area);
    }

    /**
     * @param map The identity map.
     * @param publication The publication.
     * @param area The area.
     * @return A document store.
     */
    protected DocumentStore getStore(DocumentIdentityMap map, Publication publication, String area) {
        String key = getKey(publication, area);
        DocumentStore store;
        IdentifiableFactory factory = map.getFactory(DocumentStore.IDENTIFIABLE_TYPE);
        if (factory == null) {
            factory = new DocumentStoreFactory(this.manager);
            ContainerUtil.enableLogging(factory, getLogger());
            map.setFactory(DocumentStore.IDENTIFIABLE_TYPE, factory);
        }
        store = (DocumentStore) map.get(DocumentStore.IDENTIFIABLE_TYPE, key);

        return store;
    }

    /**
     * @param publication The publication.
     * @param area The area.
     * @return The key to store sitetree objects in the identity map.
     */
    protected String getKey(Publication publication, String area) {
        return publication.getId() + ":" + area;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#contains(org.apache.lenya.cms.publication.Document)
     */
    public boolean contains(Document resource) throws SiteException {

        if (resource.getId().equals(DocumentStore.DOCUMENT_ID)) {
            return true;
        }

        try {
            return getStore(resource).contains(resource);
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

            String[] languages = resource.getPublication().getLanguages();
            for (int i = 0; i < languages.length; i++) {
                Document doc = resource.getIdentityMap().getLanguageVersion(resource, languages[i]);
                DocumentStore store = getStore(doc);
                contains = contains || store.contains(doc);
            }

            return getStore(resource).contains(resource);
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
            if (!store.contains(document)) {
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
     * @see org.apache.lenya.cms.site.SiteManager#getDocuments(org.apache.lenya.cms.publication.DocumentIdentityMap,
     *      org.apache.lenya.cms.publication.Publication, java.lang.String)
     */
    public Document[] getDocuments(DocumentIdentityMap identityMap, Publication publication,
            String area) throws SiteException {
        DocumentStore store = getStore(identityMap, publication, area);
        try {
            return store.getDocuments();
        } catch (DocumentException e) {
            throw new SiteException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getSiteStructure(org.apache.lenya.cms.publication.DocumentIdentityMap,
     *      org.apache.lenya.cms.publication.Publication, java.lang.String)
     */
    public SiteStructure getSiteStructure(DocumentIdentityMap map, Publication publication,
            String area) throws SiteException {
        return getStore(map, publication, area);
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getAvailableDocument(org.apache.lenya.cms.publication.Document)
     */
    public Document getAvailableDocument(Document document) throws SiteException {
        return document;
    }

}