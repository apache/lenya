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

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.io.File;
import java.util.Map;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.util.ServletHelper;

/**
 * Abstract superclass for classes which need access to the page envelope.
 * 
 * The web application URL can be provided in the attribute name, separated by a colon (":").
 */
public abstract class AbstractPageEnvelopeModule extends OperationModule {

    /**
     * Get the the page envelope for the given objectModel.
     * @param objectModel the objectModel for which the page enevelope is requested.
     * @param name The attribute name.
     * @return a <code>PageEnvelope</code>
     * @throws ConfigurationException if the page envelope could not be instantiated.
     */
    protected PageEnvelope getEnvelope(Map objectModel, String name) throws ConfigurationException {

        String webappUrl = null;
        Request request = ObjectModelHelper.getRequest(objectModel);

        PageEnvelope envelope = (PageEnvelope) request.getAttribute(PageEnvelope.class.getName());
        if (envelope == null) {

            String[] snippets = name.split(":");
            if (snippets.length > 1) {
                webappUrl = snippets[1];
            } else {
                webappUrl = ServletHelper.getWebappURI(request);
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Resolving page envelope for URL [" + webappUrl + "]");
            }

            String contextPath = request.getContextPath();
            Context context = ObjectModelHelper.getContext(objectModel);
            String servletContextPath = context.getRealPath("");

            try {
                DocumentFactory factory = getDocumentFactory();
                Publication pub = null;
                String pubId = new URLInformation(webappUrl).getPublicationId();
                if (pubId != null && factory.existsPublication(pubId)) {
                    pub = factory.getPublication(pubId);
                }
                envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(
                        factory,
                        contextPath,
                        webappUrl,
                        new File(servletContextPath),
                        pub);
            } catch (Exception e) {
                throw new ConfigurationException("Resolving page envelope failed: ", e);
            }
            request.setAttribute(PageEnvelope.class.getName(), envelope);
        }
        return envelope;
    }

    /**
     * @param name The original attribute name.
     * @return The attribute name without URL attachment.
     */
    protected String getAttributeName(String name) {
        final String[] snippets = name.split(":");
        return snippets[0];
    }

}