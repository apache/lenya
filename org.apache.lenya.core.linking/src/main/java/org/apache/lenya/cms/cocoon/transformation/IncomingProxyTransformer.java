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
import org.apache.lenya.cms.linking.IncomingLinkRewriter;
import org.apache.lenya.cms.linking.LinkRewriter;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.xml.sax.SAXException;

/**
 * Converts links in proxy syntax to web application links.
 * @see IncomingLinkRewriter
 */
public class IncomingProxyTransformer extends AbstractLinkTransformer {
    
    private LinkRewriter rewriter;

    public void setup(SourceResolver _resolver, Map _objectModel, String _source,
            Parameters params) throws ProcessingException, SAXException, IOException {
        super.setup(_resolver, _objectModel, _source, params);
        Request request = ObjectModelHelper.getRequest(_objectModel);

        try {
            Session session = RepositoryUtil.getSession(this.manager, request);
            DocumentFactory factory = DocumentUtil.createDocumentFactory(this.manager, session);
            String webappUrl = getWebappUrl(params, objectModel);
            URLInformation info = new URLInformation(webappUrl);
            String pubId = info.getPublicationId();
            this.rewriter = new IncomingLinkRewriter(factory.getPublication(pubId));
        } catch (final Exception e) {
            throw new RuntimeException(e);
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
