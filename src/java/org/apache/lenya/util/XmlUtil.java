package org.wyona.util;

import java.io.File; 
import java.io.IOException; 
import java.io.OutputStreamWriter;
import java.io.Writer; 

// JAXP
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

// SAX
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.SAXException;

/**
 * XmlUtil.java
 * $Id: XmlUtil.java,v 1.2 2002/02/19 13:47:12 memo Exp $
 *
 * Created: Thu Jan 24 18:27:05 2002
 *
 * Utility Class for checking XML content for well-formedness
 * 
 * @author <a href="mailto:memo@otego.com">Memo Birgi</a>
 * @version 0.1
 */
public class XmlUtil {

  /**
   * @param       xmlFile       Name of the XML-File to be checked.
   * @return      If well-formed: "OK"; if not: the error message. 
   */
  public static String check(String xmlFile) {
      
    String retMsg = "OK";

    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser parser = factory.newSAXParser();
      // MyHandler handler = new MyHandler();
      parser.parse(xmlFile, new HandlerBase());
    } catch (FactoryConfigurationError e) {
      retMsg = "unable to get a document builder factory";
    } catch (ParserConfigurationException e) {
      retMsg = "unable to configure parser";
    } catch (SAXException e) {
      retMsg = e.toString();
    } catch (IOException e) {
      retMsg = "i/o error";
    }
    
    return retMsg;
    
  }

  public class MyHandler extends HandlerBase {
  // custom handler 
  }
  
}// XmlUtil
