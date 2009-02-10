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

import java.util.HashMap;
import java.util.Map;

import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.RoleManager;

/**
 * File-based role manager implementation.
 * @version $Id$
 */
public final class FileRoleManager extends FileItemManager implements RoleManager {
    protected static final String SUFFIX = ".rml";
    private static Map instances = new HashMap();

    /**
     * Return the <code>RoleManager</code> for this configuration directory. The
     * <code>RoleManager</code> is a singleton.
     * @param mgr The accreditable manager.
     * @param sourceResolver
     */
    protected FileRoleManager(AccreditableManager mgr, SourceResolver sourceResolver) {
        super(mgr, sourceResolver);
    }

    /**
     * Returns the role manager for this configuration directory.
     * @param mgr The accreditable manager.
     * @param configUri The configuration directory.
     * @param logger The logger.
     * @param sourceResolver
     * @return A role manager.
     * @throws AccessControlException when something went wrong.
     */
    public static FileRoleManager instance(AccreditableManager mgr, String configUri,
            SourceResolver sourceResolver) throws AccessControlException {
        if (!instances.containsKey(configUri)) {
            FileRoleManager manager = new FileRoleManager(mgr, sourceResolver);
            manager.configure(configUri);
            instances.put(configUri, manager);
        }

        return (FileRoleManager) instances.get(configUri);
    }

    /**
     * Get the role for the given ID.
     * @param roleId The name of the role requested.
     * @return a <code>Role</code> or null if no role with the given name found
     */
    public Role getRole(String roleId) {
        return (Role) getItem(roleId);
    }

    /**
     * @see org.apache.lenya.ac.file.FileItemManager#getSuffix()
     */
    protected String getSuffix() {
        return SUFFIX;
    }

    /**
     * Get all roles
     * @return an array of roles.
     */
    public Role[] getRoles() {
        Item[] items = super.getItems();
        Role[] roles = new Role[items.length];
        for (int i = 0; i < roles.length; i++) {
            roles[i] = (Role) items[i];
        }
        return roles;
    }

    /**
     * Add a role
     * @param role The role to add.
     * @throws AccessControlException if an error occurs.
     */
    public void add(Role role) throws AccessControlException {
        super.add(role);
    }

    /**
     * Remove a role
     * @param role The role to remove.
     * @throws AccessControlException if an error occurs.
     */
    public void remove(Role role) throws AccessControlException {
        super.remove(role);
    }

    protected Item createItem() {
        return new FileRole(this, getLogger());
    }
}