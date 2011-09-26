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

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.metadata.MetaDataCache;
import org.apache.lenya.cms.repository.NodeFactory;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryItem;
import org.apache.lenya.cms.repository.RepositoryItemFactory;
import org.apache.lenya.cms.repository.SessionHolder;
import org.apache.lenya.utils.URLInformation;
/**
 * A DocumentIdentityMap avoids the multiple instanciation of a document object.
 * 
 * @version $Id: DocumentIdentityMap.java 264153 2005-08-29 15:11:14Z andreas $
 */
public class DocumentFactoryImpl implements DocumentFactory, RepositoryItemFactory {

    private static final Log logger = LogFactory.getLog(DocumentFactoryImpl.class);

    private Session session;
    private MetaDataCache metaDataCache;
    private SourceResolver sourceResolver;
    private NodeFactory nodeFactory;
    private ResourceTypeResolver resourceTypeResolver;
    
    /**
     * @return The session.
     */
    public Session getSession() {
        return this.session;
    }

    /**
     * Ctor.
     * @param session The session to use.
     */
    public DocumentFactoryImpl(Session session) {
        this.session = session;
    }

    /**
     * Returns a document.
     * @param publication The publication.
     * @param area The area.
     * @param uuid The document UUID.
     * @param language The language.
     * @return A document.
     * @throws ResourceNotFoundException if an error occurs.
     */
    public Document get(Publication publication, String area, String uuid, String language)
            throws ResourceNotFoundException {
        return get(publication, area, uuid, language, -1);
    }

    public Document get(Publication publication, String area, String uuid, String language,
            int revision) throws ResourceNotFoundException {
        if (logger.isDebugEnabled())
            logger.debug(
                    "DocumentIdentityMap::get() called on publication [" + publication.getId()
                            + "], area [" + area + "], UUID [" + uuid + "], language [" + language
                            + "]");

        String key = getKey(publication, area, uuid, language, revision);

        if (logger.isDebugEnabled())
            logger.debug(
                    "DocumentIdentityMap::get() got key [" + key + "] from DocumentFactory");

        try {
            return (Document) getRepositorySession().getRepositoryItem(this, key);
        } catch (RepositoryException e) {
            throw new ResourceNotFoundException(e);
        }
    }

    protected org.apache.lenya.cms.repository.Session getRepositorySession() {
        SessionHolder holder = (SessionHolder) this.session;
        return holder.getRepositorySession();
    }

    /**
     * Returns the document identified by a certain web application URL.
     * @param webappUrl The web application URL.
     * @return A document.
     * @throws ResourceNotFoundException if an error occurs.
     */
    public Document getFromURL(String webappUrl) throws ResourceNotFoundException {
        String key = getKey(webappUrl);
        try {
            return (Document) getRepositorySession().getRepositoryItem(this, key);
        } catch (RepositoryException e) {
            throw new ResourceNotFoundException(e);
        }
    }

    /**
     * Builds a clone of a document for another language.
     * @param document The document to clone.
     * @param language The language of the target document.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
  //florent : seems never use, imply cyclic dependencies
    /*
    public Document getLanguageVersion(Document document, String language)
            throws DocumentBuildException {
        return get(document.getPublication(), document.getArea(), document.getUUID(), language);
    }*/

    /**
     * Builds a clone of a document for another area.
     * @param document The document to clone.
     * @param area The area of the target document.
     * @return A document.
     * @throws ResourceNotFoundException if an error occurs.
     */
    //florent : seems never use, imply cyclic dependencies
    /*
    public Document getAreaVersion(Document document, String area) throws ResourceNotFoundException {
        return get(document.getPublication(), area, document.getUUID(), document.getLanguage());
    }*/

    /**
     * Builds a document for the default language.
     * @param publication The publication.
     * @param area The area.
     * @param documentId The document ID.
     * @return A document.
     * @throws ResourceNotFoundException if an error occurs.
     */
    public Document get(Publication publication, String area, String documentId)
            throws ResourceNotFoundException {
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
     * @throws ResourceNotFoundException if an error occurs.
     */
    public boolean isDocument(String webappUrl) throws ResourceNotFoundException {
        Validate.notNull(webappUrl);
        try {
            //florent URLInformation info = new URLInformation(webappUrl);
        	URLInformation info = new URLInformation();
            String pubId = info.getPublicationId();
            String[] pubIds = getPublicationIds();
            if (pubId != null && Arrays.asList(pubIds).contains(pubId)) {
                Publication pub = getPublication(pubId);
                DocumentBuilder builder = pub.getDocumentBuilder();
                return builder.isDocument(this.session, webappUrl);
            } else {
                return false;
            }
        } catch (PublicationException e) {
            throw new ResourceNotFoundException(e);
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
    public String getKey(Publication publication, String area, String uuid, String language,
            int revision) {
        Validate.notNull(publication);
        Validate.notNull(area);
        Validate.notNull(uuid);
        Validate.notNull(language);
        return publication.getId() + ":" + area + ":" + uuid + ":" + language + ":" + revision;
    }

    /**
     * Builds a document key.
     * @param webappUrl The web application URL.
     * @return A key.
     */
    public String getKey(String webappUrl) {
        Validate.notNull(webappUrl);
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
            //florent URLInformation info = new URLInformation(webappUrl);
        	URLInformation info = new URLInformation();
            Publication publication = getPublication(info.getPublicationId());
            DocumentBuilder builder = publication.getDocumentBuilder();
            locator = builder.getLocator(this.session, webappUrl);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return locator;
    }

    /**
     * @see org.apache.lenya.transaction.IdentifiableFactory#build(org.apache.lenya.transaction.IdentityMap,
     *      java.lang.String)
     */
    //florent : public RepositoryItem buildItem(org.apache.lenya.cms.repository.Session session, String key) throws RepositoryException {
    public RepositoryItem buildItem(String key) throws RepositoryException {
        if (logger.isDebugEnabled())
            logger.debug("DocumentFactory::build() called with key [" + key + "]");

        StringTokenizer tokenizer = new StringTokenizer(key, ":");
        String publicationId = tokenizer.nextToken();
        String area = tokenizer.nextToken();
        String uuid = tokenizer.nextToken();
        String language = tokenizer.nextToken();
        String revisionString = tokenizer.nextToken();
        int revision = Integer.valueOf(revisionString).intValue();

        DocumentImpl document;
        try {
            Publication publication = getPublication(publicationId);
            DocumentBuilder builder = publication.getDocumentBuilder();
            DocumentIdentifier identifier = new DocumentIdentifierImpl(publicationId, area, uuid,
                    language);
            document = buildDocument(identifier, revision, builder);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
        if (logger.isDebugEnabled())
            logger.debug("DocumentFactory::build() done.");

        return document;
    }

    protected DocumentImpl buildDocument(DocumentIdentifier identifier, int revision,
            DocumentBuilder builder) throws DocumentBuildException {
        return createDocument(identifier, revision, builder);
    }

    /**
     * Creates a new document object. Override this method to create specific document objects,
     * e.g., for different document IDs.
     * @param identifier The identifier.
     * @param revision The revision or -1 for the latest revision.
     * @param builder The document builder.
     * @return A document.
     * @throws DocumentBuildException when something went wrong.
     */
    protected DocumentImpl createDocument(DocumentIdentifier identifier, int revision,
            DocumentBuilder builder) throws DocumentBuildException {
        DocumentImpl doc = new DocumentImpl(session, identifier, revision);
        doc.setMetaDataCache(getMetaDataCache());
        doc.setSourceResolver(getSourceResolver());
        doc.setNodeFactory(this.nodeFactory);
        doc.setResourceTypeResolver(this.resourceTypeResolver);
        return doc;
    }

    public Document get(DocumentIdentifier identifier) throws ResourceNotFoundException {
        try {
            Publication pub = getPublication(identifier.getPublicationId());
            return get(pub, identifier.getArea(), identifier.getUUID(), identifier.getLanguage());
        } catch (PublicationException e) {
            throw new ResourceNotFoundException(e);
        }
    }

    public String getItemType() {
        return Document.TRANSACTIONABLE_TYPE;
    }

    public Publication getPublication(String id) throws PublicationException {
        return getPublicationManager().getPublication(this, id);
    }

    public String[] getPublicationIds() {
        return getPublicationManager().getPublicationIds();
    }

    private PublicationManager pubManager;

    public void setPublicationManager(PublicationManager pubManager) {
        this.pubManager = pubManager;
    }

    public PublicationManager getPublicationManager() {
        return this.pubManager;
    }

    public boolean existsPublication(String id) {
        return Arrays.asList(getPublicationManager().getPublicationIds()).contains(id);
    }

    protected MetaDataCache getMetaDataCache() {
        return metaDataCache;
    }

    public void setMetaDataCache(MetaDataCache metaDataCache) {
        this.metaDataCache = metaDataCache;
    }

    public SourceResolver getSourceResolver() {
        return sourceResolver;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

    public void setNodeFactory(NodeFactory nodeFactory) {
        Validate.notNull(nodeFactory, "node factory");
        this.nodeFactory = nodeFactory;
    }

    public void setResourceTypeResolver(ResourceTypeResolver resourceTypeResolver) {
        this.resourceTypeResolver = resourceTypeResolver;
    }

}