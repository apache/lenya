/*
 * $Id: ProxyConf.java,v 1.5 2003/03/04 19:44:56 gregor Exp $
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
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.net;

import org.w3c.dom.*;

import org.lenya.xml.*;

import java.util.Vector;


/**
 * DOCUMENT ME!
 *
 * @author Philipp Klaus
 * @version 0.8.0
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
