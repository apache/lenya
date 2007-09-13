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
package org.apache.shibboleth.util;

import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Credentials manager.
 */
public interface CredentialsManager {
    
    /**
     * The service role.
     */
    String ROLE = CredentialsManager.class.getName();

    /**
     * @return The keystore.
     */
    KeyStore getKeyStore();

    /**
     * @return The truststore.
     */
    KeyStore getTrustStore();

    /**
     * @return The key manager factory.
     */
    KeyManagerFactory getKeyManagerFactory();

    /**
     * @return The trust manager factory.
     */
    TrustManagerFactory getTrustManagerFactory();

    /**
     * @param daysFromNow A number of days.
     * @return if the server certificate is still valid.
     */
    boolean checkServerCertValidity(int daysFromNow);

}
