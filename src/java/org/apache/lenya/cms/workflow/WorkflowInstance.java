/*
 * WorkflowInstance.java
 *
 * Created on 8. April 2003, 17:14
 */

package org.apache.lenya.cms.workflow;

import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.publication.Document;

/**
 *
 * @author  andreas
 */
public interface WorkflowInstance {
    
    /**
     * Returns the document of this WorkflowInstance.
     */
    Document getDocument();
    
    /**
     * Returns the current state of this WorkflowInstance.
     */
    State getCurrentState();
    
    /**
     * Returns the transitions that can fire for this user.
     */
    Transition[] getExecutableTransitions(User user);
    
    /**
     * Indicates that the user invoked an event.
     * @param user The user who invoked the event.
     * @param event The event that was invoked.
     */
    void invoke(User user, Event event);
    
}
