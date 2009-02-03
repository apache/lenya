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

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
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
import org.apache.lenya.util.Assert;

/**
 * File-based accreditable manager.
 */
public class FileAccreditableManager extends AbstractAccreditableManager {

    private ServiceManager manager;

    /**
     * Creates a new FileAccessController based on a configuration directory.
     * @param manager The service manager.
     * @param logger The logger.
     * @param configurationUri The configuration directory URI.
     * @param _userTypes The supported user types.
     */
    public FileAccreditableManager(ServiceManager manager, Logger logger,
            String configurationUri, UserType[] _userTypes) {
        super(logger);

        Assert.notNull("service manager", manager);
        this.manager = manager;

        Assert.notNull("configuration directory", configurationUri);
        this.configurationDirectoryUri = configurationUri;

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

            if (this.configurationDirectoryUri == null) {
                throw new AccessControlException("Configuration directory not set!");
            }

            Source source = null;
            SourceResolver resolver = null;
            File directory;
            try {

                getLogger().debug(
                        "Configuration directory Path: [" + this.configurationDirectoryUri + "]");

                resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);
                source = resolver.resolveURI(this.configurationDirectoryUri);

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
            this.configurationDirectory = directory;
        }

        return this.configurationDirectory;
    }

    // provided for backward compatibility
    protected static final String DEFAULT_USER_TYPE_CLASS = FileUser.class.getName();
    protected static final String DEFAULT_USER_TYPE_KEY = "Local User";
    protected static final String DEFAULT_USER_CREATE_USE_CASE = "userAddUser";

    /**
     * Returns the default user type.
     * @return A user type.
     */
    public static UserType getDefaultUserType() {
        return new UserType(DEFAULT_USER_TYPE_KEY, DEFAULT_USER_TYPE_CLASS,
                DEFAULT_USER_CREATE_USE_CASE);
    }

    private String configurationDirectoryUri;

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
        FileGroupManager _manager = FileGroupManager.instance(this, getConfigurationDirectory(),
                getLogger());
        return _manager;
    }

    /**
     * @see org.apache.lenya.ac.impl.AbstractAccreditableManager#initializeIPRangeManager()
     */
    protected IPRangeManager initializeIPRangeManager() throws AccessControlException {
        FileIPRangeManager _manager = FileIPRangeManager.instance(this,
                getConfigurationDirectory(), getLogger());
        return _manager;
    }

    /**
     * @see org.apache.lenya.ac.impl.AbstractAccreditableManager#initializeRoleManager()
     */
    protected RoleManager initializeRoleManager() throws AccessControlException {
        FileRoleManager _manager = FileRoleManager.instance(this, getConfigurationDirectory(),
                getLogger());
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
            Assert.notNull("configuration directory", this.getConfigurationDirectory());
            return this.getConfigurationDirectory().getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch(AccessControlException e) {
            throw new RuntimeException(e);
        }
    }

}
