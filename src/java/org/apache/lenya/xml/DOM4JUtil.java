/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
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
