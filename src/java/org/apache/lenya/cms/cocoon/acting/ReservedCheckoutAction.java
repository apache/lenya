package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

import java.util.HashMap;
import java.util.Map;

import org.wyona.cms.rc.FileReservedCheckOutException;

/**
 * @author Edth Chevrier 
 * @version 2002.6.25
 */
public class ReservedCheckoutAction extends RevisionControllerAction{
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
    super.act(redirector, resolver, objectModel, src, parameters);
    HashMap actionMap=new HashMap(); 

//check out
    try{
      getLogger().debug(".act(): Filename: "+filename);
      getLogger().debug(".act(): Username: "+username);
      if(filename == null) throw new Exception("Filename is null");
      if(username == null) throw new Exception("Username is null");
      rc.reservedCheckOut(filename, username);
      }
    catch(FileReservedCheckOutException e){
      actionMap.put("exception","fileReservedCheckOutException");
      actionMap.put("filename",filename);
      actionMap.put("user",e.checkOutUsername);
      actionMap.put("date",e.checkOutDate);
      getLogger().warn("Document "+filename+" already checked-out by "+e.checkOutUsername+" since "+e.checkOutDate);
      return actionMap;
      }  
    catch(Exception e){
      actionMap.put("exception","genericException");
      actionMap.put("filename",filename);
      getLogger().warn(".act(): The document "+filename+" couldn't be checked out: "+e);
      return actionMap; 
      }
    return null;
    }
  }
