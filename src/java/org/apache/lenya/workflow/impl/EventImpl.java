/*
 * EventImpl.java
 *
 * Created on 8. April 2003, 19:44
 */

package org.apache.lenya.workflow.impl;

import org.apache.lenya.workflow.Event;

/**
 *
 * @author  andreas
 */
public class EventImpl implements Event {

    /** Creates a new instance of EventImpl */
    protected EventImpl(String eventName) {
        assert eventName != null;
        name = eventName;
    }

    private String name;

    public String toString() {
        return getName();
    }

    public boolean equals(Object otherObject) {
        boolean equals = false;

        if (otherObject instanceof EventImpl) {
            EventImpl otherEvent = (EventImpl) otherObject;
            equals = getName().equals(otherEvent.getName());
        } else {
            equals = super.equals(otherObject);
        }

        return equals;
    }

    public int hashCode() {
        return getName().hashCode();
    }

    /* (non-Javadoc)
     * @see org.apache.lenya.cms.workflow.Event#getName()
     */
    public String getName() {
        return name;
    }

}
