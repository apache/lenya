/*
$Id: MultipartHttpServletRequestAction.java,v 1.4 2003/07/23 13:21:30 gregor Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.servlet.multipart.Part;

import java.io.*;

import java.util.*;


/**
 * @author <a href="mailto:michael.wechner@wyona.org">Michael Wechner</a>
 */
public class MultipartHttpServletRequestAction extends AbstractConfigurableAction
    implements ThreadSafe {
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
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
        Parameters parameters) throws Exception {
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
