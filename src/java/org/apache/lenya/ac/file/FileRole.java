/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

package org.apache.lenya.ac.file;

import java.io.File;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.impl.AbstractRole;
import org.apache.lenya.ac.impl.ItemConfiguration;

/**
 * File-based role implementation.
 * @version $Id: FileRole.java 473841 2006-11-12 00:46:38Z gregor $
 */
public class FileRole extends AbstractRole implements FileItem {
    
    /**
    * Creates a new file role.
    * @param configurationDirectory The configuration directory.
    * @param id The role ID.
    */
    public FileRole(File configurationDirectory, String id) {
        setId(id);
        setConfigurationDirectory(configurationDirectory);
    }

    public static final String ROLE = "role";

    /**
     * Creates a new FileRole object.
     */
    public FileRole() {
    }

    /**
     * Configure this instance of <code>FileRole</code>
     *
     * @param config containing the role details
     * @throws ConfigurationException if the <code>FileRole</code> could not be configured
     */
    public void configure(Configuration config) throws ConfigurationException {
        new ItemConfiguration().configure(this, config);
    }

    /**
     * Save the role
     *
     * @throws AccessControlException if the save fails
     */
    public void save() throws AccessControlException {
        DefaultConfigurationSerializer serializer = new DefaultConfigurationSerializer();
        Configuration config = createConfiguration();
        File xmlPath = getConfigurationDirectory();
        File xmlfile = new File(xmlPath, getId() + FileRoleManager.SUFFIX);

        try {
            serializer.serializeToFile(xmlfile, config);
        } catch (Exception e) {
            throw new AccessControlException(e);
        }
    }

    /**
     * Create a configuration containing the role details
     *
     * @return a <code>Configuration</code>
     */
    private Configuration createConfiguration() {
        DefaultConfiguration config = new DefaultConfiguration(ROLE);
        new ItemConfiguration().save(this, config);
        return config;
    }

    private File configurationDirectory;

    /**
     * Returns the configuration directory.
     * @return A file object.
     */
    public File getConfigurationDirectory() {
        return configurationDirectory;
    }

    /**
     * @see org.apache.lenya.ac.Item#setConfigurationDirectory(java.io.File)
     */
    public void setConfigurationDirectory(File file) {
        configurationDirectory = file;
    }
}
