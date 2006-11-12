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
package org.apache.lenya.cms.publication;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryItem;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.repository.UUIDGenerator;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.SiteUtil;

/**
 * A DocumentIdentityMap avoids the multiple instanciation of a document object.
 * 
 * @version $Id: DocumentIdentityMap.java 264153 2005-08-29 15:11:14Z andreas $
 */
public class DocumentFactoryImpl extends AbstractLogEnabled implements DocumentFactory {

    private Session session;
    protected ServiceManager manager;

    /**
     * @return The session.
     */
    public Session getSession() {
        return this.session;
    }

    /**
     * Ctor.
     * @param session The session to use.
     * @param manager The service manager.
     * @param logger The logger to use.
     */
    public DocumentFactoryImpl(Session session, ServiceManager manager, Logger logger) {
        this.session = session;
        this.manager = manager;
        ContainerUtil.enableLogging(this, logger);
    }

    /**
     * Returns a document.
     * @param publication The publication.
     * @param area The area.
     * @param uuid The document UUID.
     * @param language The language.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document get(Publication publication, String area, String uuid, String language)
            throws DocumentBuildException {

        if (getLogger().isDebugEnabled())
            getLogger().debug(
                    "DocumentIdentityMap::get() called on publication [" + publication.getId()
                            + "], area [" + area + "], UUID [" + uuid + "], language [" + language
                            + "]");

        String key = getKey(publication, area, uuid, language);

        if (getLogger().isDebugEnabled())
            getLogger().debug(
                    "DocumentIdentityMap::get() got key [" + key + "] from DocumentFactory");

        try {
            return (Document) getSession().getRepositoryItem(this, key);
        } catch (RepositoryException e) {
            throw new DocumentBuildException(e);
        }
    }

    /**
     * Returns the document identified by a certain web application URL.
     * @param webappUrl The web application URL.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getFromURL(String webappUrl) throws DocumentBuildException {
        String key = getKey(webappUrl);
        try {
            return (Document) getSession().getRepositoryItem(this, key);
        } catch (RepositoryException e) {
            throw new DocumentBuildException(e);
        }
    }

    /**
     * Builds a clone of a document for another language.
     * @param document The document to clone.
     * @param language The language of the target document.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getLanguageVersion(Document document, String language)
            throws DocumentBuildException {
        return get(document.getPublication(), document.getArea(), document.getUUID(), language);
    }

    /**
     * Builds a clone of a document for another area.
     * @param document The document to clone.
     * @param area The area of the target document.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getAreaVersion(Document document, String area) throws DocumentBuildException {
        return get(document.getPublication(), area, document.getUUID(), document.getLanguage());
    }

    /**
     * Builds a document for the default language.
     * @param publication The publication.
     * @param area The area.
     * @param documentId The document ID.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document get(Publication publication, String area, String documentId)
            throws DocumentBuildException {
        return get(publication, area, documentId, publication.getDefaultLanguage());
    }

    /**
     * Checks if a string represents a valid document ID.
     * @param id The string.
     * @return A boolean value.
     */
    public boolean isValidDocumentId(String id) {

        if (!id.startsWith("/")) {
            return false;
        }

        String[] snippets = id.split("/");

        if (snippets.length < 2) {
            return false;
        }

        for (int i = 1; i < snippets.length; i++) {
            if (!snippets[i].matches("[a-zA-Z0-9\\-]+")) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if a webapp URL represents a document.
     * @param webappUrl A web application URL.
     * @return A boolean value.
     * @throws DocumentBuildException if an error occurs.
     */
    public boolean isDocument(String webappUrl) throws DocumentBuildException {

        try {
            Publication publication = PublicationUtil.getPublicationFromUrl(this.manager, this,
                    webappUrl);
            if (publication.exists()) {

                ServiceSelector selector = null;
                DocumentBuilder builder = null;
                try {
                    selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE
                            + "Selector");
                    builder = (DocumentBuilder) selector.select(publication
                            .getDocumentBuilderHint());
                    if (builder.isDocument(webappUrl)) {
                        DocumentLocator locator = builder.getLocator(this, webappUrl);
                        return SiteUtil.contains(this.manager, this, locator);
                    } else {
                        return false;
                    }
                } finally {
                    if (selector != null) {
                        if (builder != null) {
                            selector.release(builder);
                        }
                        this.manager.release(selector);
                    }
                }
            } else {
                return false;
            }
        } catch (ServiceException e) {
            throw new DocumentBuildException(e);
        } catch (PublicationException e) {
            throw new DocumentBuildException(e);
        }
    }

    /**
     * Builds a document key.
     * @param publication The publication.
     * @param area The area.
     * @param uuid The document UUID.
     * @param language The language.
     * @return A key.
     */
    public String getKey(Publication publication, String area, String uuid, String language) {
        return publication.getId() + ":" + area + ":" + uuid + ":" + language;
    }

    /**
     * Builds a document key.
     * @param webappUrl The web application URL.
     * @return A key.
     */
    public String getKey(String webappUrl) {
        ServiceSelector selector = null;
        DocumentBuilder builder = null;
        try {
            Publication publication = PublicationUtil.getPublicationFromUrl(this.manager, this,
                    webappUrl);
            selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE + "Selector");
            builder = (DocumentBuilder) selector.select(publication.getDocumentBuilderHint());
            DocumentLocator locator = builder.getLocator(this, webappUrl);

            String area = locator.getArea();
            String uuid = null;
            if (SiteUtil.isDocument(this.manager, this, webappUrl)) {
                uuid = publication.getArea(area).getSite().getNode(locator.getPath()).getUuid();
            } else {
                UUIDGenerator generator = (UUIDGenerator) this.manager.lookup(UUIDGenerator.ROLE);
                uuid = generator.nextUUID();
            }
            return getKey(publication, area, uuid, locator.getLanguage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                if (builder != null) {
                    selector.release(builder);
                }
                this.manager.release(selector);
            }
        }
    }

    /**
     * @see org.apache.lenya.transaction.IdentifiableFactory#build(org.apache.lenya.transaction.IdentityMap,
     *      java.lang.String)
     */
    public RepositoryItem buildItem(Session session, String key) throws RepositoryException {
        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentFactory::build() called with key [" + key + "]");

        String[] snippets = key.split(":");
        String publicationId = snippets[0];
        String area = snippets[1];
        String uuid = snippets[2];
        String language = snippets[3];

        ServiceSelector selector = null;
        DocumentBuilder builder = null;
        Document document;
        try {

            Publication publication = getPublication(publicationId);

            selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE + "Selector");
            builder = (DocumentBuilder) selector.select(publication.getDocumentBuilderHint());
            DocumentIdentifier identifier = new DocumentIdentifier(publication, area, uuid,
                    language);
            document = buildDocument(this, identifier, builder);
        } catch (Exception e) {
            throw new RepositoryException(e);
        } finally {
            if (selector != null) {
                if (builder != null) {
                    selector.release(builder);
                }
                this.manager.release(selector);
            }
        }
        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentFactory::build() done.");

        return document;
    }

    protected Document buildDocument(DocumentFactory map, DocumentIdentifier identifier,
            DocumentBuilder builder) throws DocumentBuildException {

        DocumentImpl document = createDocument(map, identifier, builder);
        ContainerUtil.enableLogging(document, getLogger());
        return document;
    }

    /**
     * Creates a new document object. Override this method to create specific document objects,
     * e.g., for different document IDs.
     * @param map The identity map.
     * @param identifier The identifier.
     * @param builder The document builder.
     * @return A document.
     * @throws DocumentBuildException when something went wrong.
     */
    protected DocumentImpl createDocument(DocumentFactory map, DocumentIdentifier identifier,
            DocumentBuilder builder) throws DocumentBuildException {
        return new DocumentImpl(this.manager, map, identifier, getLogger());
    }

    public Document get(DocumentIdentifier identifier) throws DocumentBuildException {
        return get(identifier.getPublication(), identifier.getArea(), identifier.getUUID(),
                identifier.getLanguage());
    }

    public String getItemType() {
        return Document.TRANSACTIONABLE_TYPE;
    }

    public Document get(DocumentLocator locator) throws DocumentBuildException {
        try {
            Publication pub = getPublication(locator.getPublicationId());
            SiteStructure site = pub.getArea(locator.getArea()).getSite();
            String uuid = site.getNode(locator.getPath()).getUuid();
            return get(pub, locator.getArea(), uuid, locator.getLanguage());
        } catch (PublicationException e) {
            throw new DocumentBuildException(e);
        }
    }

    public Publication getPublication(String id) throws PublicationException {
        PublicationManager pubManager = null;
        try {
            pubManager = (PublicationManager) manager.lookup(PublicationManager.ROLE);
            return pubManager.getPublication(this, id);
        } catch (ServiceException e) {
            throw new PublicationException(e);
        } finally {
            if (pubManager != null) {
                manager.release(pubManager);
            }
        }
    }

    public Publication[] getPublications() {
        PublicationManager pubManager = null;
        try {
            pubManager = (PublicationManager) manager.lookup(PublicationManager.ROLE);
            return pubManager.getPublications(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (pubManager != null) {
                manager.release(pubManager);
            }
        }
    }

}