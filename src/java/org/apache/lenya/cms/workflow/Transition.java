/*
 * Transition.java
 *
 * Created on 8. April 2003, 17:05
 */

package org.lenya.cms.workflow;

/**
 *
 * @author  andreas
 */
public interface Transition {
    
    /**
     * Returns the event of this transition.
     */
    Event getEvent();

    /**
     * Returns the actions of this transition.
     */
    Action[] getActions();
    
    /**
     * Returns if the transition can fire in a certain situation.
     */
    boolean canFire(Situation situation);
    
}
