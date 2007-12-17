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
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.impl.AbstractUser;
import org.apache.lenya.ac.impl.ItemConfiguration;
import org.apache.log4j.Logger;

/**
 * File-based user implementation.
 * @version $Id: FileUser.java 473841 2006-11-12 00:46:38Z gregor $
 */
public class FileUser extends AbstractUser implements FileItem {
    
    private static final Logger log = Logger.getLogger(FileUser.class);

    public static final String ID = "identity";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String PASSWORD_ATTRIBUTE = "type";

    /**
     * Creates a new FileUser object.
     */
    public FileUser() {
    }

    /**
     * Create a FileUser
     * 
     * @param configurationDirectory where the user will be attached to
     * @param id the user id
     * @param fullName the full name of the user
     * @param email the users email address
     * @param password the users password
     */
    public FileUser(File configurationDirectory, String id, String fullName, String email,
            String password) {
        super(id, fullName, email, password);
        setConfigurationDirectory(configurationDirectory);
    }

    /**
     * Configure this FileUser.
     * 
     * @param config where the user details are specified
     * @throws ConfigurationException if the necessary details aren't specified in the config
     */
    public void configure(Configuration config) throws ConfigurationException {
        new ItemConfiguration().configure(this, config);
        setEmail(config.getChild(EMAIL).getValue(""));
        setEncryptedPassword(config.getChild(PASSWORD).getValue(null));

        removeFromAllGroups();
        Configuration[] groups = config.getChildren(GROUPS);

        if (groups.length == 1) {
            groups = groups[0].getChildren(GROUP);

            FileGroupManager manager = null;

            try {
                manager = (FileGroupManager) getItemManager().getAccreditableManager()
                        .getGroupManager();
            } catch (AccessControlException e) {
                throw new ConfigurationException(
                        "Exception when trying to fetch GroupManager for directory: ["
                                + configurationDirectory + "]", e);
            }

            for (int i = 0; i < groups.length; i++) {
                String groupId = groups[i].getValue();
                Group group = manager.getGroup(groupId);
                if (group == null) {
                    throw new ConfigurationException("Group [" + groupId + "] does not exist.");
                }
                group.add(this);
            }
        } else {
            // strange, it should have groups
            log.error("User " + getId() + " doesn't seem to have any groups");
        }
    }

    /**
     * Create a configuration from the current user details. Can be used for saving.
     * 
     * @return a <code>Configuration</code>
     */
    protected Configuration createConfiguration() {
        DefaultConfiguration config = new DefaultConfiguration(ID);
        new ItemConfiguration().save(this, config);

        DefaultConfiguration child = null;

        // add email node
        child = new DefaultConfiguration(EMAIL);
        child.setValue(getEmail());
        config.addChild(child);

        // add password node
        child = new DefaultConfiguration(PASSWORD);
        child.setValue(getEncryptedPassword());
        child.setAttribute(PASSWORD_ATTRIBUTE, "md5");
        config.addChild(child);

        // add group nodes
        child = new DefaultConfiguration(GROUPS);
        config.addChild(child);

        Group[] groups = getGroups();

        for (int i = 0; i < groups.length; i++) {
            DefaultConfiguration groupNode = new DefaultConfiguration(GROUP);
            groupNode.setValue(groups[i].getId());
            child.addChild(groupNode);
        }

        return config;
    }

    /**
     * @see org.apache.lenya.ac.User#save()
     */
    public void save() throws AccessControlException {
        DefaultConfigurationSerializer serializer = new DefaultConfigurationSerializer();
        Configuration config = createConfiguration();

        try {
            serializer.serializeToFile(getFile(), config);
        } catch (Exception e) {
            throw new AccessControlException(e);
        }
    }

    /**
     * @see org.apache.lenya.ac.User#delete()
     */
    public void delete() throws AccessControlException {
        super.delete();
        getFile().delete();
    }

    /**
     * Returns the configuration file.
     * @return A file object.
     */
    protected File getFile() {
        File xmlPath = getConfigurationDirectory();
        File xmlFile = new File(xmlPath, getId() + FileUserManager.SUFFIX);
        return xmlFile;
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
