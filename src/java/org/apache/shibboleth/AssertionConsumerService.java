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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.opensaml.SAMLException;
import org.opensaml.SAMLBrowserProfile.BrowserProfileResponse;

import edu.internet2.middleware.shibboleth.wayf.IdPSite;

/**
 * Assertion consumer service.
 */
public interface AssertionConsumerService {

    /**
     * The service role.
     */
    String ROLE = AssertionConsumerService.class.getName();

    /**
     * @param req The request.
     * @param baseUrl The URL to append the shire URL to.
     * @return A browser profile response.
     * @throws SAMLException
     */
    BrowserProfileResponse processRequest(HttpServletRequest req, String hostUrl) throws SAMLException;

    /**
     * Uses an HTTP Status 307 redirect to forward the user the HS.
     * @param locale The locale.
     * @param idpSite The IdP site.
     * @param baseUrl The URL to append the shire URL to.
     * @return A string.
     */
    String buildRequest(Locale locale, IdPSite idpSite, String baseUrl);

}
