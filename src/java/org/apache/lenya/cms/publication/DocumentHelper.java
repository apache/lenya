/*
 * $Id: DocumentHelper.java,v 1.3 2003/11/06 10:15:09 andreas Exp $ <License>
 * 
 * ============================================================================ The Apache Software
 * License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica- tion, are permitted
 * provided that the following conditions are met: 1. Redistributions of source code must retain
 * the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)." Alternately, this acknowledgment may
 * appear in the software itself, if and wherever such third-party acknowledgments normally appear. 4.
 * The names "Apache Lenya" and "Apache Software Foundation" must not be used to endorse or promote
 * products derived from this software without prior written permission. For written permission,
 * please contact apache@apache.org. 5. Products derived from this software may not be called
 * "Apache", nor may "Apache" appear in their name, without prior written permission of the Apache
 * Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU- DING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation and was originally created by Michael Wechner <michi@apache.org> .
 * For more information on the Apache Soft- ware Foundation, please see <http://www.apache.org/> .
 * 
 * Lenya includes software developed by the Apache Software Foundation, W3C, DOM4J Project,
 * BitfluxEditor, Xopus, and WebSHPINX. </License>
 */
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
 * 
 * @author andreas
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
     * @return A document or <code>null</code> if the document parameter is the root document.
     * @throws DocumentBuildException when the parent document could not be created.
     */
    public static Document getParentDocument(Document document) throws DocumentBuildException {

        Document parent = null;

        int lastSlashIndex = document.getId().lastIndexOf("/");
        if (lastSlashIndex > 0) {
            String parentId = document.getId().substring(0, lastSlashIndex);
            Publication publication = document.getPublication();
            DocumentBuilder builder = publication.getDocumentBuilder();
            String parentUrl =
                builder.buildCanonicalUrl(
                    publication,
                    document.getArea(),
                    parentId,
                    document.getLanguage());
            parent = builder.buildDocument(publication, parentUrl);
        }

        return parent;
    }

}
