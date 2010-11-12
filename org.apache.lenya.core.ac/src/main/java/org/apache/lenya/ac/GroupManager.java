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
 * A group manager.
 * @version $Id$
 */
public interface GroupManager extends ItemManager {
    
    /**
     * Get all groups.
     * @return an array of groups.
     */
    Group[] getGroups();
    
    /**
     * Add a group to this manager.
     * @param id the ID of the group to be added.
     * @return A group.
     * @throws AccessControlException when the group is already contained.
     */
    Group add(String id) throws AccessControlException;
    
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