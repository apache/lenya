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

/* $Id: Configuration.java,v 1.16 2004/03/01 16:18:22 gregor Exp $  */

package org.apache.lenya.cms.rc;

import java.util.Properties;

import org.apache.log4j.Category;


/**
 * Reads conf.properties
 */
public class Configuration {
    private static Category log = Category.getInstance(Configuration.class);
    
    private String rcmlDirectory = null;
    private String backupDirectory = null;

    /**
     * Creates a new Configuration object.
     */
    public Configuration() {
        String propertiesFileName = "conf.properties";
        Properties properties = new Properties();

        try {
            properties.load(Configuration.class.getResourceAsStream(propertiesFileName));
        } catch (Exception e) {
            log.fatal(": Failed to load properties from resource: " + propertiesFileName);
        }

        rcmlDirectory = properties.getProperty("rcmlDirectory");
        backupDirectory = properties.getProperty("backupDirectory");
    }

    /**
     * Get the backup directory
     * 
     * @return the backup directory
     */
    public String getBackupDirectory() {
        return backupDirectory;
    }

    /**
     * Get the rcml directory
     * 
     * @return the rcml directory
     */
    public String getRcmlDirectory() {
        return rcmlDirectory;
    }
}
