package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.Constants;
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.Tokenizer;

import java.util.HashMap;
import java.util.Map;

import org.wyona.cms.ac.Identity;
import org.wyona.cms.rc.RevisionController;

/**
 * @author Michael Wechner
 * @version 2002.6.17
 */
public class RevisionControllerAction extends AbstractComplementaryConfigurableAction implements Configurable{
  String rcmlDirectory=null;
  String backupDirectory=null;
  RevisionController rc=null;
/**
 *
 */
  public void configure(Configuration conf) throws ConfigurationException{
    super.configure(conf);

    rcmlDirectory=conf.getChild("rcmlDirectory").getAttribute("href");
    backupDirectory=conf.getChild("backupDirectory").getAttribute("href");

    if(getLogger().isDebugEnabled()) {
      getLogger().debug("CONFIGURATION:\nRCML Directory="+rcmlDirectory);
      getLogger().debug("CONFIGURATION:\nRCBAK Directory="+backupDirectory);
      }
    }
/**
 *
 */
  public Map act(Redirector redirector,SourceResolver resolver,Map objectModel,String src,Parameters parameters) throws Exception {
    // Get Absolute Path of sitemap
    org.apache.cocoon.environment.Source inputSource=resolver.resolve("");
    String sitemapParentPath=inputSource.getSystemId();
    sitemapParentPath=sitemapParentPath.substring(5); // Remove "file:" protocoll
    getLogger().error("RESOLVED SOURCE: "+sitemapParentPath);
    // /Get Absolute Path of sitemap

    // Initialize Revision Controller
    rc=new RevisionController(sitemapParentPath+rcmlDirectory,sitemapParentPath+backupDirectory);
    getLogger().debug(""+rc);
    // /Initialize Revision Controller

    // Get request object
    Request request=(Request)objectModel.get(Constants.REQUEST_OBJECT);
    if(request == null){
      getLogger().error ("No request object");
      return null;
      }
    // Get session
    Session session=request.getSession(true);
    if(session == null){
      getLogger().error("No session object");
      return null;
      }

    String sitemap_uri=request.getSitemapURI();
    String request_uri=request.getRequestURI();
    Identity identity=(Identity)session.getAttribute("org.wyona.cms.ac.Identity");
    getLogger().debug(sitemap_uri);
    getLogger().debug(request_uri);
    getLogger().debug(""+identity);

    if(true){
      HashMap actionMap=new HashMap();
      actionMap.put("user","levi");
      getLogger().warn("Document already checked-out");
      return actionMap;
      }
    else{
      return null;
      }
    }
  }
