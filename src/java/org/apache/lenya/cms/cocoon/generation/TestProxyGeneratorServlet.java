/*
 * $Id: TestProxyGeneratorServlet.java,v 1.7 2003/03/06 20:45:41 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.cms.cocoon.generation;

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
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @author Christian Egli
 * @version 2002.8.27
 */
public class TestProxyGeneratorServlet extends HttpServlet {
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
