package org.wyona.cms.cocoon.acting;

import java.io.Reader;
import java.io.StringReader;
import java.io.InputStream;
import java.io.IOException;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.excalibur.io.IOUtil;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ComposerAction;
import org.apache.cocoon.components.parser.Parser;
import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.PostInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

  public java.util.Map act (Redirector redirector, 
                            SourceResolver resolver, 
                            Map objectModel, 
                            String source, 
                            Parameters params)
                            throws IOException, ComponentException, SAXException {

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
    int n = 0;
    while ((n = reqContent.read()) != -1) {
      request.append((char)n);
    }
    
    // construct DOM tree from the request contents
    Reader reqReader = new StringReader(request.toString());
    Parser parser = (Parser) this.manager.lookup(Parser.ROLE);
    Document requestDoc = parser.newDocument();
    InputSource saxSource = new InputSource(reqReader);
    requestDoc = parser.parseDocument(saxSource);
    
    // get the root element (should be "request") and its attributes ---> FixMe: Add error handling
    Element root = requestDoc.getDocumentElement();
    getLogger().debug ("root element (should be 'request'): " + root.getTagName());
    String reqId = root.getAttribute("id");
    getLogger().debug ("Request ID: " + reqId);
    String reqType = root.getAttribute("type");
    getLogger().debug ("Request Type: " + reqType);
    
    // get the first child element for root element (should be "data") and its attributes ---> FixMe: Add error handling
    Element data = (Element) root.getFirstChild();
    getLogger().debug ("first child element (should be 'data'): " + data.getTagName());
    String reqFile = data.getAttribute("id");
    getLogger().debug ("Requested File: " + reqFile);
    String fileType = data.getAttribute("type");
    getLogger().debug ("Requested File's Type: " + fileType);
    
    // close the input stream
    reqContent.close();
    
    // set sitemap params for response routing
    Map sitemapParams = new HashMap();
    sitemapParams.put("reqId", reqId);
    sitemapParams.put("reqType", reqType);
    sitemapParams.put("reqFile", reqFile);
    sitemapParams.put("fileType", fileType);
    return sitemapParams;
    
  }
}
