/*
 * Publication.java
 *
 * Created on 8. April 2003, 18:38
 */

package org.apache.lenya.cms.publication;

import java.io.File;
import org.apache.lenya.cms.publishing.PublishingEnvironment;

/**
 * A publication.
 * 
 * @author <a href="mailto:andreas.hartmann@wyona.org">Andreas Hartmann</a>
 */
public class Publication {
    
    /** Creates a new instance of Publication */
    protected Publication(String id, String servletContextPath) {
        
        assert id != null;
        this.id = id;
        
        assert servletContextPath != null;
        File servletContext = new File(servletContextPath);
        assert servletContext.exists();
        this.servletContext = servletContext;
        
        // FIXME: remove PublishingEnvironment from publication
        environment = new PublishingEnvironment(servletContextPath, id);
    }
    
    private String id;
    
    /**
     * Returns the publication ID.
     * @return A string value.
     */
    public String getId() {
        return id;
    }
    
    private PublishingEnvironment environment;
    
    /**
     * Returns the publishing environment of this publication.
     * @return A {@link PublishingEnvironment} object.
     * @deprecated It is planned to decouple the environments from the publication.
     */
    public PublishingEnvironment getEnvironment() {
        return environment;
    }
    
    private File servletContext;
    
    /**
     * Returns the servlet context this publication belongs to
     * (usually, the <code>webapps/lenya</code> directory).
     * @return A <code>File</code> object.
     */
    public File getServletContext() {
        return servletContext;
    }
    
    public static final String PUBLICATION_PREFIX = "lenya" + File.separator + "pubs";
    
    /**
     * Returns the publication directory.
     * @return A <code>File</code> object.
     */
    public File getDirectory() {
        return new File(getServletContext(), PUBLICATION_PREFIX + File.separator + getId());
    }

}
