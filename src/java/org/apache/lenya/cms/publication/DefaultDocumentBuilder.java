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

/* $Id: DefaultDocumentBuilder.java,v 1.28 2004/03/01 16:18:17 gregor Exp $  */

package org.apache.lenya.cms.publication;

public class DefaultDocumentBuilder implements DocumentBuilder {
    /**
     * Non-public constructor.
     */
    protected DefaultDocumentBuilder() {
    }

    private static DefaultDocumentBuilder instance;

    /**
     * Returns the singleton instance.
     * 
     * @return A document builder.
     */
    public static DefaultDocumentBuilder getInstance() {
        if (instance == null) {
            instance = new DefaultDocumentBuilder();
        }

        return instance;
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentBuilder#buildDocument(org.apache.lenya.cms.publication.Publication,
     *      java.lang.String)
     */
    public Document buildDocument(Publication publication, String url)
        throws DocumentBuildException {

        URLInformation info = new URLInformation(url);

        String documentURL = info.getDocumentUrl();
        String originalURL = documentURL;

        String extension = getExtension(documentURL);
        documentURL = removeExtensions(documentURL);

        String language = getLanguage(documentURL);
        String fullLanguage = "".equals(language) ? "" : ("_" + language);
        documentURL = documentURL.substring(0, documentURL.length() - fullLanguage.length());

        if ("".equals(language)) {
            language = publication.getDefaultLanguage();
        }

        String documentId = documentURL;

        if (!documentId.startsWith("/")) {
            throw new DocumentBuildException(
                "Document ID [" + documentId + "] does not start with '/'!");
        }

        DefaultDocument document =
            createDocument(publication, info.getArea(), documentId, language);
        document.setExtension(extension);
        document.setDocumentURL(originalURL);

        return document;
    }

    /**
     * Creates a new document object.
     * Override this method to create specific document objects,
     * e.g., for different document IDs.
     * @param publication The publication.
     * @param area The area. 
     * @param documentId The document ID.
     * @param language The language.
     * @return A document.
     * @throws DocumentException when something went wrong.
     */
    protected DefaultDocument createDocument(
        Publication publication,
        String area,
        String documentId,
        String language)
        throws DocumentBuildException {
        DefaultDocument document = new DefaultDocument(publication, documentId, area, language);
        return document;
    }

    /**
     * Removes all "."-separated extensions from a URL (e.g., <code>/foo.print.html</code> is
     * transformed to <code>/foo</code>).
     * 
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
     * 
     * @param urlWithoutSuffix The URL without the suffix.
     * @return A string.
     */
    protected String getLanguage(String urlWithoutSuffix) {

        String suffix = "";
        String url = urlWithoutSuffix;
        if (url.length() >= 3 && url.charAt(url.length() - 3) == '_') {
            suffix = url.substring(url.length() - 2);
        }
        return suffix;
    }

    /**
     * Returns the extension of a URL.
     * 
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
     * @see org.apache.lenya.cms.publication.DocumentBuilder#isDocument(org.apache.lenya.cms.publication.Publication,
     *      java.lang.String)
     */
    public boolean isDocument(Publication publication, String url) throws DocumentBuildException {
        boolean isDocument = false;

        String publicationURI = url.substring(("/" + publication.getId()).length());
        if (publicationURI.startsWith("/")) {
            publicationURI = publicationURI.substring(1);

            int slashIndex = publicationURI.indexOf("/");
            if (slashIndex > -1) {
                String documentUri = publicationURI.substring(slashIndex);
                if (documentUri.startsWith("/")) {
                    isDocument = true;
                }
            }
        }

        return isDocument;
    }

    /**
     * Builds the canonical document URL.
     * 
     * @param publication The publication.
     * @param documentid The document ID.
     * @param language The language of the document.
     * @return A string.
     */
    protected String buildCanonicalDocumentUrl(
        Publication publication,
        String documentid,
        String language) {

        String languageSuffix = "";
        if (!language.equals(publication.getDefaultLanguage())) {
            languageSuffix = "_" + language;
        }

        String url = documentid + languageSuffix + ".html";
        return url;
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentBuilder#buildCanonicalUrl(org.apache.lenya.cms.publication.Publication,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public String buildCanonicalUrl(
        Publication publication,
        String area,
        String documentid,
        String language) {

        String documentUrl = buildCanonicalDocumentUrl(publication, documentid, language);
        String url = "/" + publication.getId() + "/" + area + documentUrl;

        return url;
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentBuilder#buildCanonicalUrl(org.apache.lenya.cms.publication.Publication,
     *      java.lang.String, java.lang.String)
     */
    public String buildCanonicalUrl(Publication publication, String area, String documentId) {
        return buildCanonicalUrl(publication, area, documentId, publication.getDefaultLanguage());
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentBuilder#buildLanguageVersion(org.apache.lenya.cms.publication.Document,
     *      java.lang.String)
     */
    public Document buildLanguageVersion(Document document, String language) {
        DefaultDocument newDocument =
            new DefaultDocument(
                document.getPublication(),
                document.getId(),
                document.getArea(),
                language);
        newDocument.setExtension(document.getExtension());
        String url =
            buildCanonicalDocumentUrl(document.getPublication(), document.getId(), language);
        newDocument.setDocumentURL(url);

        return newDocument;
    }

}
