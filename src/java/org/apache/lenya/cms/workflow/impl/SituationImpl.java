/*
 * Situation.java
 *
 * Created on 8. April 2003, 17:42
 */

package org.lenya.cms.workflow.impl;

import org.lenya.cms.ac.User;
import org.lenya.cms.publication.Document;
import org.lenya.cms.workflow.Situation;

/**
 *
 * @author  andreas
 */
public class SituationImpl
    implements Situation {
    
    /** Creates a new instance of Situation */
    public SituationImpl(Document document, User user) {
        this.document = document;
        this.user = user;
        
        assert document != null;
        assert user != null;
    }
    
    private Document document;
    
    public Document getDocument() {
        return document;
    }
    
    private User user;
    
    public User getUser() {
        return user;
    }
}
