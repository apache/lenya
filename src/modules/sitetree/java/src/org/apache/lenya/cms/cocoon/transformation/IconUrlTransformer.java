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
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.linking.Link;
import org.apache.lenya.cms.linking.LinkResolver;
import org.apache.lenya.cms.linking.LinkRewriter;
import org.apache.lenya.cms.linking.LinkTarget;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.SAXException;

/**
 * Transforms <code>icon:{uuid}...</code> URLs into
 * <code>/{pub}/{area}/{resourceType}.gif?lenya.module=sitetree</code> URLs.
 */
public class IconUrlTransformer extends AbstractLinkTransformer {

    private LinkResolver linkResolver;
    private IconLinkRewriter rewriter = null;
    private DocumentFactory factory;
    private String pubId;
    private String area;

    protected LinkRewriter getLinkRewriter() {
        if (this.rewriter == null) {
            this.rewriter = new IconLinkRewriter();
        }
        return this.rewriter;
    }

    protected class IconLinkRewriter implements LinkRewriter {

        protected static final String PROTOCOL = "icon:";

        public boolean matches(String url) {
            return url.startsWith(PROTOCOL);
        }

        public String rewrite(String url) {
            String pubId = IconUrlTransformer.this.pubId;
            String area = IconUrlTransformer.this.area;
            String name = "default";
            if (url.length() > PROTOCOL.length()) {
                String suffix = url.substring(PROTOCOL.length());
                String linkUri = "lenya-document:" + suffix;
                try {
                    Link link = getLink(linkUri, pubId, area);
                    LinkTarget target = IconUrlTransformer.this.linkResolver.resolve(
                            IconUrlTransformer.this.factory, link.getUri());
                    if (target.exists()) {
                        Document doc = target.getDocument();
                        ResourceType type = doc.getResourceType();
                        if (Arrays.asList(type.getFormats()).contains("icon")) {
                            name = type.getName();
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return "/modules/sitetree/" + name + ".gif";
        }

        protected Link getLink(String linkUri, String pubId, String area)
                throws MalformedURLException {
            Link link = new Link(linkUri);
            if (link.getArea() == null) {
                link.setArea(area);
            }
            if (link.getPubId() == null) {
                link.setPubId(pubId);
            }
            return link;
        }

    }

    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters params)
            throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, params);
        try {
            Request request = ObjectModelHelper.getRequest(objectModel);
            this.factory = DocumentUtil.getDocumentFactory(this.manager, request);
            this.linkResolver = (LinkResolver) this.manager.lookup(LinkResolver.ROLE);

            String webappUrl = getWebappUrl(params, objectModel);
            URLInformation url = new URLInformation(webappUrl);
            this.pubId = url.getPublicationId();
            this.area = url.getArea();

        } catch (ServiceException e) {
            throw new ProcessingException(e);
        }
    }

    public void dispose() {
        super.dispose();
        if (this.linkResolver != null) {
            this.manager.release(linkResolver);
        }
    }

    public void recycle() {
        super.recycle();
        this.factory = null;
        this.pubId = null;
        this.area = null;
        this.linkResolver = null;
    }

}
