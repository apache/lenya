package org.lenya.lucene.html;

import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 */
public class HtmlContentHandler extends DefaultHandler {
//public class HtmlContentHandler implements ContentHandler {
/**
 *
 */
   public static void main(String[] args) {
       ContentHandler ch = new HtmlContentHandler();
       org.apache.excalibur.xml.sax.JTidyHTMLParser parser = new org.apache.excalibur.xml.sax.JTidyHTMLParser();
       try {
           parser.parse(new org.xml.sax.InputSource(new java.io.FileInputStream("/home/michi/eurex/htdocs_dump_BAK1/index.html")), ch);
       } catch(Exception e) {
           System.err.println(e);
       }
   }
/**
 *
 */
    public void endDocument () throws SAXException {
    }
/**
 *
 */
    public void startPrefixMapping (String prefix, String uri) throws SAXException {
    }
/**
 *
 */
    public void setDocumentLocator (Locator locator) {
    }
/**
 *
 */
    public void startDocument () throws SAXException {
    }
}
