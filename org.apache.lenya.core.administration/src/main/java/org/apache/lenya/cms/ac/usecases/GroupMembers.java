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
package org.apache.lenya.cms.ac.usecases;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Groupable;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to change the members of a group.
 * 
 * @version $Id: GroupMembers.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class GroupMembers extends AccessControlUsecase {

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
        
        Groupable[] members = getGroup().getMembers();
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
        
        setExitParameter(GroupProfile.ID, getParameterAsString(GroupProfile.ID));
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        Set usersToSave = new HashSet();
        
        final Group group = getGroup();
        Groupable[] members = group.getMembers();
        for (int i = 0; i < members.length; i++) {
            if (members[i] instanceof User) {
                usersToSave.add(members[i]);
            }
        }
        
        group.removeAllMembers();

        List groupUsers = (List) getParameter(GROUP_USERS);
        for (Iterator i = groupUsers.iterator(); i.hasNext();) {
            User user = (User) i.next();
            group.add(user);
            usersToSave.add(user);
        }
        
        for (Iterator i = usersToSave.iterator(); i.hasNext(); ) {
            ((User) i.next()).save();
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
    
    protected Group getGroup() {
        String groupId = getParameterAsString(GroupProfile.ID);
        Group group = getGroupManager().getGroup(groupId);
        if (group == null) {
            throw new RuntimeException("Group [" + groupId + "] not found.");
        }
        return group;
    }

}