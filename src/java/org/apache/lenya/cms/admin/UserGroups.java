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
package org.apache.lenya.cms.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.User;

/**
 * Usecase to edit a user's group affiliation.
 */
public class UserGroups extends AccessControlUsecase {

    private User user;

    protected static final String USER_GROUPS = "userGroups";
    protected static final String OTHER_GROUPS = "otherGroups";
    protected static final String ADD = "add";
    protected static final String REMOVE = "remove";
    protected static final String USER_GROUP = "userGroup";
    protected static final String OTHER_GROUP = "otherGroup";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        
        this.user.removeFromAllGroups();
        
        List userGroups = (List) getParameter(USER_GROUPS);
        for (Iterator i = userGroups.iterator(); i.hasNext(); ) {
            Group group = (Group) i.next();
            group.add(this.user);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#advance()
     */
    public void advance() {
        super.advance();

        String add = getParameterAsString(ADD);
        String remove = getParameterAsString(REMOVE);
        if (add != null || remove != null) {

            List userGroups = (List) getParameter(USER_GROUPS);
            List otherGroups = (List) getParameter(OTHER_GROUPS);
            
            if (add != null) {
                String groupId = getParameterAsString(OTHER_GROUP);
                if (groupId != null) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("add group [" + groupId + "]");
                    }
                    Group group = getGroupManager().getGroup(groupId);
                    userGroups.add(group);
                    otherGroups.remove(group);
                }
            }

            if (remove != null) {
                String groupId = getParameterAsString(USER_GROUP);
                if (groupId != null) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("remove group [" + groupId + "]");
                    }
                    Group group = getGroupManager().getGroup(groupId);
                    otherGroups.add(group);
                    userGroups.remove(group);
                }
            }
            
            deleteParameter(ADD);
            deleteParameter(REMOVE);
            deleteParameter(USER_GROUP);
            deleteParameter(OTHER_GROUP);
        }

    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String,
     *      java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);
        if (name.equals(UserProfile.USER_ID)) {
            String userId = (String) value;
            this.user = getUserManager().getUser(userId);
            if (this.user == null) {
                throw new RuntimeException("User [" + userId + "] not found.");
            }

            Group[] userGroupArray = this.user.getGroups();
            List userGroups = new ArrayList(Arrays.asList(userGroupArray));

            setParameter(USER_GROUPS, userGroups);

            Group[] allGroups = getGroupManager().getGroups();

            List otherGroups = new ArrayList();

            for (int i = 0; i < allGroups.length; i++) {
                if (!userGroups.contains(allGroups[i])) {
                    otherGroups.add(allGroups[i]);
                }
            }

            setParameter(OTHER_GROUPS, otherGroups);
        }

    }

}