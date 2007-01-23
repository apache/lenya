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

/* $Id$  */

package org.apache.lenya.ac.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.util.NetUtils;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.GroupManager;
import org.apache.lenya.ac.IPRangeManager;
import org.apache.lenya.ac.RoleManager;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.ac.UserType;
import org.apache.lenya.ac.impl.AbstractAccreditableManager;

/**
 * File-based accreditable manager.
 */
public class FileAccreditableManager extends AbstractAccreditableManager implements Serviceable,
        Configurable, Parameterizable {

    /**
     * Creates a new FileAccreditableManager. If you use this constructor, you have to set the
     * configuration directory either by calling {@link #setConfigurationDirectory(File)} or by
     * calling {@link #parameterize(Parameters)}.
     */
    public FileAccreditableManager() {
	    // do nothing
    }

    /**
     * Creates a new FileAccessController based on a configuration directory.
     * @param _configurationDirectory The configuration directory.
     * @param _userTypes The supported user types.
     */
    public FileAccreditableManager(File _configurationDirectory, UserType[] _userTypes) {
        assert _configurationDirectory != null;
        assert _configurationDirectory.exists();
        assert _configurationDirectory.isDirectory();
        this.configurationDirectory = _configurationDirectory;
        this.userTypes = new HashSet(Arrays.asList(_userTypes));
    }

    private File configurationDirectory;
    private Set userTypes;

    /**
     * Returns the supported user types.
     * @return An array of user types.
     * @throws AccessControlException if an error occurs.
     */
    public UserType[] getUserTypes() throws AccessControlException {
        if (this.userTypes == null)
            throw new AccessControlException("User types not initialized");
        return (UserType[]) this.userTypes.toArray(new UserType[this.userTypes.size()]);
    }

    /**
     * Returns the configuration directory.
     * @return The configuration directory.
     * @throws AccessControlException when something went wrong.
     */
    public File getConfigurationDirectory() throws AccessControlException {

        if (this.configurationDirectory == null) {

            if (this.configurationDirectoryPath == null) {
                throw new AccessControlException("Configuration directory not set!");
            }

            Source source = null;
            SourceResolver resolver = null;
            File directory;
            try {

                getLogger().debug(
                        "Configuration directory Path: [" + this.configurationDirectoryPath + "]");

                resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);
                source = resolver.resolveURI(this.configurationDirectoryPath);

                getLogger().debug("Configuration directory URI: " + source.getURI());
                directory = new File(new URI(NetUtils.encodePath(source.getURI())));
            } catch (Exception e) {
                throw new AccessControlException(e);
            } finally {
                if (resolver != null) {
                    if (source != null) {
                        resolver.release(source);
                    }
                    getManager().release(resolver);
                }
            }
            setConfigurationDirectory(directory);
        }

        return this.configurationDirectory;
    }

    protected static final String DIRECTORY = "directory";

    /**
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters parameters) throws ParameterException {
        if (parameters.isParameter(DIRECTORY)) {
            this.configurationDirectoryPath = parameters.getParameter(DIRECTORY);
            getLogger().debug("Configuration directory: [" + this.configurationDirectoryPath + "]");
        }
    }

    protected static final String A_M_TAG = "accreditable-manager";
    protected static final String U_M_CHILD_TAG = "user-manager";
    protected static final String U_T_CHILD_TAG = "user-type";
    protected static final String U_T_CLASS_ATTRIBUTE = "class";
    protected static final String U_T_CREATE_ATTRIBUTE = "create-use-case";
    // provided for backward compatibility
    protected static final String DEFAULT_USER_TYPE_CLASS = FileUser.class.getName();
    protected static final String DEFAULT_USER_TYPE_KEY = "Local User";
    protected static final String DEFAULT_USER_CREATE_USE_CASE = "userAddUser";

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     *      added to read new user-manager block within accreditable-manager
     */
    public void configure(Configuration configuration) throws ConfigurationException {
        if (A_M_TAG.equals(configuration.getName())) {
            this.userTypes = new HashSet();
            Configuration umConf = configuration.getChild(U_M_CHILD_TAG, false);
            if (umConf != null) {
                Configuration[] typeConfs = umConf.getChildren();
                for (int i = 0; i < typeConfs.length; i++) {
                    this.userTypes.add(new UserType(typeConfs[i].getValue(), typeConfs[i]
                            .getAttribute(U_T_CLASS_ATTRIBUTE), typeConfs[i]
                            .getAttribute(U_T_CREATE_ATTRIBUTE)));
                }
            } else {
                getLogger().debug(
                        "FileAccreditableManager: using default configuration for user types");
                // no "user-manager" block in access control: provide
                // a default for backward compatibility
                this.userTypes.add(getDefaultUserType());
            }
            // maybe TODO (or is it overkill?) : validate the parametrized user
            // types, for example, check if the classes are in the classpath ?
        } else {
            // TODO: In most cases it doesn't seem to find this element ...
            //throw new ConfigurationException("No such element: " + A_M_TAG);
        }
    }

    /**
     * Returns the default user type.
     * @return A user type.
     */
    public static UserType getDefaultUserType() {
        return new UserType(DEFAULT_USER_TYPE_KEY, DEFAULT_USER_TYPE_CLASS,
                DEFAULT_USER_CREATE_USE_CASE);
    }

    private String configurationDirectoryPath;

    /**
     * Sets the configuration directory.
     * @param file The configuration directory.
     * @throws AccessControlException if an error occurs
     */
    public void setConfigurationDirectory(File file) throws AccessControlException {
        if (file == null || !file.isDirectory()) {
            throw new AccessControlException("Configuration directory [" + file
                    + "] does not exist!");
        }
        this.configurationDirectory = file;
    }

    private ServiceManager manager;

    /**
     * Set the global component manager.
     * @param _manager The global component manager
     * @throws ServiceException when something went wrong.
     */
    public void service(ServiceManager _manager) throws ServiceException {
        this.manager = _manager;
    }

    /**
     * Returns the service manager.
     * @return A service manager.
     */
    protected ServiceManager getManager() {
        return this.manager;
    }

    /**
     * @see org.apache.lenya.ac.impl.AbstractAccreditableManager#initializeGroupManager()
     */
    protected GroupManager initializeGroupManager() throws AccessControlException {
        FileGroupManager _manager = FileGroupManager.instance(this, getConfigurationDirectory(), getLogger());
        return _manager;
    }

    /**
     * @see org.apache.lenya.ac.impl.AbstractAccreditableManager#initializeIPRangeManager()
     */
    protected IPRangeManager initializeIPRangeManager() throws AccessControlException {
        FileIPRangeManager _manager = FileIPRangeManager.instance(this, getConfigurationDirectory(), getLogger());
        return _manager;
    }

    /**
     * @see org.apache.lenya.ac.impl.AbstractAccreditableManager#initializeRoleManager()
     */
    protected RoleManager initializeRoleManager() throws AccessControlException {
        FileRoleManager _manager = FileRoleManager.instance(this, getConfigurationDirectory(), getLogger());
        return _manager;
    }

    /**
     * @see org.apache.lenya.ac.impl.AbstractAccreditableManager#initializeUserManager()
     */
    protected UserManager initializeUserManager() throws AccessControlException {
        FileUserManager _manager = FileUserManager.instance(this, getConfigurationDirectory(),
                getUserTypes(), getLogger());
        return _manager;
    }

    public String getConfigurationCollectionUri() {
        try {
            return "file://" + getConfigurationDirectory().getCanonicalPath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getId() {
        try {
            return this.configurationDirectory.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
