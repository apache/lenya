/*
 * DefaultDocumentCreator.java
 *
 * Created on 19. März 2003, 18:20
 */

package org.lenya.lucene.index;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.InterruptedException;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.lenya.lucene.parser.HTMLParser;
import org.lenya.lucene.parser.HTMLParserFactory;

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
        
        HTMLParser parser = HTMLParserFactory.newInstance();
        parser.parse(file);
        
        document.add(Field.Text("title", parser.getTitle()));
        document.add(Field.Text("contents", parser.getReader()));
        
        return document;
    }
}
