package org.wyona.cms.cocoon.generation;  

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.IOException;

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
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
	throws IOException, ServletException {
        log.warn("GET");

	response.setContentType("text/xml");
	PrintWriter writer = response.getWriter();
	
	writer.println("<hello>world</hello>");
  	
    }
/**
 *
 */
    public void doPost(HttpServletRequest req,
		       HttpServletResponse resp)
	throws ServletException, IOException {
        log.warn("POST");
	doGet(req, resp);
    }
}
