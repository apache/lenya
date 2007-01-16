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

/* $Id$  */

package org.apache.lenya.ac.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.GroupManager;
import org.apache.lenya.ac.Item;

/**
 * File-based group manager.
 */
public final class FileGroupManager extends FileItemManager implements GroupManager {

    private static Map instances = new HashMap();

    /**
     * Ctor.
     * @param mgr The accreditable manager.
     */
    private FileGroupManager(AccreditableManager mgr) {
        super(mgr);
    }

    /**
     * Return the <code>GroupManager</code> for the given publication. The
     * <code>GroupManager</code> is a singleton.
     * @param mgr The accreditable manager.
     * @param configurationDirectory for which the GroupManager is requested
     * @param logger The logger.
     * @return a <code>GroupManager</code>
     * @throws AccessControlException if no GroupManager could be instanciated
     */
    public static FileGroupManager instance(AccreditableManager mgr, File configurationDirectory,
            Logger logger) throws AccessControlException {
        assert configurationDirectory != null;

        if (!instances.containsKey(configurationDirectory)) {
            FileGroupManager manager = new FileGroupManager(mgr);
            manager.enableLogging(logger);
            manager.configure(configurationDirectory);
            instances.put(configurationDirectory, manager);
        }

        return (FileGroupManager) instances.get(configurationDirectory);
    }

    /**
     * Get all groups
     * 
     * @return an array of groups.
     */
    public Group[] getGroups() {
        Item[] items = super.getItems();
        Group[] groups = new Group[items.length];
        for (int i = 0; i < groups.length; i++) {
            groups[i] = (Group) items[i];
        }
        return groups;
    }

    /**
     * @see org.apache.lenya.ac.GroupManager#add(java.lang.String)
     */
    public Group add(String id) throws AccessControlException {
        Group group = new FileGroup(this, getLogger(), id);
        super.add(group);
        return group;
    }

    /**
     * Remove a group from this manager
     * 
     * @param group the group to be removed
     * @throws AccessControlException when the notification failed.
     */
    public void remove(Group group) throws AccessControlException {
        super.remove(group);
    }

    /**
     * Get the group with the given group name.
     * 
     * @param groupId the id of the requested group
     * @return a <code>Group</code> or null if there is no group with the
     *         given name
     */
    public Group getGroup(String groupId) {
        return (Group) getItem(groupId);
    }

    protected static final String SUFFIX = ".gml";

    /**
     * @see org.apache.lenya.ac.file.FileItemManager#getSuffix()
     */
    protected String getSuffix() {
        return SUFFIX;
    }

}
