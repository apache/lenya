package org.wyona.cms.cocoon.acting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.excalibur.io.IOUtil;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.parameters.Parameters;
//import org.apache.cocoon.acting.ComposerAction;
import org.apache.cocoon.acting.ConfigurableComposerAction;
import org.apache.cocoon.components.parser.Parser;
import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Source;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.serialization.Serializer;
import org.apache.cocoon.util.IOUtils;
import org.apache.cocoon.util.PostInputStream;
import org.apache.cocoon.xml.dom.DOMStreamer;
import org.dom4j.io.DOMReader;
import org.dom4j.io.XMLWriter;
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
public class XopusHandlerAction extends ConfigurableComposerAction {
  private String xmlRoot=null;
  private String xsdRoot=null;
  private String xslRoot=null;
/**
 *
 */
  public void configure(Configuration conf) throws ConfigurationException{
    super.configure(conf);

    xmlRoot=conf.getChild("xml").getAttribute("href");
    xslRoot=conf.getChild("xsd").getAttribute("href");
    xsdRoot=conf.getChild("xsl").getAttribute("href");
    getLogger().debug("CONFIGURATION:\n"+xmlRoot+"\n"+xslRoot+"\n"+xsdRoot);
    }

  public java.util.Map act (Redirector redirector, 
                            SourceResolver resolver, 
                            Map objectModel, 
                            String source, 
                            Parameters params)
                            throws IOException, 
                                   ComponentException, 
                                   SAXException, 
                                   ProcessingException {

    // Get absolute path of parent of sitemap
    org.apache.cocoon.environment.Source input_source=resolver.resolve("");
    String sitemapParentPath=input_source.getSystemId();
    sitemapParentPath=sitemapParentPath.substring(5); // Remove "file:" protocoll
    getLogger().debug("PARENT PATH OF SITEMAP: "+sitemapParentPath);
    getLogger().debug("XML: "+sitemapParentPath+"/"+xmlRoot);
    getLogger().debug("XSL: "+sitemapParentPath+"/"+xslRoot);
    getLogger().debug("XSD: "+sitemapParentPath+"/"+xsdRoot);


    // Get request object
    HttpRequest httpReq = (HttpRequest)objectModel.get(ObjectModelHelper.REQUEST_OBJECT);
    if(httpReq == null){
      getLogger().error ("Could not get HTTP_REQUEST_OBJECT from objectModel");
      return null;
    }
    int length = httpReq.getContentLength();
    PostInputStream reqContent = new PostInputStream(httpReq.getInputStream(), length);
    
    // read inputstream into stringbuffer
    /*
    StringBuffer request = new StringBuffer();
    int n = 0;
    while ((n = reqContent.read()) != -1) {
      request.append((char)n);
    }
    */
    
    // construct DOM document from the request contents
    //Reader reqReader = new StringReader(request.toString());
    Parser parser = (Parser) this.manager.lookup(Parser.ROLE);
    //InputSource saxSource = new InputSource(reqReader);
    InputSource saxSource = new InputSource(reqContent);
    //InputSource saxSource = new InputSource(reqContent);
    Document requestDoc = parser.parseDocument(saxSource);
    
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
    
    // save to temporary file, if needed
    if ("save".equals(reqType) || "checkin".equals(reqType)) {    
      //FIXME(): remove hard coding
      File tempFileDir = new File("xopustmp");
      if (!(tempFileDir.exists())) tempFileDir.mkdir();
      File tempFile = IOUtils.createFile(tempFileDir, reqFile); 
      final OutputStream os = new FileOutputStream(tempFile);
      ComponentSelector selector = (ComponentSelector)super.manager.lookup(Serializer.ROLE + "Selector");
      //FIXME: remove hardcoding stuff
      Serializer serializer = (Serializer)selector.select("xml");
      serializer.setOutputStream(os);
      serializer.startDocument();
      DOMStreamer domStreamer = new DOMStreamer(serializer);
      Element contentNode = (Element) data.getFirstChild();
      domStreamer.stream(contentNode);
      serializer.endDocument();
      getLogger().debug("NODE IS: \n" + contentNode.toString());
      selector.release(serializer);
      super.manager.release(selector);
      os.flush();
      os.close();
      
      // save to permanent file, if needed
      if (reqType.equals("checkin")) {
        File permFileDir = new File("resources/html/scratchpad/xopus");
        File permFile = IOUtils.createFile(permFileDir, reqFile); 
        FileUtil.copyFile(tempFile, permFile);
      }
    }
    
    return sitemapParams;
    
  }
}
