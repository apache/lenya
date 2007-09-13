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

/* $Id: XPath.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.xml;

import java.util.StringTokenizer;

import org.w3c.dom.Node;

public class XPath {
    String xpath = null;
    String[] parts = null;

    /**
     *
     */
    public XPath(String xpath) {
        this.xpath = xpath;

        StringTokenizer st = new StringTokenizer(xpath, "/");
        int length = st.countTokens();
        parts = new String[length];

        for (int i = 0; i < length; i++) {
            parts[i] = st.nextToken();
        }
    }

    /**
     *
     */
    public XPath getParent() {
        StringBuffer parentXPath = new StringBuffer();

        for (int i = 0; i < (parts.length - 1); i++) {
            parentXPath.append("/").append(parts[i]);
        }
        return new XPath(parentXPath.toString());
    }

    /**
     *
     */
    public short getType() {
        if (parts[parts.length - 1].indexOf("@") == 0) {
            return Node.ATTRIBUTE_NODE;
        }

        return Node.ELEMENT_NODE;
    }

    /**
     *
     */
    public String toString() {
        return xpath;
    }

    /**
     *
     */
    public String getName() {
        if (getType() == Node.ATTRIBUTE_NODE) {
            return parts[parts.length - 1].substring(1);
        }

        return parts[parts.length - 1];
    }

    /**
     * Describe 'getName' method here.
     *
     * @return a value of type 'String'
     */
    public String getElementName() {
        if (getType() == Node.ATTRIBUTE_NODE) {
            return parts[parts.length - 2];
        }

        return parts[parts.length - 1];
    }

    /**
     *
     */
    public String getNameWithoutPredicates() {
        return removePredicates(getName());
    }

    /**
     * Remove predicates (square brackets), http://www.w3.org/TR/xpath
     */
    public String removePredicates(String s) {
        int index = s.indexOf("[");

        if (index >= 0) {
            return s.substring(0, index);
        }

        return s;
    }
}
