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

import org.apache.log4j.Category;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author Michael Wechner
 * @version 2002.02.04
 */
public class PublisherAction extends AbstractComplementaryConfigurableAction implements Configurable {

  private String authoringPath = null;
  private String livePath = null;
  private String treeAuthoringPath = null;
  private String treeLivePath = null;
    
  //private String replication_queue_href=null;
  private String directoryPrefix=null;
  private String prefixSubstitute=null; //"s/\\/wyona-cms\\/unipublic//g";

  static Category log=Category.getInstance(PublisherAction.class);

/**
 *
 */
  public void configure(Configuration conf) throws ConfigurationException{
    super.configure(conf);

    authoringPath = conf.getChild("authoring").getAttribute("href");
    livePath = conf.getChild("live").getAttribute("href");
    treeAuthoringPath = conf.getChild("tree-authoring").getAttribute("href");
    treeLivePath = conf.getChild("tree-live").getAttribute("href");

    directoryPrefix=conf.getChild("export-directory").getAttribute("prefix");
    prefixSubstitute=conf.getChild("export-url-prefix").getAttribute("substitute");

    //replication_queue_href=conf.getChild("replication-queue").getAttribute("href");
    if (getLogger().isDebugEnabled()) {
	getLogger().debug("CONFIGURATION:\nauthoring path=" +
			  authoringPath + "\nlive path=" + livePath);
	getLogger().debug("CONFIGURATION:\ntree authoring path=" +
			  treeAuthoringPath + "\ntree live path=" +
			  treeLivePath);
	
	//getLogger().debug("CONFIGURATION:\nReplication Queue: href="+replication_queue_href);
      getLogger().debug("CONFIGURATION:\nDirectory Prefix: href="+directoryPrefix);
      getLogger().debug("CONFIGURATION:\nPrefix Substitute: href="+prefixSubstitute);
    }
  }
    /**
     *
     */
    public Map act(Redirector redirector, SourceResolver resolver,
		   Map objectModel, String src, Parameters parameters)
	throws Exception {

	// Get Source
	org.apache.cocoon.environment.Source inputSource = resolver.resolve("");
	String sitemapParentPath = inputSource.getSystemId();
	// Remove "file:" protocoll
	sitemapParentPath = sitemapParentPath.substring(5); 
	
	getLogger().error("RESOLVED SOURCE: " + sitemapParentPath);
	getLogger().error("CONFIGURATION:\nauthoring path=" + authoringPath +
			  "\nlive path=" + livePath);
	
	// Set absolute paths
	String absoluteAuthoringPath = sitemapParentPath + authoringPath;
	String absoluteLivePath = sitemapParentPath + livePath;
	String absoluteTreeAuthoringPath = sitemapParentPath + treeAuthoringPath;
	String absoluteTreeLivePath = sitemapParentPath + treeLivePath;

    String exportDirectory=sitemapParentPath+directoryPrefix;

	// Get request object
	Request request = (Request)objectModel.get(Constants.REQUEST_OBJECT);

	if (request == null) {
	    getLogger().error ("No request object");
	    return null;
	}
	
	// Get parameters
/*
	String submit = request.getParameter("submit");
	if (submit.equals("cancel")) {
	    // cancel
	    //return ;
	}
*/
	String docid = request.getParameter("docid");
	String docids = request.getParameter("docids");

	// Update (copy) files
	StringTokenizer st = new StringTokenizer(docids,",");
	while (st.hasMoreTokens()) {
	    String docId = st.nextToken();
	    File sourceFile = new File(absoluteAuthoringPath+docId);
	    File destinationFile = new File(absoluteLivePath+docId);
	    if (copyFile(sourceFile, destinationFile)) {
		getLogger().error("Document published: " + sourceFile +
				  " " + destinationFile);
	    } else {
		getLogger().error("EXCEPTION: Document not published: " +
				  sourceFile + " " + destinationFile);
	    }
	}
	
	// Update (copy) tree
	if (copyFile(new File(absoluteTreeAuthoringPath),
		     new File(absoluteTreeLivePath))) {
	    getLogger().error("COPY\ntree source=" + absoluteTreeAuthoringPath +
			      "\ntree destination=" + absoluteTreeLivePath);
	    getLogger().error("Tree published");
	} else {
	    getLogger().error("Tree not published");
	}

    // Export static HTML
    exportStaticHTML(docid,request.getServerPort(),exportDirectory);
	
	// Get session
	Session session = request.getSession(true);
	if (session == null) {
	    getLogger().error("No session object");
	    return null;
	}
	
	// Return referer
	String parent_uri = (String)session.getAttribute("org.wyona.cms.cocoon.acting.PublisherAction.parent_uri");
	HashMap actionMap = new HashMap();
	actionMap.put("parent_uri", parent_uri);
	session.removeAttribute("org.wyona.cms.cocoon.acting.PublisherAction.parent_uri");
	return actionMap;
    }

    /**
     *
     */
    private boolean copyFile(File source, File destination) throws Exception {

	if (source.exists()) {
	    File parentDestination = new File(destination.getParent());
	    if (!parentDestination.exists()) {
		getLogger().warn("Directory will be created: " +
				 parentDestination);
		parentDestination.mkdirs();
	    }
	    org.apache.avalon.excalibur.io.FileUtil.copyFile(source,destination);
	    return true;
	} else {
	    getLogger().error("File does not exist: " + source);
	    return false;
	}
    }
/**
 * Export Static HTML
 */
  public void exportStaticHTML(String docid,int port,String exportDirectory){
    try{
      org.wyona.net.WGet wget=new org.wyona.net.WGet();
      getLogger().info("Export directory: "+exportDirectory);
      wget.setDirectoryPrefix(exportDirectory);
      URL url=new URL("http://127.0.0.1:"+port+docid);
      getLogger().info("Export static HTML: "+url);
      byte[] response=wget.download(url,prefixSubstitute);
      wget.saveToFile(url.getFile(),response);
      }
    catch(Exception e){
      getLogger().error(""+e);
      }
    }
  }
