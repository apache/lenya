/*
 * PageEnvelope.java
 *
 * Created on 10. April 2003, 13:46
 */

package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.Source;
import org.xml.sax.SAXException;

import org.apache.log4j.Category;

import org.apache.lenya.cms.rc.RCEnvironment;
/**
 *
 * @author  nobby
 */
public class PageEnvelope {
    static Category log = Category.getInstance(PageEnvelope.class);

    public static final String PUBLICATION_ID = "publication-id";
    public static final String CONTEXT = "context-prefix";
    public static final String AREA = "area";
    public static final String DOCUMENT_ID = "document-id";

    public static final int AREA_POS = 3;
    public static final int DOCUMENT_ID_POS = 4;

    private Publication publication;
    private RCEnvironment rcEnvironment;
    private String context;
    private String area;
    private String documentId;

    /** Creates a new instance of PageEnvelope */
    public PageEnvelope(SourceResolver resolver, Request request)
            throws PageEnvelopeException, ProcessingException, SAXException, IOException {
        Source inputSource = resolver.resolveURI("");
        String publicationUri = inputSource.getURI();
        String directories[] = publicationUri.split("/");
	//FIXME: what if no publicationId is specified?
        String publicationId = directories[directories.length - 1];
        String path = null; 
        if (publicationUri.indexOf("/lenya/pubs/" + publicationId) >= 0)  {
          path = publicationUri.substring(0, publicationUri.indexOf("/lenya/pubs/" +
								    publicationId));
        } else {
          throw new PageEnvelopeException("Cannot find the publication because no " +
					  "publicationId specified in URI : " +
					  publicationUri);       
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
        area = directories[AREA_POS];

	// the computation of the document id is based on the
	// assumption that and URI matches of the following pattern:
	// http:/<context-prefix>/<publication-id>/<area>/<document-id>.*
	// where document-id can be foo/bar/baz
	documentId = "";
	List documentIds = Arrays.asList(directories).subList(DOCUMENT_ID_POS, directories.length);
	// remove the suffix from the last element
	log.debug("documentIds: " + documentIds);
	String lastPartOfDocumentId = (String)documentIds.get(documentIds.size()-1);
	log.debug("lastPartOfDocumentId: " + lastPartOfDocumentId);
	int startOfSuffix = lastPartOfDocumentId.indexOf('.');
	log.debug("startOfSuffix: " + startOfSuffix);
	if (startOfSuffix >= 0) {
	    documentIds.set(documentIds.size()-1, lastPartOfDocumentId.substring(0, startOfSuffix));
	    log.debug("w/oSuffix: " + lastPartOfDocumentId.substring(0, startOfSuffix));
	}
	log.debug("documentIds: " + documentIds);
	for (Iterator i = documentIds.iterator(); i.hasNext();) {
	    documentId += "/" + i.next();
	}

        publication = new Publication(publicationId, path);
        rcEnvironment = new RCEnvironment(path);
        context = request.getContextPath();
    }
    
    
    /**
     * Returns the publication of this PageEnvelope.
     */
    public Publication getPublication() {
        return publication;
    }
    
    /**
     * Returns the rcEnvironment.
     */
    public RCEnvironment getRCEnvironment() {
        return rcEnvironment;
    }

    /**
     * Returns the context.
     */
    public String getContext() {
        return context;
    }
    
    /**
     * Returns the area (authoring/live).
     */
    public String getArea() {
        return area;
    }
    
    /**
     * Returns the document-id.
     */
    public String getDocumentId() {
        return documentId;
    }
}
