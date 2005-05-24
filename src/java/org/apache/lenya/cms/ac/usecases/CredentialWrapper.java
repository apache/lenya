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

package org.apache.lenya.cms.ac.usecases;

import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.IPRange;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;

/**
 * Wrapper class for credentials.
 * @version $Id: CredentialWrapper.java 152682 2005-02-08 18:13:39Z gregor $
 */
public class CredentialWrapper {

    /**
     * Returns the accreditable ID.
     * @return A string.
     */
    public String getAccreditableId() {
        return this.accreditableId;
    }

    /**
     * Returns the accreditable name.
     * @return A string.
     */
    public String getAccreditableName() {
        return this.accreditableName;
    }

    /**
     * Returns the role ID.
     * @return A string.
     */
    public String getRoleId() {
        return this.roleId;
    }

    /**
     * Returns the role name.
     * @return A string.
     */
    public String getRoleName() {
        return this.roleName;
    }

    /**
     * Returns the accreditable type ({@link #USER}, {@link #GROUP}, or {@link #IPRANGE})
     * @return A string.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Ctor.
     * @param accreditable The accreditable of the credential to wrap.
     * @param role The role of the credential to wrap.
     */
    public CredentialWrapper(Accreditable accreditable, Role role) {
        if (accreditable instanceof Item) {
            Item item = (Item) accreditable;
            this.accreditableId = item.getId();
            this.accreditableName = item.getName();
        
            if (item instanceof User) {
                this.type = USER;
            }
            else if (item instanceof Group) {
                this.type = GROUP;
            }
            else if (item instanceof IPRange) {
                this.type = IPRANGE;
            }
        }
        else {
            this.accreditableId = "world";
            this.accreditableName = "the world";
            this.type = "world";
        }
        this.roleId = role.getId();
        this.roleName = role.getName();
        
    }
    
    protected static final String USER = "user";
    protected static final String GROUP = "group";
    protected static final String IPRANGE = "iprange";
    
    private String type;
    private String accreditableId;
    private String accreditableName;
    private String roleId;
    private String roleName;

}
