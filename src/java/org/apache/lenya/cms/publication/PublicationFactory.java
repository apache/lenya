/*
 * PublicationFactory.java
 *
 * Created on 15. Mai 2003, 17:49
 */

package org.apache.lenya.cms.publication;

import java.util.HashMap;
import java.util.Map;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.log4j.Category;

/**
 *
 * @author  andreas
 */
public final class PublicationFactory {
	
	private PublicationFactory() {
	}
    
    private static Category log = Category.getInstance(PublicationFactory.class);
    
    private static Map idToPublication = new HashMap();
    
    /**
     * Creates a new publication.
     * The publication ID is resolved from the request URI.
     * The servlet context path is resolved from the context object.
     * @param objectModel The object model of the Cocoon component.
     */
    public static Publication getPublication(Map objectModel) {
        assert objectModel != null;
        
        Request request = ObjectModelHelper.getRequest(objectModel);
        String contextPath = request.getContextPath();
        if (contextPath == null) {
            contextPath = "";
        }
        String webappUri = request.getRequestURI().substring(contextPath.length());
        
        String publicationId = webappUri.split("/")[1];
        assert !"".equals(publicationId);
        
        Context context = ObjectModelHelper.getContext(objectModel);
        String servletContextPath = context.getRealPath("");
        
        return getPublication(publicationId, servletContextPath);
    }
    
    public static Publication getPublication(String id, String servletContextPath) {
        assert id != null;
        assert servletContextPath != null;
        Publication publication;
        if (idToPublication.containsKey(id)) {
            publication = (Publication) idToPublication.get(id);
        }
        else {
            publication = new Publication(id, servletContextPath);
            idToPublication.put(id, publication);
        }
        assert publication != null;
        return publication;
    }
    
}
