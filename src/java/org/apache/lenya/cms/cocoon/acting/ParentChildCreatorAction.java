package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.Constants;
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.Tokenizer;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.XPath;

/**
 * Describe class <code>ParentChildCreatorAction</code> here.
 *
 * @author Michael Wechner
 * @version 2002.02.26
 * @created 2002.01.30
 */
public class ParentChildCreatorAction extends AbstractComplementaryConfigurableAction implements Configurable{
  private String treeAuthoringPath=null;
  private String docsPath=null;
  private String doctypesPath=null;
/**
 *
 */
  public void configure(Configuration conf) throws ConfigurationException{
    super.configure(conf);

    treeAuthoringPath=conf.getChild("tree-authoring").getAttribute("href");
    docsPath=conf.getChild("docs").getAttribute("href");
    doctypesPath=conf.getChild("doctypes").getAttribute("href");
    getLogger().debug("CONFIGURATION:\nAUTHORING PATH OF TREE="+treeAuthoringPath);
    }
/**
 *
 */
  public Map act(Redirector redirector,SourceResolver resolver,Map objectModel,String src,Parameters parameters) throws Exception {
    org.apache.cocoon.environment.Source input_source=resolver.resolve("");
    String sitemapParentPath=input_source.getSystemId();
    sitemapParentPath=sitemapParentPath.substring(5); // Remove "file:" protocoll
    getLogger().debug("PARENT PATH OF SITEMAP: "+sitemapParentPath);

    // Get request object
    Request request=(Request)objectModel.get(Constants.REQUEST_OBJECT);
    if(request == null){
      getLogger().error ("No request object");
      return null;
    }
    
    // Get parameters
    String parentid=request.getParameter("parentid");
    String childid=request.getParameter("childid");
    String childname=request.getParameter("childname");
    String childtype=request.getParameter("childtype"); // branch or leaf
    String doctype=request.getParameter("doctype");
    if(!validate(parentid,childid,childname,childtype,doctype)){
      return null;
    }

    // Get session
    Session session=request.getSession(true);
    if(session == null){
      getLogger().error("No session object");
      return null;
    }

    // Transaction should actually be started here!

    // Read tree
    String treefilename=sitemapParentPath+treeAuthoringPath;
    getLogger().debug("FILENAME OF TREE: "+treefilename);
    if(!new File(treefilename).exists()){
      return null;
    }
    //InputStream in=intercept(new FileInputStream(treefilename));
    Document doc=new SAXReader().read("file:"+treefilename);

/*
    // Get trunk
    Element trunk=(Element)doc.selectSingleNode("/tree/branch");
    getLogger().error("TRUNK: "+trunk.getName()+" "+trunk.getPath());
*/

    // Get parent element
    StringTokenizer st=new StringTokenizer(parentid,"/");
    String xpath_string="/tree/branch"; // Trunk of tree
    while(st.hasMoreTokens()){
      xpath_string=xpath_string+"/branch[@relURI='"+st.nextToken()+"']";
      }
    getLogger().debug("XPATH: "+xpath_string);
    XPath xpathSelector=DocumentHelper.createXPath(xpath_string);
    List nodes=xpathSelector.selectNodes(doc);
    if(nodes.isEmpty()){
      getLogger().error("No child added");
      return null;
      }
    Element parent_element=(Element)nodes.get(0);
    getLogger().debug("PARENT ELEMENT: "+parent_element.getPath());

    // Add node: branch or leaf
    parent_element.addElement(childtype).addAttribute("relURI",childid).addAttribute("doctype",doctype).addAttribute("menuName",childname);
    getLogger().debug("Tree has been modified: "+doc.asXML());

    // Write new tree
    java.io.FileWriter fileWriter = new java.io.FileWriter(treefilename);
    doc.write(fileWriter);
    fileWriter.close();

    // Transaction should actually be finished here!

    // Create actual document
    String filename=sitemapParentPath+docsPath+parentid+"/"+childid+"/index.xml"; // Filename Generator
    String filenameMeta=sitemapParentPath+docsPath+parentid+"/"+childid+"/index-meta.xml"; // Filename Generator
    String absoluteDoctypesPath=sitemapParentPath+doctypesPath;
    String doctypeSample=absoluteDoctypesPath+"Group.xml";
    String doctypeMeta=absoluteDoctypesPath+"Meta.xml";
    getLogger().error("Filename: "+filename);
    getLogger().error("Doctypes: "+absoluteDoctypesPath);
    copyFile(new File(doctypeSample),new File(filename));
    copyFile(new File(doctypeMeta),new File(filenameMeta));

    // Redirect to referer
    if(7 > 1){
      String parent_uri=(String)session.getAttribute("org.wyona.cms.cocoon.acting.ParentChildCreatorAction.parent_uri");
      getLogger().error("Child added");
      HashMap actionMap=new HashMap();
      actionMap.put("parent_uri",parent_uri);
      session.removeAttribute("org.wyona.cms.cocoon.acting.ParentChildCreatorAction.parent_uri");
      return actionMap;
      }
    getLogger().error("No child added");
    return null;
    }
/**
 *
 */
  public boolean validate(String parentid,String childid,String childname,String childtype,String doctype){
    getLogger().debug(".validate(): parentid="+parentid+" ; childid="+childid+" ; childname="+childname+" ; childtype="+childtype+" ; doctype="+doctype);
    if((childid.indexOf(" ") >= 0) || (childid.length() == 0)){
      return false;
    }
    if(childname.length() == 0){
      return false;
    }
    return true;
    }
/**
 * Write input stream to Logger (for debugging)
 *
 * @param in an <code>InputStream</code> value
 * @return an <code>InputStream</code> value
 * @exception Exception if an error occurs
 */
  private InputStream intercept(InputStream in) throws Exception{
    byte[] buffer=new byte[1024];
    int bytes_read;
    ByteArrayOutputStream bufferOut=new ByteArrayOutputStream();
    while((bytes_read=in.read(buffer)) != -1){
      bufferOut.write(buffer,0,bytes_read);
    }
    getLogger().debug("Intercepted Input Stream:\n\n"+bufferOut.toString());
    return new ByteArrayInputStream(bufferOut.toByteArray());
    }
/**
 * Write output stream to Logger (for debugging)
 *
 * @param out an <code>OutputStream</code> value
 * @return an <code>OutputStream</code> value
  * @exception Exception if an error occurs
 */
  private OutputStream intercept(OutputStream out) throws Exception{
    return null;
    }
/**
 *
 */
  private boolean copyFile(File source,File destination) throws Exception{
    if(source.exists()){
      File parentDestination=new File(destination.getParent());
      if(!parentDestination.exists()){
       getLogger().warn("Directory will be created: "+parentDestination);
        parentDestination.mkdirs();
        }
      org.apache.avalon.excalibur.io.FileUtil.copyFile(source,destination);
      return true;
      }
    else{
      getLogger().error("File does not exist: "+source);
      return false;
      }
    }
  }
