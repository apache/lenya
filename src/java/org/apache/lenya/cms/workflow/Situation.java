/*
 * Situation.java
 *
 * Created on 8. April 2003, 17:48
 */

package org.apache.lenya.cms.workflow;

import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.publication.Document;

/**
 *
 * @author  andreas
 */
public interface Situation {
    
    Document getDocument();
    User getUser();
    
}
