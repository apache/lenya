/*
 * PageEnvelope.java
 *
 * Created on 10. April 2003, 13:46
 */

package org.apache.lenya.cms.publication;

import java.io.File;
import java.util.Map;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;

import org.apache.log4j.Category;

import org.apache.lenya.cms.rc.RCEnvironment;

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

    private RCEnvironment rcEnvironment;
    private String context;

    /**
     * Constructor.
     */
    protected PageEnvelope() {
    }

    /**
     * Creates a new instance of PageEnvelope from a sitemap inside a publication.
     * @param publication The publication the page belongs to.
     * @param request The request that calls the page.
     * @exception PageEnvelopeException if an error occurs
     * @deprecated Performance problems. Use {@link PageEnvelopeFactory#getPageEnvelope(Map)} instead.
     */
    public PageEnvelope(Publication publication, Request request) throws PageEnvelopeException {

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
                DefaultDocumentBuilder.getInstance().buildDocument(publication, webappURI);
            setDocument(document);

            rcEnvironment = new RCEnvironment(publication.getServletContext().getCanonicalPath());
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
     * Creates a page envelope from an object model.
     * @param objectModel The object model.
     * @throws PageEnvelopeException when something went wrong.
     * @deprecated Performance problems. Use {@link PageEnvelopeFactory#getPageEnvelope(Map)} instead.
     */
    public PageEnvelope(Map objectModel) throws PageEnvelopeException {
        this(
            PublicationFactory.getPublication(objectModel),
            ObjectModelHelper.getRequest(objectModel));
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
     * Returns the publication of this PageEnvelope.
     * @return a <code>Publication</code> value
     */
    public Publication getPublication() {
        return getDocument().getPublication();
    }

    /**
     * Returns the rcEnvironment.
     * @return a <code>RCEnvironment</code> value
     */
    public RCEnvironment getRCEnvironment() {
        return rcEnvironment;
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
            PageEnvelope.DOCUMENT_PATH };

    /**
     * @param string The context.
     */
    protected void setContext(String string) {
        context = string;
    }

    /**
     * @param environment The revision control environment.
     * @deprecated We should detach the RC environment from the page envelope.
     */
    protected void setRcEnvironment(RCEnvironment environment) {
        rcEnvironment = environment;
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
