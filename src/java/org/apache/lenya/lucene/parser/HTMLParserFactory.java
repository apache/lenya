/*
 * ParserFactory.java
 *
 * Created on 20. März 2003, 15:50
 */

package org.apache.lenya.lucene.parser;

import java.io.File;
import org.apache.lenya.util.CommandLineLogger;

/**
 * Factory to create HTML parsers that are used for indexing HTML.
 *
 * @author  hrt
 */
public class HTMLParserFactory {

    /**
     * Returns an HTMLParser.
     */
    public static HTMLParser newInstance(File file) {
        HTMLParser parser = null;
        
        // HTML files
        if (file.getName().endsWith(".html")) {
            parser =  new SwingHTMLParser();
        }
        
        // PDF files
        else if (file.getName().endsWith(".txt")) {
            parser =  new PDFParserWrapper();
        }
        
        new CommandLineLogger(HTMLParserFactory.class).debug(
            "returning a " + parser.getClass().getName() + " for " + file.getName());
        
        return parser;
    }
    
}
