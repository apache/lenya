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
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.ItemManager;
import org.apache.lenya.ac.impl.AbstractRole;
import org.apache.lenya.ac.impl.ItemConfiguration;

/**
 * File-based role implementation.
 * @version $Id$
 */
public class FileRole extends AbstractRole implements Item {

    protected static final String ATTR_ASSIGNABLE = "assignable";

    /**
     * Creates a new file role.
     * @param itemManager The item manager.
     * @param logger The logger.
     * @param id The role ID.
     */
    public FileRole(ItemManager itemManager, Logger logger, String id) {
        this(itemManager, logger);
        setId(id);
    }

    protected static final String ROLE = "role";

    /**
     * Creates a new FileRole object.
     * @param itemManager The item manager.
     * @param logger The logger.
     */
    public FileRole(ItemManager itemManager, Logger logger) {
        super(itemManager, logger);
        FileItemManager fileItemManager = (FileItemManager) itemManager;
        setConfigurationDirectory(fileItemManager.getConfigurationDirectory());
    }

    /**
     * Configure this instance of <code>FileRole</code>
     * @param config containing the role details
     * @throws ConfigurationException if the <code>FileRole</code> could not
     *         be configured
     */
    public void configure(Configuration config) throws ConfigurationException {
        new ItemConfiguration().configure(this, config);
        this.isAssignable = config.getAttributeAsBoolean(ATTR_ASSIGNABLE, true);
    }

    /**
     * Save the role
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
     * @return a <code>Configuration</code>
     */
    private Configuration createConfiguration() {
        DefaultConfiguration config = new DefaultConfiguration(ROLE);
        new ItemConfiguration().save(this, config);
        return config;
    }

    private File configurationDirectory;
    private boolean isAssignable;

    /**
     * Returns the configuration directory.
     * @return A file object.
     */
    public File getConfigurationDirectory() {
        return this.configurationDirectory;
    }

    protected void setConfigurationDirectory(File file) {
        this.configurationDirectory = file;
    }

    public boolean isAssignable() {
        return this.isAssignable;
    }
}