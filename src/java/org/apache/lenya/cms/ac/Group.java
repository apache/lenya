/*
 * $Id: Group.java,v 1.5 2003/06/24 17:44:26 egli Exp $
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author egli
 * 
 * 
 */
public class Group {
	
	private String name;
	private Set roles = new HashSet();
	private Set users = new HashSet();

	/**
	 * Create a group
	 * 
	 * @param name the name for this group
	 */
	public Group(String name) {
		this.name = name;
	}
	
	/**
	 * Get the name of this group
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get all roles of this group
	 * 
	 * @return an <code>Iterator</code>
	 */
	public Iterator getRoles() {
		return roles.iterator();
	}

	/**
	 * Get all users
	 * 
	 * @return an <code>Iterator</code>
	 */
	public Iterator getUsers() {
		return users.iterator();
	}
	
	/**
	 * Set the name of this group
	 * 
	 * @param string whith which the name is replaced
	 */
	public void setName(String string) {
		name = string;
	}
	
	/**
	 * Add a role to this group
	 * 
	 * @param role the role that is to be added
	 */
	public void addRole(Role role) {
        assert role != null;
		roles.add(role);
		role.addGroup(this);
	}
	
	/**
	 * Remove a role from this group
	 * 
	 * @param role the role that is to be removed
	 */
	public void removeRole(Role role) {
		roles.remove(role);
		role.removeGroup(this);
	}
	
	/**
	 * Add a user to this group
	 * 
	 * @param user the user that is to be added
	 */
	public void addUser(User user) {
		users.add(user);
	}
	
	/**
	 * Remove the given user from the group.
	 * 
	 * @param user the user that is to be removed
	 */
	public void removeUser(User user) {
		users.remove(user);
	}
	
	/**
	 * Is this group equal to an object
	 * 
	 * @param obj the object that the Group is compared to
	 * @return true if the object is also of type Group and has the same name.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Group) {
			return getName().equals(((Group) obj).getName());
		}
		return false;
	}

	/** (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getName().hashCode();
	}

}
