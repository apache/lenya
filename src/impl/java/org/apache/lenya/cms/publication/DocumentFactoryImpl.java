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

import java.util.Arrays;
import java.util.StringTokenizer;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryItem;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.util.Assert;

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
        return get(publication, area, uuid, language, -1);
    }

    public Document get(Publication publication, String area, String uuid, String language,
            int revision) throws DocumentBuildException {
        if (getLogger().isDebugEnabled())
            getLogger().debug(
                    "DocumentIdentityMap::get() called on publication [" + publication.getId()
                            + "], area [" + area + "], UUID [" + uuid + "], language [" + language
                            + "]");

        String key = getKey(publication, area, uuid, language, revision);

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
        Assert.notNull("webapp URL", webappUrl);
        PublicationManager pubMgr = getPubManager();
        try {
            URLInformation info = new URLInformation(webappUrl);
            String pubId = info.getPublicationId();
            if (pubId != null && Arrays.asList(pubMgr.getPublicationIds()).contains(pubId)) {
                Publication pub = pubMgr.getPublication(this, pubId);
                DocumentBuilder builder = pub.getDocumentBuilder();
                return builder.isDocument(this, webappUrl);
            } else {
                return false;
            }
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
     * @param revision 
     * @return A key.
     */
    public String getKey(Publication publication, String area, String uuid, String language, int revision) {
        Assert.notNull("publication", publication);
        Assert.notNull("area", area);
        Assert.notNull("uuid", uuid);
        Assert.notNull("language", language);
        return publication.getId() + ":" + area + ":" + uuid + ":" + language + ":" + revision;
    }

    /**
     * Builds a document key.
     * @param webappUrl The web application URL.
     * @return A key.
     */
    public String getKey(String webappUrl) {
        Assert.notNull("webapp URL", webappUrl);
        try {
            if (!isDocument(webappUrl)) {
                throw new RuntimeException("No document for URL [" + webappUrl + "] found.");
            }
            DocumentLocator locator = getLocator(webappUrl);
            Publication publication = getPublication(locator.getPublicationId());
            String area = locator.getArea();
            String uuid = publication.getArea(area).getSite().getNode(locator.getPath()).getUuid();
            return getKey(publication, area, uuid, locator.getLanguage(), -1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected DocumentLocator getLocator(String webappUrl) {
        DocumentLocator locator;
        try {
            Publication publication = PublicationUtil.getPublicationFromUrl(this.manager, this,
                    webappUrl);
            DocumentBuilder builder = publication.getDocumentBuilder();
            locator = builder.getLocator(this, webappUrl);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return locator;
    }

    /**
     * @see org.apache.lenya.transaction.IdentifiableFactory#build(org.apache.lenya.transaction.IdentityMap,
     *      java.lang.String)
     */
    public RepositoryItem buildItem(Session session, String key) throws RepositoryException {
        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentFactory::build() called with key [" + key + "]");

        StringTokenizer tokenizer = new StringTokenizer(key, ":");
        String publicationId = tokenizer.nextToken();
        String area = tokenizer.nextToken();
        String uuid = tokenizer.nextToken();
        String language = tokenizer.nextToken();
        String revisionString = tokenizer.nextToken();
        int revision = Integer.valueOf(revisionString).intValue();

        Document document;
        try {
            Publication publication = getPublication(publicationId);
            DocumentBuilder builder = publication.getDocumentBuilder();
            DocumentIdentifier identifier = new DocumentIdentifier(publicationId, area, uuid,
                    language);
            document = buildDocument(this, identifier, revision, builder);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentFactory::build() done.");

        return document;
    }

    protected Document buildDocument(DocumentFactory map, DocumentIdentifier identifier,
            int revision, DocumentBuilder builder) throws DocumentBuildException {

        DocumentImpl document = createDocument(map, identifier, revision, builder);
        ContainerUtil.enableLogging(document, getLogger());
        return document;
    }

    /**
     * Creates a new document object. Override this method to create specific document objects,
     * e.g., for different document IDs.
     * @param map The identity map.
     * @param identifier The identifier.
     * @param revision The revision or -1 for the latest revision.
     * @param builder The document builder.
     * @return A document.
     * @throws DocumentBuildException when something went wrong.
     */
    protected DocumentImpl createDocument(DocumentFactory map, DocumentIdentifier identifier,
            int revision, DocumentBuilder builder) throws DocumentBuildException {
        return new DocumentImpl(this.manager, map, identifier, revision, getLogger());
    }

    public Document get(DocumentIdentifier identifier) throws DocumentBuildException {
        try {
            Publication pub = getPublication(identifier.getPublicationId());
            return get(pub, identifier.getArea(), identifier.getUUID(), identifier.getLanguage());
        } catch (PublicationException e) {
            throw new DocumentBuildException(e);
        }
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
        return getPubManager().getPublication(this, id);
    }

    public Publication[] getPublications() {
        return getPubManager().getPublications(this);
    }

    private static PublicationManager pubManager;

    protected PublicationManager getPubManager() {
        if (pubManager == null) {
            try {
                pubManager = (PublicationManager) this.manager.lookup(PublicationManager.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return pubManager;
    }

    public boolean existsPublication(String id) {
        return Arrays.asList(getPubManager().getPublicationIds()).contains(id);
    }

}