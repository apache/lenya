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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.linking.LinkResolver;
import org.apache.lenya.cms.linking.LinkRewriter;
import org.apache.lenya.cms.linking.UuidToUrlRewriter;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.xml.sax.SAXException;

/**
 * UUID to URL transformer.
 * @see AbstractLinkTransformer
 * @see UuidToUrlRewriter
 * 
 * $Id: LinkRewritingTransformer.java,v 1.7 2004/03/16 11:12:16 gregor
 */
public class UuidToUrlTransformer extends AbstractLinkTransformer implements Disposable {

    private UuidToUrlRewriter rewriter;
    private LinkResolver linkResolver;
    
    public void setup(SourceResolver resolver, Map objectModel, String source,
            Parameters params) throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, source, params);

        Request request = ObjectModelHelper.getRequest(objectModel);
        this.useIgnore = true;
        try {
            String currentUrl = getWebappUrl(params, objectModel);
            DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);
            this.linkResolver = (LinkResolver) this.manager.lookup(LinkResolver.ROLE);
            this.rewriter = new UuidToUrlRewriter(currentUrl, this.linkResolver, factory);
            
            if (factory.isDocument(currentUrl)) {
                this.rewriter.setCurrentDocument(factory.getFromURL(currentUrl));
            }
            
        } catch (final Exception e) {
            throw new ProcessingException(e);
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        if (this.linkResolver != null) {
            this.manager.release(this.linkResolver);
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
