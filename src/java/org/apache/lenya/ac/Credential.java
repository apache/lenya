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
 * A credential assigns a set of {@link Role}s to an {@link Accreditable}.
 */
public interface Credential {

    /**
     * @return The accreditable this credential applies to.
     */
    Accreditable getAccreditable();

    /**
     * @return The roles of this credential.
     */
    Role[] getRoles();

    /**
     * Returns if the credential contains a certain role.
     * @param role A role.
     * @return A boolean value.
     */
    boolean contains(Role role);
    
    String getMethod();
}