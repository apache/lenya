/*
 * Created on Jul 10, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.apache.lenya.cms.ac2;

import org.apache.avalon.framework.component.Component;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.publication.Publication;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface AccessController extends Component {
    
    public static final String ROLE = AccessController.class.getName();
    
    /**
     * Authenticates a request.
     * @param request A request.
     * @return A boolean value.
     */
    boolean authenticate(Request request) throws AccessControlException;
    
    /**
     * Authorizes a request inside a publication.
     * @param publication A publication.
     * @param request A request.
     * @return A boolean value.
     */
    boolean authorize(Publication publication, Request request) throws AccessControlException;

}
