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
import java.io.Serializable;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.GroupManager;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.ItemManager;
import org.apache.lenya.ac.impl.AbstractUser;
import org.apache.lenya.ac.impl.ItemConfiguration;

/**
 * File-based user implementation.
 * @version $Id$
 */
public class FileUser extends AbstractUser implements Item, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected static final String ID = "identity";
    protected static final String EMAIL = "email";
    protected static final String MENU_LOCALE = "default-menu-locale";
    protected static final String DOCUMENT_LOCALE = "default-document-locale";
    protected static final String PASSWORD = "password";
    protected static final String GROUPS = "groups";
    protected static final String GROUP = "group";
    protected static final String PASSWORD_ATTRIBUTE = "type";

    /**
     * Creates a new FileUser object.
     * @param itemManager The item manager.
     * @param logger The logger.
     */
    public FileUser(ItemManager itemManager, Logger logger) {
        super(itemManager, logger);
        FileItemManager fileItemManager = (FileItemManager) itemManager;
        setConfigurationDirectory(fileItemManager.getConfigurationDirectory());
    }

    /**
     * Create a FileUser
     * @param itemManager The item manager.
     * @param logger The logger.
     * @param id the user id
     * @param fullName the full name of the user
     * @param email the users email address
     * @param password the users password
     */
    public FileUser(ItemManager itemManager, Logger logger, String id, String fullName,
            String email, String password) {
        super(itemManager, logger, id, fullName, email, password);
        FileItemManager fileItemManager = (FileItemManager) itemManager;
        setConfigurationDirectory(fileItemManager.getConfigurationDirectory());
    }

    /**
     * Configure this FileUser.
     * @param config where the user details are specified
     * @throws ConfigurationException if the necessary details aren't specified
     *         in the config
     */
    public void configure(Configuration config) throws ConfigurationException {
        new ItemConfiguration().configure(this, config);
        setEmail(config.getChild(EMAIL).getValue(""));
        setDefaultMenuLocale(config.getChild(MENU_LOCALE).getValue(""));
        setDefaultDocumentLocale(config.getChild(DOCUMENT_LOCALE).getValue(""));
        setEncryptedPassword(config.getChild(PASSWORD).getValue(null));

        removeFromAllGroups();
        Configuration[] groups = config.getChildren(GROUPS);

        if (groups.length == 1) {
            groups = groups[0].getChildren(GROUP);

            GroupManager manager;
            try {
                manager = getAccreditableManager().getGroupManager();
            } catch (AccessControlException e) {
                throw new ConfigurationException("configuration failed: ", e);
            }

            for (int i = 0; i < groups.length; i++) {
                String groupId = groups[i].getValue();
                Group group = manager.getGroup(groupId);

                if (group == null) {
                    throw new ConfigurationException("Couldn't find Group for group name ["
                            + groupId + "]");
                }

                if (!group.contains(this)) {
                    group.add(this);
                }

            }
        } else {
            // strange, it should have groups
            getLogger().error("User " + getId() + " doesn't seem to have any groups");
        }
    }

    /**
     * Create a configuration from the current user details. Can be used for
     * saving.
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

        // add defaultMenuLocale node
        child = new DefaultConfiguration(MENU_LOCALE);
        child.setValue(getDefaultMenuLocale());
        config.addChild(child);

        // add defaultDocumentLocale node
        child = new DefaultConfiguration(DOCUMENT_LOCALE);
        child.setValue(getDefaultDocumentLocale());
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
        return this.configurationDirectory;
    }

    protected void setConfigurationDirectory(File _configurationDirectory) {
        assert (_configurationDirectory != null) && _configurationDirectory.isDirectory();
        this.configurationDirectory = _configurationDirectory;
    }

}