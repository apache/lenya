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

/**
 * @author Michael Wechner
 * @version 2001.12.10
 */
public abstract class AbstractAuthenticatorAction extends AbstractComplementaryConfigurableAction implements Configurable{
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
    // Get request object
    Request req=(Request)objectModel.get(Constants.REQUEST_OBJECT);
    if(req == null){
      getLogger().error ("No request object");
      return null;
      }
    // Get session
    Session session=req.getSession(true);
    if(session == null){
      getLogger().error("No session object");
      return null;
      }
    if(authenticate(req,new HashMap())){
      getLogger().info(".act(): Authentication succeeded");
      HashMap actionMap=new HashMap();
      actionMap.put("protected_destination",session.getAttribute("protected_destination"));
      return actionMap;
      }
    getLogger().error("Authentication failed");
    return null;
    }
/**
 *
 */
  public abstract boolean authenticate(Request request,Map map) throws Exception;
  }
