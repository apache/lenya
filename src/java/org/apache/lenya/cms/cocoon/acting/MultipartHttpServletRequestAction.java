/*
 * $Id: MultipartHttpServletRequestAction.java,v 1.1 2003/05/27 02:34:59 michi Exp $
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
package org.apache.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Source;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.cocoon.util.PostInputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;

import java.util.*;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.servlet.multipart.MultipartHttpServletRequest;
import org.apache.cocoon.servlet.multipart.Part;


/**
 * @author <a href="mailto:michael.wechner@wyona.org">Michael Wechner</a>
 */
public class MultipartHttpServletRequestAction extends AbstractConfigurableAction implements ThreadSafe {

    /**
     * Describe <code>configure</code> method here.
     *
     * @param conf a <code>Configuration</code> value
     *
     * @exception ConfigurationException if an error occurs
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);
    }

    /**
     * Describe <code>act</code> method here.
     *
     * @param redirector a <code>Redirector</code> value
     * @param resolver a <code>SourceResolver</code> value
     * @param objectModel a <code>Map</code> value
     * @param source a <code>String</code> value
     * @param parameters a <code>Parameters</code> value
     *
     * @return a <code>Map</code> value
     *
     * @exception Exception if an error occurs
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source, Parameters parameters) throws Exception {

        Request request = ObjectModelHelper.getRequest(objectModel);
        String title = request.getParameter("title");
        getLogger().debug(".act(): title: " + title);
        String usecase = request.getParameter("lenya.usecase");
        getLogger().debug(".act(): usecase: " + usecase);

/*
        HttpRequest httpRequest = (HttpRequest) objectModel.get(ObjectModelHelper.REQUEST_OBJECT);
        int length = httpRequest.getContentLength();
        PostInputStream pis = new PostInputStream(intercept(httpRequest.getInputStream()), length);
*/

        java.util.Enumeration params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String name = (String) params.nextElement();
            getLogger().debug(".act(): Parameter: " + name);
        }

        Part part = (Part) request.get("title");
        if (part != null) {
            getLogger().debug(".act(): TITLE: " + part);
        } else {
            getLogger().error(".act(): No such parameter: title");
        }

        getLogger().debug(".act(): Classname: " + request.getClass().getName());

/*
        byte[] boundary = new byte[65535];
        try {
            org.apache.commons.fileupload.MultipartStream multipartStream = new org.apache.commons.fileupload.MultipartStream(httpRequest.getInputStream(), boundary);
            boolean nextPart = multipartStream.skipPreamble();
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            while(nextPart) {
                getLogger().debug(".act(): next part");
                multipartStream.readBodyData(out);
                nextPart = multipartStream.readBoundary();
            }
            getLogger().debug(".act(): out:" + out.toString());
            out.close();
        } catch (Exception e) {
            getLogger().error(".act(): " + e.getClass().getName());
        }
*/
        


/*
        // FIXME: Thread "upload problem, 2003-05-16 3:45:07, cocoon-dev"
        if (false) {
        //if (!(request instanceof MultipartHttpServletRequest)) {
            getLogger().error("Not a multipart request!");
        } else {
            Part part = (Part) request.get("title");
            getLogger().debug(".act(): TITLE: " + part.getFileName());
        }
*/

        return null;
    }

    /**
     * Intercept Input Stream
     * @param in is the <code>InputStream</code> from the HttpRequest
     */
    private InputStream intercept(InputStream in) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes_read;
        ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();

        while ((bytes_read = in.read(buffer)) != -1) {
            bufferOut.write(buffer, 0, bytes_read);
        }

        getLogger().debug("\n" + bufferOut.toString());

        return new ByteArrayInputStream(bufferOut.toByteArray());
    }
}
