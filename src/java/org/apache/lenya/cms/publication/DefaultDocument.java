/*
 * DefaultDocument.java
 *
 * Created on 9. April 2003, 13:47
 */

package org.apache.lenya.cms.publication;

/**
 *
 * @author  andreas
 */
public class DefaultDocument
    implements Document {
    
    /** Creates a new instance of DefaultDocument */
    public DefaultDocument(DocumentType type) {
        assert type != null;
        this.type = type;
    }
    
    private DocumentType type;
    
    /** Returns the document type this document belongs to.
     *
     */
    public DocumentType getType() {
        return type;
    }
    
}
