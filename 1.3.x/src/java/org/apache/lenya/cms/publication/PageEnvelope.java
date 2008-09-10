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
import org.apache.lenya.util.Globals;
import org.apache.lenya.util.ServletHelper;
/**
 * A page envelope carries a set of information that are needed during the presentation of a document.
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
   public static final String DOCUMENT_NODE_ID = "document-node-id";
   public static final String DOCUMENT_LABEL = "document-label";
   public static final String DOCUMENT_URL = "document-url";
   public static final String DOCUMENT_URL_WITHOUT_LANGUAGE = "document-url-without-language";
   public static final String DOCUMENT_FILE = "document-file";
   public static final String DOCUMENT_PATH = "document-path";
   public static final String DOCUMENT_EXTENSION = "document-extension";
   public static final String DOCUMENT_LANGUAGE = "document-language";
   public static final String DOCUMENT_LANGUAGES = "document-languages";
   public static final String DOCUMENT_LANGUAGES_CSV = "document-languages-csv";
   /**
    * @deprecated Use {@link org.apache.lenya.cms.publication.Document#getDublinCore()} instead
    */
   public static final String DOCUMENT_DC_TITLE = "document-dc-title";
   /**
    * @deprecated Use {@link org.apache.lenya.cms.publication.Document#getDublinCore()} instead
    */
   public static final String DOCUMENT_DC_CREATOR = "document-dc-creator";
   /**
    * @deprecated Use {@link org.apache.lenya.cms.publication.Document#getDublinCore()} instead
    */
   public static final String DOCUMENT_DC_SUBJECT = "document-dc-subject";
   /**
    * @deprecated Use {@link org.apache.lenya.cms.publication.Document#getDublinCore()} instead
    */
   public static final String DOCUMENT_DC_PUBLISHER = "document-dc-publisher";
   /**
    * @deprecated Use {@link org.apache.lenya.cms.publication.Document#getDublinCore()} instead
    */
   public static final String DOCUMENT_DC_DATE_CREATED = "document-dc-date-created";
   /**
    * @deprecated Use {@link org.apache.lenya.cms.publication.Document#getDublinCore()} instead
    */
   public static final String DOCUMENT_DC_DESCRIPTION = "document-dc-description";
   /**
    * @deprecated Use {@link org.apache.lenya.cms.publication.Document#getDublinCore()} instead
    */
   public static final String DOCUMENT_DC_RIGHTS = "document-dc-rights";
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
    * Creates a new instance of PageEnvelope from a sitemap inside a publication.
    * 
    * @param publication
    *           The publication the page belongs to.
    * @param request
    *           The request that calls the page.
    * @exception PageEnvelopeException
    *               if an error occurs
    * @deprecated Performance problems. Use {@link PageEnvelopeFactory#getPageEnvelope(Map)} instead.
    */
   public PageEnvelope(Publication publication, Request request) throws PageEnvelopeException {
      init(publication, request);
   }
   /**
    * Creates a page envelope from an object model.
    * 
    * @param objectModel
    *           The object model.
    * @throws PageEnvelopeException
    *            when something went wrong.
    * @deprecated Performance problems. Use {@link PageEnvelopeFactory#getPageEnvelope(Map)} instead.
    */
   public PageEnvelope(Map objectModel) throws PageEnvelopeException {
      // try{
      // init(PublicationFactory.getPublication(objectModel), ObjectModelHelper.getRequest(objectModel));
      init(Globals.getPublication(), ObjectModelHelper.getRequest(objectModel));
      // }catch(PublicationException e){
      // throw new PageEnvelopeException(e);
      // }
   }
   /**
    * Creates a new instance of PageEnvelope from a sitemap inside a publication.
    * 
    * @param publication
    *           The publication the page belongs to.
    * @param request
    *           The request that calls the page.
    * @param createdByFactory
    *           A dummy parameter to allow creating an additional protected constructor that is not deprecated.
    * @exception PageEnvelopeException
    *               if an error occurs
    */
   public PageEnvelope(Publication publication, Request request, boolean createdByFactory) throws PageEnvelopeException {
      this(publication, request);
   }
   /**
    * Creates a page envelope from an object model.
    * 
    * @param objectModel
    *           The object model.
    * @param createdByFactory
    *           A dummy parameter to allow creating an additional protected constructor that is not deprecated.
    * @throws PageEnvelopeException
    *            when something went wrong.
    */
   protected PageEnvelope(Map objectModel, boolean createdByFactory) throws PageEnvelopeException {
      this(objectModel);
   }
   /**
    * Setup an instance of Publication.
    * 
    * Shared by multiple constructors.
    * 
    * @param publication
    *           The publication the page belongs to.
    * @param request
    *           The request that calls the page.
    * 
    * @throws PageEnvelopeException
    *            if an error occurs.
    */
   protected void init(Publication publication, Request request)
   // FIXME: this method is mainly needed because the deprecated
         // constructor PageEnvelope(Map objectModel) needs to handle an
         // exception in
         // one of the arguments to another constructor. That's why the
         // constructor
         // functionality is factored out into this method.
         // If the deprecated constructor PageEnvelope(Map objectModel) is
         // removed
         // this method might not be needed anymore and the functionality
         // could
         // be moved back to the constructor PageEnvelope(Publication
         // publication, Request request).
         throws PageEnvelopeException {
      // assert publication != null;
      // assert request != null;
      String webappURI;
      try{
         context = request.getContextPath();
         if(context == null){
            context = "";
         }
         webappURI = ServletHelper.getWebappURI(request);
         Document document = publication.getDocumentBuilder().buildDocument(publication, webappURI);
         setDocument(document);
      }catch(Exception e){
         throw new PageEnvelopeException(e);
      }
      // plausibility check
      /*
       * if (!webappURI .startsWith( "/" + getPublication().getId() + "/" + document.getArea() + document.getId())) { throw new PageEnvelopeException(createExceptionMessage(request)); }
       */
   }
   /**
    * Returns current PageEnvelope. Checks cached version in Request. Replaces PageEnvelopeFactory.
    * 
    * @since 1.3
    * @return PageEnvelope
    * @throws PageEnvelopeException
    */
   static public PageEnvelope getCurrent() throws PageEnvelopeException {
      // Check if already set.
      Request request = Globals.getRequest();
      if(null != request){
         Object o = request.getAttribute(PageEnvelope.class.getName());
         if(null != o){ return (PageEnvelope) o; }
      }
      // Otherwise create.
      PageEnvelope envelope = new PageEnvelope(Globals.getObjectModel(), true);
      if(null != request){
         // Store for future use.
         request.setAttribute(PageEnvelope.class.getName(), envelope);
      }
      return envelope;
   }
   /**
    * Creates the message to report when creating the envelope failed.
    * 
    * @param request
    *           The request.
    * @return A string.
    */
   protected String createExceptionMessage(Request request) {
      return "Resolving page envelope failed:" + "\n  URI: " + request.getRequestURI() + "\n  Context: " + getContext() + "\n  Publication ID: " + getPublication().getId() + "\n  Area: " + document.getArea() + "\n  Document ID: " + document.getId();
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
      return getPublication().getPathMapper().getPath(getDocument().getId(), getDocument().getLanguage());
   }
   /**
    * The names of the page envelope parameters.
    */
   public static final String[] PARAMETER_NAMES = {PageEnvelope.AREA, PageEnvelope.CONTEXT, PageEnvelope.PUBLICATION_ID, PageEnvelope.PUBLICATION, PageEnvelope.PUBLICATION_LANGUAGES_CSV, PageEnvelope.DOCUMENT, PageEnvelope.DOCUMENT_ID, PageEnvelope.DOCUMENT_NAME, PageEnvelope.DOCUMENT_NODE_ID, PageEnvelope.DOCUMENT_LABEL, PageEnvelope.DOCUMENT_URL, PageEnvelope.DOCUMENT_URL_WITHOUT_LANGUAGE, PageEnvelope.DOCUMENT_PATH, PageEnvelope.DOCUMENT_EXTENSION, PageEnvelope.DEFAULT_LANGUAGE, PageEnvelope.DOCUMENT_LANGUAGE, PageEnvelope.DOCUMENT_LANGUAGES, PageEnvelope.DOCUMENT_LANGUAGES_CSV, PageEnvelope.DOCUMENT_DC_TITLE, PageEnvelope.DOCUMENT_DC_CREATOR, PageEnvelope.DOCUMENT_DC_PUBLISHER, PageEnvelope.DOCUMENT_DC_SUBJECT, PageEnvelope.DOCUMENT_DC_DATE_CREATED, PageEnvelope.DOCUMENT_DC_DESCRIPTION, PageEnvelope.DOCUMENT_DC_RIGHTS, PageEnvelope.DOCUMENT_LASTMODIFIED, PageEnvelope.BREADCRUMB_PREFIX, PageEnvelope.SSL_PREFIX};
   /**
    * @param string
    *           The context.
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
    * @param document
    *           A document.
    */
   public void setDocument(Document document) {
      this.document = document;
   }
}
