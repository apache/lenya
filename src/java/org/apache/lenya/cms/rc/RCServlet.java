/*
 * $Id: RCServlet.java,v 1.3 2003/02/18 18:16:41 egli Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
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
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.wyona.cms.rc;

import org.apache.log4j.Category;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @author Edith Chevrier
 * @version 2002.7.16
 */
public class RCServlet extends javax.servlet.http.HttpServlet {
    static Category log = Category.getInstance(RCServlet.class);

    /**
     * DOCUMENT ME!
     *
     * @return The ServletInfo value
     */
    public String getServletInfo() {
        return this.getClass().getName();
    }

    /**
     * DOCUMENT ME!
     *
     * @param config Description of Parameter
     *
     * @exception ServletException Description of Exception
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    /**
     * DOCUMENT ME!
     *
     * @param request Description of Parameter
     * @param response Description of Parameter
     *
     * @exception ServletException Description of Exception
     * @exception IOException Description of Exception
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        PrintWriter out = new PrintWriter(response.getWriter());
	// FIXME: we would like to return some XML which can be used
	// to create a user interface. Maybe this should be done with
	// XSP? Check older versions for code that emitted HTML.
        out.println("<rollbackservlet/>");

        out.close();
    }
}
