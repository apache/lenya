package org.wyona.cms.cocoon.acting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
import org.apache.cocoon.environment.Source;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.Tokenizer;
import org.w3c.dom.Document;
import org.wyona.util.Stack;

/**
 * $Id: EditorMainAction.java,v 1.3 2002/02/02 23:55:23 uid523 Exp $
 *
 * @author Martin Lüthi
 * @created 2002.01.22
 * @version 2002.01.22
 */
public class EditorMainAction extends AbstractComplementaryConfigurableAction implements Configurable{
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

    // Get Source
    getLogger().error ("SOURCE: "+src);
    if(src == null){
      getLogger().error ("No source: e.g. src=\"docs/editfile.xml\"");
      return null;
      }
    org.apache.cocoon.environment.Source inputSource=resolver.resolve(src);

    // get request
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

    // Get request object
    Context context=(Context)objectModel.get(Constants.CONTEXT_OBJECT);

    // Get uri
    String request_uri=request.getRequestURI();
    String sitemap_uri=request.getSitemapURI();
    String action=request.getParameter("action");
    String save=request.getParameter("save");
    getLogger().error("**** request-uri="+request_uri);
    getLogger().error("**** sitemap-uri="+sitemap_uri);
    getLogger().error("**** action="+action);
    
    if(save!=null && save.equals("Save")){
      // get the temporary document and save it back to it's initial location
      getLogger().error("**** saving ****");
      Source source = resolver.resolve("cocoon:/saveedit");
      getLogger().error ("======= URL:"+source.getSystemId());
      String editFile = (String)session.getAttribute("org.wyona.cms.editor.HTMLForm.editFile");
      getLogger().error ("**** Saving to :"+editFile);

      try{
        BufferedReader in = new BufferedReader(new InputStreamReader(source.getInputStream()));  
        BufferedWriter out = new BufferedWriter(new FileWriter(editFile));
        String line;
        while ((line = in.readLine()) != null) {
          // we need this in order to let EditOnPro save the XHTMLmarkup:
          line=org.wyona.util.StringUtil.replace(line, "&lt;", "<");
          line=org.wyona.util.StringUtil.replace(line, "&gt;", ">");
          out.write(line+"\n");
        } 
        in.close();
        out.close();
      }catch (IOException e) {
        getLogger().error("Unable to copy temporary file "+e);
      }
      getLogger().error ("**** file saved to :"+editFile);
      return null;
    } else if(action!=null && action.equals("request")){
      getLogger().error("**** request (do nothing) ****");
      HashMap actionMap=new HashMap();
      return actionMap;
    } else {
      // at this place  the checkout, revision control aso. will be plugged in
      boolean checkout=true;
      if(checkout){
        String formeditorPath = context.getRealPath("formeditor");
        String tempFile=formeditorPath+request.getRequestURI()+".xml";
        String editFile=inputSource.getSystemId().substring(5); // 5: remove protocol "file:"
        getLogger().error("****TINU EDITFILE: "+editFile);

        File tf=new File(tempFile);
        boolean success=new File(tf.getParent()).mkdirs();

        // get the Document and copy it to the temporary file
        Source source = resolver.resolve("cocoon:/"+request.getSitemapURI()+".temp");
        getLogger().error ("======= URL:"+source.getSystemId());
        BufferedReader in = new BufferedReader(new InputStreamReader(source.getInputStream()));  
        BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
        String line;
        while ((line = in.readLine()) != null) {
          out.write(line+"\n");
        }
        in.close();
        out.close();

        session.setAttribute("org.wyona.cms.editor.HTMLForm.tempFile",tempFile);
        session.setAttribute("org.wyona.cms.editor.HTMLForm.editFile",editFile);
        HashMap actionMap=new HashMap();
        actionMap.put("tempFile",tempFile);
        actionMap.put("editFile",editFile);
        return actionMap;
      }
    }
    return null;
  }

}
