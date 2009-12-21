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
package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.linking.LinkRewriter;
import org.apache.lenya.cms.linking.OutgoingLinkRewriter;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.SAXException;

/**
 * <p>
 * Proxy transformer.
 * </p>
 * <p>
 * The resulting URLs can either be absolute (default) or relative. You can
 * either configure this when declaring the transformer:
 * </p>
 * <code><pre>
 *     &lt;map:transformer ... &gt;
 *       &lt;urls type=&quot;relative&quot;/&gt;
 *       ...
 *     &lt;/map:transformer&gt;
 * </pre></code>
 * <p>
 * or pass a parameter:
 * </p>
 * <code><pre>
 *     &lt;map:parameter name=&quot;urls&quot; value=&quot;relative&quot;/&gt;
 * </pre></code>
 * @see OutgoingLinkRewriter
 */
public class ProxyTransformer extends AbstractLinkTransformer {

    protected static final String ATTRIBUTE_TYPE = "type";
    protected static final String URL_TYPE_ABSOLUTE = "absolute";
    protected static final String URL_TYPE_RELATIVE = "relative";
    protected static final String PARAMETER_URLS = "urls";

    private boolean relativeUrls = false;
    private LinkRewriter rewriter;

    public void setup(SourceResolver resolver, Map objectModel, String source,
            Parameters params) throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, source, params);
        Request request = ObjectModelHelper.getRequest(objectModel);

        try {
            if (params.isParameter(PARAMETER_URLS)) {
                setUrlType(params.getParameter(PARAMETER_URLS));
            }
            Session session = RepositoryUtil.getSession(this.manager, request);
            String webappUrl = getWebappUrl(params, objectModel);
            this.rewriter = new OutgoingLinkRewriter(this.manager, session, webappUrl,
                    request.isSecure(), false, this.relativeUrls);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);
        Configuration urlConfig = config.getChild(PARAMETER_URLS, false);
        if (urlConfig != null) {
            String value = urlConfig.getAttribute(ATTRIBUTE_TYPE);
            setUrlType(value);
        }
    }

    protected void setUrlType(String value) throws ConfigurationException {
        if (value.equals(URL_TYPE_RELATIVE)) {
            this.relativeUrls = true;
        } else if (value.equals(URL_TYPE_ABSOLUTE)) {
            this.relativeUrls = false;
        } else {
            throw new ConfigurationException("Invalid URL type [" + value
                    + "], must be relative or absolute.");
        }
    }

    protected LinkRewriter getLinkRewriter() {
        return this.rewriter;
    }

    public void recycle() {
        super.recycle();
        this.rewriter = null;
    }

}
