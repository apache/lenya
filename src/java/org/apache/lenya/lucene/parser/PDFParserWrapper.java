/*
 * PDFParserWrapper.java
 *
 * Created on 27. März 2003, 15:37
 */

package org.lenya.lucene.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;

/**
 *
 * @author  hrt
 */
public class PDFParserWrapper
    extends AbstractHTMLParser {
    
    /** Creates a new instance of PDFParserWrapper */
    public PDFParserWrapper() {
    }
    
    /** Returns a reader that reads the contents of the HTML document.
     *
     */
    public Reader getReader() throws IOException {
        return getParser().getReader();
    }
    
    /** Returns the title of the HTML document.
     *
     */
    public String getTitle() throws IOException {
        try {
            return getParser().getTitle();
        }
        catch (InterruptedException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    org.lenya.lucene.html.HTMLParser parser;
    
    protected org.lenya.lucene.html.HTMLParser getParser() {
        return parser;
    }
    
    public void parse(File file) throws ParseException {
        try {
            parser = new org.lenya.lucene.html.HTMLParser(file);
        }
        catch (FileNotFoundException e) {
            throw new ParseException(e);
        }
    }
    
    public void parse(URI uri) throws ParseException {
        try {
            URLConnection connection = uri.toURL().openConnection();
            Reader reader = new InputStreamReader(connection.getInputStream());
            parser = new org.lenya.lucene.html.HTMLParser(reader);
        }
        catch (MalformedURLException e) {
            throw new ParseException(e);
        }
        catch (IOException e) {
            throw new ParseException(e);
        }
    }
    
}
