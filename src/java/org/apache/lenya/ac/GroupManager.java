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

/* $Id: GroupManager.java,v 1.2 2004/03/03 12:56:31 gregor Exp $  */

package org.apache.lenya.ac;

public interface GroupManager extends ItemManager {
    
    /**
     * Get all groups.
     * @return an array of groups.
     */
    Group[] getGroups();
    
    /**
     * Add a group to this manager.
     * @param group the group to be added.
     * @throws AccessControlException when the group is already contained.
     */
    void add(Group group) throws AccessControlException;
    
    /**
     * Remove a group from this manager.
     * @param group the group to be removed.
     * @throws AccessControlException when the group is not contained.
     */
    void remove(Group group) throws AccessControlException;
    
    /**
     * Get the group with the given group name.
     *
     * @param groupId the id of the requested group.
     * @return a <code>Group</code> or null if there is no group with the given name
     */
    Group getGroup(String groupId);

}