/*
 * $Id: Configuration.java,v 1.5 2003/02/17 13:06:57 egli Exp $
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
package org.wyona.net;

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
    public static final String DEFAULT_CONFIGURATION_FILE = "org/wyona/net/conf.properties";
    public static final String DEFAULT_CONFIGURATION_KEY = "wyona.configuration";
    public static final String OVERRIDE_DEFAULT_CONFIGURATION_KEY = "override.wyona.configuration";
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
     * CLASSPATH=/home/wyona/src/xps/build/properties:... java
     * -Doverride.wyona.configuration=org/wyona/altconf.properties org.wyona.net.Configuration
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
                    "org.wyona.net.ProxyManager.configurationPath");
            smtpHost = getProperty(properties, "org.wyona.net.SMTP.host");
            smtpPort = getProperty(properties, "org.wyona.net.SMTP.port");
            smtpDomain = getProperty(properties, "org.wyona.net.SMTP.domain");
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
