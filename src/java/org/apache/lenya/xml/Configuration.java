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

import java.net.URL;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 1.8.10
 * @deprecated replaced by config/ directory
 */
public class Configuration {
    static Category log = Category.getInstance(Configuration.class);
    public static final String DEFAULT_CONFIGURATION_FILE = "org/apache/lenya/xml/xpsconf.properties";
    public static final String DEFAULT_CONFIGURATION_KEY = "xps.configuration";
    public static final String OVERRIDE_DEFAULT_CONFIGURATION_KEY = "override.xps.configuration";
    public String cacheFolder = null;
    public boolean cacheHTTP = false;
    public String INCLUDE = null;
    public String JAVA_ZONE = null;
    public String proxyHost = null;
    public String proxyPort = null;
    public String servletZone = null;
    public String xslt = null;
    public String xslt_dir = null;
    public String xslt_urlspace = null;
    public String listeners = null;
    public String password = null;
    public String spaces = null;
    public String html2xml = null;
    public String xslt_structure = null;
    public String spellcheckProviderClass = null;
    public String spellcheckRootElement = null;
    public String editor = null;
    public String PublishConf = null;
    public String MDMLRoot = null;
    public String signalMasterStateFile = null;
    public String signalMasterConfFile = null;
    public String signalMasterMonitorXSLT = null;
    public String webserverHostname = null;
    public String WorkFlowConfiguration = null;
    public String DocumentTypeXPath = null;
    public String DefaultStatus = null;
    public String WFDocTypeDefaultXSLT = null;
    public String html2xmlTranslation = null;
    public String xpsgwsconf = null;
    public String xpsgwssimpleconf = null;
    public String xpsguiconf = null;
    public int maxNumberOfRollbacks;
    public int maxNumberOfEntries;

    /**
     * Creates a new Configuration object.
     */
    public Configuration() {
        getProperties(load());
    }

    /**
     * http://www.artima.com/java/answers/Mar2001/messages/164.html export
     * CLASSPATH=/home/lenya/src/xps/build/properties:... java
     * -Doverride.xps.configuration=org/apache/lenya/xps/altconf.properties org.apache.lenya.xps.Configuration
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        Configuration conf = new Configuration();

        System.out.println("Caching directory: " + conf.cacheFolder);
        System.out.println("Cache xml from http connections: " + conf.cacheHTTP);

        if ((conf.proxyHost != null) && (conf.proxyHost != null)) {
            System.out.println("Proxy set:");
            System.out.println(conf.proxyHost);
            System.out.println(conf.proxyPort);
        } else {
            System.out.println("No proxy set.");
        }

        System.out.println("XSLT: " + conf.xslt);
        System.out.println("XSLT (Directory): " + conf.xslt_dir);
        System.out.println("XSLT (URLSpace): " + conf.xslt_urlspace);
        System.out.println("Listeners: " + conf.listeners);
        System.out.println("Space Password: " + conf.password);
        System.out.println("Spaces: " + conf.spaces);
        System.out.println("html2xml: " + conf.html2xml);
        System.out.println("XSLT (View structure): " + conf.xslt_structure);
        System.out.println("Editor: " + conf.editor);
        System.out.println(conf.MDMLRoot);
        System.out.println(conf.signalMasterStateFile);
        System.out.println("signalMasterConfFile: " + conf.signalMasterConfFile);
        System.out.println("signalMasterMonitorXSLT: " + conf.signalMasterMonitorXSLT);
        System.out.println("Hostname and Port: " + conf.webserverHostname);
        System.out.println("Workflow: " + conf.WorkFlowConfiguration);
        System.out.println(conf.DocumentTypeXPath);
        System.out.println("Default Workflow Status: " + conf.DefaultStatus);

        System.out.println(conf.html2xmlTranslation);
        System.out.println(conf.xpsgwsconf);
        System.out.println(conf.xpsgwssimpleconf);
        System.out.println(conf.xpsguiconf);
        System.out.println(conf.maxNumberOfRollbacks);
        System.out.println(conf.maxNumberOfEntries);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Properties load() {
        String resourcePathRelativeToClasspath = System.getProperty(OVERRIDE_DEFAULT_CONFIGURATION_KEY);

        if (resourcePathRelativeToClasspath == null) {
            resourcePathRelativeToClasspath = System.getProperty(DEFAULT_CONFIGURATION_KEY,
                    DEFAULT_CONFIGURATION_FILE);
            log.debug(DEFAULT_CONFIGURATION_KEY + "=" + resourcePathRelativeToClasspath);
        } else {
            log.debug(OVERRIDE_DEFAULT_CONFIGURATION_KEY + "=" + resourcePathRelativeToClasspath);
        }

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        // FIXME:
        URL url = org.apache.log4j.helpers.Loader.getResource("hallo");

        if (url == null) {
            //return null;
        }

        log.debug(url);

        Properties properties = new Properties();

        try {
            properties.load(Configuration.class.getResourceAsStream("xpsconf.properties"));
        } catch (Exception e) {
            log.error(e);
        }

        return properties;
    }

    /**
     * DOCUMENT ME!
     *
     * @param properties DOCUMENT ME!
     */
    public void getProperties(Properties properties) {
        if (properties != null) {
            cacheFolder = getProperty(properties,
                    "org.apache.lenya.xps.XLinkInterpreter.cacheFolder");
            cacheHTTP = false;
            INCLUDE = getProperty(properties, "Include");
            JAVA_ZONE = getProperty(properties, "JavaZone");
            proxyHost = null;
            proxyPort = null;
            servletZone = getProperty(properties, "servletZone");
            xslt = getProperty(properties, "xslt");
            xslt_dir = getProperty(properties, "xslt_dir");
            xslt_urlspace = getProperty(properties, "xslt_urlspace");
            listeners = getProperty(properties, "listeners");
            password = getProperty(properties, "password");
            spaces = getProperty(properties, "spaces");
            html2xml = getProperty(properties, "html2xml");
            xslt_structure = getProperty(properties, "xslt_structure");
            spellcheckProviderClass = getProperty(properties, "spellcheckProviderClass");
            spellcheckRootElement = getProperty(properties, "spellcheckRootElement");
            editor = getProperty(properties, "editor");
            PublishConf = getProperty(properties, "PublishConf");
            MDMLRoot = getProperty(properties, "MDMLRoot");
            signalMasterStateFile = getProperty(properties, "signalMasterStateFile");
            signalMasterConfFile = getProperty(properties, "signalMasterConfFile");
            signalMasterMonitorXSLT = getProperty(properties, "signalMasterMonitorXSLT");
            webserverHostname = getProperty(properties, "webserverHostname");
            WorkFlowConfiguration = getProperty(properties, "WorkFlowConfiguration");
            DocumentTypeXPath = getProperty(properties, "DocumentTypeXPath");
            DefaultStatus = getProperty(properties, "DefaultStatus");

            html2xmlTranslation = getProperty(properties, "html2xmlTranslation");
            xpsgwsconf = getProperty(properties, "xpsgwsconf");
            xpsgwssimpleconf = getProperty(properties, "xpsgwssimpleconf");
            xpsguiconf = getProperty(properties, "xpsguiconf");
            maxNumberOfRollbacks = new Integer(getProperty(properties, "maxNumberOfRollbacks")).intValue();
            maxNumberOfEntries = new Integer(getProperty(properties, "maxNumberOfEntries")).intValue();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param properties DOCUMENT ME!
     * @param key DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getProperty(Properties properties, String key) {
        String value = properties.getProperty(key);

        if (value != null) {
            log.debug(key + "=" + value);

            return value;
        } else {
            log.error("No such property: " + key);
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     */
    public static void register() {
    }
}
