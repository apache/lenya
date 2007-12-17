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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class UserReference implements Identifiable {

    private String id;
    private String managerId;
    
    public UserReference(String id, String managerId) {
        this.id = id;
        this.managerId = managerId;
    }
    
    public String getId() {
        return this.id;
    }
    
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

    public Accreditable[] getAccreditables(AccreditableManager accrMgr) {
        Set accreditables = new HashSet();
        if (canGetUserFrom(accrMgr)) {
            ManagedUser user = (ManagedUser) getUser(accrMgr);
            accreditables.add(user);
            if (user instanceof Groupable) {
                accreditables.addAll(Arrays.asList(((Groupable) user).getGroups()));
            }
            accreditables.addAll(getMatchingGroups(accrMgr, user));
        }
        return (Accreditable[]) accreditables.toArray(new Accreditable[accreditables.size()]);
    }

    protected Set getMatchingGroups(AccreditableManager accrMgr, AttributeOwner user) {
        Set matchingGroups = new HashSet();
        if (user.getAttributeNames().length > 0) {
            try {
                Group[] groups = accrMgr.getGroupManager()
                        .getGroups();
                for (int i = 0; i < groups.length; i++) {
                    if (groups[i].matches(user)) {
                        matchingGroups.add(groups[i]);
                    }
                }
            } catch (AccessControlException e) {
                throw new RuntimeException(e);
            }
        }
        return matchingGroups;
    }

}
