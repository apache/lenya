/*
$Id: XPath.java,v 1.5 2003/07/23 13:21:29 gregor Exp $
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
/*
 <License>
 </License>
 */
package org.apache.lenya.xml;

import org.w3c.dom.Node;

import java.util.StringTokenizer;


/**
 * @author Michael Wechner
 * @created 1.7.23
 * @version 1.7.24
 */
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
        String parentXPath = "";

        for (int i = 0; i < (parts.length - 1); i++) {
            parentXPath = parentXPath + "/" + parts[i];
        }

        return new XPath(parentXPath);
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
