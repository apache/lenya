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
package org.apache.lenya.ac;

/**
 * A reference to a managed user.
 */
public class ManagedUserReference extends UserReference {

    /**
     * @param id The user ID.
     * @param managerId The ID of the accreditable manager the user belongs to.
     */
    public ManagedUserReference(String id, String managerId) {
        super(id);
        this.managerId = managerId;
    }
    
    private static final long serialVersionUID = 1L;
    private String managerId;

    protected String getManagerId() {
        return this.managerId;
    }

    public User getUser(AccreditableManager accrMgr) {
        try {
            if (canGetUserFrom(accrMgr)) {
                return accrMgr.getUserManager().getUser(getId());
            } else {
                throw new RuntimeException("Invalid accreditable manager.");
            }
        } catch (AccessControlException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean canGetUserFrom(AccreditableManager accrMgr) {
        try {
            UserManager userMgr = accrMgr.getUserManager();
            return userMgr.getId().equals(getManagerId());
        } catch (AccessControlException e) {
            throw new RuntimeException(e);
        }
    }

}
