/*
 * $Id: User.java,v 1.15 2003/06/25 08:56:32 egli Exp $
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

import org.apache.log4j.Category;

/**
 *
 * @author  nobby
 */
public abstract class User {
	private Category log = Category.getInstance(User.class);
	
	private String id;
	private String fullName;
	private String email;
	private String encryptedPassword;
	private Set groups = new HashSet();

   /**
	* Create a User instance
	* 
	* @param id the user id
	* @param fullName the full name of the user
	* @param email the users email address
	* @param password the users password
	*/
	public User(String id, String fullName, String email, String password) {
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		setPassword(password);
	}

	/**
	 * Get the email address
	 * 
	 * @return a <code>String</code>
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Get the full name
	 * 
	 * @return a <code>String</code>
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Get all groups
	 * 
	 * @return an <code>Iterator</code>
	 */
	public Iterator getGroups() {
		return groups.iterator();
	}

	/**
	 * Get the user id
	 * 
	 * @return the user id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the email address
	 * 
	 * @param email the new email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Set the full name
	 * 
	 * @param name the new full name
	 */
	public void setFullName(String name) {
		fullName = name;
	}

	/**
	 * Remove all groups
	 */
	public void removeAllGroups() {
		for (Iterator iter = groups.iterator(); iter.hasNext();) {
			Group group = (Group) iter.next();
			group.removeUser(this);
		}
		groups.clear();
	}

	/**
	 * Add the specified group to this user
	 * 
	 * @param group which is to be added
	 */
	public void addGroup(Group group) {
		assert group != null;
		groups.add(group);
		group.addUser(this);
	}

	/**
	 * Remove the specified group from this user
	 * 
	 * @param group which is to be removed
	 */
	public void removeGroup(Group group) {
		groups.remove(group);
		group.removeUser(this);
	}

	/**
	 * Set the password
	 * 
	 * @param plainTextPassword the password in plain text
	 */
	public void setPassword(String plainTextPassword) {
		encryptedPassword = Password.encrypt(plainTextPassword);
	}

	/**
	 * This method can be used for subclasses to set the password without it
	 * being encrypted again. Some subclass might have knowledge of the encrypted
	 * password and needs to be able to set it.
	 * 
	 * @param encryptedPassword the encrypted password
	 */
	protected void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}
	
	/**
	 * Get the encrypted password
	 * 
	 * @return the encrypted password
	 */
	protected String getEncryptedPassword() {
		return encryptedPassword;
	}
	
	/**
	 * Save the user
	 * 
	 * @throws AccessControlException if the save failed
	 */
	public abstract void save() throws AccessControlException;

	/**
	 * Delete a user
	 * 
	 * @throws AccessControlException if the delete failed
	 */
	public void delete() throws AccessControlException {
		removeAllGroups();
	}

	/** (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			return getId().equals(((User) obj).getId());
		}
		return false;
	}

	/** (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getId().hashCode();
	}
	
	/**
	 * Authenticate a user. This is done by encrypting 
	 * the given password and comparing this to the 
	 * encryptedPassword.
	 * 
	 * @param password to authenticate with
	 * @return true if the given password matches the password for this user
	 */
	public boolean authenticate(String password) {
		log.debug("Password: " + password);
		log.debug("pw encypted: " + Password.encrypt(password));
		log.debug("orig encrypted pw: " + this.encryptedPassword);
		
		return this.encryptedPassword.equals(Password.encrypt(password));
	}
}
