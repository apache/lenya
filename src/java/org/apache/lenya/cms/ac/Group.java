/*
 * $Id: Group.java,v 1.6 2003/06/25 14:38:29 andreas Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment: "This product
 *    includes software developed by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor
 *    may "lenya" appear in their names without prior written permission
 *    of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment: "This product includes software developed by lenya
 *    (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS
 * OR IMPLIED, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * lenya WILL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY YOU AS A RESULT
 * OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE FOR ANY SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS BEEN
 * ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE
 * FOR ANY THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
 
package org.apache.lenya.cms.ac;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.lenya.cms.ac2.*;

/**
 * A group is a set of {@link Groupable}s.
 * @author egli
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public class Group implements Accreditable {
	
	private String name;
    
    /**
     * Creates a new group.
     */
    public Group() {
    }

    /**
     * Creates a new group.
     * @param name The group name.
     */
	public Group(String name) {
		this.name = name;
	}
	
	/**
	 * Get the name of this group
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

    /**
     * Set the name of this group
     * @param string The name.
     */
    public void setName(String string) {
        name = string;
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
        assert member != null && !members.contains(member);
        members.add(member);
        member.addedToGroup(this);
	}
	
    /**
     * Removes a member from this group.
     * @param member The member to remove.
     */
	public void remove(Groupable member) {
        assert member != null && members.contains(member);
		members.remove(member);
        member.removedFromGroup(this);
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
        boolean equals = false;
		if (obj instanceof Group) {
			equals = getName().equals(((Group) obj).getName());
		}
		return equals;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getName().hashCode();
	}
    
    /**
     * Returns if this group contains this member.
     * @param member The member to check.
     * @return A boolean value.
     */
    boolean contains(Groupable member) {
        return members.contains(member);
    }

    /**
     * @see org.apache.lenya.cms.ac.Accreditable#getAccreditables()
     */
    public Accreditable[] getAccreditables() {
        return (Accreditable[]) Collections.singleton(this).toArray(new Accreditable[1]);
    }

}
