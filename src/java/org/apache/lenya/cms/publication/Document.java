/*
 * Document.java
 *
 * Created on 8. April 2003, 17:03
 */

package org.apache.lenya.cms.publication;

/**
 * A CMS document.
 *
 * @author <a href="mailto:andreas.hartmann@wyona.org">Andreas Hartmann</a>
 */
public interface Document {
    
    /**
     * Returns the document ID of this document.
     * @return A URI object.
     */
    String getId();

    /**
     * Returns the publication this document belongs to.
     * @return A publication object.
     */    
    Publication getPublication();
    
}
