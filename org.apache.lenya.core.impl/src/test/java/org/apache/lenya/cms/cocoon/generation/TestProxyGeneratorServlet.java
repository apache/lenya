/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id$  */

package org.apache.lenya.cms.cocoon.generation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Proxy Generator Servlet Test
 */
public class TestProxyGeneratorServlet extends HttpServlet {
    /**
     * Initialize, given a servlet configuration
     * @param config The configuration
     * @throws ServletException if an error occurs
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    /**
     * Get a request
     * @param request The request to get
     * @param response The response to put it in
     * @throws IOException if an IO error occurs
     * @throws ServletException if an error occurs
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        response.setContentType("text/xml");

        PrintWriter writer = response.getWriter();
        writer.print("<?xml version=\"1.0\"?>");
        writer.print("<servlet class=\"" + this.getClass().getName() + "\">");
        writer.print("<request method=\"GET\">");
        writer.print(getRequestInfo(request));
        writer.print(getParameters(request));
        writer.print(getSession(request));
        writer.print(getCookies(request));
        writer.print("</request>");
        writer.print("</servlet>");
    }

    /**
     * Do a POST using the request and store the response in the response object
     * @param request The request
     * @param response The response
     * @throws ServletException if an error occurs
     * @throws IOException if an IO error occurs
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/xml");

        PrintWriter writer = response.getWriter();
        writer.print("<?xml version=\"1.0\"?>");
        writer.print("<servlet class=\"" + this.getClass().getName() + "\">");
        writer.print("<request method=\"POST\">");
        writer.print(getRequestInfo(request));
        writer.print(getParameters(request));
        writer.print(getSession(request));
        writer.print(getCookies(request));
        writer.print("</request>");
        writer.print("</servlet>");
    }

    /**
     * Return the information about a request: URI, Server name and port
     * @param request The request
     * @return The information (as XML)
     */
    public String getRequestInfo(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer("");
        sb.append("<URI>" + request.getRequestURI() + "</URI>");
        sb.append("<servername>" + request.getServerName() + "</servername>");
        sb.append("<serverport>" + request.getServerPort() + "</serverport>");

        return sb.toString();
    }

    /**
     * Get all parameters from a request
     * @param request The request
     * @return The parameters (as XML)
     */
    public String getParameters(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer("");
        Enumeration parameters = request.getParameterNames();
        boolean hasParameters = parameters.hasMoreElements();

        if (hasParameters) {
            sb.append("<parameters>");
        }

        while (parameters.hasMoreElements()) {
            String name = (String) parameters.nextElement();
            String[] values = request.getParameterValues(name);
            sb.append("<parameter name=\"" + name + "\">");

            for (int i = 0; i < values.length; i++) {
                sb.append("<value>" + values[i] + "</value>");
            }

            sb.append("</parameter>");
        }

        if (hasParameters) {
            sb.append("</parameters>");
        }

        return sb.toString();
    }

    /**
     * Get all session attributes of a request
     * @param request The request
     * @return The session attributes (in XML)
     */
    public String getSession(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer("");

        //HttpSession session=request.getSession(true);
        HttpSession session = request.getSession(false);

        if (session != null) {
            sb.append("<session>");

            Enumeration attributes = session.getAttributeNames();

            if (!attributes.hasMoreElements()) {
                sb.append("<noattributes/>");
            }

            while (attributes.hasMoreElements()) {
                String attributeName = (String) attributes.nextElement();
                sb.append("<attribute name=\"" + attributeName + "\">");
                sb.append("" + session.getAttribute(attributeName));
                sb.append("</attribute>");
            }

            sb.append("</session>");
        } else {
            sb.append("<nosession/>");
        }

        return sb.toString();
    }

    /**
     * Return all cookies of a request
     * @param request The request
     * @return The cookies (as XML)
     */
    public String getCookies(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer("");

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            if (cookies.length > 0) {
                sb.append("<cookies>");

                for (int i = 0; i < cookies.length; i++) {
                    sb.append("<cookie>");
                    sb.append("<comment>" + cookies[i].getComment() + "</comment>");
                    sb.append("<domain>" + cookies[i].getDomain() + "</domain>");
                    sb.append("<maxage>" + cookies[i].getMaxAge() + "</maxage>");
                    sb.append("<name>" + cookies[i].getName() + "</name>");
                    sb.append("<path>" + cookies[i].getPath() + "</path>");
                    sb.append("<secure>" + cookies[i].getSecure() + "</secure>");
                    sb.append("<value>" + cookies[i].getValue() + "</value>");
                    sb.append("<version>" + cookies[i].getVersion() + "</version>");
                    sb.append("</cookie>");
                }

                sb.append("</cookies>");
            } else {
                sb.append("<nocookies/>");
            }
        } else {
            sb.append("<nocookies/>");
        }

        return sb.toString();
    }
}
