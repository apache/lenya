/*
 * DefaultIndexer.java
 *
 * Created on 21. März 2003, 10:08
 */

package org.apache.lenya.lucene.index;

import org.w3c.dom.Element;

/**
 *
 * @author  hrt
 */
public class DefaultIndexer
    extends AbstractIndexer {
    
    /** Creates a new instance of DefaultIndexer */
    public DefaultIndexer() {
    }
    
    public DocumentCreator createDocumentCreator(Element element) throws Exception {
        return new DefaultDocumentCreator();
    }
    
}
