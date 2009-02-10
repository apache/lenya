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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class FileAccreditableManager extends AbstractAccreditableManager {

    private static final Log logger = LogFactory.getLog(FileAccreditableManager.class);
    private SourceResolver sourceResolver;

    /**
     * Creates a new FileAccessController based on a configuration directory.
     * @param manager The service manager.
     * @param logger The logger.
     * @param configurationUri The configuration directory URI.
     * @param _userTypes The supported user types.
     */
    public FileAccreditableManager(String configurationUri, UserType[] _userTypes) {

        Validate.notNull(configurationUri, "configuration directory");
        this.configUri = configurationUri;

        this.userTypes = new HashSet(Arrays.asList(_userTypes));
    }

    private String configUri;
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

    /**
     * @see org.apache.lenya.ac.impl.AbstractAccreditableManager#initializeGroupManager()
     */
    protected GroupManager initializeGroupManager() throws AccessControlException {
        FileGroupManager _manager = FileGroupManager.instance(this, getConfigurationUri(),
                this.sourceResolver);
        return _manager;
    }

    private String getConfigurationUri() {
        return this.configUri;
    }

    /**
     * @see org.apache.lenya.ac.impl.AbstractAccreditableManager#initializeIPRangeManager()
     */
    protected IPRangeManager initializeIPRangeManager() throws AccessControlException {
        FileIPRangeManager _manager = FileIPRangeManager.instance(this, getConfigurationUri(),
                this.sourceResolver);
        return _manager;
    }

    /**
     * @see org.apache.lenya.ac.impl.AbstractAccreditableManager#initializeRoleManager()
     */
    protected RoleManager initializeRoleManager() throws AccessControlException {
        FileRoleManager _manager = FileRoleManager.instance(this, getConfigurationUri(),
                this.sourceResolver);
        return _manager;
    }

    /**
     * @see org.apache.lenya.ac.impl.AbstractAccreditableManager#initializeUserManager()
     */
    protected UserManager initializeUserManager() throws AccessControlException {
        FileUserManager _manager = FileUserManager.instance(this, getConfigurationCollectionUri(),
                getUserTypes(), this.sourceResolver);
        return _manager;
    }

    public String getId() {
        return this.configUri;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

    public String getConfigurationCollectionUri() {
        return getConfigurationUri();
    }

}
