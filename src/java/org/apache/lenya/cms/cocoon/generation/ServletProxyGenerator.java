package org.wyona.cms.cocoon.generation;

import org.apache.avalon.excalibur.xml.Parser;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.cocoon.Constants;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
//import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.cocoon.environment.http.HttpSession;
import org.apache.cocoon.environment.Cookie;
import org.apache.cocoon.environment.SourceResolver;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.PostMethod;

import org.xml.sax.InputSource;

import org.apache.log4j.Category;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Christian Egli
 * @author Michael Wechner
 */
public class ServletProxyGenerator extends org.apache.cocoon.generation.ServletGenerator implements Parameterizable {
  static Category log=Category.getInstance(ServletProxyGenerator.class);
  protected String src;
  //protected org.apache.avalon.excalibur.source.Source src;

    /** The URI of the namespace of this generator. */
    private String URI="http://www.wyona.org/wyona-cms/servletproxygenerator/1.0";
/*
    private String global_container_encoding;
    private String global_form_encoding;
    private String container_encoding;
    private String form_encoding;
*/
/**
 *
 */
    public void parameterize(Parameters parameters)
    throws ParameterException {
        // super.parameterize(parameters);

/*
        global_container_encoding = parameters.getParameter("container-encoding", "ISO-8859-1");
        global_form_encoding = parameters.getParameter("form-encoding", null);
*/
    }
/**
 *
 */
  public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par) throws ProcessingException, SAXException, IOException{
    super.setup(resolver, objectModel, src, par);
    try{
      log.warn("SETUP: "+src);
      this.src=src;
      //this.src=resolver.resolveURI(src);
      }
    catch(Exception e){
      log.error(e);
      }
/*
        container_encoding = par.getParameter("container-encoding", global_container_encoding);
        form_encoding = par.getParameter("form-encoding", global_form_encoding);
*/
    }
/**
 * Generate XML data.
 */
  public void generate() throws SAXException{
    //Request request = ObjectModelHelper.getRequest(objectModel);
    HttpRequest httpRequest = (HttpRequest)objectModel.get(ObjectModelHelper.REQUEST_OBJECT);

    String submitMethod=httpRequest.getMethod();


        Parser parser = null;
        try{
          // Debug
          if(submitMethod.equals("POST")){
            java.io.InputStream is=intercept(httpRequest.getInputStream());
            log.warn("HTTP method:"+submitMethod);
            }
          else if(submitMethod.equals("GET")){
            log.warn("HTTP method:"+submitMethod);
            }
          else{
            log.warn("HTTP method:"+submitMethod);
            }

          // Forward "InputStream", Parameters, QueryString to Servlet
          URL url=createURL(httpRequest);
          //URL url=new URL(this.src.getSystemId());
          //URL url=new URL("http://127.0.0.1:8080/wyona-cms/servlet/HelloWorld");
          log.warn("SERVLET: "+url);
          org.apache.commons.httpclient.HttpMethod httpMethod=null;
          if(submitMethod.equals("POST")){
            httpMethod=new PostMethod();
            Enumeration params=httpRequest.getParameterNames();
            while(params.hasMoreElements()){
              String paramName=(String)params.nextElement();
              String[] paramValues=httpRequest.getParameterValues(paramName);
              for(int i=0;i<paramValues.length;i++){
                ((PostMethod)httpMethod).setParameter(paramName,paramValues[i]);
                }
              }
            //postMethod.setRequestBody("LeviVanya");
            }
          else if(submitMethod.equals("GET")){
            httpMethod=new org.apache.commons.httpclient.methods.GetMethod();
            httpMethod.setQueryString(httpRequest.getQueryString());
            }

          // Forward Session
          HttpSession httpSession=(HttpSession)httpRequest.getSession(false);
          if(httpSession != null){
            Enumeration attributes=httpSession.getAttributeNames();
            while(attributes.hasMoreElements()){
              String attributeName=(String)attributes.nextElement();
              log.warn("SESSION Attribute: name="+attributeName+" value="+httpSession.getAttribute(attributeName));
              }
            }
          Cookie[] cookies=httpRequest.getCookies();
          org.apache.commons.httpclient.Cookie[] transferedCookies=new org.apache.commons.httpclient.Cookie[cookies.length];
          for(int i=0;i<cookies.length;i++){
            log.warn("COOKIE: "+cookies[i].getPath()+" "+cookies[i].getName()+" "+cookies[i].getDomain()+" "+cookies[i].getValue());
            transferedCookies[i]=new org.apache.commons.httpclient.Cookie(cookies[i].getDomain(),cookies[i].getName(),cookies[i].getValue());
            log.warn("COOKIE transfered: "+transferedCookies[i]);
            }

          // Send request to servlet
          httpMethod.setRequestHeader("Content-type","text/plain");
          httpMethod.setPath(url.getPath());

          HttpClient httpClient=new HttpClient();
          if(transferedCookies.length > 0){
            HttpState httpState=new HttpState();
            httpState.addCookies(transferedCookies);
            httpClient.setState(httpState);
            }
          httpClient.startSession(url);
          httpClient.executeMethod(httpMethod);
          byte[] sresponse=httpMethod.getResponseBody();
          log.warn("Response: "+new String(sresponse));
          org.apache.commons.httpclient.Cookie[] tcookies=httpClient.getState().getCookies();
          for(int i=0;i<tcookies.length;i++){
            log.warn("tCOOKIE: "+tcookies[i]);
            }
          httpClient.endSession();


          // Return XML
          InputSource input = new InputSource(new ByteArrayInputStream(sresponse));
          parser = (Parser)this.manager.lookup(Parser.ROLE);
          parser.parse(input, this.xmlConsumer);
          }
        catch(Exception e){
          this.contentHandler.startDocument();
          AttributesImpl attr=new AttributesImpl();
          this.start("servletproxygenerator",attr);
          this.data(".generate(): "+e);
          this.end("servletproxygenerator");
          this.contentHandler.endDocument();

          log.error(e);
          }
        finally{
          this.manager.release(parser);
          }
    }
/**
 * Log input stream for debugging
 *
 * @param in an <code>InputStream</code> value
 * @return an <code>InputStream</code> value
 * @exception Exception if an error occurs
 */
  private InputStream intercept(InputStream in) throws Exception{
    byte[] buffer=new byte[1024];
    int bytes_read;
    ByteArrayOutputStream bufferOut=new ByteArrayOutputStream();
    while((bytes_read=in.read(buffer)) != -1){
      bufferOut.write(buffer,0,bytes_read);
    }
    log.warn("Intercepted Input Stream:\n\n"+bufferOut.toString());
    return new ByteArrayInputStream(bufferOut.toByteArray());
    }
/**
 *
 */
  private URL createURL(HttpRequest request) throws MalformedURLException{
    URL url=null;
    try{
      url=new URL(this.src);
      }
    catch(MalformedURLException e){
      log.warn(".createURL(): "+e);
      url=new URL("http://127.0.0.1:"+request.getServerPort()+this.src);
      }
    return url;
    }
/**
 *
 */
    private void attribute(AttributesImpl attr, String name, String value) {
        attr.addAttribute("",name,name,"CDATA",value);
    }
/**
 *
 */
    private void start(String name, AttributesImpl attr)
    throws SAXException {
        super.contentHandler.startElement(URI,name,name,attr);
        attr.clear();
    }
/**
 *
 */
    private void end(String name)
    throws SAXException {
        super.contentHandler.endElement(URI,name,name);
    }
/**
 *
 */
    private void data(String data)
    throws SAXException {
        super.contentHandler.characters(data.toCharArray(),0,data.length());
    }
}
