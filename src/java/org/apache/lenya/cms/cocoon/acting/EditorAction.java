package org.wyona.cms.cocoon.acting;

import java.io.File;
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
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.Tokenizer;
import org.w3c.dom.Document;
import org.wyona.util.Stack;

/**
 * @author Michael Wechner
 * @created 2001.11.18
 * @version 2001.12.28
 */
public class EditorAction extends AbstractComplementaryConfigurableAction implements Configurable{
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
    getLogger().error("********* here i am Editor *****************************************");

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

    String tempfile=(String)session.getAttribute("org.wyona.cms.editor.HTMLForm.tempFile");
    getLogger().error("**** tempfile="+tempfile);
    if (tempfile!=null){
      Enumeration reqparams=request.getParameterNames();                                                  
      String newparams="?";
      while (reqparams.hasMoreElements()) {                        
        String param=(String)reqparams.nextElement();
        String[] values=request.getParameterValues(param);
        if (values!=null) for (int i=0; i<values.length; i++) {
          getLogger().error("parameter: " +param+" : "+values[i]);
          newparams=newparams+param+"="+values[i]+"&";
        }
      }
      getLogger().error("**** new parameter: " +newparams);

//       String request_uri=request.getRequestURI();
//       String thisContext=request_uri.substring(0,request_uri.lastIndexOf("/formedit"));
//       if (thisContext.startsWith("/cocoon/")){
//         thisContext=thisContext.substring(8);
//       }
//       getLogger().error("**** request part="+thisContext);

      // get the Document and write it to the temporary file
//       String url="http://127.0.0.1:"+request.getServerPort()+"/cocoon/"+thisContext+"/dorequest"+newparams;
//       getLogger().error("**** URI="+url);
//       org.dom4j.Document temp = new org.dom4j.io.SAXReader().read(url);
//       java.io.FileWriter tempWriter=new java.io.FileWriter(tempfile);
//       temp.write(tempWriter);
//       tempWriter.close();

      HashMap actionMap=new HashMap();
      actionMap.put("tempFile",tempfile);
      return actionMap;
    }

    return null;
    }
/**
 *
 */
  private org.dom4j.Document getXMLwithIDs(Request request) throws Exception{
    String url="http://127.0.0.1:"+request.getServerPort()+request.getRequestURI()+".temp";
    getLogger().error(url);
    //DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
    //DocumentBuilder db=dbf.newDocumentBuilder();
    return new org.dom4j.io.SAXReader().read(url);
    //return db.parse(new URL(url).openStream());
  }
}
