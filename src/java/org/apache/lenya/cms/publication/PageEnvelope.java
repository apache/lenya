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

package org.apache.lenya.cms.publication;

import java.util.Map;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.rc.RCEnvironment;
import org.apache.lenya.util.ServletHelper;

/**
 * A page envelope carries a set of information that are needed during the presentation of a
 * document.
 */
public class PageEnvelope {
    public static final String PUBLICATION_ID = "publication-id";
    public static final String PUBLICATION = "publication";
    public static final String PUBLICATION_LANGUAGES_CSV = "publication-languages-csv";
    public static final String CONTEXT = "context-prefix";
    public static final String AREA = "area";
    public static final String DEFAULT_LANGUAGE = "default-language";
    public static final String DOCUMENT = "document";
    public static final String DOCUMENT_ID = "document-id";
    public static final String DOCUMENT_NAME = "document-name";
    public static final String DOCUMENT_TYPE = "document-type";
    public static final String DOCUMENT_LABEL = "document-label";
    public static final String DOCUMENT_URL = "document-url";
    public static final String DOCUMENT_URL_WITHOUT_LANGUAGE = "document-url-without-language";
    public static final String DOCUMENT_FILE = "document-file";
    public static final String DOCUMENT_PATH = "document-path";
    public static final String DOCUMENT_EXTENSION = "document-extension";
    public static final String DOCUMENT_LANGUAGE = "document-language";
    public static final String DOCUMENT_LANGUAGES = "document-languages";
    public static final String DOCUMENT_LANGUAGES_CSV = "document-languages-csv";

    public static final String DOCUMENT_LASTMODIFIED = "document-lastmodified";

    public static final String BREADCRUMB_PREFIX = "breadcrumb-prefix";

    public static final String SSL_PREFIX = "ssl-prefix";

    public static final String NAMESPACE = "http://apache.org/cocoon/lenya/page-envelope/1.0";
    public static final String DEFAULT_PREFIX = "lenya";

    private String context;

    /**
     * Constructor.
     */
    protected PageEnvelope() {
    }

    /**
     * Creates a page envelope from an object model.
     * 
     * @param map The identity map to use.
     * @param objectModel The object model.
     * @throws PageEnvelopeException when something went wrong.
     */
    public PageEnvelope(DocumentIdentityMap map, Map objectModel) throws PageEnvelopeException {
        this.identityMap = map;
        Request request = ObjectModelHelper.getRequest(objectModel);
        String webappURI;
        try {

            context = request.getContextPath();
            if (context == null) {
                context = "";
            }

            webappURI = ServletHelper.getWebappURI(request);
            Document document = map.getFactory().getFromURL(webappURI);
            setDocument(document);

        } catch (Exception e) {
            throw new PageEnvelopeException(e);
        }
    }
    
    private DocumentIdentityMap identityMap;
    
    /**
     * Returns the identity map.
     * @return An identity map.
     */
    public DocumentIdentityMap getIdentityMap() {
        return this.identityMap;
    }

    /**
     * Creates the message to report when creating the envelope failed.
     * 
     * @param request The request.
     * @return A string.
     */
    protected String createExceptionMessage(Request request) {
        return "Resolving page envelope failed:" + "\n  URI: " + request.getRequestURI()
                + "\n  Context: " + getContext() + "\n  Publication ID: "
                + getPublication().getId() + "\n  Area: " + document.getArea()
                + "\n  Document ID: " + document.getId();
    }

    /**
     * Returns the publication of this PageEnvelope.
     * 
     * @return a <code>Publication</code> value
     */
    public Publication getPublication() {
        return getDocument().getPublication();
    }

    /**
     * Returns the rcEnvironment.
     * 
     * @return a <code>RCEnvironment</code> value
     * @deprecated We should detach the RC environment from the page envelope.
     */
    public RCEnvironment getRCEnvironment() {
        return RCEnvironment.getInstance(getPublication().getServletContext().getAbsolutePath());
    }

    /**
     * Returns the context, e.g. "/lenya".
     * 
     * @return a <code>String</code> value
     */
    public String getContext() {
        return context;
    }

    /**
     * Returns the document-path.
     * 
     * @return a <code>File<code> value
     */
    public String getDocumentPath() {

        return getPublication().getPathMapper().getPath(getDocument().getId(),
                getDocument().getLanguage());
    }

    /**
     * The names of the page envelope parameters.
     */
    public static final String[] PARAMETER_NAMES = { PageEnvelope.AREA, PageEnvelope.CONTEXT,
            PageEnvelope.PUBLICATION_ID, PageEnvelope.PUBLICATION,
            PageEnvelope.PUBLICATION_LANGUAGES_CSV, PageEnvelope.DOCUMENT,
            PageEnvelope.DOCUMENT_ID, PageEnvelope.DOCUMENT_NAME,
            PageEnvelope.DOCUMENT_LABEL, PageEnvelope.DOCUMENT_URL,
            PageEnvelope.DOCUMENT_URL_WITHOUT_LANGUAGE, PageEnvelope.DOCUMENT_PATH,
            PageEnvelope.DOCUMENT_EXTENSION, PageEnvelope.DEFAULT_LANGUAGE,
            PageEnvelope.DOCUMENT_LANGUAGE, PageEnvelope.DOCUMENT_LANGUAGES,
            PageEnvelope.DOCUMENT_LANGUAGES_CSV, PageEnvelope.DOCUMENT_LASTMODIFIED,
            PageEnvelope.BREADCRUMB_PREFIX, PageEnvelope.SSL_PREFIX };

    /**
     * @param string The context.
     */
    protected void setContext(String string) {
        context = string;
    }

    private Document document;

    /**
     * Returns the document.
     * 
     * @return A document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Sets the document.
     * 
     * @param document A document.
     */
    public void setDocument(Document document) {
        this.document = document;
    }

}