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

import java.util.HashMap;
import java.util.Map;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Publication;

/**
 * A DocumentIdentityMap avoids the multiple instanciation of a document object.
 * 
 * @version $Id:$
 */
public class DocumentIdentityMap {

    private Publication publication;
    private Map key2document = new HashMap();

    /**
     * Ctor.
     * @param publication The publication to use.
     */
    public DocumentIdentityMap(Publication publication) {
        this.publication = publication;
    }

    /**
     * Returns the publication.
     * @return A publication.
     */
    public Publication getPublication() {
        return publication;
    }

    /**
     * Returns a document.
     * @param area The area.
     * @param documentId The document ID.
     * @param language The language.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document get(String area, String documentId, String language)
            throws DocumentBuildException {
        String key = getKey(area, documentId, language);
        Document document = (Document) key2document.get(key);
        if (document == null) {
            DocumentBuilder builder = getPublication().getDocumentBuilder();
            String url = builder.buildCanonicalUrl(getPublication(), area, documentId, language);
            document = builder.buildDocument(this, url);
            key2document.put(key, document);
        }
        return document;
    }

    /**
     * Returns a document.
     * @param area The area.
     * @param documentId The document ID.
     * @return A document.
     * @throws DocumentBuildException If an error occurs.
     */
    public Document get(String area, String documentId) throws DocumentBuildException {
        return get(area, documentId, getPublication().getDefaultLanguage());
    }

    /**
     * Returns the document identified by a certain web application URL.
     * @param webappUrl The web application URL.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document get(String webappUrl) throws DocumentBuildException {
        DocumentBuilder builder = getPublication().getDocumentBuilder();
        if (!builder.isDocument(getPublication(), webappUrl)) {
            throw new DocumentBuildException("The webapp URL [" + webappUrl
                    + "] does not identify a valid document");
        }

        Document document = builder.buildDocument(this, webappUrl);
        String key = getKey(document.getArea(), document.getId(), document.getLanguage());

        Document resultDocument;
        if (key2document.containsKey(key)) {
            resultDocument = (Document) key2document.get(key);
        } else {
            resultDocument = document;
            key2document.put(key, resultDocument);
        }
        return resultDocument;
    }
    
    /**
     * Calculates a map key.
     * @param area The area.
     * @param documentId The document ID.
     * @param language The language.
     * @return A string.
     */
    protected String getKey(String area, String documentId, String language) {
        return area + ":" + documentId + ":" + language;
    }

}