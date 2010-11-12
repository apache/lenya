package org.apache.lenya.cms.publication;

public interface Revision {

    /**
     * @return The revision number.
     */
    int getNumber();
    
    /**
     * @return The time when this revision was created.
     */
    long getTime();
    
    /**
     * @return The ID of the user who created this revision.
     */
    String getUserId();
    
}
