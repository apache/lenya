package org.wyona.cms.cocoon.acting;


import java.io.File;
import java.net.URL;
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
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.Tokenizer;
import org.w3c.dom.Document;
import org.wyona.util.Stack;

/**
 * this class does nothing but provide the name of the tempfile to the sitemap
 *
 * @author Martin Lüthi
 * @version 2002.02.06
 */
public class EditorTempfileAction extends AbstractComplementaryConfigurableAction implements Configurable{
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
    // Get tempfile
    String tempFile=(String)session.getAttribute("org.wyona.cms.editor.HTMLForm.tempFile");
    if (tempFile!=null){
      HashMap actionMap=new HashMap();
      actionMap.put("tempFile",tempFile);
      return actionMap;
    }
    return null;
  }
}
