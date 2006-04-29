/*
 * Copyright 1999-2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *  
 */

/* $Id: ProxyGenerator.java 332837 2005-11-12 21:06:37Z michi $ */
package org.apache.lenya.cms.cocoon.generation;

import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Read configuration
 */
public class Configuration {
    static Logger log = Logger.getLogger(Configuration.class);
    public static final String DEFAULT_CONFIGURATION_FILE = "org/apache/lenya/cms/cocoon/generation/conf.properties";
    public static final String DEFAULT_CONFIGURATION_KEY = "lenya.configuration";
    public static final String OVERRIDE_DEFAULT_CONFIGURATION_KEY = "override.lenya.configuration";
    public String trustStore = null;
    public String trustStorePassword = null;

    /**
     * Creates a new Configuration object.
     */
    public Configuration() {
        getProperties(load());
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
            trustStore = getProperty(properties, "org.apache.lenya.cms.cocoon.generation.ProxyGenerator.trustStore");
            trustStorePassword = getProperty(properties, "org.apache.lenya.cms.cocoon.generation.ProxyGenerator.trustStorePassword");
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