package org.wyona.cms.cocoon.acting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;
import java.lang.String;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

//import javax.servlet.ServletInputStream;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ComposerAction;
import org.apache.cocoon.Constants;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.PostInputStream;

/**
 * Interfaces with Xopus: handles the requests and replies to them
 *
 * @author Memo Birgi
 * @created 2002.02.21
 * @version 0.1
 */
public class XopusHandlerAction extends ComposerAction {

  public java.util.Map act (Redirector redirector, 
                            SourceResolver resolver, 
                            Map objectModel, 
                            String source, 
                            Parameters params)
                            throws IOException {

//     File homedir = new File(System.getProperty("user.home"));
    File workfile = new File("c:/home/work/playground/wyona-cms/xopus", "xopus-temp.txt");
//     File workfile = new File(homedir, "xopus-temp.txt");
    getLogger().debug("workfile="+workfile.getAbsoluteFile());

    // Get request object
    Request request=(Request)objectModel.get(Constants.REQUEST_OBJECT);
    if(request == null){
      getLogger().error ("No request object");
      return null;
    }
    else {
      getLogger().debug ("SitemapURI = " + request.getSitemapURI());
    }
    
    // Get session
//     Session session=request.getSession(true);
//     if(session == null){
//       getLogger().error("No session object");
//       return null;
//     }

    boolean appendFlag = true;
    PrintWriter out = new PrintWriter(new FileWriter(workfile.toString(), appendFlag));
    
    HttpRequest httpReq = (HttpRequest) objectModel.get(HttpEnvironment.HTTP_REQUEST_OBJECT);
    //InputStream input = httpReq.getInputStream();
    int length = request.getContentLength();
    //PostInputStream reqContent = new PostInputStream(input, length);
        
//     Enumeration reqParams = request.getParameterNames();
//     String currParamName = null;
//     while (reqParams.hasMoreElements()) {
//       currParamName = (String)reqParams.nextElement();
//       out.println("Request Parameter: " + currParamName + " = " + request.getParameter(currParamName));
//       getLogger().debug("Request Parameter: " + currParamName + " = " + request.getParameter(currParamName));
//     }
//     Enumeration reqAttribs = request.getAttributeNames();
//     String currAttribName = null;
//     while (reqAttribs.hasMoreElements()) {
//       currAttribName = (String)reqAttribs.nextElement();
//       out.println("Request Attribute: " + currAttribName + " = " + request.getAttribute(currAttribName));
//       getLogger().debug("Request Attribute: " + currAttribName + " = " + request.getAttribute(currAttribName));
//     }
//     Enumeration reqHeaders = request.getHeaderNames();
//     String currHeaderName = null;
//     while (reqHeaders.hasMoreElements()) {
//       currHeaderName = (String)reqHeaders.nextElement();
//       out.println("Request Header: " + currHeaderName + " = " + request.getHeader(currHeaderName));
//       getLogger().debug("Request Header: " + currHeaderName + " = " + request.getHeader(currHeaderName));
//     }

    out.close();
    
    
    Map sitemapParams = new HashMap();
    sitemapParams.put("world", "hello");

//     Request request = (Request) objectModel.get(Constants.REQUEST_OBJECT);
// 
//     request.setAttribute("hello", "world");

    return sitemapParams;
  }
}

