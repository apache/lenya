/*
 * Publication.java
 *
 * Created on 8. April 2003, 18:38
 */

package org.lenya.cms.publication;

import org.lenya.cms.publishing.PublishingEnvironment;

/**
 *
 * @author  andreas
 */
public class Publication {
    
    /** Creates a new instance of Publication */
    public Publication(String id, String servletContextPath) {
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
