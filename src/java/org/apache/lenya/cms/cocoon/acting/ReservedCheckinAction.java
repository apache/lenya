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

import org.wyona.cms.rc.FileReservedCheckInException;

/**
 * @author Edith Chevrier 
 * @version 2002.6.25
 */
public class ReservedCheckinAction extends RevisionControllerAction implements Configurable{
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

//check in 
    try{
      rc.reservedCheckIn(filename, username, true);
     }
    catch(FileReservedCheckInException e){
      actionMap.put("exception","fileReservedCheckInException");
      actionMap.put("filename",filename);
      actionMap.put("checkType",e.typeString);
      actionMap.put("user",e.username);
      actionMap.put("date",e.date);
      getLogger().warn(e.getMessage());
      return actionMap;
      }  
    catch(Exception e){
      actionMap.put("exception","exception");
      actionMap.put("filename",filename);
      getLogger().warn("The document "+filename+" couldn't be checked in");
      return actionMap; 
      }
    return null;                                                                                                                 

/*
    if(true){
      HashMap actionMap=new HashMap();
      actionMap.put("user",username);
      actionMap.put("filename",filename);
      getLogger().warn("Document already checked-out");
      return actionMap;
      }
    else{
      return null;
      }
*/
    }
  }
