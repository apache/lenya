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

/* $Id: FileGroup.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.ac.file;

import java.io.File;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.impl.AbstractGroup;
import org.apache.lenya.ac.impl.ItemConfiguration;

/**
 * File-based group implementation.
 */
public class FileGroup extends AbstractGroup implements FileItem {

    protected static final String RULE = "rule";

    /**
     * @see org.apache.lenya.ac.Group#delete()
     */
    public void delete() throws AccessControlException {
        super.delete();
        getFile().delete();
    }

    /**
     * Creates a new FileGroup object.
     */
    public FileGroup() {
    }

    /**
     * Create a new instance of <code>FileGroup</code>
     * @param configurationDirectory to which the group will be attached to
     * @param id the ID of the group
     */
    public FileGroup(File configurationDirectory, String id) {
        super(id);
        setConfigurationDirectory(configurationDirectory);
    }

    /**
     * Configures this file group.
     * @param config The configuration.
     * @throws ConfigurationException when something went wrong.
     */
    public void configure(Configuration config) throws ConfigurationException {
        new ItemConfiguration().configure(this, config);
        Configuration ruleConfig = config.getChild(RULE, false);
        if (ruleConfig != null) {
            String rule = ruleConfig.getValue();
            try {
                setRule(rule);
            } catch (AccessControlException e) {
                throw new ConfigurationException("Setting rule [" + rule + "] failed: ", e);
            }
        }
    }

    /**
     * Returns the configuration file.
     * @return A file object.
     */
    protected File getFile() {
        File xmlPath = getConfigurationDirectory();
        File xmlFile = new File(xmlPath, getId() + FileGroupManager.SUFFIX);
        return xmlFile;
    }

    /**
     * Save this group
     * 
     * @throws AccessControlException if the save failed
     */
    public void save() throws AccessControlException {
        DefaultConfigurationSerializer serializer = new DefaultConfigurationSerializer();
        Configuration config = createConfiguration();
        File xmlfile = getFile();
        try {
            serializer.serializeToFile(xmlfile, config);
        } catch (Exception e) {
            throw new AccessControlException(e);
        }
    }

    /**
     * Group configuration element.
     */
    public static final String GROUP = "group";

    /**
     * Create a configuration containing the group details
     * 
     * @return a <code>Configuration</code>
     */
    private Configuration createConfiguration() {
        DefaultConfiguration config = new DefaultConfiguration(GROUP);
        new ItemConfiguration().save(this, config);
        if (getRule() != null) {
            DefaultConfiguration ruleConfig = new DefaultConfiguration(RULE);
            ruleConfig.setValue(getRule());
            config.addChild(ruleConfig);
        }
        return config;
    }

    private File configurationDirectory;

    /**
     * Returns the configuration directory.
     * @return A file object.
     */
    protected File getConfigurationDirectory() {
        return configurationDirectory;
    }

    /**
     * @see org.apache.lenya.ac.Item#setConfigurationDirectory(java.io.File)
     */
    public void setConfigurationDirectory(File configurationDirectory) {
        assert (configurationDirectory != null) && configurationDirectory.isDirectory();
        this.configurationDirectory = configurationDirectory;
    }
}
