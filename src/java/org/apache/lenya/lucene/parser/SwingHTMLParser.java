/*
 * SwingHTMLParser.java
 *
 * Created on 20. März 2003, 15:48
 */

package org.lenya.lucene.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import javax.swing.text.html.parser.ParserDelegator;

/**
 *
 * @author  hrt
 */
public class SwingHTMLParser
    extends AbstractHTMLParser {
    
    /** Creates a new instance of SwingHTMLParser */
    public SwingHTMLParser() {
    }
    
    /**
     * Parses a URI.
     */
    public void parse(URI uri)
            throws ParseException  {
        try {
            ParserDelegator delagator = new ParserDelegator();
            handler = new SwingHTMLHandler();
            delagator.parse(getReader(uri), handler, true);
        }
        catch (IOException e) {
            throw new ParseException(e);
        }
    }
    
    private SwingHTMLHandler handler;
    
    protected SwingHTMLHandler getHandler() {
        return handler;
    }
    
    public String getTitle() {
        return getHandler().getTitle();
    }
    
    private Reader reader;
    
    public Reader getReader() {
        return getHandler().getReader();
    }
    
    protected Reader getReader(URI uri)
            throws IOException, MalformedURLException {
        if (uri.toString().startsWith("http:")) {
            // uri is url
            URLConnection connection = uri.toURL().openConnection();
            return new InputStreamReader(connection.getInputStream());
            
        } else {
            // uri is file
            return new FileReader(new File(uri));
        }
    }
    
}
