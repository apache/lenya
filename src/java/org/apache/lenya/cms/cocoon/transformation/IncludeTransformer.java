/*
 * $Id: IncludeTransformer.java,v 1.7 2003/03/04 17:46:34 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.cms.cocoon.transformation;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.transformation.AbstractDOMTransformer;

import org.w3c.dom.Document;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2002.5.30
 */
public class IncludeTransformer extends AbstractDOMTransformer implements Configurable {

    private String domain = "127.0.0.1";
    private String port = null;
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

    protected Document transform(Document doc) {
        try {
            org.apache.cocoon.environment.Source input_source = this.resolver.resolve("");
            String sitemapPath = input_source.getSystemId();
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
                return new org.lenya.xml.XPSAssembler().assemble(doc, sitemapPath + href,
                    cocoon_base_request);
            } else {
                return new org.lenya.xml.XPSAssembler().assemble(doc, sitemapPath + sitemap_uri,
                    cocoon_base_request);
            }
        } catch (Exception e) {
            getLogger().error(".transform(): " + e);
        }

        return doc;
    }
}
