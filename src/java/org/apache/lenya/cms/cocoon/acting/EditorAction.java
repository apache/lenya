package org.wyona.cms.cocoon.acting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.Constants;
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.acting.ValidatorActionHelper;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.Source;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.Tokenizer;
import org.w3c.dom.Document;
import org.wyona.util.Stack;

/**
 * @author Martin Lüthi
 * @created 2002.01.20
 * @version 2002.01.27
 */
public class EditorAction extends AbstractComplementaryConfigurableAction implements Configurable{
/**
 *
 */
  public void configure(Configuration conf) throws ConfigurationException{
    super.configure(conf);
    }
/**
 * At this time, the action does nothing more than provide the tempfilename to
 * the sitmap
 */
  public Map act(Redirector redirector,SourceResolver resolver,Map objectModel,String src,Parameters parameters) throws Exception {
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

    String tempFile=(String)session.getAttribute("org.wyona.cms.editor.HTMLForm.tempFile");
    getLogger().error("**** tempfile="+tempFile);

    // prepare the return params
    String xmlErrorFlag = "";
    String xmlErrorMessage = "";
    
    if (tempFile!=null){
      // get the Document and copy it to the temporary file
      getLogger().debug("**** saving ****");
      Source source = resolver.resolve("cocoon:/performrequest");
      getLogger().debug ("======= URL:"+source.getSystemId());

      BufferedReader in = new BufferedReader(new InputStreamReader(source.getInputStream()));  
      BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
      String line;
      while ((line = in.readLine()) != null) {
        out.write(line+"\n");
      }
      in.close();
      out.close();

      HashMap actionMap=new HashMap();
      actionMap.put("tempFile",tempFile);
      actionMap.put("xmlErrorFlag", xmlErrorFlag);
      actionMap.put("xmlErrorMessage", xmlErrorMessage);
      return actionMap;
    }
    return null;
  }
}

