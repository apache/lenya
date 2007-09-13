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
package org.apache.shibboleth.saml;

import java.net.HttpURLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.shibboleth.ShibbolethModule;
import org.apache.shibboleth.util.CredentialsManager;
import org.opensaml.SAMLException;
import org.opensaml.SAMLSOAPHTTPBinding.HTTPHook;

import edu.internet2.middleware.shibboleth.common.Constants;

/**
 * Description:<br>
 * Code heavily borrowed and adapted from Internet2 Shibboleth SP
 * implementation.
 * <P>
 * Initial Date: 24.08.2005 <br>
 * 
 * @author Mike Stock
 */
public class HttpHookImpl extends AbstractLogEnabled implements HTTPHook {

    private ServiceManager manager;

    /**
     * @param manager The service manager.
     * @param logger The logger.
     */
    public HttpHookImpl(ServiceManager manager, Logger logger) {
        this.manager = manager;
        ContainerUtil.enableLogging(this, logger);
    }

    private static SSLContext staticSSLContext = null;

    /**
     * @see org.opensaml.SAMLSOAPHTTPBinding.HTTPHook#incoming(javax.servlet.http.HttpServletRequest,
     *      java.lang.Object, java.lang.Object)
     */
    public boolean incoming(HttpServletRequest r, Object globalCtx, Object callCtx)
            throws SAMLException {
        getLogger().error("OLATHttpHook method incoming-1 should not have been called.");
        return true;
    }

    /**
     * @see org.opensaml.SAMLSOAPHTTPBinding.HTTPHook#outgoing(javax.servlet.http.HttpServletResponse,
     *      java.lang.Object, java.lang.Object)
     */
    public boolean outgoing(HttpServletResponse r, Object globalCtx, Object callCtx)
            throws SAMLException {
        getLogger().error("OLATHttpHook method outgoing-1 should not have been called.");
        return true;
    }

    /**
     * @see org.opensaml.SAMLSOAPHTTPBinding.HTTPHook#incoming(java.net.HttpURLConnection,
     *      java.lang.Object, java.lang.Object)
     */
    public boolean incoming(HttpURLConnection conn, Object globalCtx, Object callCtx)
            throws SAMLException {
        // Called with the AA response, but I have nothing to add here
        return true;
    }

    /**
     * @see org.opensaml.SAMLSOAPHTTPBinding.HTTPHook#outgoing(java.net.HttpURLConnection,
     *      java.lang.Object, java.lang.Object)
     */
    public boolean outgoing(HttpURLConnection conn, Object globalCtx, Object callCtx)
            throws SAMLException {
        conn.setRequestProperty("Shibboleth", Constants.SHIB_VERSION);

        if (!(conn instanceof HttpsURLConnection)) {
            // HTTP (non-SSL) sessions need no additional processing
            return true;
        }

        HttpsURLConnection sslconn = (HttpsURLConnection) conn;
        SSLContext sslContext = getSSLContext();
        if (sslContext == null) {
            getLogger().error("Unable to get SLL Context for outgoing connection.");
            return true;
        }
        // Now we can get our own custom SocketFactory and replace
        // the default factory in the caller's URLConnection
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        sslconn.setSSLSocketFactory(socketFactory);

        // The KeyManager and TrustManager get callbacks from JSSE during
        // the URLConnection.connect() call
        return true;
    }

    private SSLContext getSSLContext() {
        if (staticSSLContext != null)
            return staticSSLContext;

        SSLContext sslContext = null;
        CredentialsManager credManager = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            // Attach the KeyManager and TrustManager to the Context
            credManager = (CredentialsManager) this.manager.lookup(CredentialsManager.ROLE);
            KeyManagerFactory kmf = credManager.getKeyManagerFactory();
            TrustManager[] tms = new TrustManager[] { new OShibTrustManager(credManager) };
            sslContext.init(kmf.getKeyManagers(), tms, new java.security.SecureRandom());
        } catch (Exception e) {
            throw new RuntimeException("Error getting SSL Context for Shibboleth HttpHook.", e);
        } finally {
            if (credManager != null) {
                this.manager.release(credManager);
            }
        }

        staticSSLContext = sslContext;
        return staticSSLContext;
    }

    class OShibTrustManager implements X509TrustManager {

        private CredentialsManager credentialsManager;

        /**
         * @param credManager The credentials manager.
         */
        public OShibTrustManager(CredentialsManager credManager) {
            this.credentialsManager = credManager;
        }

        public X509Certificate[] getAcceptedIssuers() {
            getLogger()
                    .error("ShibHttpHook method getAcceptedIssuers should not have been called.");
            return new X509Certificate[0];
        }

        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
            getLogger()
                    .error("ShibHttpHook method checkClientTrusted should not have been called.");
        }

        public void checkServerTrusted(X509Certificate[] certs, String arg1)
                throws CertificateException {

            ShibbolethModule module = null;
            try {
                module = (ShibbolethModule) HttpHookImpl.this.manager.lookup(ShibbolethModule.ROLE);

                if (module.checkCertificates()) {
                    TrustManagerFactory tmf = this.credentialsManager.getTrustManagerFactory();
                    X509TrustManager tm = (X509TrustManager) tmf.getTrustManagers()[0];
                    tm.checkServerTrusted(certs, arg1);
                }
            } catch (Exception e) {
                getLogger().error("Unable to verify server certificate authenticity.");
                throw new CertificateException("Unable to verify Certificate Authenticity.");
            } finally {
                if (module != null) {
                    HttpHookImpl.this.manager.release(module);
                }
            }
        }
    }
}
