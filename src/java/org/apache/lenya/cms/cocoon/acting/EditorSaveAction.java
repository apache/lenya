package org.wyona.cms.cocoon.acting;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.Constants;
import org.apache.cocoon.acting.AbstractValidatorAction;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.Tokenizer;



/**
 * @author Martin Lüthi
 * @created 2002.01.10
 * @version 2002.01.10
 */
public class EditorSaveAction extends AbstractValidatorAction implements ThreadSafe{

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
    //     String editfilename = (String)request.getParameter("filename");
    //     String tempfilename = (String)request.getParameter("tempfilename");
    Session session=request.getSession(true);
    if(session == null){
      getLogger().error ("No session object");
      return null;
    }
    String tempfilename = (String)session.getAttribute("org.wyona.cms.cocoon.acting.EditorTempfile");
    String editfilename = (String)session.getAttribute("org.wyona.cms.cocoon.acting.EditorEditfile");
    String finalredirect= (String)session.getAttribute("org.wyona.cms.cocoon.acting.EditorFinalRedirect");

    //    String editfilename=request.getParameter("filename");
    getLogger().error("=======> editfilename   ="+editfilename);
    getLogger().error("=======> tempfilename   ="+tempfilename);
    getLogger().error("=======> finalRedirect   ="+finalredirect);

    if (editfilename != null) {
      try {
        File editFile = new File(editfilename);
//         if (!editFile.isAbsolute()) {
//           editfilename = context.getRealPath("")+File.separator+editfilename;
//           getLogger().error("=======> new editfilename   ="+editfilename);
//         }
        // good old Java has no file copy method, so we do it the hard way
        InputStream is = new FileInputStream(tempfilename);
        OutputStream os = new FileOutputStream(editfilename);

        byte[] bytes_buffer = new byte[1024];                                       
        int bytes_read;                                                           
        while((bytes_read = is.read(bytes_buffer)) >= 0){                       
          os.write(bytes_buffer, 0, bytes_read);                           
        }                                                                       
        getLogger().error("------------------------------------------ Writing file  "+editfilename);
        HashMap sitemapParams = new HashMap();
        sitemapParams.put("tempfilename", tempfilename);
        sitemapParams.put("finalRedirect", finalredirect);
        return sitemapParams;
      } catch (Exception e) {
        getLogger().error("filename not existing "+editfilename+" "+e);
        return null;
      }
    } 
    return null; 
    
    }
  }
