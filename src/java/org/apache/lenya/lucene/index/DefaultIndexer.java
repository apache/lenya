/*
 * DefaultIndexer.java
 *
 * Created on 21. März 2003, 10:08
 */

package org.lenya.lucene.index;

import org.apache.avalon.framework.configuration.Configuration;

/**
 *
 * @author  hrt
 */
public class DefaultIndexer
    extends AbstractIndexer {
    
    /** Creates a new instance of DefaultIndexer */
    public DefaultIndexer() {
    }
    
    public DocumentCreator createDocumentCreator(Configuration configuration) throws Exception {
        return new DefaultDocumentCreator();
    }
    
}
