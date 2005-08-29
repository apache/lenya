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

import java.io.File;

import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.rc.RCEnvironment;

/**
 * A page envelope carries a set of information that are needed during the presentation of a
 * document.
 */
public class PageEnvelope {
    /**
     * The names of the page envelope parameters.
     */
    public static final String[] PARAMETER_NAMES = { PageEnvelope.AREA, PageEnvelope.CONTEXT,
            PageEnvelope.PUBLICATION_ID, PageEnvelope.PUBLICATION,
            PageEnvelope.PUBLICATION_LANGUAGES_CSV, PageEnvelope.DOCUMENT,
            PageEnvelope.DOCUMENT_ID, PageEnvelope.DOCUMENT_NAME, PageEnvelope.DOCUMENT_LABEL,
            PageEnvelope.DOCUMENT_URL, PageEnvelope.DOCUMENT_URL_WITHOUT_LANGUAGE,
            PageEnvelope.DOCUMENT_PATH, PageEnvelope.DOCUMENT_EXTENSION,
            PageEnvelope.DOCUMENT_TYPE, PageEnvelope.DEFAULT_LANGUAGE,
            PageEnvelope.DOCUMENT_LANGUAGE, PageEnvelope.DOCUMENT_LANGUAGES,
            PageEnvelope.DOCUMENT_LANGUAGES_CSV, PageEnvelope.DOCUMENT_LASTMODIFIED,
            PageEnvelope.BREADCRUMB_PREFIX, PageEnvelope.SSL_PREFIX };
    /**
     * <code>PUBLICATION_ID</code> The publication id
     */
    public static final String PUBLICATION_ID = "publication-id";
    /**
     * <code>PUBLICATION</code> The publication
     */
    public static final String PUBLICATION = "publication";
    /**
     * <code>PUBLICATION_LANGUAGES_CSV</code> A list of the publication's languages,
     * comma-seperated
     */
    public static final String PUBLICATION_LANGUAGES_CSV = "publication-languages-csv";
    /**
     * <code>CONTEXT</code> The context prefix
     */
    public static final String CONTEXT = "context-prefix";
    /**
     * <code>AREA</code> The area
     */
    public static final String AREA = "area";
    /**
     * <code>DEFAULT_LANGUAGE</code> The default language of the publication
     */
    public static final String DEFAULT_LANGUAGE = "default-language";
    /**
     * <code>DOCUMENT</code> The current document
     */
    public static final String DOCUMENT = "document";
    /**
     * <code>DOCUMENT_ID</code> The document id of the current document
     */
    public static final String DOCUMENT_ID = "document-id";
    /**
     * <code>DOCUMENT_NAME</code> The name of the current document
     */
    public static final String DOCUMENT_NAME = "document-name";
    /**
     * <code>DOCUMENT_TYPE</code> The type of the current document
     */
    public static final String DOCUMENT_TYPE = "document-type";
    /**
     * <code>DOCUMENT_LABEL</code> The label of the current document
     */
    public static final String DOCUMENT_LABEL = "document-label";
    /**
     * <code>DOCUMENT_URL</code> The URL of the current document
     */
    public static final String DOCUMENT_URL = "document-url";
    /**
     * <code>DOCUMENT_URL_WITHOUT_LANGUAGE</code> The URL of the current document without a
     * language extension.
     */
    public static final String DOCUMENT_URL_WITHOUT_LANGUAGE = "document-url-without-language";
    /**
     * <code>DOCUMENT_FILE</code> The file of the current document
     */
    public static final String DOCUMENT_FILE = "document-file";
    /**
     * <code>DOCUMENT_PATH</code> The path of the current document
     */
    public static final String DOCUMENT_PATH = "document-path";
    /**
     * <code>DOCUMENT_EXTENSION</code> The extension of the current document
     */
    public static final String DOCUMENT_EXTENSION = "document-extension";
    /**
     * <code>DOCUMENT_LANGUAGE</code> The language of the current document
     */
    public static final String DOCUMENT_LANGUAGE = "document-language";
    /**
     * <code>DOCUMENT_LANGUAGES</code> The languages the current document is available in
     */
    public static final String DOCUMENT_LANGUAGES = "document-languages";
    /**
     * <code>DOCUMENT_LANGUAGES_CSV</code> The languages the current document is available in,
     * comma-seperated
     */
    public static final String DOCUMENT_LANGUAGES_CSV = "document-languages-csv";
    /**
     * <code>DOCUMENT_LASTMODIFIED</code> The last modified date of the current document
     */
    public static final String DOCUMENT_LASTMODIFIED = "document-lastmodified";
    /**
     * <code>BREADCRUMB_PREFIX</code> The breadcrumb prefix of the publication, used for
     * navigation
     */
    public static final String BREADCRUMB_PREFIX = "breadcrumb-prefix";
    /**
     * <code>SSL_PREFIX</code> The SSL prefix of the publication
     */
    public static final String SSL_PREFIX = "ssl-prefix";
    /**
     * <code>NAMESPACE</code> The page envelope namespace
     */
    public static final String NAMESPACE = "http://apache.org/cocoon/lenya/page-envelope/1.0";
    /**
     * <code>DEFAULT_PREFIX</code> The default prefix
     */
    public static final String DEFAULT_PREFIX = "lenya";

    private String context;
    private String area;
    private Publication publication;

    /**
     * Constructor.
     */
    protected PageEnvelope() {
        // do nothing
    }

    /**
     * Creates a page envelope from an object model.
     * @param map The identity map to use.
     * @param contextPath The servlet context prefix.
     * @param webappUrl The web application URL.
     * @param servletContext The servlet context directory.
     * @param publication The publication.
     * @throws PageEnvelopeException when something went wrong.
     */
    public PageEnvelope(DocumentIdentityMap map, String contextPath, String webappUrl,
            File servletContext, Publication publication) throws PageEnvelopeException {
        this.identityMap = map;
        this.context = contextPath;
        this.webappUrl = webappUrl;
        this.publication = publication;
    }

    private String webappUrl;

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
     * @param request The request.
     * @return A string.
     */
    protected String createExceptionMessage(Request request) {
        return "Resolving page envelope failed:" + "\n  URI: " + request.getRequestURI()
                + "\n  Context: " + getContext() + "\n  Publication ID: "
                + getPublication().getId() + "\n  Area: " + this.document.getArea()
                + "\n  Document ID: " + this.document.getId();
    }

    /**
     * Returns the publication of this PageEnvelope.
     * @return a <code>Publication</code> value
     */
    public Publication getPublication() {
        /*
        if (this.publication == null) {
            try {
                Publication pub = PublicationManagerImpl.getInstance(new ConsoleLogger())
                        .getPublication(this.webappUrl, this.servletContext);
                if (pub.exists()) {
                    this.publication = pub;
                    if (getIdentityMap().isDocument(this.webappUrl)) {
                        Document _document = getIdentityMap().getFromURL(this.webappUrl);
                        setDocument(_document);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        */
        return this.publication;
    }

    /**
     * @return The current area.
     */
    public String getArea() {
        if (this.area == null) {
            URLInformation info = new URLInformation(this.webappUrl);
            this.area = info.getArea();
        }
        return this.area;
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
     * @return a <code>String</code> value
     */
    public String getContext() {
        return this.context;
    }

    /**
     * Returns the document-path.
     * @return a <code>File<code> value
     */
    public String getDocumentPath() {
        return getPublication().getPathMapper().getPath(getDocument().getId(),
                getDocument().getLanguage());
    }

    /**
     * @param string The context.
     */
    protected void setContext(String string) {
        this.context = string;
    }

    private Document document;
    private boolean documentChecked = false;

    /**
     * Returns the document or <code>null</code> if the current URL does not represent a document.
     * @return A document
     */
    public Document getDocument() {
        if (!documentChecked) {
            try {
                documentChecked = true;
                if (getIdentityMap().isDocument(this.webappUrl)) {
                    this.document = getIdentityMap().getFromURL(this.webappUrl);
                }
            } catch (final DocumentBuildException e) {
                throw new RuntimeException(e);
            }
        }
        return this.document;
    }

    /**
     * Sets the document.
     * @param _document A document.
     */
    public void setDocument(Document _document) {
        this.document = _document;
    }

}