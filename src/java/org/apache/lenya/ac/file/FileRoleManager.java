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

/* $Id: FileRoleManager.java,v 1.3 2004/03/03 12:56:32 gregor Exp $  */

package org.apache.lenya.ac.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.RoleManager;

public final class FileRoleManager extends FileItemManager implements RoleManager {
    protected static final String SUFFIX = ".rml";
    private static Map instances = new HashMap();

    /**
     * Return the <code>RoleManager</code> for this configuration directory.
     * The <code>RoleManager</code> is a singleton.
     *
     * @param configurationDirectory the directory for which the RoleManager is requested.
     * @throws AccessControlException if the <code>RoleManager<code> could not be instantiated
     */
    protected FileRoleManager(File configurationDirectory)
        throws AccessControlException {
        super(configurationDirectory);
    }

    /**
     * Returns the role manager for this configuration directory.
     * @param configurationDirectory The configuration directory.
     * @return A role manager.
     * @throws AccessControlException when something went wrong.
     */
    public static FileRoleManager instance(File configurationDirectory)
        throws AccessControlException {
        if (!instances.containsKey(configurationDirectory)) {
            instances.put(configurationDirectory, new FileRoleManager(configurationDirectory));
        }

        return (FileRoleManager) instances.get(configurationDirectory);
    }

    /**
     * Get the role for the given ID.
     *
     * @param roleId The name of the role requested.
     * @return a <code>Role</code> or null if no role with the given name found
     */
    public Role getRole(String roleId) {
        return (Role) getItem(roleId);
    }

    /**
     * @see org.apache.lenya.cms.ac.ItemManager#getSuffix()
     */
    protected String getSuffix() {
        return SUFFIX;
    }

    /**
     * Get all roles
     *
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
     *
     * @param role Role to add
     */
    public void add(Role role) throws AccessControlException {
        super.add(role);
    }

    /**
     * Remove a role
     *
     * @param role Role to remove
     */
    public void remove(Role role) throws AccessControlException {
        super.remove(role);
    }
}
