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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.Credential;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.ModifiablePolicy;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;
import org.apache.lenya.util.Assert;

/**
 * A DefaultPolicy is the own policy of a certain URL (not merged).
 */
public class DefaultPolicy implements ModifiablePolicy {

    private List credentials = new ArrayList();

    /**
     * Adds a credential to this policy.
     * 
     * @param credential A credential.
     */
    public void addCredential(Credential credential) {
        assert credential != null;
        if (this.credentials.contains(credential)) {
            throw new IllegalArgumentException("The credential [" + credential
                    + "] is already contained!");
        } else {
            this.credentials.add(credential);
        }
    }

    /**
     * Adds a role to this policy for a certain accreditable and a certain role.
     * 
     * @param accreditable An accreditable.
     * @param role A role.
     */
    public void addRole(Accreditable accreditable, Role role, String method) {
        assert accreditable != null;
        assert role != null;
        CredentialImpl cred = new CredentialImpl(accreditable, role);
        cred.setMethod(method);
        addCredential(cred);
    }

    /**
     * Removes a role from this policy for a certain accreditable and a certain
     * role.
     * 
     * @param accreditable An accreditable.
     * @param role A role.
     * @throws AccessControlException if the accreditable-role pair is not
     *         contained.
     */
    public void removeRole(Accreditable accreditable, Role role) throws AccessControlException {
        assert accreditable != null;
        assert role != null;
        removeCredential(getCredential(accreditable, role));
    }

    /**
     * Returns the credentials of this policy in top-down order.
     * 
     * @return An array of credentials.
     */
    public Credential[] getCredentials() {
        return (Credential[]) this.credentials.toArray(new Credential[this.credentials.size()]);
    }

    /**
     * Returns the credentials for a certain accreditable.
     * 
     * @param accreditable An accreditable.
     * @param role
     * @return A credential.
     */
    public Credential getCredential(Accreditable accreditable, Role role) {
        Credential credential = null;
        for (Iterator i = this.credentials.iterator(); i.hasNext();) {
            Credential cred = (Credential) i.next();
            if (cred.getAccreditable().equals(accreditable) && cred.getRole().equals(role)) {
                credential = cred;
            }
        }
        return credential;
    }

    private boolean isSSL;

    /**
     * @see org.apache.lenya.ac.Policy#isSSLProtected()
     */
    public boolean isSSLProtected() throws AccessControlException {
        return this.isSSL;
    }

    /**
     * Sets if this policy requires SSL protection.
     * 
     * @param ssl A boolean value.
     */
    public void setSSL(boolean ssl) {
        this.isSSL = ssl;
    }

    /**
     * @see org.apache.lenya.ac.Policy#isEmpty()
     */
    public boolean isEmpty() throws AccessControlException {
        return getCredentials().length == 0;
    }

    /**
     * Removes a credential.
     * 
     * @param credential The credential to remove.
     * @throws AccessControlException If the credential does not exist.
     */
    protected void removeCredential(Credential credential) throws AccessControlException {
        if (this.credentials.contains(credential)) {
            this.credentials.remove(credential);
        }
    }

    /**
     * Removes all roles for a certain accreditable.
     * 
     * @param accreditable The accreditable to remove all roles for.
     * @throws AccessControlException If no credential exists for this
     *         accreditable.
     */
    public void removeRoles(Accreditable accreditable) throws AccessControlException {
        Credential[] credentials = getCredentials();
        for (int credIndex = 0; credIndex < credentials.length; credIndex++) {
            Credential credential = credentials[credIndex];
            if (credential.getAccreditable().equals(accreditable)) {
                this.credentials.remove(credential);
            }
        }
    }

    public Credential[] getCredentials(Identity identity) throws AccessControlException {
        Accreditable[] accreditables = identity.getAccreditables();
        Credential[] credentials = getCredentials();
        Set returnCredential = new LinkedHashSet();
        for (int credIndex = 0; credIndex < credentials.length; credIndex++) {
            Credential credential = credentials[credIndex];
            for (int accrIndex = 0; accrIndex < accreditables.length; accrIndex++) {
                Accreditable accreditable = accreditables[accrIndex];
                if (credential.getAccreditable().equals(accreditable)) {
                    returnCredential.add(credential);
                }
            }
        }
        return (Credential[]) returnCredential.toArray(new Credential[returnCredential.size()]);
    }

    public void moveRoleDown(Accreditable accreditable, Role role) throws AccessControlException {
        moveRole(accreditable, role, true);
    }

    private void moveRole(Accreditable accreditable, Role role, boolean down) {

        Credential cred = getCredential(accreditable, role);
        int position = this.credentials.indexOf(cred);

        if (!down && position > 0) {
            this.credentials.remove(cred);
            this.credentials.add(position - 1, cred);
        } else if (down && position < this.credentials.size() - 1) {
            this.credentials.remove(cred);
            this.credentials.add(position + 1, cred);
        }
    }

    public void moveRoleUp(Accreditable accreditable, Role role) throws AccessControlException {
        moveRole(accreditable, role, false);
    }

    public int check(Identity identity, Role role) throws AccessControlException {
        Assert.notNull("identity", identity);
        Assert.notNull("role", role);
        Credential[] credentials = getCredentials();
        for (int i = credentials.length - 1; i >= 0; i--) {
            if (matches(identity, credentials[i].getAccreditable())
                    && credentials[i].getRole().equals(role)) {
                if (credentials[i].getMethod().equals(CredentialImpl.GRANT)) {
                    return Policy.RESULT_GRANTED;
                } else {
                    return Policy.RESULT_DENIED;
                }
            }
        }
        return Policy.RESULT_NOT_MATCHED;
    }

    protected boolean matches(Identity identity, Accreditable accreditable) {
        Assert.notNull("identity", identity);
        Assert.notNull("accreditable", accreditable);
        Accreditable[] accrs = identity.getAccreditables();
        return Arrays.asList(accrs).contains(accreditable);
    }

}