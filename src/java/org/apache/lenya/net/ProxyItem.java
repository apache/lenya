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

/* $Id: ProxyItem.java,v 1.11 2004/03/01 16:18:25 gregor Exp $  */

package org.apache.lenya.net;

import org.apache.lenya.xml.XPointerFactory;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.w3c.dom.Element;


/**
 * DOCUMENT ME!
 */
public class ProxyItem {
    RE filter = null;
    boolean action = false;

    /**
     * Creates a new ProxyItem object.
     *
     * @param itemElement DOCUMENT ME!
     */
    public ProxyItem(Element itemElement) {
        XPointerFactory xpf = new XPointerFactory();

        if (itemElement.getNodeName().equals("include")) {
            action = true;
        } else {
            action = false;
        }

        try {
            filter = new RE(xpf.getElementValue(itemElement));
        } catch (RESyntaxException e) {
            System.err.println(this.getClass().getName() + ": " + e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param hostname DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int check(String hostname) {
        if (filter.match(hostname)) {
            if (action) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return 0;
        }
    }
}
