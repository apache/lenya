/*
 * AbstractHTMLParser.java
 *
 * Created on 21. März 2003, 11:10
 */

package org.lenya.lucene.parser;

import java.io.File;

/**
 * Abstract base class for HTML parsers.
 * @author  hrt
 */
public abstract class AbstractHTMLParser
        implements HTMLParser {
    
    /** Creates a new instance of AbstractHTMLParser */
    public AbstractHTMLParser() {
    }
    
    /**
     * Parses a file.
     */
    public void parse(File file)
            throws ParseException {
        parse(file.toURI());
    }
    
}
