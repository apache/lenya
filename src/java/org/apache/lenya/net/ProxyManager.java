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

/* $Id: ProxyManager.java,v 1.17 2004/03/01 16:18:25 gregor Exp $  */

package org.apache.lenya.net;

import java.io.File;
import java.util.Properties;
import java.util.Vector;

import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.XPointerFactory;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * The <code>ProxyManager</code> Class is used to set or unset the java systems proxy settings
 * based on the hostname of the host that want to be reached.
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
            System.err.println(
                "Usage: java org.apache.lenya.net.ProxyManager host [configfile.xml]");

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
     * Read proxy configuration
     *
     * @param fname Filename of proxy configuration
     *
     * @return proxies
     */
    public Vector readConfig(String fname) {
        Document document = null;
        File configFile = null;

        try {
	    configFile = new File(new java.net.URI(ProxyManager.class.getClassLoader().getResource(fname).toString()));
            if (configFile.exists()) {
                document = DocumentHelper.readDocument(configFile);
            } else {
                log.warn("No such file or directory: " + configFile.getAbsolutePath());
                return null;
            }
        } catch (Exception e) {
            log.error(e);
            return null;
        }


        Vector proxyElements = null;
        XPointerFactory xpf = new XPointerFactory();

        try {
            proxyElements = xpf.select(document.getDocumentElement(), "xpointer(/conf/Proxy)");
            if (proxyElements.size() == 0) log.info("No proxy defined (" + configFile + ")");
        } catch (Exception e) {
            log.error(e);
            return null;
        }

        Vector proxies = new Vector();
        for (int i = 0; i < proxyElements.size(); i++) {
            ProxyConf proxy = new ProxyConf((Element) proxyElements.elementAt(i));

            proxies.addElement(proxy);
        }

        return proxies;
    }
}
