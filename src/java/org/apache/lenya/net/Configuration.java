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
package org.apache.lenya.net;

import org.apache.log4j.Category;

import java.net.URL;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 1.8.10
 */
public class Configuration {
    static Category log = Category.getInstance(Configuration.class);
    public static final String DEFAULT_CONFIGURATION_FILE = "org/apache/lenya/net/conf.properties";
    public static final String DEFAULT_CONFIGURATION_KEY = "lenya.configuration";
    public static final String OVERRIDE_DEFAULT_CONFIGURATION_KEY = "override.lenya.configuration";
    public String configurationPath = null;
    public String smtpHost = null;
    public String smtpPort = null;
    public String smtpDomain = null;

    /**
     * Creates a new Configuration object.
     */
    public Configuration() {
        getProperties(load());
    }

    /**
     * http://www.artima.com/java/answers/Mar2001/messages/164.html export
     * CLASSPATH=/home/lenya/src/xps/build/properties:... java
     * -Doverride.lenya.configuration=org/apache/lenya/altconf.properties org.apache.lenya.net.Configuration
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        Configuration conf = new Configuration();

        System.out.println("Proxy Manager Configuration Path: " + conf.configurationPath);
        System.out.println("SMTP Host: " + conf.smtpHost);
        System.out.println("SMTP Port: " + conf.smtpPort);
        System.out.println("SMTP Domain: " + conf.smtpDomain);
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
        URL url = cl.getResource(resourcePathRelativeToClasspath);

        if (url == null) {
            log.error("Could not find resource on classpath: " + resourcePathRelativeToClasspath);

            //return null;
        }

        log.debug(url);

        Properties properties = new Properties();

        try {
            properties.load(Configuration.class.getResourceAsStream("conf.properties"));
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
            configurationPath = getProperty(properties,
                    "org.apache.lenya.net.ProxyManager.configurationPath");
            smtpHost = getProperty(properties, "org.apache.lenya.net.SMTP.host");
            smtpPort = getProperty(properties, "org.apache.lenya.net.SMTP.port");
            smtpDomain = getProperty(properties, "org.apache.lenya.net.SMTP.domain");
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
