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

/* $Id: IncludeTransformer.java,v 1.18 2004/03/01 16:18:20 gregor Exp $  */

package org.apache.lenya.cms.cocoon.transformation;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.Source;
import org.w3c.dom.Document;


public class IncludeTransformer extends AbstractDOMTransformer implements Configurable {
    private String domain = "127.0.0.1";
    private String context = null;
    private String publication = null;

    /**
     * DOCUMENT ME!
     *
     * @param conf DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void configure(Configuration conf) throws ConfigurationException {
        if (conf != null) {
            publication = conf.getChild("publication").getAttribute("type");
            getLogger().debug("PUBLICATION TYPE: " + publication);
        } else {
            getLogger().error("Configuration is null");
        }
    }

	/**
	 *  (non-Javadoc)
	 * @see org.apache.lenya.cms.cocoon.transformation.AbstractDOMTransformer#transform(org.w3c.dom.Document)
	 */
    protected Document transform(Document doc) {
        try {
            Source input_source = this.resolver.resolveURI("");
            String sitemapPath = input_source.getURI();
            getLogger().debug("Absolute SITEMAP Directory: " + sitemapPath);

            String href = this.parameters.getParameter("href", null);

            if (href != null) {
                getLogger().debug("Parameter href = " + href);
            } else {
                getLogger().debug("No Parameter");
            }

            Request request = ObjectModelHelper.getRequest(objectModel);

            String request_uri = request.getRequestURI();
            String sitemap_uri = request.getSitemapURI();
            getLogger().debug("REQUEST URI: " + request_uri);
            getLogger().debug("SITEMAP URI: " + sitemap_uri);

            context = request.getContextPath();

            String context_publication = context + "/" + publication;
            int port = request.getServerPort();
            String cocoon_base_request = "http://" + domain + ":" + port + context_publication;
            getLogger().debug("COCOON_BASE_REQUEST: " + cocoon_base_request);

            if (href != null) {
                return new org.apache.lenya.xml.XPSAssembler().assemble(doc, sitemapPath + href,
                    cocoon_base_request);
            } else {
                return new org.apache.lenya.xml.XPSAssembler().assemble(doc,
                    sitemapPath + sitemap_uri, cocoon_base_request);
            }
        } catch (Exception e) {
            getLogger().error(".transform(): " + e, e);
        }

        return doc;
    }
}
