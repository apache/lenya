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

/* $Id: DocumentHelper.java,v 1.8 2004/03/01 16:18:16 gregor Exp $  */

package org.apache.lenya.cms.publication;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.util.ServletHelper;

/**
 * Helper class to handle documents from XSP.
 */
public class DocumentHelper {

    private Map objectModel;

    /**
     * Ctor.
     * 
     * @param objectModel The Cocoon object model.
     */
    public DocumentHelper(Map objectModel) {
        this.objectModel = objectModel;
    }

    /**
     * Creates a document URL.<br/>
     * If the document ID is null, the current document ID is used.<br/>
     * If the document area is null, the current area is used.<br/>
     * If the language is null, the current language is used.
     * @param documentId The target document ID.
     * @param documentArea The target area.
     * @param language The target language.
     * @return A string.
     * @throws ProcessingException if something went wrong.
     */
    public String getDocumentUrl(String documentId, String documentArea, String language)
        throws ProcessingException {

        String url = null;
        try {
            PageEnvelope envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);

            if (documentId == null) {
                documentId = envelope.getDocument().getId();
            }

            Request request = ObjectModelHelper.getRequest(objectModel);

            if (documentArea == null) {
                String webappUrl = ServletHelper.getWebappURI(request);
                URLInformation info = new URLInformation(webappUrl);
                String completeArea = info.getCompleteArea();
                documentArea = completeArea;
            }

            if (language == null) {
                language = envelope.getDocument().getLanguage();
            }

            Publication publication = envelope.getPublication();
            DocumentBuilder builder = publication.getDocumentBuilder();

            url = builder.buildCanonicalUrl(publication, documentArea, documentId, language);

            String contextPath = request.getContextPath();
            if (contextPath == null) {
                contextPath = "";
            }

            url = contextPath + url;

        } catch (Exception e) {
            throw new ProcessingException(e);
        }

        return url;

    }

    /**
     * Returns the complete URL of the parent document. If the document is a top-level document,
     * the /index document is chosen. If the parent does not exist in the appropriate language, the
     * default language is chosen.
     * 
     * @return A string.
     * @throws ProcessingException when something went wrong.
     */
    public String getCompleteParentUrl() throws ProcessingException {

        PageEnvelope envelope;
        try {
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        } catch (PageEnvelopeException e) {
            throw new ProcessingException(e);
        }

        Document document = envelope.getDocument();
        Publication publication = envelope.getPublication();

        Request request = ObjectModelHelper.getRequest(objectModel);
        String webappUrl = ServletHelper.getWebappURI(request);
        URLInformation info = new URLInformation(webappUrl);
        String completeArea = info.getCompleteArea();
        DocumentBuilder builder = publication.getDocumentBuilder();

        String parentId;

        int lastSlashIndex = document.getId().lastIndexOf("/");
        if (lastSlashIndex > 0) {
            parentId = document.getId().substring(0, lastSlashIndex);
        } else {
            parentId = "/index";
        }

        String parentUrl = builder.buildCanonicalUrl(publication, completeArea, parentId);
        Document parentDocument;

        try {
            parentDocument = builder.buildDocument(publication, parentUrl);
            parentDocument = getExistingLanguageVersion(parentDocument, document.getLanguage());
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
        parentUrl =
            builder.buildCanonicalUrl(
                publication,
                completeArea,
                parentDocument.getId(),
                parentDocument.getLanguage());

        String contextPath = request.getContextPath();
        if (contextPath == null) {
            contextPath = "";
        }

        return contextPath + parentUrl;
    }

    /**
     * Returns an existing language version of a document. If the document exists in the default
     * language, the default language version is returned. Otherwise, a random language version is
     * returned. If no language version exists, a DocumentException is thrown.
     * 
     * @param document The document.
     * @return A document.
     * @throws DocumentException when an error occurs.
     */
    public static Document getExistingLanguageVersion(Document document) throws DocumentException {
        return getExistingLanguageVersion(document, document.getPublication().getDefaultLanguage());
    }
    /**
     * Returns an existing language version of a document. If the document exists in the preferred
     * language, this version is returned. Otherwise, if the document exists in the default
     * language, the default language version is returned. Otherwise, a random language version is
     * returned. If no language version exists, a DocumentException is thrown.
     * 
     * @param document The document.
     * @param preferredLanguage The preferred language.
     * @return A document.
     * @throws DocumentException when an error occurs.
     */
    public static Document getExistingLanguageVersion(Document document, String preferredLanguage)
        throws DocumentException {

        Publication publication = document.getPublication();
        DocumentBuilder builder = publication.getDocumentBuilder();

        String[] languages = document.getLanguages();

        if (languages.length == 0) {
            throw new DocumentException(
                "The document [" + document.getId() + "] does not exist in any language!");
        }

        List languageList = Arrays.asList(languages);

        String existingLanguage = null;

        if (languageList.contains(preferredLanguage)) {
            existingLanguage = preferredLanguage;
        } else if (languageList.contains(publication.getDefaultLanguage())) {
            existingLanguage = publication.getDefaultLanguage();
        } else {
            existingLanguage = languages[0];
        }

        document = builder.buildLanguageVersion(document, existingLanguage);

        return document;
    }

    /**
     * Returns the parent document of a document in the same language.
     * @param document The document.
     * @return A document or <code>null</code> if the document parameter is a top-level document.
     * @throws ProcessingException when the parent document could not be created.
     */
    public static Document getParentDocument(Document document) throws ProcessingException {

        Document parent = null;
        String id = document.getId();
        int lastSlashIndex = id.lastIndexOf("/");
        if (lastSlashIndex > 0) {
            String parentId = id.substring(0, lastSlashIndex);
            Publication publication = document.getPublication();
            DocumentBuilder builder = publication.getDocumentBuilder();
            String url =
                builder.buildCanonicalUrl(
                    publication,
                    document.getArea(),
                    parentId,
                    document.getLanguage());
            try {
                parent = builder.buildDocument(publication, url);
            } catch (DocumentBuildException e) {
                throw new ProcessingException(e);
            }
        }

        return parent;
    }

}
