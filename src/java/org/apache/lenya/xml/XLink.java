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

/* $Id$  */

package org.apache.lenya.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * Helper class for XLinks
 */
public class XLink {

    /**
     * <code>type</code> The Xlink type
     */
    public String type = null;
    /**
     * <code>href</code> The XLink href
     */
    public String href = null;
    /**
     * <code>show</code> The value of the show attribute
     */
    public String show = null;
    /**
     * <code>name</code> The Xlink name
     */
    public String name = null;
    /**
     * <code>element</code> The Xlink element
     */
    public Element element = null;
    /**
     * <code>XLINK_NAMESPACE</code> The XLink namespace
     */
    public static final String XLINK_NAMESPACE = "http://www.w3.org/1999/xlink";
    /**
     * <code>ATTRIBUTE_HREF</code> The href attribte
     */
    public static final String ATTRIBUTE_HREF = "href";
    /**
     * <code>ATTRIBUTE_SHOW</code> The show attribute
     */
    public static final String ATTRIBUTE_SHOW = "show";
    /**
     * <code>ATTRIBUTE_TYPE</code> The type attribute
     */
    public static final String ATTRIBUTE_TYPE = "type";

    /**
     * Constructor
     */
    public XLink() {
        this.type = "simple";
        this.show = "undefined";
    }

    /**
     * Constructor
     * @param _element The element
     */
    public XLink(Element _element) {
        this();
        this.element = _element;

        this.name = _element.getNodeName();

        Attr hrefAttribute = _element.getAttributeNodeNS(XLINK_NAMESPACE, ATTRIBUTE_HREF);
        if (hrefAttribute != null) {
            this.href = hrefAttribute.getNodeValue();
        }
        Attr typeAttribute = _element.getAttributeNodeNS(XLINK_NAMESPACE, ATTRIBUTE_TYPE);
        if (typeAttribute != null) {
            this.type = typeAttribute.getNodeValue();
        }
        Attr showAttribute = _element.getAttributeNodeNS(XLINK_NAMESPACE, ATTRIBUTE_SHOW);
        if (showAttribute != null) {
            this.show = showAttribute.getNodeValue();
        }

    }

    /**
     * Returns a printout of the XLink values
     * @return The printout
     */
    public String toString() {
        return "XLink: type=\""
            + this.type
            + "\", href=\""
            + this.href
            + "\", show=\""
            + this.show
            + "\", name=\""
            + this.name
            + "\"";
    }
}
