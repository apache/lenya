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

/* $Id: AbstractGroupable.java,v 1.2 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.ac.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Groupable;

public abstract class AbstractGroupable extends AbstractItem implements Groupable, Accreditable {
    private Set groups = new HashSet();

    /**
     * @see org.apache.lenya.cms.ac.Groupable#addedToGroup(org.apache.lenya.ac.Group)
     */
    public void addedToGroup(Group group) {
        assert group != null;
        assert group.contains(this);
        groups.add(group);
    }

    /**
     * @see org.apache.lenya.cms.ac.Groupable#removedFromGroup(org.apache.lenya.ac.Group)
     */
    public void removedFromGroup(Group group) {
        assert group != null;
        assert !group.contains(this);
        groups.remove(group);
    }

    /**
     * @see org.apache.lenya.ac.Groupable#getGroups()
     */
    public Group[] getGroups() {
        return (Group[]) groups.toArray(new Group[groups.size()]);
    }

    /**
     * Removes this groupable from all its groups.
     */
    public void removeFromAllGroups() {
        Group[] groups = getGroups();

        for (int i = 0; i < groups.length; i++) {
            groups[i].remove(this);
        }
    }
    
    /**
     * @see org.apache.lenya.ac.Accreditable#getAccreditables()
     */
    public Accreditable[] getAccreditables() {
        Set accreditables = new HashSet();
        accreditables.add(this);

        Group[] groups = getGroups();

        for (int i = 0; i < groups.length; i++) {
            Accreditable[] groupAccreditables = groups[i].getAccreditables();
            accreditables.addAll(Arrays.asList(groupAccreditables));
        }

        return (Accreditable[]) accreditables.toArray(new Accreditable[accreditables.size()]);
    }
    
}
