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

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author Michael Wechner
 * @created 2002.01.30
 * @version 2002.01.30
 */
public class ParentChildCreatorAction extends AbstractComplementaryConfigurableAction implements Configurable{
/**
 *
 */
  public void configure(Configuration conf) throws ConfigurationException{
    super.configure(conf);
    }
/**
 *
 */
  public Map act(Redirector redirector,SourceResolver resolver,Map objectModel,String src,Parameters parameters) throws Exception {
    // Get Source
    getLogger().error ("SOURCE: "+src);
    if(src == null){
      getLogger().error ("No source: e.g. src=\"docs/wyona/cms/tree.xml\"");
      return null;
      }
    org.apache.cocoon.environment.Source inputSource=resolver.resolve(src);
    getLogger().error("RESOLVED SOURCE: "+inputSource.getSystemId());

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
    String childtype=request.getParameter("childtype");
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

    // Read tree
    String treefilename=inputSource.getSystemId().substring(5); // 5: remove protocol "file:"
    getLogger().error("Tree filename: "+treefilename);
    if(!new File(treefilename).exists()){
      return null;
      }
    //InputStream in=intercept(new FileInputStream(treefilename));
    Document doc=new SAXReader().read("file:"+treefilename);
    Element trunk=(Element)doc.selectSingleNode("/tree/branch");
    trunk.addElement(childtype).addAttribute("relURI",childid).addAttribute("doctype",doctype).addAttribute("menuName",childname);
    getLogger().error("TRUNK: "+trunk.getName()+" "+trunk.getPath());
    getLogger().error("Modified Tree: "+doc.asXML());
    java.io.FileWriter fileWriter = new java.io.FileWriter(treefilename);
    //java.io.FileWriter fileWriter = new java.io.FileWriter(treefilename+".tmp");
    doc.write(fileWriter);
    fileWriter.close();

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
    getLogger().error(".validate(): parentid="+parentid+" ; childid="+childid+" ; childname="+childname+" ; childtype="+childtype+" ; doctype="+doctype);
    if((childid.indexOf(" ") >= 0) || (childid.length() == 0)){
      return false;
      }
    if(childname.length() == 0){
      return false;
      }
    return true;
    }
/**
 *
 */
  private InputStream intercept(InputStream in) throws Exception{
    byte[] buffer=new byte[1024];
    int bytes_read;
    ByteArrayOutputStream bufferOut=new ByteArrayOutputStream();
    while((bytes_read=in.read(buffer)) != -1){
      bufferOut.write(buffer,0,bytes_read);
      }
    getLogger().error("Intercepted Input Stream:\n\n"+bufferOut.toString());
    return new ByteArrayInputStream(bufferOut.toByteArray());
    }
/**
 *
 */
  private OutputStream intercept(OutputStream out) throws Exception{
    return null;
    }
  }
