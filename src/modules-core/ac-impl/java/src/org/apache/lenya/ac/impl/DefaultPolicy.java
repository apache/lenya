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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.Credential;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.ModifiablePolicy;
import org.apache.lenya.ac.Role;

/**
 * A DefaultPolicy is the own policy of a certain URL (not merged).
 */
public class DefaultPolicy implements ModifiablePolicy {

    private static final String KEY_SEPERATOR = ":";

    private Map accreditableToCredential = new LinkedHashMap();

    /**
     * Adds a credential to this policy.
     * 
     * @param credential
     *            A credential.
     */
    public void addCredential(Credential credential) {
        assert credential != null;
        if (credential.getRoles().length > 0) {
            Credential[] credentials = null;
            String key = credential.getAccreditable().toString()
                    + KEY_SEPERATOR + credential.getRoles()[0];
            if (!this.accreditableToCredential.containsKey(key)) {
                credentials = new Credential[1];
                credentials[0] = credential;
            } else {
                Credential[] oldCredentials = (Credential[]) this.accreditableToCredential
                        .get(key);
                int oldSize = oldCredentials.length;
                credentials = new Credential[oldSize + 1];
                int i;
                for (i = 0; i < oldSize; i++) {
                    credentials[i] = (Credential) oldCredentials[i];
                }
                credentials[i] = credential;
            }
            this.accreditableToCredential.put(key, credentials);
        }
    }

    /**
     * Adds a role to this policy for a certain accreditable and a certain role.
     * If a credenital exists for the accreditable, the role is added to this
     * credential. Otherwise, a new credential is created.
     * 
     * @param accreditable
     *            An accreditable.
     * @param role
     *            A role.
     */
    public void addRole(Accreditable accreditable, Role role, String method) {
        assert accreditable != null;
        assert role != null;

        CredentialImpl credential = (CredentialImpl) getCredential(
                accreditable, role);
        if (credential == null) {
            credential = new CredentialImpl(accreditable);
            credential.addRole(role);
            addCredential(credential);
        }
        credential.setMethod(method);
        if (!credential.contains(role)) {
            credential.addRole(role);
        }
    }

    /**
     * Removes a role from this policy for a certain accreditable and a certain
     * role.
     * 
     * @param accreditable
     *            An accreditable.
     * @param role
     *            A role.
     * @throws AccessControlException
     *             if the accreditable-role pair is not contained.
     */
    public void removeRole(Accreditable accreditable, Role role)
            throws AccessControlException {
        assert accreditable != null;
        assert role != null;
        CredentialImpl credential = (CredentialImpl) getCredential(
                accreditable, role);
        if (credential == null) {
            throw new AccessControlException("No credential for accreditable ["
                    + accreditable + "] ["
                    + this.accreditableToCredential.keySet().size() + "]");
        }
        if (!credential.contains(role)) {
            throw new AccessControlException("Credential for accreditable ["
                    + accreditable + "] does not contain role [" + role + "]");
        }
        removeCredential(credential);
    }

    /**
     * Returns the credentials of this policy.
     * 
     * @return An array of credentials.
     */
    public Credential[] getCredentials() {
        Credential[] credentials = null;
        LinkedHashSet returnCredential = new LinkedHashSet();
        for (Iterator iter = accreditableToCredential.keySet().iterator(); iter
                .hasNext();) {
            String key = (String) iter.next();
            Credential[] oldCredentials = (Credential[]) this.accreditableToCredential
                    .get(key);
            for (int i = 0; i < oldCredentials.length; i++) {
                returnCredential.add((Credential) oldCredentials[i]);
            }
        }
        return (Credential[]) returnCredential
                .toArray(new Credential[returnCredential.size()]);
    }

    /**
     * @see org.apache.lenya.ac.Policy#getRoles(org.apache.lenya.ac.Identity)
     */
    public Role[] getRoles(Identity identity) {
        Accreditable[] accreditables = identity.getAccreditables();
        Credential[] credentials = getCredentials();

        Set roles = new LinkedHashSet();

        for (int credIndex = 0; credIndex < credentials.length; credIndex++) {
            for (int accrIndex = 0; accrIndex < accreditables.length; accrIndex++) {

                Credential credential = credentials[credIndex];
                Accreditable accreditable = accreditables[accrIndex];

                if (credential.getAccreditable().equals(accreditable)) {
                    roles.addAll(Arrays.asList(credential.getRoles()));
                }
            }
        }

        return (Role[]) roles.toArray(new Role[roles.size()]);
    }

    /**
     * Returns the credentials for a certain accreditable.
     * 
     * @param accreditable
     *            An accreditable.
     * @param role
     * @return A credential.
     */
    public Credential getCredential(Accreditable accreditable, Role role) {
        Credential returnCredential = null;
        String key = accreditable.toString() + KEY_SEPERATOR + role;
        if (this.accreditableToCredential.containsKey(key)) {
            Credential[] oldCredentials = (Credential[]) this.accreditableToCredential
                    .get(key);
            int i;
            boolean out = false;
            for (i = 0; i < oldCredentials.length; i++) {
                Credential current = oldCredentials[i];
                Role[] checkRole = current.getRoles();
                for (int j = 0; j < checkRole.length; j++) {
                    Role role2 = checkRole[j];
                    if (role2.equals(role)) {
                        out = true;
                        returnCredential = current;
                        break;
                    }
                }
                if (out)
                    break;
            }
        }
        return returnCredential;
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
     * @param ssl
     *            A boolean value.
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
     * @param credential
     *            The credential to remove.
     * @throws AccessControlException
     *             If the credential does not exist.
     */
    protected void removeCredential(Credential credential)
            throws AccessControlException {
        if (credential.getRoles().length > 0) {
            String key = credential.getAccreditable().toString()
                    + KEY_SEPERATOR + credential.getRoles()[0];
            if (this.accreditableToCredential.containsKey(key)) {
                Credential[] oldCredentials = (Credential[]) this.accreditableToCredential
                        .get(key);
                int occurrence;
                boolean out = false;
                for (occurrence = 0; occurrence < oldCredentials.length; occurrence++) {
                    Credential current = oldCredentials[occurrence];
                    Role[] checkRole = current.getRoles();
                    for (int j = 0; j < checkRole.length; j++) {
                        Role role2 = checkRole[j];
                        if (role2.equals(credential.getRoles()[0])) {
                            out = true;
                            break;
                        }
                    }
                    if (out)
                        break;
                }
                if (out) {
                    Credential[] newCredentials = new Credential[oldCredentials.length - 1];
                    for (int i = 0; i < oldCredentials.length; i++) {
                        if (i < occurrence) {
                            newCredentials[i] = oldCredentials[i];
                        } else if (i > occurrence) {
                            newCredentials[i - 1] = oldCredentials[i];
                        }
                    }
                    this.accreditableToCredential.put(key, newCredentials);
                }
            }
        }

    }

    /**
     * Removes all roles for a certain accreditable.
     * 
     * @param accreditable
     *            The accreditable to remove all roles for.
     * @throws AccessControlException
     *             If no credential exists for this accreditable.
     */
    public void removeRoles(Accreditable accreditable)
            throws AccessControlException {
        Credential[] credentials = getCredentials();
        for (int credIndex = 0; credIndex < credentials.length; credIndex++) {
            Credential credential = credentials[credIndex];
            if (credential.getAccreditable().equals(accreditable)) {
                String key = accreditable.toString()+KEY_SEPERATOR+credential.getRoles()[0];
                if (this.accreditableToCredential.containsKey(key)) {
                    this.accreditableToCredential.remove(key);
                }
            }
        }
    }

    public Credential[] getCredentials(Identity identity)
            throws AccessControlException {
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
        return (Credential[]) returnCredential
                .toArray(new Credential[returnCredential.size()]);
    }

    public void moveRoleDown(Accreditable accreditable, Role role)
            throws AccessControlException {
        moveRole(accreditable, role, true);
    }

    private void moveRole(Accreditable accreditable, Role role, boolean down) {
        String key = accreditable.toString() + KEY_SEPERATOR + role;
        if (this.accreditableToCredential.containsKey(key)) {
            String[] keys = new String[accreditableToCredential.keySet().size()];
            int currentPosition = 0, matchedPosition = 0;
            for (Iterator iter = accreditableToCredential.keySet().iterator(); iter
                    .hasNext();) {
                String currentKey = (String) iter.next();
                keys[currentPosition] = currentKey;
                if (key.equals(currentKey)) {
                    matchedPosition = currentPosition;
                }
                currentPosition++;
            }
            int newPosition = 0;
            if (down) {
                // need to move it one down the tree
                newPosition = matchedPosition + 1;
            } else {
                // need to move it one up the tree
                newPosition = matchedPosition - 1;
            }
            String credentialMove = keys[newPosition];
            keys[newPosition] = key;
            keys[matchedPosition] = credentialMove;
            Map newAccreditable = new LinkedHashMap();
            for (int i = 0; i < keys.length; i++) {
                String oldKey = keys[i];
                newAccreditable.put(oldKey, this.accreditableToCredential
                        .get(oldKey));
            }
            this.accreditableToCredential = newAccreditable;

        }
    }

    public void moveRoleUp(Accreditable accreditable, Role role)
            throws AccessControlException {
        moveRole(accreditable, role, false);
    }

}
