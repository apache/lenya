/*
 * DocumentCreator.java
 *
 * Created on 19. März 2003, 10:38
 */

package org.lenya.lucene.index;

import java.io.File;
import org.apache.lucene.document.Document;

/**
 * An object of a class implementing this interface creates Lucene documents
 * from files. 
 *
 * @author  hrt
 */
public interface DocumentCreator {
    
    /**
     * Create a document from a file.
     */
    public Document getDocument(File file, File htdocsDumpDir)
        throws Exception;
    
}
