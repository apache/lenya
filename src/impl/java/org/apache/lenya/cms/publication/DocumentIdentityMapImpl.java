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
package org.apache.lenya.cms.publication;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.repository.SessionImpl;
import org.apache.lenya.transaction.Identifiable;
import org.apache.lenya.transaction.IdentifiableFactory;
import org.apache.lenya.transaction.IdentityMap;

/**
 * A DocumentIdentityMap avoids the multiple instanciation of a document object.
 * 
 * @version $Id: DocumentIdentityMap.java 264153 2005-08-29 15:11:14Z andreas $
 */
public class DocumentIdentityMapImpl extends AbstractLogEnabled implements DocumentIdentityMap {

    private IdentityMap map;
    protected ServiceManager manager;

    /**
     * @return The identity map.
     */
    public IdentityMap getIdentityMap() {
        return this.map;
    }

    /**
     * Ctor.
     * @param session The session to use.
     * @param manager The service manager.
     * @param logger The logger to use.
     */
    public DocumentIdentityMapImpl(Session session, ServiceManager manager, Logger logger) {
        this.map = ((SessionImpl) session).getUnitOfWork().getIdentityMap();
        this.manager = manager;
        ContainerUtil.enableLogging(this, logger);
    }

    /**
     * Returns a document.
     * @param publication The publication.
     * @param area The area.
     * @param documentId The document ID.
     * @param language The language.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document get(Publication publication, String area, String documentId, String language)
            throws DocumentBuildException {

        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentIdentityMap::get() called on publication ["
                    + publication.getId() + "], area [" + area + "], documentId [" + documentId
                    + "], language [" + language + "]");

        String key = getKey(publication, area, documentId, language);

        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentIdentityMap::get() got key [" + key
                    + "] from DocumentFactory");

        return (Document) getIdentityMap().get(this, key);
    }

    /**
     * Returns the document identified by a certain web application URL.
     * @param webappUrl The web application URL.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getFromURL(String webappUrl) throws DocumentBuildException {
        String key = getKey(webappUrl);
        return (Document) getIdentityMap().get(this, key);
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
        return get(document.getPublication(), document.getArea(), document.getId(), language);
    }

    /**
     * Builds a clone of a document for another area.
     * @param document The document to clone.
     * @param area The area of the target document.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getAreaVersion(Document document, String area) throws DocumentBuildException {
        return get(document.getPublication(), area, document.getId(), document.getLanguage());
    }

    /**
     * Returns the parent of a document.
     * @param document A document.
     * @return A document or <code>null</code> if the document has no parent.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getParent(Document document) throws DocumentBuildException {
        Document parent = null;
        int lastSlashIndex = document.getId().lastIndexOf("/");
        if (lastSlashIndex > 0) {
            String parentId = document.getId().substring(0, lastSlashIndex);
            parent = get(document.getPublication(),
                    document.getArea(),
                    parentId,
                    document.getLanguage());
        }
        return parent;
    }

    /**
     * Returns the parent of a document.
     * @param document A document.
     * @param defaultDocumentId The document ID to use if the document has no parent.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getParent(Document document, String defaultDocumentId)
            throws DocumentBuildException {
        Document parent = getParent(document);
        if (parent == null) {
            parent = get(document.getPublication(),
                    document.getArea(),
                    defaultDocumentId,
                    document.getLanguage());
        }
        return parent;
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
            Publication publication = PublicationUtil.getPublicationFromUrl(this.manager, webappUrl);
            if (publication.exists()) {

                ServiceSelector selector = null;
                DocumentBuilder builder = null;
                try {
                    selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE
                            + "Selector");
                    builder = (DocumentBuilder) selector.select(publication.getDocumentBuilderHint());
                    return builder.isDocument(webappUrl);
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
     * @param documentId The document ID.
     * @param language The language.
     * @return A key.
     */
    public String getKey(Publication publication, String area, String documentId, String language) {
        return publication.getId() + ":" + area + ":" + documentId + ":" + language;
    }

    /**
     * Builds a document key.
     * @param webappUrl The web application URL.
     * @return A key.
     */
    public String getKey(String webappUrl) {
        ServiceSelector selector = null;
        DocumentBuilder builder = null;
        DocumentIdentifier identifier;
        try {
            Publication publication = PublicationUtil.getPublicationFromUrl(this.manager, webappUrl);
            selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE + "Selector");
            builder = (DocumentBuilder) selector.select(publication.getDocumentBuilderHint());
            identifier = builder.getIdentitfier(webappUrl);
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
        return getKey(identifier.getPublication(),
                identifier.getArea(),
                identifier.getId(),
                identifier.getLanguage());
    }

    /**
     * @see org.apache.lenya.transaction.IdentifiableFactory#build(org.apache.lenya.transaction.IdentityMap,
     *      java.lang.String)
     */
    public Identifiable build(IdentityMap map, String key) throws Exception {

        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentFactory::build() called with key [" + key + "]");

        String[] snippets = key.split(":");
        String publicationId = snippets[0];
        String area = snippets[1];
        String documentId = snippets[2];
        String language = snippets[3];

        ServiceSelector selector = null;
        DocumentBuilder builder = null;
        Document document;
        try {

            Publication publication = PublicationUtil.getPublication(this.manager, publicationId);

            selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE + "Selector");
            builder = (DocumentBuilder) selector.select(publication.getDocumentBuilderHint());
            DocumentIdentifier identifier = new DocumentIdentifier(publication,
                    area,
                    documentId,
                    language);
            document = buildDocument(this, identifier, builder);
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

    /**
     * *
     * @see org.apache.lenya.transaction.IdentifiableFactory#getType()
     */
    public String getType() {
        return Document.TRANSACTIONABLE_TYPE;
    }

    public Object buildObject(Object factory, String key) throws Exception {
        return getIdentityMap().get((IdentifiableFactory) factory, key);
    }

    protected Document buildDocument(DocumentIdentityMap map, DocumentIdentifier identifier,
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
    protected DocumentImpl createDocument(DocumentIdentityMap map,
            DocumentIdentifier identifier, DocumentBuilder builder) throws DocumentBuildException {
        DocumentImpl document = new DocumentImpl(this.manager, map, identifier, getLogger());
        final String canonicalUrl = builder.buildCanonicalUrl(identifier);
        final String prefix = "/" + identifier.getPublication().getId() + "/"
                + identifier.getArea();
        final String canonicalDocumentUrl = canonicalUrl.substring(prefix.length());
        document.setDocumentURL(canonicalDocumentUrl);
        return document;
    }

    public Document get(DocumentIdentifier identifier) throws DocumentBuildException {
        return get(identifier.getPublication(),
                identifier.getArea(),
                identifier.getId(),
                identifier.getLanguage());
    }

}
