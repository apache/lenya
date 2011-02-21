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

/* $Id$  */

package org.apache.lenya.cms.publication;

import java.util.Map;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
//florent import org.apache.lenya.util.ServletHelper;
import org.apache.lenya.utils.ServletHelper;
import org.apache.lenya.utils.URLInformation;

/**
 * Common entry point for creating page envelopes.
 */
public class PageEnvelopeFactory {
    /**
     * Creates a new PageEnvelopeFactory.
     */
    private PageEnvelopeFactory() {
        // do nothing
    }

    private static PageEnvelopeFactory instance;

    /**
     * Returns the singleton PageEnvelopeFactory.
     * @return The factory.
     */
    public static PageEnvelopeFactory getInstance() {
        if (instance == null) {
            instance = new PageEnvelopeFactory();
        }
        return instance;
    }

    /**
     * Returns the page envelope for the object model of a Cocoon component.
     * @param objectModel The object model.
     * @param pub The publication.
     * @return A page envelope.
     * @throws PageEnvelopeException if something went wrong.
     */
    public PageEnvelope getPageEnvelope(Map objectModel, Publication pub)
            throws PageEnvelopeException {
        Request request = ObjectModelHelper.getRequest(objectModel);
        String contextPath = request.getContextPath();
        //florent String webappUrl = ServletHelper.getWebappURI(request);
        String webappUrl = new URLInformation().getWebappUrl();
        return getPageEnvelope(contextPath, webappUrl, pub);
    }

    /**
     * Creates a page envelope.
     * @param contextPath The servlet context prefix.
     * @param webappUrl The web application URL.
     * @param pub The publication.
     * @return A page envelope.
     * @throws PageEnvelopeException if something went wrong.
     */
    public PageEnvelope getPageEnvelope(String contextPath, String webappUrl, Publication pub)
            throws PageEnvelopeException {
        PageEnvelope envelope = new PageEnvelope(contextPath, webappUrl, pub);
        return envelope;
    }

}