/*
 * PageEnvelope.java
 *
 * Created on 10. April 2003, 13:46
 */

package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.Source;
import org.xml.sax.SAXException;

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

    private Publication publication;
    private RCEnvironment rcEnvironment;
    private String context;
    private String area;
    private String documentId;
    private String documentUrl;

    /**
     * Creates a new instance of PageEnvelope from a sitemap inside a publication.
     * @param publication The publication the page belongs to.
     * @param request The request that calls the page.
     * @exception PageEnvelopeException if an error occurs
     * @exception ProcessingException if an error occurs
     * @exception SAXException if an error occurs
     * @exception IOException if an error occurs
     */
    public PageEnvelope(Publication publication, Request request)
        throws PageEnvelopeException {

        assert publication != null;
        assert request != null;

        try {        
            this.publication = publication;
            String requestURI = request.getRequestURI();
            context = request.getContextPath();
            if (context == null) {
                context = "";
            }
        
            String webappURI = requestURI.substring(context.length());
            String publicationURI = webappURI.substring(("/" + publication.getId()).length());
        
            area = publicationURI.split("/")[1];
        
            documentUrl = publicationURI.substring(("/" + area).length());
            documentId = computeDocumentId(documentUrl);
            
            rcEnvironment = new RCEnvironment(publication.getServletContext().getCanonicalPath());
        }
        catch (Exception e) {
            throw new PageEnvelopeException(createExceptionMessage(request), e);
        }
        
        // plausibility check
        if (!request.getRequestURI().startsWith(getContext() + "/" + getPublication().getId() + "/" + getArea() + getDocumentId())) {
            throw  new PageEnvelopeException(createExceptionMessage(request));
        }
    }
    
    protected String createExceptionMessage(Request request) {
        return "Resolving page envelope failed:"
                    + "\n  URI: " + request.getRequestURI()
                    + "\n  Context: " + getContext()
                    + "\n  Publication ID: " + getPublication().getId()
                    + "\n  Area: " + getArea()
                    + "\n  Document ID: " + getDocumentId();
    }

    /**
     * Creates a page envelope from an object model.
     * @param objectModel The object model.
     * @throws ProcessingException
     * @throws PageEnvelopeException
     * @throws SAXException
     * @throws IOException
     */
    public PageEnvelope(Map objectModel)
        throws PageEnvelopeException {
        this(
            PublicationFactory.getPublication(objectModel),
            ObjectModelHelper.getRequest(objectModel));
    }

    /**
     * Creates a new instance of PageEnvelope
     * @param resolver a <code>SourceResolver</code> value
     * @param request a <code>Request</code> value
     * @exception PageEnvelopeException if an error occurs
     * @exception ProcessingException if an error occurs
     * @exception SAXException if an error occurs
     * @exception IOException if an error occurs
     * @deprecated This constructor does not work outside a publication directory. Use {@link #PageEnvelope(Map)} instead!
     * 
     * 
     */
    public PageEnvelope(SourceResolver resolver, Request request)
        throws PageEnvelopeException, ProcessingException, SAXException, IOException {
        Source inputSource = resolver.resolveURI("");
        String publicationUri = inputSource.getURI();
        String directories[] = publicationUri.split("/");
        //FIXME: what if no publicationId is specified?
        String publicationId = directories[directories.length - 1];
        String path = null;
        if (publicationUri.indexOf("/lenya/pubs/" + publicationId) >= 0) {
            path =
                publicationUri.substring(0, publicationUri.indexOf("/lenya/pubs/" + publicationId));
        } else {
            throw new PageEnvelopeException(
                "Cannot find the publication because no "
                    + "publicationId specified in URI : "
                    + publicationUri);
        }
        // apparently on windows the path will be something like
        // "file://foo/bar/baz" where as on *nix it will be
        // "file:/foo/bar/baz". The following hack will transparently
        // take care of this.
        path = path.replaceAll("file://", "/");
        path = path.replaceAll("file:", "");
        path = path.replace('/', File.separatorChar);

        // compute area
        String requestURI = request.getRequestURI();
        log.debug("requestURI: " + requestURI);
        directories = requestURI.split("/");
        area = directories[3];

        documentId = computeDocumentId(requestURI);
        publication = new Publication(publicationId, path);
        rcEnvironment = new RCEnvironment(path);
        context = request.getContextPath();
    }

    /**
     * <code>computeDocumentId</code> contains some heuristicts derive
     * a document-id from a given requestURI. The basic assumption is
     * that an URL consists of
     * http:/<context-prefix>/<publication-id>/<area>/<document-id>.*. So
     * to figure out the document-id we simply need to trim the parts
     * before and including "area" and after and including '.'
     *
     * @param requestURI a <code>String</code> value
     * @return a <code>String</code> value
     */
    protected String computeDocumentId(String documentURL) {
        // the computation of the document id is based on the
        // assumption that and URI matches of the following pattern:
        // <publication-id>/<area><document-id>.*
        // where document-id can be /foo/bar/baz

        // remove the suffix from the last element
        int startOfSuffix = documentURL.lastIndexOf('.');
        if (startOfSuffix > -1) {
            documentURL = documentURL.substring(0, startOfSuffix);
        }
        
        return documentURL;
    }

    /**
     * Returns the publication of this PageEnvelope.
     * @return a <code>Publication</code> value
     */
    public Publication getPublication() {
        return publication;
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
        return area;
    }

    /**
     * Returns the document-id.
     * @return a <code>String</code> value
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Returns the document URL (without prefix and area).
     * @return a <code>String</code> value
     */
    public String getDocumentURL() {
        return documentUrl;
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
            PageEnvelope.DOCUMENT_URL };

}
