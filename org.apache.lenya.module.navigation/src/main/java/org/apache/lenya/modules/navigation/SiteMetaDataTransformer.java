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
package org.apache.lenya.modules.navigation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.cms.linking.Link;
import org.apache.lenya.cms.linking.LinkResolver;
import org.apache.lenya.cms.linking.LinkTarget;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Updates navigation nodes according to the site meta data of the corresponding
 * documents.
 */
public class SiteMetaDataTransformer extends AbstractSAXTransformer implements Initializable {

    private Area area;
    private LinkResolver linkResolver;

    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters params)
            throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, params);

        Request req = ObjectModelHelper.getRequest(objectModel);
        DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);
        String webappUrl = ServletHelper.getWebappURI(req);
        URLInformation info = new URLInformation(webappUrl);
        try {
            Publication pub = factory.getPublication(info.getPublicationId());
            this.area = pub.getArea(info.getArea());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() throws Exception {
        this.linkResolver = (LinkResolver) this.manager.lookup(LinkResolver.ROLE);
    }

    public void dispose() {
        super.dispose();
        if (this.linkResolver == null) {
            this.manager.release(linkResolver);
        }
    }

    protected static final String NAV_NAMESPACE = "http://apache.org/lenya/site/1.0";
    protected static final String ELEM_LINK = "link";
    protected static final String ATTR_HREF = "href";

    /**
     * The site meta data namespace.
     */
    public static final String NAMESPACE = "http://apache.org/lenya/metadata/site/1.0";

    /**
     * The folderNode meta data element name.
     */
    public static final String ELEM_FOLDER_NODE = "folderNode";

    /**
     * The externalLink meta data element name.
     */
    public static final String ELEM_EXTERNAL_LINK = "externalLink";

    public void startElement(String uri, String localName, String qName, Attributes attr)
            throws SAXException {
        if (uri != null && uri.equals(NAV_NAMESPACE) && localName.equals(ELEM_LINK)) {
            AttributesImpl attrs = new AttributesImpl(attr);
            String href = attrs.getValue(ATTR_HREF);
            try {
                if (href != null && href.startsWith("lenya-document:")) {
                    LinkTarget target = linkResolver.resolve(this.area.getPublication()
                            .getFactory(), getLinkUri(href));
                    if (target.exists()) {
                        Document doc = target.getDocument();
                        int hrefIndex = attrs.getIndex(ATTR_HREF);
                        if (isFolderNode(doc)) {
                            attrs.removeAttribute(hrefIndex);
                        } else {
                            String externalLink = getExternalLink(doc);
                            if (externalLink != null) {
                                attrs.setAttribute(hrefIndex, "", ATTR_HREF, ATTR_HREF, "CDATA",
                                        externalLink);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new SAXException(e);
            } finally {
                super.startElement(uri, localName, qName, attrs);
            }
        } else {
            super.startElement(uri, localName, qName, attr);
        }
    }

    protected boolean isFolderNode(Document doc) throws MetaDataException {
        MetaData meta = doc.getMetaData(NAMESPACE);
        String value = meta.getFirstValue(ELEM_FOLDER_NODE);
        boolean isFolderNode = value != null && Boolean.valueOf(value).booleanValue();
        return isFolderNode;
    }

    protected String getExternalLink(Document doc) throws MetaDataException {
        MetaData meta = doc.getMetaData(NAMESPACE);
        return meta.getFirstValue(ELEM_EXTERNAL_LINK);
    }

    protected String getLinkUri(String href) throws MalformedURLException {
        int qm = href.indexOf("?");
        String linkUri = qm < 0 ? href : href.substring(0, qm - 1);
        Link link = new Link(linkUri);
        if (link.getArea() == null) {
            link.setArea(this.area.getName());
        }
        if (link.getPubId() == null) {
            link.setPubId(this.area.getPublication().getId());
        }
        return link.getUri();
    }

}
