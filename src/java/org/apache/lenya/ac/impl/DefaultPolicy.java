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

/* $Id: DefaultPolicy.java,v 1.4 2004/03/08 16:48:20 gregor Exp $  */

package org.apache.lenya.ac.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;

/**
 * A DefaultPolicy is the own policy of a certain URL (not merged).
 */
public class DefaultPolicy implements Policy {

    private Map accreditableToCredential = new HashMap();

    /**
	 * Adds a credential to this policy.
	 * 
	 * @param credential A credential.
	 */
    public void addCredential(Credential credential) {
        assert credential != null;
        assert !accreditableToCredential.containsKey(credential.getAccreditable());
        accreditableToCredential.put(credential.getAccreditable(), credential);
    }

    /**
	 * Adds a role to this policy for a certain accreditable and a certain role. If a credenital
	 * exists for the accreditable, the role is added to this credential. Otherwise, a new
	 * credential is created.
	 * 
	 * @param accreditable An accreditable.
	 * @param role A role.
	 */
    public void addRole(Accreditable accreditable, Role role) {
        assert accreditable != null;
        assert role != null;

        Credential credential = getCredential(accreditable);
        if (credential == null) {
            credential = new Credential(accreditable);
            addCredential(credential);
        }
        if (!credential.contains(role)) {
            credential.addRole(role);
        }
    }

    /**
	 * Removes a role from this policy for a certain accreditable and a certain role.
	 * 
	 * @param accreditable An accreditable.
	 * @param role A role.
	 * @throws AccessControlException if the accreditable-role pair is not contained.
	 */
    public void removeRole(Accreditable accreditable, Role role) throws AccessControlException {
        assert accreditable != null;
        assert role != null;
        Credential credential = getCredential(accreditable);
        if (credential == null) {
            throw new AccessControlException(
                "No credential for accreditable ["
                    + accreditable
                    + "] ["
                    + accreditableToCredential.keySet().size()
                    + "]");
        }
        if (!credential.contains(role)) {
            throw new AccessControlException(
                "Credential for accreditable ["
                    + accreditable
                    + "] does not contain role ["
                    + role
                    + "]");
        }
        credential.removeRole(role);

        if (credential.isEmpty()) {
            removeCredential(credential);
        }
    }

    /**
	 * Returns the credentials of this policy.
	 * 
	 * @return An array of credentials.
	 */
    public Credential[] getCredentials() {
        Collection values = accreditableToCredential.values();
        return (Credential[]) values.toArray(new Credential[values.size()]);
    }

    /**
	 * @see org.apache.lenya.ac.Policy#getRoles(org.apache.lenya.ac.Identity)
	 */
    public Role[] getRoles(Identity identity) {
        Accreditable[] accreditables = identity.getAccreditables();
        Credential[] credentials = getCredentials();

        Set roles = new HashSet();

        for (int credIndex = 0; credIndex < credentials.length; credIndex++) {
            for (int accrIndex = 0; accrIndex < accreditables.length; accrIndex++) {
                Credential credential = credentials[credIndex];
                Accreditable accreditable = accreditables[accrIndex];

                if (credential.getAccreditable().equals(accreditable)) {
                    roles.addAll(Arrays.asList(credential.getRoles()));
                }
            }
        }

        return (AbstractRole[]) roles.toArray(new AbstractRole[roles.size()]);
    }

    /**
	 * Returns the credential for a certain accreditable.
	 * 
	 * @param accreditable An accreditable.
	 * @return A credential.
	 */
    protected Credential getCredential(Accreditable accreditable) {
        return (Credential) accreditableToCredential.get(accreditable);
    }

    private boolean isSSL;

    /**
	 * @see org.apache.lenya.ac.Policy#isSSLProtected()
	 */
    public boolean isSSLProtected() throws AccessControlException {
        return isSSL;
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
        if (!accreditableToCredential.containsValue(credential)) {
            throw new AccessControlException("Credential [" + credential + "] does not exist!");
        }
        accreditableToCredential.remove(credential.getAccreditable());
    }

    /**
	 * Removes all roles for a certain accreditable.
	 * 
	 * @param accreditable The accreditable to remove all roles for.
	 * @throws AccessControlException If no credential exists for this accreditable.
	 */
    public void removeRoles(Accreditable accreditable) throws AccessControlException {
        if (accreditableToCredential.containsKey(accreditable)) {
            Credential credential = getCredential(accreditable);
            removeCredential(credential);
        }
    }

}
