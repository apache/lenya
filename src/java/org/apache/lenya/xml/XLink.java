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

/* $Id: XLink.java,v 1.12 2004/03/01 16:18:23 gregor Exp $  */

package org.apache.lenya.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XLink {

    public String type = null;
    public String href = null;
    public String show = null;
    public String name = null;
    public Element element = null;

    public static final String XLINK_NAMESPACE = "http://www.w3.org/1999/xlink";
    public static final String ATTRIBUTE_HREF = "href";
    public static final String ATTRIBUTE_SHOW = "show";
    public static final String ATTRIBUTE_TYPE = "type";

    /**
     *
     */
    public XLink() {
        type = "simple";
        show = "undefined";
    }

    /**
     *
     */
    public XLink(Element element) {
        this();
        this.element = element;

        name = element.getNodeName();

        Attr hrefAttribute = element.getAttributeNodeNS(XLINK_NAMESPACE, ATTRIBUTE_HREF);
        if (hrefAttribute != null) {
            href = hrefAttribute.getNodeValue();
        }
        Attr typeAttribute = element.getAttributeNodeNS(XLINK_NAMESPACE, ATTRIBUTE_TYPE);
        if (typeAttribute != null) {
            type = typeAttribute.getNodeValue();
        }
        Attr showAttribute = element.getAttributeNodeNS(XLINK_NAMESPACE, ATTRIBUTE_SHOW);
        if (showAttribute != null) {
            show = showAttribute.getNodeValue();
        }

    }

    /**
     *
     */
    public Element getXLink(Document document, DOMParserFactory dpf) {
        return (Element) dpf.cloneNode(document, element, true);
    }

    /**
     *
     */
    public String toString() {
        return "XLink: type=\""
            + type
            + "\", href=\""
            + href
            + "\", show=\""
            + show
            + "\", name=\""
            + name
            + "\"";
    }
}
