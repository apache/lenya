/*
$Id: PageEnvelope.java,v 1.29 2003/07/15 15:03:40 egli Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.publication;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;

import org.apache.lenya.cms.rc.RCEnvironment;

import org.apache.log4j.Category;

import java.io.File;

import java.util.Map;

/**
 * A page envelope carries a set of information that are needed
 * during the presentation of a document.
 *
 * @author <a href="mailto:andreas.hartmann@wyona.org">Andreas Hartmann</a>
 */
public class PageEnvelope {
    private static Category log = Category.getInstance(PageEnvelope.class);
    public static final String PUBLICATION_ID = "publication-id";
    public static final String PUBLICATION = "publication";
    public static final String CONTEXT = "context-prefix";
    public static final String AREA = "area";
    public static final String DOCUMENT_ID = "document-id";
    public static final String DOCUMENT_URL = "document-url";
    public static final String DOCUMENT_PATH = "document-path";
    public static final String DOCUMENT_LANGUAGE = "document-language";
    private String context;

    /**
     * Constructor.
     */
    protected PageEnvelope() {}

    /**
     * Creates a new instance of PageEnvelope from a sitemap inside a publication.
     * @param publication The publication the page belongs to.
     * @param request The request that calls the page.
     * @exception PageEnvelopeException if an error occurs
     * @deprecated Performance problems. Use {@link PageEnvelopeFactory#getPageEnvelope(Map)} instead.
     */
    public PageEnvelope(Publication publication, Request request)
        throws PageEnvelopeException {
        init(publication, request);
    }

    /**
     * Creates a page envelope from an object model.
     * @param objectModel The object model.
     * @throws PageEnvelopeException when something went wrong.
     * @deprecated Performance problems. Use {@link PageEnvelopeFactory#getPageEnvelope(Map)} instead.
     */
    public PageEnvelope(Map objectModel) throws PageEnvelopeException {
        try {
            init(
                PublicationFactory.getPublication(objectModel),
                ObjectModelHelper.getRequest(objectModel));
        } catch (PublicationException e) {
            throw new PageEnvelopeException(e);
        }
    }

    /**
     * Creates a page envelope from an object model.
     * @param objectModel The object model.
     * @param createdByFactory A dummy parameter to allow creating an additional
     * protected constructor that is not deprecated.
     * @throws PageEnvelopeException when something went wrong.
     */
    protected PageEnvelope(Map objectModel, boolean createdByFactory)
        throws PageEnvelopeException {
        this(objectModel);
    }

	/**
	 * Setup an instance of Publication.
	 * 
	 * Shared by multiple constructors.
	 * 
	 * @param publication The publication the page belongs to.
	 * @param request The request that calls the page.
	 * 
	 * @throws PageEnvelopeException if an error occurs.
	 */
    protected void init(Publication publication, Request request)
    	// FIXME: this method is mainly needed because the deprecated 
    	// constructor PageEnvelope(Map objectModel) needs to handle an exception in
    	// one of the arguments to another constructor. That's why the constructor
    	// functionality is factored out into this method.
    	// If the deprecated constructor PageEnvelope(Map objectModel) is removed
    	// this method might not be needed anymore and the functionality could
    	// be moved back to the constructor PageEnvelope(Publication publication, Request request).
        throws PageEnvelopeException {
        assert publication != null;
        assert request != null;

        try {
            String requestURI = request.getRequestURI();
            context = request.getContextPath();

            if (context == null) {
                context = "";
            }

            String webappURI = requestURI.substring(context.length());
            Document document =
                DefaultDocumentBuilder.getInstance().buildDocument(
                    publication,
                    webappURI);
            setDocument(document);

        } catch (Exception e) {
            throw new PageEnvelopeException(e);
        }

        // plausibility check
        if (!request
            .getRequestURI()
            .startsWith(
                getContext()
                    + "/"
                    + getPublication().getId()
                    + "/"
                    + getArea()
                    + getDocumentId())) {
            throw new PageEnvelopeException(createExceptionMessage(request));
        }
    }

    /**
     * Creates the message to report when creating the envelope failed.
     * @param request The request.
     * @return A string.
     */
    protected String createExceptionMessage(Request request) {
        return "Resolving page envelope failed:"
            + "\n  URI: "
            + request.getRequestURI()
            + "\n  Context: "
            + getContext()
            + "\n  Publication ID: "
            + getPublication().getId()
            + "\n  Area: "
            + getArea()
            + "\n  Document ID: "
            + getDocumentId();
    }

    /**
     * Returns the publication of this PageEnvelope.
     * @return a <code>Publication</code> value
     */
    public Publication getPublication() {
        return getDocument().getPublication();
    }

    /**
     * Returns the rcEnvironment.
     * @return a <code>RCEnvironment</code> value
     * @deprecated We should detach the RC environment from the page envelope.
     */
    public RCEnvironment getRCEnvironment() {
        return RCEnvironment.getInstance(
            getPublication().getServletContext().getAbsolutePath());
    }

    /**
     * Returns the context.
     * @return a <code>String</code> value
     */
    public String getContext() {
        return context;
    }

    /**
     * Returns the area (authoring/live).
     * @return a <code>String</code> value
     */
    public String getArea() {
        return getDocument().getArea();
    }

    /**
     * Returns the document-id.
     * @return a <code>String</code> value
     */
    public String getDocumentId() {
        return getDocument().getId();
    }

    /**
     * Returns the document URL (without prefix and area).
     * @return a <code>String</code> value
     */
    public String getDocumentURL() {
        return getDocument().getDocumentUrl();
    }

    /**
     * Returns the document-path.
     * @return a <code>String<code> value
     */
    public File getDocumentPath() {
        return getDocument().getFile();
    }

    /**
     * The names of the page envelope parameters.
     */
    public static final String[] PARAMETER_NAMES =
        {
            PageEnvelope.AREA,
            PageEnvelope.CONTEXT,
            PageEnvelope.PUBLICATION_ID,
            PageEnvelope.PUBLICATION,
            PageEnvelope.DOCUMENT_ID,
            PageEnvelope.DOCUMENT_URL,
            PageEnvelope.DOCUMENT_PATH,
            PageEnvelope.DOCUMENT_LANGUAGE };

    /**
     * @param string The context.
     */
    protected void setContext(String string) {
        context = string;
    }

    private Document document;

    /**
     * Returns the document.
     * @return A document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Sets the document.
     * @param document A document.
     */
    public void setDocument(Document document) {
        this.document = document;
    }
}
