/*
 * DocumentTypeImpl.java
 *
 * Created on 8. April 2003, 18:03
 */

package org.apache.lenya.cms.publication;

/**
 * A document type.
 *
 * @author <a href="mailto:andreas.hartmann@wyona.org">Andreas Hartmann</a>
 */
public class DocumentType {
    
	public static final String NAMESPACE = "http://www.lenya.org/2003/doctype";
	public static final String DEFAULT_PREFIX = "dt";
    
    /** Creates a new instance of DocumentTypeImpl */
    public DocumentType(String name) {
        assert name != null;
    	this.name = name;
    }
    
    private String name;
    
	/**
     * Returns the name of this document type.
	 * @return A string value.
	 */
	public String getName() {
		return name;
	}

}
