/*
 * Document.java
 *
 * Created on 8. April 2003, 17:03
 */

package org.apache.lenya.cms.publication;

import java.io.File;

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
    
    /**
     * Returns the complete URL of this document:<br/>
     * /{publication-id}/{area}{document-id}{language-suffix}.{extension}
     * @return A string.
     */
    String getCompleteUrl();
    
    /**
     * Returns the URL of this document:
     * {document-id}{language-suffix}.{extension}
     * The URL always starts with a slash (/).
     * @return A string.
     */
    String getDocumentUrl();
    
    /**
     * Returns the language of this document.
     * @return A string denoting the language.
     */
    String getLanguage();
    
    /**
     * Returns the area this document belongs to.
     * @return The area.
     */
    String getArea();
    
    /**
     * Returns the file for this document.
     * @return A file object.
     */
    File getFile();
    
    /**
     * Returns the extension in the URL.
     * @return A string.
     */
    String getExtension();
}
