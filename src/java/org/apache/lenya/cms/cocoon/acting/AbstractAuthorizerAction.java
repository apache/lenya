package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.Constants;
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.acting.ValidatorActionHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.Tokenizer;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.wyona.util.Stack;

/**
 * @author Michael Wechner
 * @created 2001.11.18
 * @version 2001.12.28
 */
public abstract class AbstractAuthorizerAction extends AbstractComplementaryConfigurableAction implements Configurable{
  String[] public_hrefs=null;
  boolean logRequests=false;
/**
 *
 */
  public void configure(Configuration conf) throws ConfigurationException{
    super.configure(conf);
    Configuration[] publics=conf.getChildren("public");
    public_hrefs=new String[publics.length];
    for(int i=0;i<publics.length;i++){
      public_hrefs[i]=publics[i].getAttribute("href");
      getLogger().debug("CONFIGURATION: public: "+public_hrefs[i]);
      }
    Configuration log=conf.getChild("log");
    if(log.getValue("off").equals("on")){
      logRequests=true;
      }
    if(logRequests){
      getLogger().debug("CONFIGURATION: log requests: on");
      }
    else{
      getLogger().debug("CONFIGURATION: log requests: off");
      }
    }
/**
 *
 */
  public Map act(Redirector redirector,SourceResolver resolver,Map objectModel,String src,Parameters parameters) throws Exception {
    // Get request object
    Request req=(Request)objectModel.get(Constants.REQUEST_OBJECT);
    if(req == null){
      getLogger().error ("No request object");
      return null;
      }
    Session session=req.getSession(true);
    if(session == null){
      getLogger().error("No session object");
      return null;
      }

    // Get uri
    String request_uri=req.getRequestURI();
    String sitemap_uri=req.getSitemapURI();
    getLogger().error("request-uri="+request_uri);
    getLogger().error("sitemap-uri="+sitemap_uri);

    // Set history
    Stack history=(Stack)session.getAttribute("org.wyona.cms.cocoon.acting.History");
    if(history == null){
      history=new Stack(10);
      session.setAttribute("org.wyona.cms.cocoon.acting.History",history);
      }
    history.push(sitemap_uri);

    // Check public uris from configuration above. Should only be used during development before the implementation of a concrete authorizer.
    for(int i=0;i<public_hrefs.length;i++){
      if(sitemap_uri.equals(public_hrefs[i])){
        getLogger().error("Permission granted: "+request_uri);
        HashMap actionMap=new HashMap();
        return actionMap;
        }
      }

    session.setAttribute("protected_destination",request_uri);

    HashMap actionMap=new HashMap();
    if(authorize(req,actionMap)){
      getLogger().error("Permission granted: "+request_uri);
      return actionMap;
      }
    getLogger().error("Permission denied: "+request_uri);
    return null;
    }
/**
 * Should be implemented by a concrete authorizer
 */
  public abstract boolean authorize(Request request,Map map) throws Exception;
  }
