/*
 * Indexer.java
 *
 * Created on 19. März 2003, 10:51
 */

package org.lenya.lucene.index;

import java.io.File;

import org.w3c.dom.Element;

/**
 *
 * @author  hrt
 */
public interface Indexer {
    
    /**
     * Configures this indexer.
     */
    void configure(Element element)
            throws Exception;
    
    /**
     * Indexes the contents of a directory.
     */
    void createIndex(File dumpDirectory, String index)
            throws Exception;

    /**
     * Indexes the contents of a directory.
     */
    void updateIndex(File dumpDirectory, String index)
            throws Exception;
}
