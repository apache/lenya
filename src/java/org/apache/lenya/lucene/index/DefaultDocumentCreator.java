/*
 * DefaultDocumentCreator.java
 *
 * Created on 19. M�rz 2003, 18:20
 */

package org.apache.lenya.lucene.index;

import java.io.File;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lenya.lucene.parser.HTMLParser;
import org.apache.lenya.lucene.parser.HTMLParserFactory;

/**
 *
 * @author  hrt
 */
public class DefaultDocumentCreator
    extends AbstractDocumentCreator {
    
    /** Creates a new instance of DefaultDocumentCreator */
    public DefaultDocumentCreator() {
    }
    
    public Document getDocument(File file, File htdocsDumpDir)
        throws Exception {
            
        Document document = super.getDocument(file, htdocsDumpDir);
        
        HTMLParser parser = HTMLParserFactory.newInstance(file);
        parser.parse(file);
        
        document.add(Field.Text("title", parser.getTitle()));
        document.add(Field.Text("contents", parser.getReader()));
        
        return document;
    }
}
