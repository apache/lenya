/*
 * $Id: DOM4JUtil.java,v 1.6 2003/04/24 13:53:14 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.xml;

import org.apache.log4j.Category;

import org.dom4j.Element;

import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author Edith Chevrier
 * @version 2002.07.08
 */
public class DOM4JUtil {
    static Category log = Category.getInstance(DOM4JUtil.class);

    /**
     * Creates a new DOM4JUtil object.
     */
    public DOM4JUtil() {
    }

    /**
     * insert the newElement as index-th child of the same parent like the element
     *
     * @param element element to define the parent node
     * @param newElement element to insert
     * @param index DOCUMENT ME!
     */
    public void insertElementAt(Element element, Element newElement, int index) {
        Element parent = element.getParent();
        List list = parent.content();
        list.add(index, newElement);
    }

    /**
     * insert the newElement before the element as child of the same node.
     *
     * @param element element the newElement will be insert before this element
     * @param newElement element to insert
     */
    public void insertElementBefore(Element element, Element newElement) {
        Element parent = element.getParent();
        insertElementAt(element, newElement, parent.indexOf(element));
    }

    /**
     * insert the newElement after the element as child of the same node.
     *
     * @param element element the newElement will be insert after this element
     * @param newElement element to insert
     */
    public void insertElementAfter(Element element, Element newElement) {
        Element parent = element.getParent();
        insertElementAt(element, newElement, parent.indexOf(element) + 1);
    }
}
 // DOM4JUtil
