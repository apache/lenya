/*
 * PublicationFactory.java
 *
 * Created on 15. Mai 2003, 17:49
 */

package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.Source;
import org.apache.log4j.Category;

/**
 *
 * @author  andreas
 */
public class PublicationFactory {
    
    static Category log = Category.getInstance(PublicationFactory.class);
    
    /**
     * Creates a new publication.
     * The publication ID is resolved from the request URI.
     * The servlet context path is resolved from the context object.
     * @param objectModel The object model of the Cocoon component.
     */
    public static Publication getPublication(Map objectModel) {
        
        Request request = ObjectModelHelper.getRequest(objectModel);
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String webappUri = uri.substring(contextPath.length() + 1);
        
        String publicationId;
        int slashIndex = webappUri.indexOf("/");
        if (slashIndex > -1) {
            publicationId = webappUri.substring(0, slashIndex);
        }
        else {
            publicationId = webappUri;
        }
        
        Context context = ObjectModelHelper.getContext(objectModel);
        String servletContextPath = context.getRealPath("");
        return new Publication(publicationId, servletContextPath);
    }
    
}
