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

/* $Id: Configuration.java,v 1.17 2004/03/01 16:18:25 gregor Exp $  */

package org.apache.lenya.net;

import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Category;


/**
 * Read configuration
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
            resourcePathRelativeToClasspath = System.getProperty(DEFAULT_CONFIGURATION_KEY, DEFAULT_CONFIGURATION_FILE);
            log.debug(DEFAULT_CONFIGURATION_KEY + "=" + resourcePathRelativeToClasspath);
        } else {
            log.debug(OVERRIDE_DEFAULT_CONFIGURATION_KEY + "=" + resourcePathRelativeToClasspath);
        }

        URL url = Configuration.class.getClassLoader().getResource(resourcePathRelativeToClasspath);

        if (url == null) {
            log.error(".load(): Could not find resource on classpath: " + resourcePathRelativeToClasspath);
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
            configurationPath = getProperty(properties, "org.apache.lenya.net.ProxyManager.configurationPath");
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
