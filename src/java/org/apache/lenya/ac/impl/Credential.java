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

/* $Id: Credential.java,v 1.2 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.ac.impl;


import java.util.HashSet;
import java.util.Set;

import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.Role;


/**
 * A credential assigns a set of {@link Role}s to an {@link Accreditable}.
 */
public class Credential {
    private Accreditable accreditable;
    private Set roles = new HashSet();

    /**
     * Creates a new credential object.
     * @param accreditable The accreditable.
     */
    public Credential(Accreditable accreditable) {
        setAccreditable(accreditable);
    }

    /**
     * Sets the accreditable for this credential.
     * @param accreditable The accreditable.
     */
    protected void setAccreditable(Accreditable accreditable) {
        assert accreditable != null;
        this.accreditable = accreditable;
    }

    /**
     * Returns all roles of this credential.
     *
     * @return An array of roles.
     */
    public Role[] getRoles() {
        return (Role[]) roles.toArray(new Role[roles.size()]);
    }

    /**
     * Adds a role to this credential.
     * @param role The role to add.
     */
    public void addRole(Role role) {
        assert role != null;
        assert !roles.contains(role);
        roles.add(role);
    }

    /**
     * Removes a role from this credential.
     * @param role The role to remove.
     */
    public void removeRole(Role role) {
        assert role != null;
        assert roles.contains(role);
        roles.remove(role);
    }

    /**
     * Returns the accreditable of this credential.
     * @return An accreditable.
     */
    public Accreditable getAccreditable() {
        return accreditable;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "[credential of: " + getAccreditable() + "]";
    }
    
    /**
     * Returns if a role is contained.
     * @param role A role.
     * @return A boolean value.
     */
    public boolean contains(Role role) {
        return roles.contains(role);
    }
    
    /**
     * Returns if the credential is empty (contains no roles).
     * @return A boolean value.
     */
    public boolean isEmpty() {
    	return roles.isEmpty();
    }
}
