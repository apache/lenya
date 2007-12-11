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

import java.util.StringTokenizer;

import org.w3c.dom.Node;

/**
 * Helper class for XPath operations
 */
public class XPath {
    String xpath = null;
    String[] parts = null;

    /**
     * Constructor
     * @param _xpath The Xpath
     */
    public XPath(String _xpath) {
        this.xpath = _xpath;

        StringTokenizer st = new StringTokenizer(_xpath, "/");
        int length = st.countTokens();
        this.parts = new String[length];

        for (int i = 0; i < length; i++) {
            this.parts[i] = st.nextToken();
        }
    }

    /**
     * Get the parent path
     * @return The parent path
     */
    public XPath getParent() {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < (this.parts.length - 1); i++) {
            buf.append("/" + this.parts[i]);
        }

        return new XPath(buf.toString());
    }

    /**
     * Get the type of a node. Only supports attribute and element nodes
     * @return The node type
     */
    public short getType() {
        if (this.parts[this.parts.length - 1].indexOf("@") == 0) {
            return Node.ATTRIBUTE_NODE;
        }
        return Node.ELEMENT_NODE;
    }

    /**
     * Return a string representation of the XPath
     * @return The Xpath
     */
    public String toString() {
        return this.xpath;
    }

    /**
     * Get the name
     * @return The name
     */
    public String getName() {
        if (getType() == Node.ATTRIBUTE_NODE) {
            return this.parts[this.parts.length - 1].substring(1);
        }
        return this.parts[this.parts.length - 1];
    }

    /**
     * Return the name of the element
     * @return the name of the element
     */
    public String getElementName() {
        if (getType() == Node.ATTRIBUTE_NODE) {
            return this.parts[this.parts.length - 2];
        }
        return this.parts[this.parts.length - 1];
    }

    /**
     * Get the name without predicates
     * @return The name without predicates
     */
    public String getNameWithoutPredicates() {
        return removePredicates(getName());
    }

    /**
     * Remove predicates (square brackets), http://www.w3.org/TR/xpath
     * @param s The string to remove predicates from
     * @return The string without predicates
     */
    public String removePredicates(String s) {
        int index = s.indexOf("[");

        if (index >= 0) {
            return s.substring(0, index);
        }

        return s;
    }
}
