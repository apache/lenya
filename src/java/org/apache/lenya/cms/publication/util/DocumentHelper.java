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

/* $Id$  */

package org.apache.lenya.cms.publication.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.util.ServletHelper;

/**
 * Helper class to handle documents from XSP.
 */
public class DocumentHelper {

    private Map objectModel;
    private DocumentIdentityMap identityMap;
    private Publication publication;

    /**
     * Ctor.
     * 
     * @param _objectModel The Cocoon object model.
     */
    public DocumentHelper(Map _objectModel) {
        this.objectModel = _objectModel;
        try {
            PublicationFactory factory = PublicationFactory.getInstance(new ConsoleLogger());
            this.publication = factory.getPublication(_objectModel);
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }
        this.identityMap = new DocumentIdentityMap();
    }

    /**
     * Creates a document URL. <br/>If the document ID is null, the current
     * document ID is used. <br/>If the document area is null, the current area
     * is used. <br/>If the language is null, the current language is used.
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
            PageEnvelope envelope = PageEnvelopeFactory.getInstance()
                    .getPageEnvelope(this.identityMap, this.objectModel);

            if (documentId == null) {
                documentId = envelope.getDocument().getId();
            }

            Request request = ObjectModelHelper.getRequest(this.objectModel);

            if (documentArea == null) {
                String webappUrl = ServletHelper.getWebappURI(request);
                URLInformation info = new URLInformation(webappUrl);
                String completeArea = info.getCompleteArea();
                documentArea = completeArea;
            }

            if (language == null) {
                language = envelope.getDocument().getLanguage();
            }

            Document document = this.identityMap.getFactory().get(this.publication,
                    documentArea,
                    documentId,
                    language);
            url = document.getCanonicalWebappURL();

            String contextPath = request.getContextPath();
            if (contextPath == null) {
                contextPath = "";
            }

            url = contextPath + url;
        } catch (final DocumentBuildException e) {
            throw new ProcessingException(e);
        } catch (final PageEnvelopeException e) {
            throw new ProcessingException(e);
        }

        return url;

    }

    /**
     * Returns the complete URL of the parent document. If the document is a
     * top-level document, the /index document is chosen. If the parent does not
     * exist in the appropriate language, the default language is chosen.
     * @return A string.
     * @throws ProcessingException when something went wrong.
     */
    public String getCompleteParentUrl() throws ProcessingException {

        String parentUrl;
        String contextPath;
        try {
            PageEnvelope envelope = PageEnvelopeFactory.getInstance()
                    .getPageEnvelope(this.identityMap, this.objectModel);
            Document document = envelope.getDocument();

            Request request = ObjectModelHelper.getRequest(this.objectModel);
            contextPath = request.getContextPath();

            Document parent = this.identityMap.getFactory().getParent(document, "/index");
            parentUrl = parent.getCanonicalWebappURL();
        } catch (final DocumentBuildException e) {
            throw new ProcessingException(e);
        } catch (final PageEnvelopeException e) {
            throw new ProcessingException(e);
        }

        if (contextPath == null) {
            contextPath = "";
        }

        return contextPath + parentUrl;
    }

    /**
     * Returns an existing language version of a document. If the document
     * exists in the default language, the default language version is returned.
     * Otherwise, a random language version is returned. If no language version
     * exists, a DocumentException is thrown.
     * 
     * @param document The document.
     * @return A document.
     * @throws DocumentException when an error occurs.
     */
    public static Document getExistingLanguageVersion(Document document) throws DocumentException {
        return getExistingLanguageVersion(document, document.getPublication().getDefaultLanguage());
    }

    /**
     * Returns an existing language version of a document. If the document
     * exists in the preferred language, this version is returned. Otherwise, if
     * the document exists in the default language, the default language version
     * is returned. Otherwise, a random language version is returned. If no
     * language version exists, a DocumentException is thrown.
     * 
     * @param document The document.
     * @param preferredLanguage The preferred language.
     * @return A document.
     * @throws DocumentException when an error occurs.
     */
    public static Document getExistingLanguageVersion(final Document document,
            String preferredLanguage) throws DocumentException {

        Publication publication = document.getPublication();

        String[] languages = document.getLanguages();

        if (languages.length == 0) {
            throw new DocumentException("The document [" + document.getId()
                    + "] does not exist in any language!");
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

        Document existingVersion = null;
        try {
            existingVersion = document.getIdentityMap().getFactory().getLanguageVersion(document,
                    existingLanguage);
        } catch (DocumentBuildException e) {
            throw new DocumentException(e);
        }

        return existingVersion;
    }

}