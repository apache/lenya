/*
 * Publication.java
 *
 * Created on 8. April 2003, 18:38
 */

package org.lenya.cms.publication;

import org.lenya.cms.publishing.PublishingEnvironment;

import org.apache.log4j.Category;

/**
 *
 * @author  andreas
 */
public class Publication {
    Category log = Category.getInstance(Publication.class);
    
    /** Creates a new instance of Publication */
    public Publication(String id, String servletContextPath) {
        log.debug("Servlet Context Path: " + servletContextPath);
        assert id != null;
        this.id = id;
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
    
}
