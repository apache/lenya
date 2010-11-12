package org.apache.lenya.cms.cocoon.source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Repository;
import org.apache.lenya.cms.publication.ResourceNotFoundException;
import org.apache.lenya.cms.publication.Session;

/**
 * Syntax: pub:{pubId}:/path/to/file
 */
public class PublicationSourceFactory implements SourceFactory {
    
    protected static final String PROTOCOL = "pub";

    private Repository repository;
    private SourceResolver sourceResolver;

    public Source getSource(final String location, final Map parameters) throws IOException,
            MalformedURLException {
        
        final String pathInfo = location.substring(PROTOCOL.length() + 1);
        
        final int colonIndex = pathInfo.indexOf(":");
        if (colonIndex < 0) {
            throw new MalformedURLException("The URI " + location
                    + " must contain the publication ID.");
        }

        final String pubId = pathInfo.substring(0, colonIndex);
        ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        HttpServletRequest request = process.getRequest();
        Session session = this.repository.getSession(request);
        if (session.existsPublication(pubId)) {
            Publication pub = session.getPublication(pubId);
            final String path = pathInfo.substring(colonIndex + 1);
            final String uri = pub.getSourceUri() + path;
            return this.sourceResolver.resolveURI(uri);
        } else {
            throw new ResourceNotFoundException("The publication " + pubId + " does not exist.");
        }
    }

    /**
     * Does nothing because the delegated factory does this.
     * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source)
     */
    public void release(Source source) {
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

}
