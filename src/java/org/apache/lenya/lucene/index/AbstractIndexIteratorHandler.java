/*
 * AbstractIndexIteratorHandler.java
 *
 * Created on 27. März 2003, 10:18
 */

package org.lenya.lucene.index;

import java.io.File;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

/**
 *
 * @author  hrt
 */
public abstract class AbstractIndexIteratorHandler
    implements IndexIteratorHandler {
    
    /** Creates a new instance of AbstractIndexIteratorHandler */
    public AbstractIndexIteratorHandler() {
    }
    
    /** Handles a stale document.
     *
     */
    public void handleStaleDocument(IndexReader reader, Term term) {
    }
    
    /** Handles a stale document.
     *
     */
    public void handleUnmodifiedDocument(IndexReader reader, Term term, File file) {
    }
    
    /** Handles an unmodified document and the file that represents it.
     *
     */
    public void handleNewDocument(IndexReader reader, Term term, File file) {
    }
    
    /** Handles a file. This is called for every file and mainly used for creating a new index.
     *
     */
    public void handleFile(IndexReader reader, File file) {
    }
    
}
