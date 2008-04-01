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
package org.apache.shibboleth;

import org.apache.shibboleth.util.CredentialsManager;
import org.apache.shibboleth.util.UniqueIdentifierMapper;
import org.opensaml.ReplayCache;
import org.opensaml.SAMLBrowserProfile.ArtifactMapper;

import edu.internet2.middleware.shibboleth.aap.AAP;
import edu.internet2.middleware.shibboleth.metadata.Metadata;

/**
 * Facade to Shibboleth functionality.
 */
public interface ShibbolethModule {

    /**
     * The service role.
     */
    public static final String ROLE = ShibbolethModule.class.getName();

    /**
     * @return The UID mapper.
     */
    UniqueIdentifierMapper getUidMapper();

    /**
     * @return The artifact mapper.
     */
    ArtifactMapper getArtifactMapper();

    /**
     * @return The credentials manager.
     */
    CredentialsManager getCredentialsManager();

    /**
     * @return The attribute acceptance policy.
     */
    AAP getAttributeAcceptancePolicy();

    /**
     * @return True if to check certificates.
     */
    boolean checkCertificates();

    /**
     * @return True if to check issuer IPs.
     */
    boolean checkIssuerIP();

    /**
     * @return true if the language should be sent in the aai request
     */
    boolean useLanguageInReq();

    /**
     * @return the get request paramter name to be used sending the language
     *         code.
     */
    String getLanguageParamName();

    /**
     * @return The provider ID for this shibboleth resource.
     */
    String getProviderId();

    /**
     * @return The replay cache.
     */
    ReplayCache getReplayCache();

    /**
     * @return The Shibboleth metadata object.
     */
    Metadata getMetadata();

    /**
     * @return The URL of the WAYF server (without query string).
     */
    String getWayfServerUrl();

    /**
     * @param hostUrl The host URL to append the shire URL to.
     * @return The shire URL.
     */
    String getShireUrl(String hostUrl);

    /**
     * @param targetUrl the target URL the user is send to after authentication.
     * @return The base Part of the targetUrl i.e. the scheme and the authority
     *         part of the URI.
     */
    String getTargetBaseUrl(String targetUrl);

}