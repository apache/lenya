/*
 * $Id: ProxyManager.java,v 1.5 2003/03/04 17:46:47 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
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
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.net;

import org.apache.log4j.Category;

import org.w3c.dom.*;

import org.lenya.xml.*;

import java.util.Properties;
import java.util.Vector;


/**
 * The <code>ProxyManager</code> Class is used to set or unset the java systems proxy settings
 * based on the hostname of the host that want to be reached.
 *
 * @author Philipp Klaus
 * @version 0.8.0
 */
public class ProxyManager {
    static Category log = Category.getInstance(ProxyManager.class);
    Vector proxies = null;

    /**
     * Creating an instance of ProxyManager without argurments reads the configuration from the
     * default configuration file ($XPS_HOME/xml/xps/proxyconf.xml)
     */
    public ProxyManager() {
        log.debug("" + new Configuration().configurationPath);
        proxies = readConfig(new Configuration().configurationPath);
    }

    /**
     * The <code>ProxyManager</code> is created using the customized <code>conffile</code>
     *
     * @param conffile configuration file to use
     */
    public ProxyManager(String conffile) {
        proxies = readConfig(conffile);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if ((args.length > 2) || (args.length < 1)) {
            System.err.println("Usage: java org.lenya.net.ProxyManager host [configfile.xml]");

            return;
        }

        ProxyManager pm = null;

        if (args.length > 1) {
            pm = new ProxyManager(args[1]);
        } else {
            pm = new ProxyManager();
        }

        if (pm.set(args[0])) {
            System.out.println("Proxy set: ");
        } else {
            System.out.println("No proxy set.");
        }
    }

    /**
     * Check if one of the configured proxies is appropriate for this host and setup the system
     * configuration accordingly.
     *
     * @param host name of the host the connection should be initiated to
     *
     * @return DOCUMENT ME!
     */
    public boolean set(String host) {
        Properties sp = System.getProperties();

        for (int i = 0; i < proxies.size(); i++) {
            ProxyConf proxy = (ProxyConf) proxies.elementAt(i);

            if (proxy.check(host)) {
                sp.put("proxySet", "true");
                sp.put("proxyHost", proxy.getHostName());
                sp.put("proxyPort", proxy.getHostPort());
                System.setProperties(sp);

                return true;
            }
        }

        sp.remove("proxySet");
        sp.put("proxyHost", "");
        sp.put("proxyPort", "");
        System.setProperties(sp);

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param fname DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Vector readConfig(String fname) {
        DOMParserFactory dpf = new DOMParserFactory();
        XPointerFactory xpf = new XPointerFactory();

        Vector proxies = new Vector();

        Document document = null;

        try {
            document = dpf.getDocument(fname);
        } catch (Exception e) {
            log.error(".readConfig(" + fname + "): " + e);

            return null;
        }

        Vector proxyElements = null;

        try {
            proxyElements = xpf.select(document.getDocumentElement(), "xpointer(/conf/Proxy)");
        } catch (Exception e) {
            log.warn(".readConfig(" + fname + "): No such element: /conf/Proxy");

            return null;
        }

        for (int i = 0; i < proxyElements.size(); i++) {
            ProxyConf proxy = new ProxyConf((Element) proxyElements.elementAt(i));

            proxies.addElement(proxy);
        }

        return proxies;
    }
}
