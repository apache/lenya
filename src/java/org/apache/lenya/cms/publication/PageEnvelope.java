/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

import org.apache.avalon.framework.logger.ConsoleLogger;
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
            PageEnvelope.PUBLICATION_ID, PageEnvelope.PUBLICATION, PageEnvelope.IS_PUBLICATION,
            PageEnvelope.PUBLICATION_LANGUAGES_CSV, PageEnvelope.DOCUMENT,
            PageEnvelope.DOCUMENT_ID, PageEnvelope.DOCUMENT_PARENT, PageEnvelope.DOCUMENT_NAME,
            PageEnvelope.DOCUMENT_LABEL, PageEnvelope.DOCUMENT_URL,
            PageEnvelope.DOCUMENT_URL_WITHOUT_LANGUAGE, PageEnvelope.DOCUMENT_PATH,
            PageEnvelope.DOCUMENT_EXTENSION, PageEnvelope.DOCUMENT_SOURCE_EXTENSION,
            PageEnvelope.DOCUMENT_UUID, PageEnvelope.DOCUMENT_TYPE, PageEnvelope.DEFAULT_LANGUAGE,
            PageEnvelope.DOCUMENT_LANGUAGE, PageEnvelope.DOCUMENT_LANGUAGES,
            PageEnvelope.DOCUMENT_LANGUAGES_CSV, PageEnvelope.DOCUMENT_LASTMODIFIED,
            PageEnvelope.BREADCRUMB_PREFIX, PageEnvelope.SSL_PREFIX, PageEnvelope.LANGUAGE,
            PageEnvelope.DOCUMENT_MIME_TYPE };
    /**
     * <code>PUBLICATION_ID</code> The publication id
     */
    public static final String PUBLICATION_ID = "publication-id";
    /**
     * <code>PUBLICATION</code> The publication
     */
    public static final String PUBLICATION = "publication";
    /**
     * <code>IS_PUBLICATION</code> If the current URL refers to a publication ("true" or "false").
     */
    public static final String IS_PUBLICATION = "is-publication";
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
     * <code>DOCUMENT_PARENT</code> The document id of the parent document
     */
    public static final String DOCUMENT_PARENT = "document-parent";
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
     * @deprecated Reference a document via the lenya-document: URI instead.
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
     * <code>DOCUMENT_SOURCE_EXTENSION</code> The source extension of the current document
     */
    public static final String DOCUMENT_SOURCE_EXTENSION = "document-source-extension";
    /**
     * <code>DOCUMENT_MIME_TYPE</code> The MIME type of the current document
     */
    public static final String DOCUMENT_MIME_TYPE = "document-mime-type";
    /**
     * <code>DOCUMENT_UUID</code> The uuid of the current document
     */
    public static final String DOCUMENT_UUID = "document-uuid";
    /**
     * <code>DOCUMENT_LANGUAGE</code> The language of the current document
     */
    public static final String DOCUMENT_LANGUAGE = "document-language";
    /**
     * This attribute returns the document language if the document exists, or the
     * default language otherwise. This makes it suitable for 404 pages.
     */
    public static final String LANGUAGE = "language";
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
    public PageEnvelope(DocumentFactory map, String contextPath,
            String webappUrl, File servletContext, Publication publication)
            throws PageEnvelopeException {
        this.factory = map;
        this.context = contextPath;
        this.webappUrl = webappUrl;
        this.publication = publication;
    }

    private String webappUrl;

    private DocumentFactory factory;

    /**
     * Returns the document factory.
     * @return A document factory.
     */
    public DocumentFactory getDocumentFactory() {
        return this.factory;
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
                + "\n  Document UUID: " + this.document.getUUID();
    }

    /**
     * Returns the publication of this PageEnvelope.
     * @return a <code>Publication</code> value
     */
    public Publication getPublication() {
        /*
         * if (this.publication == null) { try { Publication pub =
         * PublicationManagerImpl.getInstance(new ConsoleLogger()) .getPublication(this.webappUrl,
         * this.servletContext); if (pub.exists()) { this.publication = pub; if
         * (getIdentityMap().isDocument(this.webappUrl)) { Document _document =
         * getIdentityMap().getFromURL(this.webappUrl); setDocument(_document); } } } catch
         * (Exception e) { throw new RuntimeException(e); } }
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
        return RCEnvironment.getInstance(getPublication().getServletContext().getAbsolutePath(),
                new ConsoleLogger());
    }

    /**
     * Returns the context, e.g. "/lenya".
     * @return a <code>String</code> value
     */
    public String getContext() {
        return this.context;
    }
    
    private String path;

    /**
     * Returns the document path. If the current URL doesn't point to a document, the document
     * builder is used to extract the supposed path from the URL.
     * @return A string.
     */
    public String getDocumentPath() {
        if (this.path == null) {
            final Document doc = getDocument();
            try {
                this.path = doc != null ? doc.getPath() :
                    getPublication().getDocumentBuilder().getLocator(this.factory, this.webappUrl).getPath();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
        return this.path;
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
                if (getDocumentFactory().isDocument(this.webappUrl)) {
                    this.document = getDocumentFactory().getFromURL(this.webappUrl);
                }
            } catch (final Exception e) {
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
    
    /**
     * @return The document language or the default language if the document doesn't exist.
     * @see #LANGUAGE
     */
    public String getLanguage() {
        Document document = getDocument();
        if (document == null) {
            Publication pub = getPublication();
            if (pub == null) {
                throw new RuntimeException("The language attribute can't be used outside a publication.");
            }
            return pub.getDefaultLanguage();
        } else {
            return document.getLanguage();
        }
    }

}
