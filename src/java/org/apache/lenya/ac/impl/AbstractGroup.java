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

/* $Id: AbstractGroup.java,v 1.3 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.ac.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Groupable;


/**
 * A group is a set of {@link Groupable}s.
 */
public abstract class AbstractGroup extends AbstractItem implements Accreditable, Group {
    /**
     * Creates a new group.
     */
    public AbstractGroup() {
    }

    /**
     * Creates a new group.
     * @param id The group ID.
     */
    public AbstractGroup(String id) {
        setId(id);
    }

    private Set members = new HashSet();

    /**
     * Returns the members of this group.
     * @return An array of {@link Groupable}s.
     */
    public Groupable[] getMembers() {
        return (Groupable[]) members.toArray(new Groupable[members.size()]);
    }

    /**
    * Adds a member to this group.
     * @param member The member to add.
     */
    public void add(Groupable member) {
        assert (member != null) && !members.contains(member);
        members.add(member);
        member.addedToGroup(this);
    }

    /**
     * Removes a member from this group.
     * @param member The member to remove.
     */
    public void remove(Groupable member) {
        assert (member != null) && members.contains(member);
        members.remove(member);
        member.removedFromGroup(this);
    }
    
    /**
     * Removes all members from this group.
     */
    public void removeAllMembers() {
        Groupable[] members = getMembers();
        for (int i = 0; i < members.length; i++) {
            remove(members[i]); 
        }
    }

    /**
     * Returns if this group contains this member.
     * @param member The member to check.
     * @return A boolean value.
     */
    public boolean contains(Groupable member) {
        return members.contains(member);
    }

    /**
     * @see org.apache.lenya.cms.ac.Accreditable#getAccreditables()
     */
    public Accreditable[] getAccreditables() {
        Accreditable[] accreditables = { this };
        return accreditables;
    }
    
    /**
     * Delete a group
     *
     * @throws AccessControlException if the delete failed
     */
    public void delete() throws AccessControlException {
        Groupable[] members = getMembers();
        for (int i = 0; i < members.length; i++) {
            remove(members[i]);
        }
    }

}
