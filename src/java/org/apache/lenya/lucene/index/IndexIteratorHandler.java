/*
 * IndexIteratorHandler.java
 *
 * Created on 26. März 2003, 17:15
 */

package org.lenya.lucene.index;

import java.io.File;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

/**
 *
 * @author  hrt
 */
public interface IndexIteratorHandler {
    
    /**
     * Handles a file. This is called for every file and mainly used for creating a new index.
     */
    void handleFile(IndexReader reader, File file);
    
    /**
     * Handles a stale document.
     */
    void handleStaleDocument(IndexReader reader, Term term);
    
    /**
     * Handles an unmodified document and the file that represents it.
     */
    void handleUnmodifiedDocument(IndexReader reader, Term term, File file);
    
    /**
     * Handles a new document and the file that represents it.
     */
    void handleNewDocument(IndexReader reader, Term term, File file);
    
}
