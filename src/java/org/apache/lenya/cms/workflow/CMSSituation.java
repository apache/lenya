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

/* $Id: CMSSituation.java,v 1.10 2004/03/01 16:18:16 gregor Exp $  */

package org.apache.lenya.cms.workflow;

import org.apache.lenya.workflow.Situation;

public class CMSSituation implements Situation {

    /**
     * Returns the machine IP address.
     * @return A string.
     */
    public String getMachineIp() {
        return machineIp;
    }

    /**
     * Returns the user ID.
     * @return A string.
     */
    public String getUserId() {
        return userId;
    }

	/**
	 * Creates a new instance of Situation
	 * @param roleIds The role IDs.
     * @param userId The user ID.
     * @param machineIp The machine IP address.
	 */
    protected CMSSituation(String[] roleIds, String userId, String machineIp) {
        this.roleIds = roleIds;
        this.userId = userId;
        this.machineIp = machineIp;
    }

    private String[] roleIds;

    /**
     * Get the roles
     * 
     * @return the roles
     */
    public String[] getRoleIds() {
        return roleIds;
    }

    /** (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "(roles: " + roleIds + ")";
    }
    
    private String userId;
    private String machineIp;
    
}
