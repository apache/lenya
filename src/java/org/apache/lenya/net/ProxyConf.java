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

/* $Id: ProxyConf.java,v 1.11 2004/03/01 16:18:25 gregor Exp $  */

package org.apache.lenya.net;

import java.util.Vector;

import org.apache.lenya.xml.XPointerFactory;
import org.w3c.dom.Element;


/**
 * DOCUMENT ME!
 */
public class ProxyConf {
    String proxyHost = null;
    String proxyPort = null;
    Vector items = null;

    /**
     * Creates a new ProxyConf object.
     *
     * @param proxyElement DOCUMENT ME!
     */
    public ProxyConf(Element proxyElement) {
        try {
            items = new Vector();

            XPointerFactory xpf = new XPointerFactory();

            proxyHost = proxyElement.getAttribute("host");
            proxyPort = proxyElement.getAttribute("port");

            Vector filterEls = xpf.select(proxyElement, "xpointer(include|exclude)");

            for (int i = 0; i < filterEls.size(); i++) {
                ProxyItem item = new ProxyItem((Element) filterEls.elementAt(i));
                items.addElement(item);
            }
        } catch (Exception e) {
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
    public boolean check(String hostname) {
        boolean result = false;

        for (int i = 0; i < items.size(); i++) {
            int ires = ((ProxyItem) items.elementAt(i)).check(hostname);

            if (ires > 0) {
                result = true;
            } else if (ires < 0) {
                result = false;
            }
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHostName() {
        return proxyHost;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHostPort() {
        return proxyPort;
    }
}
