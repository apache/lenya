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
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.transaction.IdentityMapImpl;
import org.apache.lenya.transaction.IdentifiableFactory;

/**
 * A DocumentIdentityMap avoids the multiple instanciation of a document object.
 * 
 * @version $Id$
 */
public class DocumentIdentityMap extends IdentityMapImpl {

    /**
     * Ctor.
     * @param manager The service manager.
     * @param logger The logger to use.
     */
    public DocumentIdentityMap(ServiceManager manager, Logger logger) {
        this.manager = manager;
        enableLogging(logger);
    }

    /**
     * @see org.apache.lenya.transaction.IdentityMap#getFactory(java.lang.String)
     */
    public IdentifiableFactory getFactory(String type) {
        IdentifiableFactory factory = super.getFactory(type);
        if (factory == null && type.equals(Document.TRANSACTIONABLE_TYPE)) {
            factory = new DocumentFactory(this.manager);
            ContainerUtil.enableLogging(factory, getLogger());
            setFactory(type, factory);
        }
        return factory;
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
            getLogger().debug("DocumentIdentityMap::get() called on publication [" + publication.getId() + "], area [" + area + "], documentId [" + documentId + "], language [" + language + "]");

        DocumentFactory factory = (DocumentFactory) getFactory(Document.TRANSACTIONABLE_TYPE);
        String key = factory.getKey(publication, area, documentId, language);

        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentIdentityMap::get() got key [" + key + "] from DocumentFactory");

        return (Document) get(Document.TRANSACTIONABLE_TYPE, key);
    }

    /**
     * Returns the document identified by a certain web application URL.
     * @param webappUrl The web application URL.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getFromURL(String webappUrl) throws DocumentBuildException {

        DocumentFactory factory = (DocumentFactory) getFactory(Document.TRANSACTIONABLE_TYPE);
        String key = factory.getKey(this, webappUrl);
        return (Document) get(Document.TRANSACTIONABLE_TYPE, key);
    }

    protected ServiceManager manager;

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
            parent = get(document.getPublication(), document.getArea(), parentId, document
                    .getLanguage());
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
            parent = get(document.getPublication(), document.getArea(), defaultDocumentId, document
                    .getLanguage());
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
            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            Publication publication = factory.getPublication(this.manager, webappUrl);
            if (publication.exists()) {

                ServiceSelector selector = null;
                DocumentBuilder builder = null;
                try {
                    selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE
                            + "Selector");
                    builder = (DocumentBuilder) selector.select(publication
                            .getDocumentBuilderHint());
                    return builder.isDocument(publication, webappUrl);
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
}
