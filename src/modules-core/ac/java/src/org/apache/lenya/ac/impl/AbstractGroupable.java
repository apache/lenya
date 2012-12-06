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

package org.apache.lenya.ac.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.GroupManager;
import org.apache.lenya.ac.Groupable;
import org.apache.lenya.ac.ItemManager;

/**
 * Abstract implementation for group members.
 * @version $Id$
 */
public abstract class AbstractGroupable extends AbstractItem implements Groupable, Accreditable {
    
    /**
     * Ctor.
     * @param itemManager The item manager.
     * @param logger The logger.
     */
    public AbstractGroupable(ItemManager itemManager, Logger logger) {
        super(itemManager, logger);
    }

    private Set<String> groups = new HashSet<String>();

    /**
     * @see org.apache.lenya.ac.Groupable#addedToGroup(org.apache.lenya.ac.Group)
     */
    public void addedToGroup(Group group) {
        assert group != null;
        this.groups.add(group.getId());
    }

    /**
     * @see org.apache.lenya.ac.Groupable#removedFromGroup(org.apache.lenya.ac.Group)
     */
    public void removedFromGroup(Group group) {
        assert group != null;
        this.groups.remove(group.getId());
    }

    /**
     * @see org.apache.lenya.ac.Groupable#getGroups()
     */
    public Group[] getGroups() {
        GroupManager groupManager;
        try {
            groupManager = getAccreditableManager().getGroupManager();
        } catch (final AccessControlException e) {
            throw new RuntimeException(e);
        }
        final List<Group> groups = new ArrayList<Group>();
        for (final Group group : groupManager.getGroups()) {
            if (this.groups.contains(group.getId())) {
                groups.add(group);
            }
        }
        return (Group[]) groups.toArray(new Group[groups.size()]);
    }

    /**
     * Removes this groupable from all its groups.
     */
    public void removeFromAllGroups() {
        this.groups.clear();
    }

    /**
     * @see org.apache.lenya.ac.Accreditable#getAccreditables()
     */
    public Accreditable[] getAccreditables() {
        Set<Accreditable> accreditables = new HashSet<Accreditable>();
        accreditables.add(this);

        Group[] _groups = getGroups();

        for (int i = 0; i < _groups.length; i++) {
            Accreditable[] groupAccreditables = _groups[i].getAccreditables();
            accreditables.addAll(Arrays.asList(groupAccreditables));
        }

        return (Accreditable[]) accreditables.toArray(new Accreditable[accreditables.size()]);
    }

    @Override
    public boolean belongsToGroup(final Group group) {
        return this.groups.contains(group.getId());
    }
    
}