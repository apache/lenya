package org.wyona.cms.cocoon.acting;

import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.String;
import java.text.DateFormat;
import java.util.Date; 
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.excalibur.io.IOUtil;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ComposerAction;
import org.apache.cocoon.components.parser.Parser;
import org.apache.cocoon.Constants;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.PostInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Interfaces with Xopus: handles the requests and replies to them
 *
 * @author Memo Birgi
 * @created 2002.02.21
 * @version 0.1
 */
public class XopusHandlerAction extends ComposerAction {

  // Constants
//   private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
//   private static final int REQUEST_TYPE_OPEN = 1;
//   private static final int REQUEST_TYPE_SAVE = 2;
  
  public java.util.Map act (Redirector redirector, 
                            SourceResolver resolver, 
                            Map objectModel, 
                            String source, 
                            Parameters params)
                            throws IOException, ComponentException, SAXException {

    // Set up the custom output file for development dumps
    File workfile = new File("c:/home/work/playground/wyona-cms/xopus", "xopus-temp.txt");
    boolean appendFlag = true;
    PrintWriter out = new PrintWriter(new FileWriter(workfile.toString(), appendFlag));
    getLogger().debug("workfile="+workfile.getAbsoluteFile());
    DateFormat timeStamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL);
    out.println("<===== START ===== " + timeStamp.format(new Date()) + " ==========>");

    // Get request object
    HttpRequest httpReq = (HttpRequest)objectModel.get(ObjectModelHelper.REQUEST_OBJECT);
    if(httpReq == null){
      getLogger().error ("Could not get HTTP_REQUEST_OBJECT from objectModel");
      return null;
    }
    int length = httpReq.getContentLength();
    PostInputStream reqContent = new PostInputStream(httpReq.getInputStream(), length);
    
    // read inputstream into stringbuffer
    StringBuffer request = new StringBuffer();
//    byte[] buffer = new byte[length];
    int n = 0;
    while ((n = reqContent.read()) != -1) {
      request.append((char)n);
    }
    
    // output request content to workfile for debugging
    out.println("Contents of the Xopus request:");
    out.print(request);
    
    // construct DOM tree from the request contents
    Reader reqReader = new StringReader(request.toString());
    Parser parser = (Parser) this.manager.lookup(Parser.ROLE);
    Document requestDoc = parser.newDocument();
    InputSource saxSource = new InputSource(reqReader);
    requestDoc = parser.parseDocument(saxSource);
    
    // get the root element (should be "request") ---> FixMe: Add error handling
    Element root = requestDoc.getDocumentElement();
    String rootTag = root.getTagName();
    out.println("INFO: Root Element Name: " + rootTag);
    
    // get the request ID
    String reqId = root.getAttribute("id");
    out.println("INFO: Request ID: " + reqId);
    
    // get the request type
    String reqType = root.getAttribute("type");
    out.println("INFO: Request Type: " + reqType);
//     int requestType = 0;
//     if (reqType.equals("open")) requestType = REQUEST_TYPE_OPEN;
//     if (reqType.equals("save")) requestType = REQUEST_TYPE_SAVE;
    
    // get the first child element for root element (should be "data") ---> FixMe: Add error handling
    Element data = (Element) root.getFirstChild();
    String dataTag = data.getTagName();
    out.println("INFO: First Child Element Name: " + dataTag);
    
    // get the request data ID (= path and name of the file to be edited)
    String reqFile = data.getAttribute("id");
    out.println("INFO: Request Data ID (path/filename): " + reqFile);
    
    // get the request data type (= type of file to be edited)
    String fileType = data.getAttribute("type");
    out.println("INFO: Request Data Type (file type): " + fileType);
    
    // handle the request depending on its type
//     switch(requestType) {
//       case REQUEST_TYPE_OPEN:
//         out.println("INFO: Xopus request type: " + reqType);
//         break;
//       case REQUEST_TYPE_SAVE:
//         out.println("INFO: Xopus request type: " + reqType);
//         break;
//       default:
//         out.println("ERROR: Xopus request type could not be identified");
//         break;
//     }
    
    // close the input stream
    reqContent.close();
    
    // Close the custom output file
    out.println("<===== END ======= " + timeStamp.format(new Date()) + " ==========>");
    out.println();
    out.close();
    
    // set sitemap params for response routing
    Map sitemapParams = new HashMap();
    sitemapParams.put("reqId", reqId);
    sitemapParams.put("reqType", reqType);
    sitemapParams.put("reqFile", reqFile);
    sitemapParams.put("fileType", fileType);
    return sitemapParams;
    
    // Give back something so that return value is not null
//     Map sitemapParams = new HashMap();
//     sitemapParams.put("world", "hello");
//     return sitemapParams;
  }
}

