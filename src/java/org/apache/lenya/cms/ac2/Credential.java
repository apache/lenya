/*
 * $Id
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.ac2;

import java.util.HashSet;
import java.util.Set;

import org.apache.lenya.cms.ac.Role;

/**
 * A credential assigns a set of {@link Role}s to an {@link Accreditable}.
 * 
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
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
        assert role != null && !roles.contains(role);
        roles.add(role);
    }

    /**
     * Removes a role from this credential.
     * @param role The role to remove.
     */
    public void removeRole(Role role) {
        assert role != null && roles.contains(role);
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

}
