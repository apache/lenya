package org.wyona.cms.cocoon.acting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.CharArrayReader;
import java.io.Writer;
import java.io.CharArrayWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.acting.ValidatorActionHelper;
import org.apache.cocoon.components.parser.Parser;
import org.apache.cocoon.components.parser.Parser;
import org.apache.cocoon.Constants;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.Source;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.PostInputStream;
import org.apache.cocoon.util.PostInputStream;
import org.apache.cocoon.util.Tokenizer;
import org.apache.cocoon.xml.AbstractXMLConsumer;
import org.w3c.dom.Document;
import org.wyona.util.Stack;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * $Id: EditorMainAction.java,v 1.7 2002/07/02 11:18:49 egliwyona Exp $
 *
 * @author Martin Lüthi
 * @version 2002.01.22
 */
public class EditorMainAction extends AbstractComplementaryConfigurableAction implements Configurable{
  /**
   *
   */
  public void configure(Configuration conf) throws ConfigurationException {
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

    // Get request object
    Context context=(Context)objectModel.get(Constants.CONTEXT_OBJECT);
    // the absolute path where Cocoon resides
    //     String xxx = context.getRealPath("/");
    //     getLogger().error("=======> context real path ="+xxx);
    //getLogger().error("=======> editfilename ="+parameters.getParameter("editfilename"));
    

    // Get uri
    String request_uri=request.getRequestURI();
    String sitemap_uri=request.getSitemapURI();
    String action=request.getParameter("action");
    String save=request.getParameter("save");
    getLogger().debug("**** request-uri="+request_uri);
    getLogger().debug("**** sitemap-uri="+sitemap_uri);
    getLogger().debug("**** action="+action);
    
    // prepare the return params
    String xmlErrorFlag = "";
    String xmlErrorMessage = "";
    
    if(save!=null && save.equals("Save")){
      // get the Document and copy it to the temporary file
      getLogger().debug("**** saving ****");
      Source source = resolver.resolve("cocoon:/saveedit");
      getLogger().debug ("======= URL:"+source.getSystemId());
      String editFile = (String)session.getAttribute("org.wyona.cms.editor.HTMLForm.editFile");
      getLogger().debug ("======= Saving to :"+editFile);
      BufferedReader in = new BufferedReader(new InputStreamReader(source.getInputStream()));  
      // first, copy to a temporary string buffer (so we can get it back later, and more than once!)
      StringBuffer trans = new StringBuffer();
      String line;
      while ((line = in.readLine()) != null) {
        // we need this in order to let EditOnPro save the XHTML markup:
        line=org.wyona.util.StringUtil.replace(line, "&lt;", "<");
        line=org.wyona.util.StringUtil.replace(line, "&gt;", ">");
        trans.append(line + "\n");
      }
      getLogger().debug ("----- Contents of the StringBuffer (trans): " + trans.toString());
      
      // check well-formedness
      Parser parser = (Parser) this.manager.lookup(Parser.ROLE);    // get the cocoon parser
      try {
        InputSource iS = new InputSource(new CharArrayReader(trans.toString().toCharArray()));
        if (iS == null) {
          getLogger().error ("----- InputStream is null!");
        } else {
          parser.setContentHandler(new AbstractXMLConsumer(){});    // the parser must have a content handler, it will not be used
          parser.parse(iS);  
        }
      } catch (SAXException saxE) {            // this is the exception we want to catch
        xmlErrorFlag = "X";                    // set the flag for signalling to the page's stylesheet (body.xsl)
        xmlErrorMessage = saxE.getMessage();   // this will also be sent to the stylesheet
        getLogger().debug ("----- SAX-Exception (message): " + saxE.getMessage());
        getLogger().debug ("----- SAX-Exception (stacktrace): ", saxE.getException());
        String tempFile=(String)session.getAttribute("org.wyona.cms.editor.HTMLForm.tempFile");
        if (tempFile!=null){
          in.close();
          HashMap actionMap=new HashMap();
          actionMap.put("tempFile", tempFile);
          actionMap.put("xmlErrorFlag", xmlErrorFlag);
          actionMap.put("xmlErrorMessage", xmlErrorMessage);
          return actionMap;
        }
        return null;
      } catch (Exception e) {                 // something went wrong
        getLogger().error ("----- Exception occured: ", e);
        return null;
      }
        
      // then, copy the string buffer to the temporary file  
      StringReader in2 = new StringReader(trans.toString());
      BufferedWriter out = new BufferedWriter(new FileWriter(editFile));
      int c;
      while ((c = in2.read()) != -1) {
        out.write((char)c);
      }
      in.close();
      in2.close();
      out.close();
      return null;
    } else if (action!=null && action.equals("request")) {
      getLogger().debug("**** request (do nothing) ****");
      HashMap actionMap=new HashMap();
      return actionMap;
    } else {                // we are not saving
      // here comes the checkout, revision control aso.
      boolean checkout=true;
      if(checkout){
        String formeditorPath = context.getRealPath("formeditor");
        String tempFile=formeditorPath+request.getRequestURI();
        //String tempFile=formeditorPath+request.getRequestURI()+".xml";
        //         Source source = resolver.resolve("cocoon:/"+request.getSitemapURI());
        //         String cont=source.getSystemId().substring(10); // remove
        //         "context://"
        String wyonaPath="wyona/cms/pubs/ethz-mat/docs/ethz/mat/";
        String editFile=context.getRealPath("/")+wyonaPath+request.getSitemapURI();
        //String editFile=context.getRealPath("/")+wyonaPath+request.getSitemapURI()+".xml";
        getLogger().debug("**** tempfile="+tempFile);
        getLogger().debug("**** editfile="+editFile);

        File tf=new File(tempFile);
        boolean success=new File(tf.getParent()).mkdirs();

        // get the Document and copy it to the temporary file
        Source source = resolver.resolve("cocoon:/"+request.getSitemapURI()+".temp");
        getLogger().debug ("======= URL:"+source.getSystemId());
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
