/*
 * $Id: LDAPUser.java,v 1.1 2003/06/12 15:47:45 egli Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2003 Wyona. All rights reserved.
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
 *    includes software developed by Wyona (http://www.wyona.com)"
 *
 * 4. The name "Lenya" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact contact@wyona.com
 *
 * 5. Products derived from this software may not be called "Lenya" nor
 *    may "Lenya" appear in their names without prior written permission
 *    of Wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment: "This product includes software developed by Wyona
 *    (http://www.wyona.com)"
 *
 * THIS SOFTWARE IS PROVIDED BY Wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS
 * OR IMPLIED, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * Wyona WILL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY YOU AS A RESULT
 * OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE FOR ANY SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF Wyona HAS BEEN
 * ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. Wyona WILL NOT BE LIABLE
 * FOR ANY THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
 
package org.apache.lenya.cms.ac;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.lenya.cms.publication.Publication;

/**
 * @author egli
 * 
 * 
 */
public class LDAPUser extends FileUser {

	public static final String LDAP_ID = "ldapid";

	private String ldapId;

	/**
	 * @param publication
	 * @param id
	 * @param fullName
	 * @param email
	 * @param password
	 */
	public LDAPUser(
		Publication publication,
		String id,
		String email,
		String ldapId) {
		super(publication, id, null, email, null);
		this.ldapId = ldapId;
	}

	/**
	 * @param publication
	 * @param config
	 * @throws ConfigurationException
	 */
	public LDAPUser(Publication publication, Configuration config)
		throws ConfigurationException {
		super(publication, config);
		ldapId = config.getChild(LDAP_ID).getValue();
	}

	/* (non-Javadoc)
	 * @see org.apache.lenya.cms.ac.FileUser#createConfiguration()
	 */
	protected Configuration createConfiguration() {
		DefaultConfiguration config = (DefaultConfiguration)super.createConfiguration();
		// add ldap_id node
		DefaultConfiguration child = new DefaultConfiguration(LDAP_ID);
		child.setValue(ldapId);
		config.addChild(child);

		return config;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.lenya.cms.ac.User#authenticate(java.lang.String)
	 */
	public boolean authenticate(String password) {
		// TODO Auto-generated method stub
		return super.authenticate(password);
	}

	/* (non-Javadoc)
	 * @see org.apache.lenya.cms.ac.User#getFullName()
	 */
	public String getFullName() {
		// FIXME: get the full name from LDAP
		return null;
	}

	/* 
	 * LDAP Users fetch their full name information from the LDAP server,
	 * so we don't store it locally. Since we only have read access we basically
	 * can't set the full name, i.e. any request to change the full name 
	 * is ignored.
	 */
	public void setFullName(String string) {
		// FIXME: we do not have write access to LDAP, so we ignore 
		// change request to the full name.
	}

}
