/*
 * Publication.java
 *
 * Created on 8. April 2003, 18:38
 */

package org.apache.lenya.cms.publication;

import java.io.File;
import org.apache.lenya.cms.publishing.PublishingEnvironment;

import org.apache.log4j.Category;

/**
 *
 * @author  andreas
 */
public class Publication {
    
    /** Creates a new instance of Publication */
    public Publication(String id, String servletContextPath) {
        
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
    
    public String getId() {
        return id;
    }
    
    private PublishingEnvironment environment;
    
    public PublishingEnvironment getEnvironment() {
        return environment;
    }
    
    private File servletContext;
    
    public File getServletContext() {
        return servletContext;
    }
    
    public static final String PUBLICATION_PREFIX = "lenya" + File.separator + "pubs";
    
    /**
     * Returns the publication directory.
     */
    public File getDirectory() {
        return new File(getServletContext(), PUBLICATION_PREFIX + File.separator + getId());
    }

}
