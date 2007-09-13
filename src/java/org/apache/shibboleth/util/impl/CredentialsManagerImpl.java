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
package org.apache.shibboleth.util.impl;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.shibboleth.util.CredentialsManager;

import edu.internet2.middleware.shibboleth.common.ShibResource;

/**
 * Credentials manager implementation.
 */
public class CredentialsManagerImpl extends AbstractLogEnabled implements CredentialsManager,
        Configurable {

    private static final String CONF_KEYSTORE = "KeyStore";
    private static final String CONF_TRUSTSTORE = "TrustStore";
    private static final String CONF_LOCATION = "Location";
    private static final String CONF_TYPE = "Type";
    private static final String CONF_STOREPASS = "StorePassword";
    private static final String CONF_KEYPASS = "KeyPassword";

    private static KeyStore keyStore, trustStore;
    private static KeyManagerFactory keyManagerFactory;
    private static TrustManagerFactory trustManagerFactory;

    public void configure(Configuration credsConfig) throws ConfigurationException {
        try {
            // initialize keystore
            Configuration keystoreConf = credsConfig.getChild(CONF_KEYSTORE);
            String ksPath = keystoreConf.getChild(CONF_LOCATION).getValue();
            ShibResource ksResource = new ShibResource(ksPath);
            String ks_type = keystoreConf.getChild(CONF_TYPE).getValue();
            String ks_pwd = keystoreConf.getChild(CONF_STOREPASS).getValue(null);
            String key_pwd = keystoreConf.getChild(CONF_KEYPASS).getValue(null);
            keyStore = KeyStore.getInstance(ks_type != null ? ks_type : "JKS");
            keyStore.load(ksResource.getInputStream(), (ks_pwd != null) ? ks_pwd.toCharArray()
                    : null);
            keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, (key_pwd != null) ? key_pwd.toCharArray() : null);

            // initialize truststore (optional)
            Configuration truststoreConf = credsConfig.getChild(CONF_TRUSTSTORE, false);
            if (truststoreConf != null) {
                String tsPath = truststoreConf.getChild(CONF_LOCATION).getValue();
                ShibResource tsResource = new ShibResource(tsPath);
                String ts_pwd = truststoreConf.getChild("ssl-truststore-pwd").getValue(null);
                String ts_type = truststoreConf.getChild("ssl-truststore-type").getValue(null);
                trustStore = KeyStore.getInstance(ts_type != null ? ts_type : "JKS");
                trustStore.load(tsResource.getInputStream(), (ts_pwd != null) ? ts_pwd
                        .toCharArray() : null);
                trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
                trustManagerFactory.init(trustStore);
            }
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException("Error initializing CredentialsManager: ", e);
        }
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public KeyStore getTrustStore() {
        return trustStore;
    }

    public KeyManagerFactory getKeyManagerFactory() {
        return keyManagerFactory;
    }

    public TrustManagerFactory getTrustManagerFactory() {
        return trustManagerFactory;
    }

    public boolean checkServerCertValidity(int daysFromNow) {
        try {
            Enumeration aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                Certificate cert = keyStore.getCertificate(alias);
                if (cert instanceof X509Certificate) {
                    return isCertificateValid((X509Certificate) cert, daysFromNow);
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * Check whether a certificate is still valid in daysFromNow's time.
     * @param x509Cert The certificate.
     * @param daysFromNow
     * @return if the certificate is still valid.
     */
    private boolean isCertificateValid(X509Certificate x509Cert, int daysFromNow) {
        try {
            x509Cert.checkValidity();
            if (daysFromNow > 0) {
                Date nowPlusDays = new Date(System.currentTimeMillis()
                        + (new Long(daysFromNow).longValue() * 24l * 60l * 60l * 1000l));
                x509Cert.checkValidity(nowPlusDays);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
