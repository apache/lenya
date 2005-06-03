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

/* $Id: CMSSituation.java 157324 2005-03-13 09:41:14Z andreas $  */

package org.apache.lenya.cms.workflow;

import org.apache.lenya.workflow.Situation;

/**
 * The CMS situation
 */
public class LenyaSituation implements Situation {

    /**
     * Returns the machine IP address.
     * @return A string.
     */
    public String getMachineIp() {
        return this.machineIp;
    }

    /**
     * Returns the user ID.
     * @return A string.
     */
    public String getUserId() {
        return this.userId;
    }

	/**
	 * Creates a new instance of Situation
	 * @param _roleIds The role IDs.
     * @param _userId The user ID.
     * @param _machineIp The machine IP address.
	 */
    protected LenyaSituation(String[] _roleIds, String _userId, String _machineIp) {
        this.roleIds = _roleIds;
        this.userId = _userId;
        this.machineIp = _machineIp;
    }

    private String[] roleIds;

    /**
     * Get the roles
     * @return the roles
     */
    public String[] getRoleIds() {
        return this.roleIds;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String rolesString = "";
        for (int i = 0; i < this.roleIds.length; i++) {
            if (i > 0) {
                rolesString += ", ";
            }
            rolesString += this.roleIds[i];
        }
        return "roles: " + rolesString;
    }
    
    private String userId;
    private String machineIp;
    
}
