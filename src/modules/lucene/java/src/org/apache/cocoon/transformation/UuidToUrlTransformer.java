/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.cocoon.transformation;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * <p>
 * Transform
 * <code><search:field name="uid">a5d30250-2b7c-11db-98f0-bef7b2781cf0:en</search:field></code>
 * into the corresponding URL.
 * </p>
 * <p>
 * Parameters:
 * </p>
 * <ul>
 * <li>pubId (optional, defaults to current page)</li>
 * <li>area (optional, defaults to current page)</li>
 * </ul>
 */
public class UuidToUrlTransformer extends AbstractSAXTransformer {

    private SiteStructure site;

    protected static final String NAMESPACE = "http://apache.org/cocoon/search/1.0";
    protected static final String ELEMENT_FIELD = "field";
    protected static final String ATTR_NAME = "name";
    protected static final String ATTR_VALUE_UID = "uid";

    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters params)
            throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, params);

        Request request = ObjectModelHelper.getRequest(objectModel);
        String url = ServletHelper.getWebappURI(request);
        URLInformation info = new URLInformation(url);

        String pubId = params.getParameter("pubId", info.getPublicationId());
        String areaId = params.getParameter("area", info.getArea());

        DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);
        try {
            Publication pub = factory.getPublication(pubId);
            Area area = pub.getArea(areaId);
            this.site = area.getSite();
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }

    private boolean insideUidElement = false;

    public void startElement(String uri, String name, String raw, Attributes attr)
            throws SAXException {
        super.startElement(uri, name, raw, attr);
        if (uri.equals(NAMESPACE) && name.equals(ELEMENT_FIELD)
                && attr.getValue(ATTR_NAME).equals(ATTR_VALUE_UID)) {
            this.insideUidElement = true;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.insideUidElement) {
            String key = new String(ch);
            String[] steps = key.split(":");
            String uuid = steps[0];
            String language = steps[1];

            if (site.containsByUuid(uuid, language)) {
                try {
                    String url = site.getByUuid(uuid, language)
                            .getDocument()
                            .getCanonicalDocumentURL();
                    char[] chars = url.toCharArray();
                    super.characters(chars, 0, chars.length);
                } catch (SiteException e) {
                    throw new SAXException(e);
                }
            }

        } else {
            super.characters(ch, start, length);
        }
    }

    public void endElement(String uri, String name, String raw) throws SAXException {
        this.insideUidElement = false;
        super.endElement(uri, name, raw);
    }

}
