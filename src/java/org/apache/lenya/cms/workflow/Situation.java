/*
 * Situation.java
 *
 * Created on 8. April 2003, 17:48
 */

package org.lenya.cms.workflow;

import org.lenya.cms.ac.User;
import org.lenya.cms.publication.Document;

/**
 *
 * @author  andreas
 */
public interface Situation {
    
    Document getDocument();
    User getUser();
    
}
