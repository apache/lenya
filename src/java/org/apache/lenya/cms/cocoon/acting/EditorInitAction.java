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
// public class EditorInitAction extends AbstractValidatorAction implements ThreadSafe, org.apache.avalon.framework.context.Contextualizable {
public class EditorInitAction extends AbstractValidatorAction implements ThreadSafe {

  String workdirectory = null;

//   public void contextualize(ort.apache.avalon.framework.context.Context context) {
//     workdirectory = context.get(Context.CONTEXT_WORK_DIR) + File.separator + "tempxml";
//     File f = new File(workdirecory);
//     f.mkdirs();
//   }
  /**
   *
   */
  public Map act(Redirector redirector,SourceResolver resolver,Map objectModel,String src,Parameters parameters) throws Exception {
    HashMap sitemapParams = new HashMap();
    Request req=(Request)objectModel.get(Constants.REQUEST_OBJECT);
    //     Context context=(Context)objectModel.get(Constants.CONTEXT_OBJECT);
    
    getLogger().error("=======> action="+parameters.getParameter("action","niet"));

    if ("form".equals(parameters.getParameter("action",null))) {
      // Get request object
      Context context=(Context)objectModel.get(Constants.CONTEXT_OBJECT);
      // the absolute path where Cocoon resides
      String formeditorPath = context.getRealPath("formeditor");
      getLogger().error("=======> formeditor path ="+formeditorPath);

      String random = "random";
      //       String tempfilename   = this.workdirectory + File.separator + random;
      String tempfilename   = formeditorPath + File.separator + "tmp-formeditor.xml";
      getLogger().error("=======> tempfile ="+tempfilename);


      if(req == null){
        getLogger().error ("No request object");
        return null;
      }
      String editfilename=req.getParameter("filename");

      getLogger().error("=======> filename="+editfilename);

      if (editfilename != null) {
        try {
          File editFile = new File(editfilename);
          if (!editFile.isAbsolute()) {
            editfilename = context.getRealPath("")+File.separator+editfilename;
            getLogger().error("=======> new editfilename   ="+editfilename);
          }
          context.setAttribute("editor.filename",editfilename);
          // good old Java has no file copy method, so we do it the hard way
          InputStream is = new FileInputStream(editfilename);                      
          OutputStream os = new FileOutputStream(tempfilename);

          byte[] bytes_buffer = new byte[1024];                                       
          int bytes_read;                                                           
          while((bytes_read = is.read(bytes_buffer)) >= 0){                       
            os.write(bytes_buffer, 0, bytes_read);                           
          }                                                                       

          sitemapParams.put("filename", editfilename);
          sitemapParams.put("tempfilename", tempfilename);
          return sitemapParams;
        } catch (Exception e) {
          getLogger().error("filename not existing "+editfilename+" "+e);
          return null;
        }
      } 
    } else if ("do".equals(parameters.getParameter("action",null))) {
      getLogger().error("doedit =====> filename: "+req.getParameter("filename"));
      getLogger().error("doedit =====> tempfilename: "+req.getParameter("tempfilename"));
      sitemapParams.put("filename", req.getParameter("filename"));
      sitemapParams.put("tempfilename", req.getParameter("tempfilename"));
      return sitemapParams;
    }
    // if none of these targets was requested, say "error"
    return null; 
    
  }
}
