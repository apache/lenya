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
 * @created 2002.02.03
 * @version 2002.02.04
 */
public class PublisherAction extends AbstractComplementaryConfigurableAction implements Configurable{
  private String authoringPath=null;
  private String livePath=null;
  private String treeAuthoringPath=null;
  private String treeLivePath=null;
/**
 *
 */
  public void configure(Configuration conf) throws ConfigurationException{
    super.configure(conf);

    authoringPath=conf.getChild("authoring").getAttribute("href");
    livePath=conf.getChild("live").getAttribute("href");
    treeAuthoringPath=conf.getChild("tree-authoring").getAttribute("href");
    treeLivePath=conf.getChild("tree-live").getAttribute("href");
    if(getLogger().isDebugEnabled()) {
      getLogger().debug("CONFIGURATION:\nauthoring path="+authoringPath+"\nlive path="+livePath);
      getLogger().debug("CONFIGURATION:\ntree authoring path="+treeAuthoringPath+"\ntree live path="+treeLivePath);
      }
    }
/**
 *
 */
  public Map act(Redirector redirector,SourceResolver resolver,Map objectModel,String src,Parameters parameters) throws Exception {
    // Get Source
    org.apache.cocoon.environment.Source inputSource=resolver.resolve("");
    String sitemapParentPath=inputSource.getSystemId();
    sitemapParentPath=sitemapParentPath.substring(5); // Remove "file:" protocoll
    getLogger().error("RESOLVED SOURCE: "+sitemapParentPath);
    getLogger().error("CONFIGURATION:\nauthoring path="+authoringPath+"\nlive path="+livePath);

    // Set absolute paths
    String absoluteAuthoringPath=sitemapParentPath+authoringPath;
    String absoluteLivePath=sitemapParentPath+livePath;
    String absoluteTreeAuthoringPath=sitemapParentPath+treeAuthoringPath;
    String absoluteTreeLivePath=sitemapParentPath+treeLivePath;

    // Get request object
    Request request=(Request)objectModel.get(Constants.REQUEST_OBJECT);
    if(request == null){
      getLogger().error ("No request object");
      return null;
      }
    
    // Get parameters
    String submit=request.getParameter("submit");
    if(submit.equals("cancel")){
      // cancel
      //return ;
      }
    String docid=request.getParameter("docid");


    // Set absolute file paths
    File source=new File(absoluteAuthoringPath+docid+".xml");
    File destination=new File(absoluteLivePath+docid+".xml");
    getLogger().error("COPY\nsource="+source+"\ndestination="+destination);
    getLogger().error("COPY\ntree source="+absoluteTreeAuthoringPath+"\ntree destination="+absoluteTreeLivePath);

    // Get session
    Session session=request.getSession(true);
    if(session == null){
      getLogger().error("No session object");
      return null;
      }

    if(copyFile(source,destination)){
      getLogger().error("Document published");
      }
    else{
      getLogger().error("Document not published");
      }
    if(copyFile(new File(absoluteTreeAuthoringPath),new File(absoluteTreeLivePath))){
      getLogger().error("Tree published");
      }
    else{
      getLogger().error("Tree not published");
      }

    String parent_uri=(String)session.getAttribute("org.wyona.cms.cocoon.acting.PublisherAction.parent_uri");
    HashMap actionMap=new HashMap();
    actionMap.put("parent_uri",parent_uri);
    session.removeAttribute("org.wyona.cms.cocoon.acting.PublisherAction.parent_uri");
    return actionMap;
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
