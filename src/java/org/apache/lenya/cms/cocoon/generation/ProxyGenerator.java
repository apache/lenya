/*
$Id: ProxyGenerator.java,v 1.21 2004/02/21 16:14:47 gregor Exp $
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
package org.apache.lenya.cms.cocoon.generation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.Cookie;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.excalibur.xml.sax.SAXParser;
import org.apache.log4j.Category;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2002.8.27
 */
public class ProxyGenerator extends org.apache.cocoon.generation.ServletGenerator
    implements Parameterizable {
    private static Category log = Category.getInstance(ProxyGenerator.class);

    // The URI of the namespace of this generator
    private String URI = "http://apache.org/cocoon/lenya/proxygenerator/1.0";

    /**
     * DOCUMENT ME!
     *
     * @param parameters DOCUMENT ME!
     *
     * @throws ParameterException DOCUMENT ME!
     */
    public void parameterize(Parameters parameters) throws ParameterException {
        //super.parameterize(parameters);
    }

    /**
     * Generate XML data.
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void generate() throws SAXException {
        Request request = (Request) objectModel.get(ObjectModelHelper.REQUEST_OBJECT);

        log.debug("\n----------------------------------------------------------------" +
            "\n- Request: (" + request.getClass().getName() + ") at port " +
            request.getServerPort() +
            "\n----------------------------------------------------------------");

        String submitMethod = request.getMethod();

        SAXParser parser = null;

        try {
            // DEBUG
            if (submitMethod.equals("POST")) {
                // FIXME: Andreas
                if (request instanceof HttpRequest) {
                    java.io.InputStream is = intercept(((HttpRequest) request).getInputStream());
                }
            }

            URL url = createURL(request);

            // Forward "InputStream", Parameters, QueryString to Servlet
            org.apache.commons.httpclient.HttpMethod httpMethod = null;

            if (submitMethod.equals("POST")) {
                httpMethod = new PostMethod();

                Enumeration params = request.getParameterNames();

                while (params.hasMoreElements()) {
                    String paramName = (String) params.nextElement();
                    String[] paramValues = request.getParameterValues(paramName);

                    for (int i = 0; i < paramValues.length; i++) {
                        ((PostMethod) httpMethod).setParameter(paramName, paramValues[i]);
                    }
                }
            } else if (submitMethod.equals("GET")) {
                httpMethod = new org.apache.commons.httpclient.methods.GetMethod();
                httpMethod.setQueryString(request.getQueryString());
            }

            // Copy/clone Cookies
            Cookie[] cookies = request.getCookies();
            org.apache.commons.httpclient.Cookie[] transferedCookies = null;

            if (cookies != null) {
                transferedCookies = new org.apache.commons.httpclient.Cookie[cookies.length];

                for (int i = 0; i < cookies.length; i++) {
                    boolean secure = false; // http: false, https: true
                    transferedCookies[i] = new org.apache.commons.httpclient.Cookie(url.getHost(),
                            cookies[i].getName(), cookies[i].getValue(), url.getFile(), null, secure);
                }
            }

            // Initialize HttpClient
            HttpClient httpClient = new HttpClient();

            // Set cookies
            if ((transferedCookies != null) && (transferedCookies.length > 0)) {
                HttpState httpState = new HttpState();
                httpState.addCookies(transferedCookies);
                httpClient.setState(httpState);
            }

            // DEBUG cookies
            // Send request to servlet
            httpMethod.setRequestHeader("Content-type", "text/plain");
            httpMethod.setPath(url.getPath());

            // FIXME
            for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
                String name = (String) e.nextElement();
                httpMethod.addRequestHeader(name, request.getHeader(name));
            }
            HostConfiguration hostConfiguration  = new HostConfiguration();
            hostConfiguration.setHost(url.getHost(), url.getPort());

             log.debug("\n----------------------------------------------------------------" +
                "\n- Starting session at URI: " + url + "\n- Host:                    " +
                url.getHost() + "\n- Port:                    " + url.getPort() +
                "\n----------------------------------------------------------------");

            int result = httpClient.executeMethod(hostConfiguration, httpMethod);

            log.debug("\n----------------------------------------------------------------" +
                "\n- Result:                    " + result +
                "\n----------------------------------------------------------------");

            byte[] sresponse = httpMethod.getResponseBody();

            httpMethod.releaseConnection();

            // Return XML
            InputSource input = new InputSource(new ByteArrayInputStream(sresponse));
            parser = (SAXParser) this.manager.lookup(SAXParser.ROLE);
            parser.parse(input, this.xmlConsumer);
        } catch (Exception e) {
            this.contentHandler.startDocument();

            AttributesImpl attr = new AttributesImpl();
            this.start("servletproxygenerator", attr);
            this.data(".generate(): " + e);
            this.end("servletproxygenerator");
            this.contentHandler.endDocument();

            //log.error(e);
            e.printStackTrace();
        } finally {
            this.manager.release((Component) parser);
        }
    }

    /**
     * Log input stream for debugging
     *
     * @param in an <code>InputStream</code> value
     *
     * @return an <code>InputStream</code> value
     *
     * @exception Exception if an error occurs
     */
    private InputStream intercept(InputStream in) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes_read;
        ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();

        while ((bytes_read = in.read(buffer)) != -1) {
            bufferOut.write(buffer, 0, bytes_read);
        }

        return new ByteArrayInputStream(bufferOut.toByteArray());
    }

    private URL createURL(Request request) throws MalformedURLException {
        URL url = null;

        try {
            url = new URL(this.source);
            log.debug(".createURL(): " + url);
        } catch (MalformedURLException e) {
            url = new URL("http://127.0.0.1:" + request.getServerPort() + this.source);
            log.debug(".createURL(): Add localhost and port: " + url);
        }

        return url;
    }

    private void attribute(AttributesImpl attr, String name, String value) {
        attr.addAttribute("", name, name, "CDATA", value);
    }

    private void start(String name, AttributesImpl attr)
        throws SAXException {
        super.contentHandler.startElement(URI, name, name, attr);
        attr.clear();
    }

    private void end(String name) throws SAXException {
        super.contentHandler.endElement(URI, name, name);
    }

    private void data(String data) throws SAXException {
        super.contentHandler.characters(data.toCharArray(), 0, data.length());
    }
}
