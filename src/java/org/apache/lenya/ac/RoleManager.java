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

/* $Id: RoleManager.java,v 1.2 2004/03/03 12:56:31 gregor Exp $  */

package org.apache.lenya.ac;

public interface RoleManager extends ItemManager {
    
    /**
     * Get the role for the given ID.
     *
     * @param roleId The name of the role requested.
     * @return a <code>Role</code> or null if no role with the given name found
     */
    Role getRole(String roleId);
    
    /**
     * Get all roles
     *
     * @return an array of roles
     */
    Role[] getRoles();
    
    /**
     * Add a role
     *
     * @param role Role to add
     * @throws AccessControlException when the role is already contained.
     */
    void add(Role role) throws AccessControlException;
    
    /**
     * Remove a role
     *
     * @param role Role to remove
     * @throws AccessControlException when the role is not contained.
     */
    void remove(Role role) throws AccessControlException;
    
}