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

/* $Id: UsecaseRoles.java,v 1.2 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.cms.ac.usecase;

import java.util.HashMap;
import java.util.Map;

public class UsecaseRoles {
    
    private Map usecaseToRoles = new HashMap();
    
    /**
     * Ctor.
     */
    public UsecaseRoles() {
    }
    
    /**
     * Sets the roles for a usecase.
     * @param usecaseId The usecase ID.
     * @param roleIds The role IDs.
     */
    public void setRoles(String usecaseId, String[] roleIds) {
        usecaseToRoles.put(usecaseId, roleIds);
    }
    
    /**
     * Returns the roles for a usecase.
     * If no roles are defined for this usecase, an array of size 0 is returned.
     * @param usecaseId The usecase ID.
     * @return A role array.
     */
    public String[] getRoles(String usecaseId) {
        String[] usecaseRoles;
        if (usecaseToRoles.containsKey(usecaseId)) {
            usecaseRoles = (String[]) usecaseToRoles.get(usecaseId);
        } else {
            usecaseRoles = new String[0];
        }
        return usecaseRoles;
    }
    
    /**
     * Checks if a usecase has roles.
     * @param usecaseId The usecase ID.
     * @return A boolean value.
     */
    public boolean hasRoles(String usecaseId) {
        return usecaseToRoles.containsKey(usecaseId);
    }

}
