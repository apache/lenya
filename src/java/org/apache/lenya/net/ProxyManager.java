/*
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
package org.apache.lenya.net;

import java.io.File;
import java.util.Properties;
import java.util.Vector;

import org.apache.lenya.xml.DOMParserFactory;
import org.apache.lenya.xml.XPointerFactory;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * The <code>ProxyManager</code> Class is used to set or unset the java systems proxy settings
 * based on the hostname of the host that want to be reached.
 *
 * @author Philipp Klaus
 * @author Michael Wechner
 * @version $Id: ProxyManager.java,v 1.14 2004/02/02 02:50:40 stefano Exp $
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
        DOMParserFactory dpf = new DOMParserFactory();
        Document document = null;
        File configFile = null;

        try {
	    configFile = new File(new java.net.URI(ProxyManager.class.getClassLoader().getResource(fname).toString()));
            if (configFile.exists()) {
                document = dpf.getDocument(configFile.getAbsolutePath());
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
            if (proxyElements.size() == 0) log.warn("No proxy defined (" + configFile + ")");
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
