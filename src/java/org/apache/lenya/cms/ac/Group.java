/*
$Id: Group.java,v 1.13 2003/07/30 15:04:50 egli Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.ac;

import org.apache.lenya.cms.ac2.*;

import java.util.HashSet;
import java.util.Set;


/**
 * A group is a set of {@link Groupable}s.
 * @author egli
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public abstract class Group extends AbstractItem implements Accreditable {
    /**
     * Creates a new group.
     */
    public Group() {
    }

    /**
     * Creates a new group.
     * @param id The group ID.
     */
    public Group(String id) {
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
    }

}
