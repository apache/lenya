/*
 * DocumentTypeBuildException.java
 *
 * Created on 9. April 2003, 10:12
 */

package org.apache.lenya.cms.publication;

/**
 *
 * @author  andreas
 */
public class DocumentTypeBuildException
    extends Exception {
    
    /** Creates a new instance of DocumentTypeBuildException */
    public DocumentTypeBuildException() {
        super(MESSAGE);
    }
    
    public DocumentTypeBuildException(Throwable cause) {
        super(MESSAGE, cause);
    }
    
    public static final String MESSAGE = "Failed to build document type: ";
    
}
