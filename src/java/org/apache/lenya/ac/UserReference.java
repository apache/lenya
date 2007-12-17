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

/**
 * An identifiable which represents a user.
 */
public abstract class UserReference implements Identifiable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * @param id The ID of the user.
     * @see Item#getId()
     */
    public UserReference(String id) {
        this.id = id;
    }

    /**
     * @return The ID of the referenced user.
     */
    public String getId() {
        return this.id;
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

    /**
     * @param accrMgr The accreditable manager.
     * @return The user of the accreditable's user manager which is represented by this user
     *         reference.
     * @throws RuntimeException if the accreditable manager doesn't contain a user which is
     *             represented by this user reference.
     */
    public abstract User getUser(AccreditableManager accrMgr);

    /**
     * @param accrMgr The accreditable manager.
     * @return If the accreditable manager contains a user which is represented by this user
     *         reference.
     */
    public abstract boolean canGetUserFrom(AccreditableManager accrMgr);

    /**
     * @param accrMgr The accreditable manager.
     * @param user The user.
     * @return All groups of the accreditable manager which have a rule matching the user.
     */
    protected Set getMatchingGroups(AccreditableManager accrMgr, AttributeOwner user) {
        Set matchingGroups = new HashSet();
        if (user.getAttributeNames().length > 0) {
            try {
                Group[] groups = accrMgr.getGroupManager().getGroups();
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
