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

/* $Id: CredentialWrapper.java,v 1.2 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.cms.ac.cocoon;

import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.IPRange;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;

public class CredentialWrapper {

    /**
     * Returns the accreditable ID.
     * @return A string.
     */
    public String getAccreditableId() {
        return accreditableId;
    }

    /**
     * Returns the accreditable name.
     * @return A string.
     */
    public String getAccreditableName() {
        return accreditableName;
    }

    /**
     * Returns the role ID.
     * @return A string.
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * Returns the role name.
     * @return A string.
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Returns the accreditable type ({@link #USER}, {@link GROUP}, or {@link IPRANGE})
     * @return A string.
     */
    public String getType() {
        return type;
    }

    /**
     * Ctor.
     * @param accreditable The accreditable of the credential to wrap.
     * @param role The role of the credential to wrap.
     */
    public CredentialWrapper(Accreditable accreditable, Role role) {
        if (accreditable instanceof Item) {
            Item item = (Item) accreditable;
            accreditableId = item.getId();
            accreditableName = item.getName();
        
            if (item instanceof User) {
                type = USER;
            }
            else if (item instanceof Group) {
                type = GROUP;
            }
            else if (item instanceof IPRange) {
                type = IPRANGE;
            }
        }
        else {
            accreditableId = "world";
            accreditableName = "the world";
            type = "world";
        }
        roleId = role.getId();
        roleName = role.getName();
        
    }
    
    public static final String USER = "user";
    public static final String GROUP = "group";
    public static final String IPRANGE = "iprange";
    
    private String type;
    private String accreditableId;
    private String accreditableName;
    private String roleId;
    private String roleName;

}
