package org.wyona.cms.cocoon.acting;

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

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.net.URL;

import org.w3c.dom.Document;

import org.wyona.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author Michael Wechner
 * @created 2001.11.18
 * @version 2001.12.28
 */
public class EditorOpenAction extends AbstractComplementaryConfigurableAction implements Configurable{
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

    // Get uri
    String request_uri=req.getRequestURI();
    String sitemap_uri=req.getSitemapURI();
    getLogger().error("request-uri="+request_uri);
    getLogger().error("sitemap-uri="+sitemap_uri);

    boolean checkout=true;
    if(checkout){
      org.dom4j.Document temp=getXMLwithIDs(req);
      Context context=(Context)objectModel.get(Constants.CONTEXT_OBJECT);
      String formeditorPath = context.getRealPath("formeditor");
      String tempFile=formeditorPath+request_uri;
      getLogger().error(tempFile);

      java.io.FileWriter tempWriter=new java.io.FileWriter(tempFile);
      temp.write(tempWriter);
      tempWriter.close();

      HashMap actionMap=new HashMap();
      session.setAttribute("org.wyona.cms.editor.HTMLForm.tempFile",tempFile);
      actionMap.put("tempFile",(String)session.getAttribute("org.wyona.cms.editor.HTMLForm.tempFile"));
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
