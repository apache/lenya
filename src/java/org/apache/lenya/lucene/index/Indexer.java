/*
 * Indexer.java
 *
 * Created on 19. März 2003, 10:51
 */

package org.lenya.lucene.index;

import java.io.File;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lucene.index.IndexWriter;

/**
 *
 * @author  hrt
 */
public interface Indexer {
    
    /**
     * Configures this indexer.
     */
    void configure(Configuration configuration)
            throws Exception;
    
    /**
     * Indexes the contents of a directory.
     */
    void createIndex(File dumpDirectory, String index, IndexWriter writer)
            throws Exception;

    /**
     * Indexes the contents of a directory.
     */
    void updateIndex(File dumpDirectory, String index, IndexWriter writer)
            throws Exception;
}
