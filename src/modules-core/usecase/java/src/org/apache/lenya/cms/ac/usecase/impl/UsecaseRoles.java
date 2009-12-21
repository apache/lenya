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

package org.apache.lenya.cms.ac.usecase.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to manage roles for a usecase.
 * 
 * @version $Id$
 */
public class UsecaseRoles {

    private Map usecaseToRoles = new HashMap();

    /**
     * Ctor.
     */
    public UsecaseRoles() {
        // do nothing
    }

    /**
     * Sets the roles for a usecase.
     * @param usecaseId The usecase ID.
     * @param roleIds The role IDs.
     */
    public void setRoles(String usecaseId, String[] roleIds) {
        this.usecaseToRoles.put(usecaseId, roleIds);
    }

    /**
     * Returns the roles for a usecase. If no roles are defined for this
     * usecase, an array of size 0 is returned.
     * @param usecaseId The usecase ID.
     * @return A role array.
     */
    public String[] getRoles(String usecaseId) {
        String[] usecaseRoles;
        if (this.usecaseToRoles.containsKey(usecaseId)) {
            usecaseRoles = (String[]) this.usecaseToRoles.get(usecaseId);
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
        return this.usecaseToRoles.containsKey(usecaseId);
    }

    /**
     * @return All available usecase names.
     */
    public String[] getUsecaseNames() {
        Set names = this.usecaseToRoles.keySet();
        return (String[]) names.toArray(new String[names.size()]);
    }

    /**
     * @param usecase The usecase name.
     * @param role The role ID.
     */
    public void addRole(String usecase, String role) {
        String[] usecaseRoles = getRoles(usecase);
        Set newRoles = new HashSet();
        newRoles.addAll(Arrays.asList(usecaseRoles));
        newRoles.add(role);
        this.usecaseToRoles.put(usecase, newRoles.toArray(new String[newRoles.size()]));
    }

    /**
     * @param usecase The usecase.
     * @param role The role.
     */
    public void removeRole(String usecase, String role) {
        String[] usecaseRoles = getRoles(usecase);
        Set newRoles = new HashSet();
        newRoles.addAll(Arrays.asList(usecaseRoles));

        if (!newRoles.contains(role)) {
            throw new RuntimeException("The role [" + role + "] is not set for usecase [" + usecase
                    + "]");
        }

        newRoles.remove(role);
        this.usecaseToRoles.put(usecase, newRoles.toArray(new String[newRoles.size()]));
    }

}
