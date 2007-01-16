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

package org.apache.lenya.ac.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Groupable;
import org.apache.lenya.ac.ItemManager;


/**
 * A group is a set of {@link Groupable}s.
 */
public abstract class AbstractGroup extends AbstractItem implements Accreditable, Group {
    
    /**
     * Creates a new group.
     * @param itemManager The item manager.
     * @param logger The logger.
     */
    public AbstractGroup(ItemManager itemManager, Logger logger) {
        super(itemManager, logger);
    }

    /**
     * Creates a new group.
     * @param itemManager The item manager.
     * @param logger The logger.
     * @param id The group ID.
     */
    public AbstractGroup(ItemManager itemManager, Logger logger, String id) {
        super(itemManager, logger);
        setId(id);
    }

    private Set members = new HashSet();

    /**
     * Returns the members of this group.
     * @return An array of {@link Groupable}s.
     */
    public Groupable[] getMembers() {
        return (Groupable[]) this.members.toArray(new Groupable[this.members.size()]);
    }

    /**
    * Adds a member to this group.
     * @param member The member to add.
     */
    public void add(Groupable member) {
        assert (member != null) && !this.members.contains(member);
        this.members.add(member);
        member.addedToGroup(this);
    }

    /**
     * Removes a member from this group.
     * @param member The member to remove.
     */
    public void remove(Groupable member) {
        assert (member != null) && this.members.contains(member);
        this.members.remove(member);
        member.removedFromGroup(this);
    }
    
    /**
     * Removes all members from this group.
     */
    public void removeAllMembers() {
        Groupable[] _members = getMembers();
        for (int i = 0; i < _members.length; i++) {
            remove(_members[i]); 
        }
    }

    /**
     * Returns if this group contains this member.
     * @param member The member to check.
     * @return A boolean value.
     */
    public boolean contains(Groupable member) {
        return this.members.contains(member);
    }

    /**
     * @see org.apache.lenya.ac.Accreditable#getAccreditables()
     */
    public Accreditable[] getAccreditables() {
        Accreditable[] accreditables = { this };
        return accreditables;
    }
    
    /**
     * Delete a group
     * @throws AccessControlException if the delete failed
     */
    public void delete() throws AccessControlException {
        removeAllMembers();
    }

}
