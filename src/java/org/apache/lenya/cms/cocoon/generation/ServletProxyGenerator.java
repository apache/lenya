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
import org.apache.cocoon.environment.SourceResolver;

import org.apache.commons.httpclient.HttpClient;
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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Christian Egli
 * @author Michael Wechner
 */
public class ServletProxyGenerator extends org.apache.cocoon.generation.ServletGenerator implements Parameterizable {
  static Category log=Category.getInstance(ServletProxyGenerator.class);

    /** The URI of the namespace of this generator. */
    private String URI="http://xml.apache.org/cocoon/requestgenerator/2.0";
    private String global_container_encoding;
    private String global_form_encoding;
    private String container_encoding;
    private String form_encoding;
/**
 *
 */
    public void parameterize(Parameters parameters)
    throws ParameterException {
        // super.parameterize(parameters);

        global_container_encoding = parameters.getParameter("container-encoding", "ISO-8859-1");
        global_form_encoding = parameters.getParameter("form-encoding", null);
    }
/**
 *
 */
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
    throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, par);

        container_encoding = par.getParameter("container-encoding", global_container_encoding);
        form_encoding = par.getParameter("form-encoding", global_form_encoding);
    }
/**
 * Generate XML data.
 */
  public void generate() throws SAXException{
    //Request request = ObjectModelHelper.getRequest(objectModel);
    HttpRequest httpRequest = (HttpRequest)objectModel.get(ObjectModelHelper.REQUEST_OBJECT);

    String httpMethod=httpRequest.getMethod();


        Parser parser = null;
        try{
          // Debug
          Enumeration params=httpRequest.getParameterNames();
          //String[] paramValues=httpRequest.getParameterValues();
          while(params.hasMoreElements()){
            String paramName=(String)params.nextElement();
            log.warn("Parameter name:"+paramName);
            log.warn("Parameter value:"+httpRequest.getParameter(paramName));
            }

          if(httpMethod.equals("POST")){
            java.io.InputStream is=intercept(httpRequest.getInputStream());
            log.warn("HTTP method:"+httpMethod);
            }
          else if(httpMethod.equals("GET")){
            log.warn("HTTP method:"+httpMethod);
            }
          else{
            log.warn("HTTP method:"+httpMethod);
            }

          // Forward InputStream to Servlet
          URL url=new URL("http://127.0.0.1:8080/wyona-cms/servlet/HelloWorld?param1=levi&param2=vanya");
          org.apache.commons.httpclient.HttpMethod postMethod=null;
          if(httpMethod.equals("POST")){
            postMethod=new PostMethod();
            }
          else if(httpMethod.equals("GET")){
            postMethod=new org.apache.commons.httpclient.methods.GetMethod();
            }
          //postMethod.setQueryString("param1=levi&param2=vanya");
          //PostMethod postMethod=new PostMethod();
          //postMethod.setRequestBody("LeviVanya");
          postMethod.setRequestHeader("Content-type","text/plain");
          postMethod.setPath(url.getPath());

          HttpClient httpClient=new HttpClient();
          httpClient.startSession(url);
          httpClient.executeMethod(postMethod);
          byte[] sresponse=postMethod.getResponseBody();
          log.warn("Response: "+new String(sresponse));
          httpClient.endSession();


          // Return XML
          InputSource input = new InputSource(new ByteArrayInputStream(sresponse));
          parser = (Parser)this.manager.lookup(Parser.ROLE);
          parser.parse(input, this.xmlConsumer);
          }
        catch(Exception e){
          log.error(e);
          }
        finally{
          this.manager.release(parser);
          }


/*
        this.contentHandler.startDocument();
        AttributesImpl attr=new AttributesImpl();
        this.start("hello",attr);
        this.data("Christian");
        this.end("hello");
        this.contentHandler.endDocument();
*/
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
}
