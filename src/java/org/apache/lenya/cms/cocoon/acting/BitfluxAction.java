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
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.excalibur.io.IOUtil;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
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
 * Interfaces with Bitflux editor: handles the requests and replies to them
 *
 * @author Michael Wechner
 * @version 0.1
 */
public class BitfluxAction extends ConfigurableComposerAction {

  private String xmlRoot=null;
  private String xslRoot=null;
  private String xsdRoot=null;
  private String tempRoot=null;
  private Map relRootDirs = new HashMap();

  /**
   * Gets the configuration from the sitemap
   */
  public void configure(Configuration conf) throws ConfigurationException{
    super.configure(conf);
    xmlRoot=conf.getChild("xml").getAttribute("href");
    xslRoot=conf.getChild("xsl").getAttribute("href");
    xsdRoot=conf.getChild("xsd").getAttribute("href");
    tempRoot=conf.getChild("temp").getAttribute("href");
    getLogger().debug("CONFIGURATION:\n" + "Relative XML Root Directory: " + xmlRoot + "\n"
                                         + "Relative XSL Root Directory: " + xslRoot + "\n"
                                         + "Relative XSD Root Directory: " + xsdRoot + "\n"
                                         + "Relative Temp Directory: " + tempRoot);
                                         
    // Encode File types and their root directories, relative to the sitemap directory
    relRootDirs.put("xml", xmlRoot);
    relRootDirs.put("xsl", xslRoot);
    relRootDirs.put("xsd", xsdRoot);
    relRootDirs.put("temp", tempRoot);
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

    // Get relative filename to save
    String relativeFilename=null;
    if(params.isParameter("save-filename")){
      relativeFilename=params.getParameter("save-filename",null);
      getLogger().debug(".act(): Filename: "+params.getParameter("save-filename",null));
      }

 

    // Get absolute path of sitemap directory
    org.apache.cocoon.environment.Source input_source=resolver.resolve("");
    String sitemapPath=input_source.getSystemId();
    sitemapPath=sitemapPath.substring(5); // Remove "file:" protocol
    getLogger().debug("Absolute SITEMAP Directory: " + sitemapPath);
    getLogger().debug("Absolute XML Root Directory: " + sitemapPath + xmlRoot);
    getLogger().debug("Absolute XSL Root Directory: " + sitemapPath + xslRoot);
    getLogger().debug("Absolute XSD Root Directory: " + sitemapPath + xsdRoot);
    getLogger().debug("Absolute Temp Root Directory: " + sitemapPath + tempRoot);
    
    // Get request object
    HttpRequest httpReq = (HttpRequest)objectModel.get(ObjectModelHelper.REQUEST_OBJECT);
    if(httpReq == null){
      getLogger().error ("Could not get HTTP_REQUEST_OBJECT from objectModel");
      return null;
    }
    int length = httpReq.getContentLength();
    PostInputStream reqContent = new PostInputStream(httpReq.getInputStream(), length);
    
    // construct DOM document from the request contents
    Parser parser = (Parser) this.manager.lookup(Parser.ROLE);
    InputSource saxSource = new InputSource(reqContent);
    Document requestDoc = parser.parseDocument(saxSource);
    
    // get the root element (should be "request") and its attributes ---> FixMe: Add error handling
    Element root = requestDoc.getDocumentElement();
    getLogger().debug ("root element (should be 'request'): " + root.getTagName());
    String reqType = root.getAttribute("type");
    getLogger().debug ("Request Type: " + reqType);
    
    // get the first child element for root element (should be "data") and its attributes ---> FixMe: Add error handling
    Element data = (Element) root.getFirstChild();
    getLogger().debug ("first child element (should be 'data'): " + data.getTagName());
    String reqFile = data.getAttribute("id");
    String fileType = data.getAttribute("type");
    getLogger().debug ("Requested File's Type: " + fileType);
    
    // close the input stream
    reqContent.close();
    
    // Define temp dir
    File tempFileDir = new File(sitemapPath + relRootDirs.get("temp") + "/" + relRootDirs.get(fileType));
    if (!(tempFileDir.exists())) tempFileDir.mkdir();

    // Define Filenames
    File tempFile = IOUtils.createFile(tempFileDir,relativeFilename); 
    File permFile = new File(sitemapPath + relRootDirs.get(fileType) + "/" +relativeFilename);
      getLogger().debug("PERMANENT FILE: " + permFile.getAbsolutePath());
      getLogger().debug("TEMPORARY FILE: " + tempFile.getAbsolutePath());

    // make a temporary copy of the file to be edited
    if ("xml".equals(fileType) && "open".equals(reqType)) {
      FileUtil.copyFile(permFile, tempFile);
    }
    
    Map sitemapParams = new HashMap();

    /* Not needed since Bitflux uses the url-back request parameter to do the redirection
    // set sitemap params for response routing
    sitemapParams.put("reqType", reqType);
    if ("xml".equals(fileType) && ("open".equals(reqType) || "save".equals(reqType))) {
      sitemapParams.put("reqFilePath", (String)relRootDirs.get("temp") + "/" + (String)relRootDirs.get(fileType) + "/" + reqFile);
    } else {
      sitemapParams.put("reqFilePath", (String)relRootDirs.get(fileType) + "/" + reqFile);
    }
    sitemapParams.put("fileType", fileType);
    getLogger().debug ("File to be edited (in temp dir): " + sitemapParams.get("reqFilePath"));
    */

    // save to temporary file, if needed
    if ("save".equals(reqType) || "checkin".equals(reqType)) {    
      //FIXME(): remove hard coding
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
    }
      
    // save to permanent file, if needed
    if ("checkin".equals(reqType)) {
        FileUtil.copyFile(tempFile, permFile);
    }
      
    return sitemapParams;
  }
}
