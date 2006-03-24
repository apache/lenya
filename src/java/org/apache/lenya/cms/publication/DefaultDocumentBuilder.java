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

package org.apache.lenya.cms.publication;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * Default document builder implementation.
 * 
 * @version $Id$
 */
public class DefaultDocumentBuilder extends AbstractLogEnabled implements DocumentBuilder,
        Serviceable, ThreadSafe {

    /**
     * Ctor.
     */
    public DefaultDocumentBuilder() {
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) {
        this.manager = manager;
    }

    protected ServiceManager manager;

    /**
     * @see org.apache.lenya.cms.publication.DocumentBuilder#buildDocument(org.apache.lenya.cms.publication.DocumentIdentityMap,
     *      org.apache.lenya.cms.publication.DocumentIdentifier)
     */
    public Document buildDocument(DocumentIdentityMap map, DocumentIdentifier identifier)
            throws DocumentBuildException {

        DefaultDocument document = createDocument(map, identifier);
        ContainerUtil.enableLogging(document, getLogger());
        /*
         * document.setExtension(extension); document.setDocumentURL(originalURL);
         */
        return document;
    }

    /**
     * Creates a new document object. Override this method to create specific document objects,
     * e.g., for different document IDs.
     * @param map The identity map.
     * @param identifier The identifier.
     * @return A document.
     * @throws DocumentBuildException when something went wrong.
     */
    protected DefaultDocument createDocument(DocumentIdentityMap map, DocumentIdentifier identifier)
            throws DocumentBuildException {
        DefaultDocument document = new DefaultDocument(this.manager, map, identifier, getLogger());
        document.setDocumentURL(buildCanonicalDocumentUrl(identifier));
        return document;
    }

    /**
     * Removes all "."-separated extensions from a URL (e.g., <code>/foo.print.html</code> is
     * transformed to <code>/foo</code>).
     * @param url The URL to trim.
     * @return A URL string.
     */
    protected String removeExtensions(String url) {
        int dotIndex = url.indexOf(".");
        if (dotIndex > -1) {
            url = url.substring(0, dotIndex);
        }
        return url;
    }

    /**
     * Returns the language of a URL.
     * @param urlWithoutSuffix The URL without the suffix.
     * @return A string.
     */
    protected String getLanguage(String urlWithoutSuffix) {

        String language = "";
        String url = urlWithoutSuffix;

        int languageSeparatorIndex = url.lastIndexOf("_");
        if (languageSeparatorIndex > -1) {
            String suffix = url.substring(languageSeparatorIndex + 1);
            if (suffix.length() <= 5) {
                language = suffix;
            }
        }
        return language;
    }

    /**
     * Returns the extension of a URL.
     * @param url The URL.
     * @return The extension.
     */
    protected String getExtension(String url) {
        int startOfSuffix = url.lastIndexOf('.');
        String suffix = "";

        if ((startOfSuffix > -1) && !url.endsWith(".")) {
            suffix = url.substring(startOfSuffix + 1);
        }

        return suffix;
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentBuilder#isDocument(java.lang.String)
     */
    public boolean isDocument(String url) throws DocumentBuildException {
        boolean isDocument = false;

        URLInformation info = new URLInformation(url);
        String area = info.getArea();
        if (area != null && PublicationImpl.isValidArea(area)) {
            String documentUrl = info.getDocumentUrl();
            if (documentUrl != null && documentUrl.startsWith("/") && documentUrl.length() > 1) {
                isDocument = true;
            }
        }

        return isDocument;
    }

    /**
     * Builds the canonical document URL.
     * @param identifier The identifier.
     * @return A string.
     */
    protected String buildCanonicalDocumentUrl(DocumentIdentifier identifier) {

        String languageSuffix = "";
        String language = identifier.getLanguage();
        if (!language.equals(identifier.getPublication().getDefaultLanguage())) {
            languageSuffix = "_" + language;
        }

        String url = identifier.getId() + languageSuffix + ".html";
        return url;
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentBuilder#buildCanonicalUrl(org.apache.lenya.cms.publication.DocumentIdentifier)
     */
    public String buildCanonicalUrl(DocumentIdentifier identifier) {

        String documentUrl = buildCanonicalDocumentUrl(identifier);
        String url = "/" + identifier.getPublication().getId() + "/" + identifier.getArea()
                + documentUrl;

        return url;
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentBuilder#getIdentitfier(java.lang.String)
     */
    public DocumentIdentifier getIdentitfier(String webappUrl) throws DocumentBuildException {

        if (!isDocument(webappUrl)) {
            throw new DocumentBuildException("The webapp URL [" + webappUrl
                    + "] does not refer to a document!");
        }

        URLInformation info = new URLInformation(webappUrl);

        Publication publication;
        try {
            publication = PublicationUtil.getPublicationFromUrl(this.manager, webappUrl);
        } catch (PublicationException e) {
            throw new DocumentBuildException(e);
        }

        String documentURL = info.getDocumentUrl();

        documentURL = removeExtensions(documentURL);

        String language = getLanguage(documentURL);
        String fullLanguage = "".equals(language) ? "" : ("_" + language);
        documentURL = documentURL.substring(0, documentURL.length() - fullLanguage.length());

        if ("".equals(language)) {
            language = publication.getDefaultLanguage();
        }

        String documentId = documentURL;

        if (!documentId.startsWith("/")) {
            throw new DocumentBuildException("Document ID [" + documentId
                    + "] does not start with '/'!");
        }

        DocumentIdentifier identifier = new DocumentIdentifier(publication,
                info.getArea(),
                documentId,
                language);
        return identifier;
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentBuilder#isValidDocumentName(java.lang.String)
     */
    public boolean isValidDocumentName(String documentName) {
        return documentName.matches("[a-zA-Z0-9\\-]+");
    }

}
