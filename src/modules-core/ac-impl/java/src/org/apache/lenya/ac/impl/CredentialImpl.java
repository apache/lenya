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

/* $Id$  */

package org.apache.lenya.ac.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.Credential;
import org.apache.lenya.ac.Role;

/**
 * Credential implementation.
 */
public class CredentialImpl implements Credential {
    private Accreditable accreditable;
    private Set roles = new HashSet();
    private String method;
    protected static final String GRANT = "grant";
    protected static final String DENY = "deny";

    /**
     * Creates a new credential object.
     * @param _accreditable The accreditable.
     */
    public CredentialImpl(Accreditable _accreditable) {
        setAccreditable(_accreditable);
    }

    /**
     * Sets the accreditable for this credential.
     * @param _accreditable The accreditable.
     */
    protected void setAccreditable(Accreditable _accreditable) {
        assert _accreditable != null;
        this.accreditable = _accreditable;
    }

    /**
     * Returns all roles of this credential.
     * @return An array of roles.
     */
    public Role[] getRoles() {
        return (Role[]) this.roles.toArray(new Role[this.roles.size()]);
    }

    /**
     * Adds a role to this credential.
     * @param role The role to add.
     */
    public void addRole(Role role) {
        assert role != null;
        assert !this.roles.contains(role);
        this.roles.add(role);
    }

    /**
     * Removes a role from this credential.
     * @param role The role to remove.
     */
    public void removeRole(Role role) {
        assert role != null;
        assert this.roles.contains(role);
        this.roles.remove(role);
    }

    /**
     * Returns the accreditable of this credential.
     * @return An accreditable.
     */
    public Accreditable getAccreditable() {
        return this.accreditable;
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
        return this.roles.contains(role);
    }

    /**
     * Returns if the credential is empty (contains no roles).
     * @return A boolean value.
     */
    public boolean isEmpty() {
        return this.roles.isEmpty();
    }
    
    /**
     * Set the method of the credential, grant or deny
     * @param method A string grant or deny
     */
    public void setMethod(String method) {
        this.method = method;
    }
    

    public String getMethod() {
        return method;
    }

    public boolean isGranted() {
        return this.method.equals(GRANT);
    }

    public boolean isDenied() {
        return this.method.equals(DENY);
    }
}
