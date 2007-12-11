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

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.linking.LinkResolver;
import org.apache.lenya.cms.linking.LinkRewriter;
import org.apache.lenya.cms.linking.UrlToUuidRewriter;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.SAXException;

/**
 * <p>
 * URL to UUID transformer.
 * </p>
 * 
 * <p>
 * This transformer is applied to an XHMTL document. It processes all URL-based
 * links to links following the {@link LinkResolver} syntax.
 * </p>
 * 
 * $Id: LinkRewritingTransformer.java,v 1.7 2004/03/16 11:12:16 gregor
 */
public class UrlToUuidTransformer extends AbstractLinkTransformer {

    private LinkRewriter rewriter;

    /**
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver,
     *      java.util.Map, java.lang.String,
     *      org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver _resolver, Map _objectModel, String _source,
            Parameters _parameters) throws ProcessingException, SAXException, IOException {
        super.setup(_resolver, _objectModel, _source, _parameters);

        Request _request = ObjectModelHelper.getRequest(_objectModel);
        try {
            Session session = RepositoryUtil.getSession(this.manager, _request);
            DocumentFactory factory = DocumentUtil.createDocumentFactory(this.manager, session);
            String url = ServletHelper.getWebappURI(_request);
            URLInformation info = new URLInformation(url);
            Publication pub = factory.getPublication(info.getPublicationId());
            Area area = pub.getArea(info.getArea());
            this.rewriter = new UrlToUuidRewriter(area);
        } catch (final Exception e1) {
            throw new ProcessingException(e1);
        }
    }

    protected LinkRewriter getLinkRewriter() {
        return this.rewriter;
    }

}