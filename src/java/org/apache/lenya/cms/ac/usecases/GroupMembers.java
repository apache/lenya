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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Groupable;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to change the members of a group.
 * 
 * @version $Id:$
 */
public class GroupMembers extends AccessControlUsecase {

    private Group group;

    protected static final String GROUP_USERS = "groupUsers";
    protected static final String OTHER_USERS = "otherUsers";
    protected static final String ADD = "add";
    protected static final String REMOVE = "remove";
    protected static final String GROUP_USER = "groupUser";
    protected static final String OTHER_USER = "otherUser";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        setExitParameter(GroupProfile.ID, getParameterAsString(GroupProfile.ID));
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        this.group.removeAllMembers();

        List groupUsers = (List) getParameter(GROUP_USERS);
        for (Iterator i = groupUsers.iterator(); i.hasNext();) {
            User user = (User) i.next();
            this.group.add(user);
            user.save();
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#advance()
     */
    public void advance() throws UsecaseException {
        super.advance();

        String add = getParameterAsString(ADD);
        String remove = getParameterAsString(REMOVE);
        if (add != null || remove != null) {

            List groupUsers = (List) getParameter(GROUP_USERS);
            List otherUsers = (List) getParameter(OTHER_USERS);

            if (add != null) {
                String userId = getParameterAsString(OTHER_USER);
                if (userId != null) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("add user [" + userId + "]");
                    }
                    User user = getUserManager().getUser(userId);
                    groupUsers.add(user);
                    otherUsers.remove(user);
                }
            }

            if (remove != null) {
                String userId = getParameterAsString(GROUP_USER);
                if (userId != null) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("remove user [" + userId + "]");
                    }
                    User user = getUserManager().getUser(userId);
                    otherUsers.add(user);
                    groupUsers.remove(user);
                }
            }

            deleteParameter(ADD);
            deleteParameter(REMOVE);
            deleteParameter(GROUP_USER);
            deleteParameter(OTHER_USER);
        }

    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);
        if (name.equals(GroupProfile.ID)) {
            String groupId = (String) value;
            this.group = getGroupManager().getGroup(groupId);
            if (this.group == null) {
                throw new RuntimeException("Group [" + groupId + "] not found.");
            }

            Groupable[] members = this.group.getMembers();
            List groupUsers = new ArrayList();
            for (int i = 0; i < members.length; i++) {
                if (members[i] instanceof User) {
                    groupUsers.add(members[i]);
                }
            }
            setParameter(GROUP_USERS, groupUsers);

            User[] allUsers = getUserManager().getUsers();

            List otherUsers = new ArrayList();

            for (int i = 0; i < allUsers.length; i++) {
                if (!groupUsers.contains(allUsers[i])) {
                    otherUsers.add(allUsers[i]);
                }
            }

            setParameter(OTHER_USERS, otherUsers);
        }

    }

}