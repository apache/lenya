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

/* $Id: Configuration.java,v 1.18 2004/03/01 16:18:23 gregor Exp $  */

package org.apache.lenya.xml;

import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Category;


/**
 * Reads xpsconf.properties
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
            log.error(".load(): " + e);
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
            log.debug(".getProperty(): No such property: " + key);
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     */
    public static void register() {
    }
}
