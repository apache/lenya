package org.wyona.cms.cocoon.generation;  

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.log4j.Category;

/**
 * @author Michael Wechner
 * @author Christian Egli
 * @version 2002.7.3
 */
public class HelloWorld extends HttpServlet {
  static Category log=Category.getInstance(HelloWorld.class);
/**
 *
 */
  public void init(ServletConfig config) throws ServletException{
    super.init(config);
    }
/**
 *
 */
  public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
    log.warn("GET");
    response.setContentType("text/xml");
    PrintWriter writer = response.getWriter();
    writer.print("<servlet class=\""+this.getClass().getName()+"\">");
    writer.print("<request method=\"GET\">");
    writer.print(getParameters(request));
    writer.print("</request>");
    writer.print("</servlet>");
    }
/**
 *
 */
  public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
    log.warn("POST");
    response.setContentType("text/xml");
    PrintWriter writer = response.getWriter();
    writer.print("<servlet class=\""+this.getClass().getName()+"\">");
    writer.print("<request method=\"POST\">");
    writer.print(getParameters(request));
    writer.print("</request>");
    writer.print("</servlet>");
    }
/**
 *
 */
  public String getParameters(HttpServletRequest request){
    StringBuffer sb=new StringBuffer("");
    Enumeration parameters=request.getParameterNames();
    boolean hasParameters=parameters.hasMoreElements();
    if(hasParameters){
      sb.append("<parameters>");
      }
    while(parameters.hasMoreElements()){
      String name=(String)parameters.nextElement();
      String[] values=request.getParameterValues(name);
      sb.append("<parameter name=\""+name+"\">");
      for(int i=0;i<values.length;i++){
        sb.append("<value>"+values[i]+"</value>");
        }
      sb.append("</parameter>");
      }
    if(hasParameters){
      sb.append("</parameters>");
      }
    return sb.toString();
    }
}
