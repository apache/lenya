package org.apache.lenya.cms.publication;

/*
 * @deprecated : this is a duplicate class of lenya-repository-api revision.
 */
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
