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

/* $Id: ProxyGenerator.java,v 1.22 2004/03/01 16:18:24 gregor Exp $  */

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
