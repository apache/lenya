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

package org.apache.lenya.ac.impl;

import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.Credential;
import org.apache.lenya.ac.Role;
import org.apache.lenya.util.Assert;

/**
 * Credential implementation.
 */
public class CredentialImpl implements Credential {
    private Accreditable accreditable;
    private Role role;
    private String method = DENY;

    /**
     * Creates a new credential object.
     * 
     * @param accreditable The accreditable.
     * @param role The role.
     */
    public CredentialImpl(Accreditable accreditable, Role role) {
        Assert.notNull("accreditable", accreditable);
        this.accreditable = accreditable;
        Assert.notNull("role", role);
        this.role = role;
    }

    /**
     * Returns the role of this credential.
     * 
     * @return A role.
     */
    public Role getRole() {
        return this.role;
    }

    /**
     * Returns the accreditable of this credential.
     * 
     * @return An accreditable.
     */
    public Accreditable getAccreditable() {
        return this.accreditable;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "[" + getAccreditable() + ":" + getRole() + " (" + getMethod() + ")]";
    }

    /**
     * Set the method of the credential, grant or deny
     * 
     * @param method A string grant or deny
     */
    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    /**
     * @return if the method is {@link Credential#GRANT}.
     */
    public boolean isGranted() {
        return this.method.equals(GRANT);
    }

    /**
     * @return if the method is {@link Credential#DENY}.
     */
    public boolean isDenied() {
        return this.method.equals(DENY);
    }

    public boolean equals(Object obj) {
        if (!getClass().isInstance(obj)) {
            return false;
        }
        Credential cred = (Credential) obj;
        return cred.getAccreditable().equals(getAccreditable()) && cred.getRole().equals(getRole());
    }

    public int hashCode() {
        Integer integer = Integer.valueOf(getAccreditable().hashCode() + getRole().hashCode());
        return integer.hashCode();
    }
}
