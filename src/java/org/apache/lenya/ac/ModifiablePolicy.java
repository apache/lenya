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
package org.apache.lenya.ac;

/**
 * Modifiable policy.
 */
public interface ModifiablePolicy extends Policy {

    /**
     * Sets if this policy requires SSL protection.
     * @param ssl A boolean value.
     */
    void setSSL(boolean ssl);

    /**
     * Adds a role to this policy for a certain accreditable and a certain role. If a credenital
     * exists for the accreditable, the role is added to this credential. Otherwise, a new
     * credential is created.
     * @param accreditable An accreditable.
     * @param role A role.
     */
    public void addRole(Accreditable accreditable, Role role);

    /**
     * Removes a role from this policy for a certain accreditable and a certain role.
     * @param accreditable An accreditable.
     * @param role A role.
     * @throws AccessControlException if the accreditable-role pair is not contained.
     */
    public void removeRole(Accreditable accreditable, Role role) throws AccessControlException;

    /**
     * Removes all roles from this policy for a certain accreditable.
     * @param accreditable An accreditable.
     * @throws AccessControlException if the accreditable-role pair is not contained.
     */
    void removeRoles(Accreditable accreditable) throws AccessControlException;

    /**
     * @return The credentials of this policy.
     */
    Credential[] getCredentials();
}
