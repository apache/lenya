/*
 * EventImpl.java
 *
 * Created on 8. April 2003, 19:44
 */

package org.apache.lenya.cms.workflow.impl;

import org.apache.lenya.cms.workflow.Event;

/**
 *
 * @author  andreas
 */
public class EventImpl implements Event {

    /** Creates a new instance of EventImpl */
    public EventImpl(String id) {
        assert id != null;
        this.id = id;
    }

    private String id;

    public String getId() {
        return id;
    }

    public String toString() {
        return getId();
    }

    public boolean equals(Object otherObject) {
        boolean equals = false;

        if (otherObject instanceof EventImpl) {
            EventImpl otherEvent = (EventImpl) otherObject;
            equals = getId().equals(otherEvent.getId());
        } else {
            equals = super.equals(otherObject);
        }

        return equals;
    }

    public int hashCode() {
        return getId().hashCode();
    }

    /* (non-Javadoc)
     * @see org.apache.lenya.cms.workflow.Event#getCommand()
     */
    public String getCommand() {
        return getId();
    }

}
