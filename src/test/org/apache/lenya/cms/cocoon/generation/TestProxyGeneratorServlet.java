/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

/* $Id: TestProxyGeneratorServlet.java,v 1.3 2004/03/04 15:41:09 egli Exp $  */

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


public class TestProxyGeneratorServlet extends HttpServlet {
    /**
     * DOCUMENT ME!
     *
     * @param config DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ServletException DOCUMENT ME!
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
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
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
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getRequestInfo(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer("");
        sb.append("<URI>" + request.getRequestURI() + "</URI>");
        sb.append("<servername>" + request.getServerName() + "</servername>");
        sb.append("<serverport>" + request.getServerPort() + "</serverport>");

        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     *
     * @return DOCUMENT ME!
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
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     *
     * @return DOCUMENT ME!
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
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     *
     * @return DOCUMENT ME!
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
