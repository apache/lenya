package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.Constants; 
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session; 
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import java.io.FileNotFoundException;
import java.io.ByteArrayOutputStream;

import org.wyona.xml.DOMWriter;
import org.wyona.cms.rc.FileReservedCheckInException;

import org.wyona.xml.DOMParserFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Edith Chevrier 
 * @version 2002.8.02
 */
public class RollbackAction extends RevisionControllerAction implements Configurable{
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

    // Get request object
    Request request=(Request)objectModel.get(Constants.REQUEST_OBJECT);
    if(request == null){
      getLogger().error ("No request object");
      return null;
      }
 
    // Get parameters                                                                                                                       
    String action = request.getParameter("action");
    String rollbackTime = request.getParameter("rollbackTime");
 
    if (action != null) {
      if (action.equals("rollback")) {
        // Do the rollback to an earlier version
        long newtime=0;
        try {
          newtime = rc.rollback(filename, username, true, new Long(rollbackTime).longValue());
        } catch ( FileNotFoundException e) {
           getLogger().error("Unable to roll back!" + e);
           return null;
        } catch (Exception e) {
           getLogger().error("Unable to roll back!" + e);
           return null;
        }
        getLogger().debug("rollback complete, old (and now current) time was " + rollbackTime + " backup time is " + newtime);
 
        String location=request.getHeader("Referer");

        getLogger().debug("redirect to " +location);
        actionMap.put("location",location);
        return actionMap; 

      } else if (action.equals("view")){
        // Show the contents of an old revision
        String backupFilename = rc.getBackupFilename(new Long(rollbackTime).longValue(), filename);

        DOMParserFactory dpf=new DOMParserFactory();
        Document doc = dpf.getDocument(backupFilename);

//      Document doc = new XPSAssembler("enclose").assemble("file:" + backupFilename);

        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        try {
          new DOMWriter(outBuffer).printWithoutFormatting(doc);
        } catch (Exception e) {
          getLogger().error("RollbackAction: Unable to view file, DOMWriter threw exception: " + backupFilename);
          return null;
        }

        actionMap.put("version", outBuffer.toString());
        return actionMap;

      } else {  
        getLogger().error("The action is no defined");
        return null;
      }
    } else {  
      getLogger().error("The action is null");
      return null;
    }
  }

}
