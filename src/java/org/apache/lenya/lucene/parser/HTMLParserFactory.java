/*
 * ParserFactory.java
 *
 * Created on 20. März 2003, 15:50
 */

package org.lenya.lucene.parser;

/**
 * Factory to create HTML parsers that are used for indexing HTML.
 *
 * @author  hrt
 */
public class HTMLParserFactory {

    /**
     * Returns an HTMLParser.
     */
    public static HTMLParser newInstance() {
        return new SwingHTMLParser();
    }
    
}
