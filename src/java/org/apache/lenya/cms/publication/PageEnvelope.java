/*
 * PageEnvelope.java
 *
 * Created on 10. April 2003, 13:46
 */

package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.IOException;
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
    public static final int AREA_POS = 3;
    
    private Publication publication;
    private RCEnvironment rcEnvironment;
    private String context;
    private String area;

    /** Creates a new instance of PageEnvelope */
    public PageEnvelope(SourceResolver resolver, Request request)
            throws PageEnvelopeException, ProcessingException, SAXException, IOException {
        Source inputSource = resolver.resolveURI("");
        String publicationUri = inputSource.getURI();
        String directories[] = publicationUri.split("/");
//FIXME if no publicationId specified?
        String publicationId = directories[directories.length - 1];
        String path = null; 
        int l = publicationUri.length();
        if(publicationUri.indexOf("/lenya/pubs/" + publicationId)>=0 & publicationUri.indexOf("/lenya/pubs/" + publicationId)<l)  {
          path = publicationUri.substring(0, publicationUri.indexOf("/lenya/pubs/" + publicationId));
        } else {
          throw new PageEnvelopeException("Cannot found the publication because no publicationId specified in URI : "+publicationUri);       
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
        area = directories[AREA_POS];;

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

    public String getContext() {
        return context;
    }
    
    public String getArea() {
        return area;
    }
    
}
