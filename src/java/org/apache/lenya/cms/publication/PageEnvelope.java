/*
 * PageEnvelope.java
 *
 * Created on 10. April 2003, 13:46
 */

package org.lenya.cms.publication;

import java.io.File;
import java.io.IOException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.Source;
import org.xml.sax.SAXException;

/**
 *
 * @author  nobby
 */
public class PageEnvelope {
    
    public static final String PUBLICATION_ID = "publication-id";
    public static final String CONTEXT = "context-prefix";
    public static final String STAGE = "stage";
    
    private Publication publication;
    private String context;
    private String stage;

    /** Creates a new instance of PageEnvelope */
    public PageEnvelope(SourceResolver resolver, Request request)
            throws ProcessingException, SAXException, IOException {
        Source inputSource = resolver.resolveURI("");
        String publicationUri = inputSource.getURI();
        String directories[] = publicationUri.split("/");
        String publicationId = directories[directories.length - 1];
        String path = publicationUri.substring(0, publicationUri.indexOf("/lenya/pubs/" + publicationId));
        path = path.replaceAll("file:/", "");
        path = path.replace('/', File.separatorChar);

	// FIXME: figure out the stage properly
        stage = "authoring";

        publication = new Publication(publicationId, path);
        context = request.getContextPath();
    }
    
    
    /**
     * Returns the publication of this PageEnvelope.
     */
    public Publication getPublication() {
        return publication;
    }
    
    public String getContext() {
        return context;
    }
    
    public String getStage() {
        return stage;
    }
    
}
