/*
 * HTMLParser.java
 *
 * Created on 17. März 2003, 14:13
 */

package org.apache.lenya.lucene.parser;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;

/**
 *
 * @author  hrt
 */
public interface HTMLParser {

    void parse(File file) throws ParseException;
    
    void parse(URI uri) throws ParseException;
    
    /**
     * Returns the title of the HTML document.
     */
    String getTitle() throws IOException;
    
    /**
     * Returns a reader that reads the contents of the HTML document.
     */
    Reader getReader() throws IOException;
    
}
