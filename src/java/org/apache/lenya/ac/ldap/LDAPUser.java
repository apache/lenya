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

/* $Id: LDAPUser.java,v 1.4 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.ac.ldap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.file.FileUser;
import org.apache.log4j.Category;

import com.sun.jndi.ldap.LdapCtxFactory;

public class LDAPUser extends FileUser {
	private static Properties defaultProperties = null;
	private static Category log = Category.getInstance(LDAPUser.class);

	public static final String LDAP_ID = "ldapid";
	private static String PROVIDER_URL = "provider-url";
	private static String MGR_DN = "mgr-dn";
	private static String MGR_PW = "mgr-pw";
	private static String PARTIAL_USER_DN = "partial-user-dn";
	private static String KEY_STORE = "key-store";
	private static String SECURITY_PROTOCOL = "security-protocol";
	private static String SECURITY_AUTHENTICATION = "security-authentication";
	private String ldapId;

	private String ldapName;

	/**
	 * Creates a new LDAPUser object.
	 */
	public LDAPUser() {
	}
	
	/**
	 * Creates a new LDAPUser object.
	 * @param configurationDirectory The configuration directory.
	 */
	public LDAPUser(File configurationDirectory) {
		setConfigurationDirectory(configurationDirectory);
	}

	/**
	 * Create an LDAPUser
	 * 
	 * @param configurationDirectory
	 *            where the user will be attached to
	 * @param id
	 *            user id of LDAPUser
	 * @param email
	 *            of LDAPUser
	 * @param ldapId
	 *            of LDAPUser
	 * @throws ConfigurationException
	 *             if the properties could not be read
	 */
	public LDAPUser(File configurationDirectory, String id, String email, String ldapId)
		throws ConfigurationException {
		super(configurationDirectory, id, null, email, null);
		this.ldapId = ldapId;

		initialize();
	}

	/**
	 * Create a new LDAPUser from a configuration
	 * 
	 * @param config
	 *            the <code>Configuration</code> specifying the user details
	 * @throws ConfigurationException
	 *             if the user could not be instantiated
	 */
	public void configure(Configuration config) throws ConfigurationException {
		super.configure(config);
		ldapId = config.getChild(LDAP_ID).getValue();

		initialize();
	}
	
	/**
	 * Checks if a user exists.
	 * @param ldapId The LDAP id.
	 * @return A boolean value.
	 * @throws AccessControlException when an error occurs.
	 * FIXME: This method does not work.
	 */
	public boolean existsUser(String ldapId) throws AccessControlException {
		
		boolean exists = false;
		LdapContext context = null;
		
		try {
			readProperties();

			context =
				bind(defaultProperties.getProperty(MGR_DN), defaultProperties.getProperty(MGR_PW));

			String peopleName = "ou=People";
			Attributes attributes = new BasicAttributes("uid", ldapId);
			NamingEnumeration enumeration = context.search(peopleName, attributes);
			
			exists = enumeration.hasMoreElements();
		} catch (Exception e) {
			throw new AccessControlException("Exception during search: ", e);
		}
		finally {
			try {
				if (context != null) {
					close(context);
				}
			}
			catch (NamingException e) {
				throw new AccessControlException("Closing context failed: ", e);
			}
		}
		return exists;
	}

	/**
	 * Initializes this user.
	 * 
	 * @throws ConfigurationException
	 *             when something went wrong.
	 */
	protected void initialize() throws ConfigurationException {
		LdapContext context = null;
		try {
			readProperties();

			String name = null;
			context =
				bind(defaultProperties.getProperty(MGR_DN), defaultProperties.getProperty(MGR_PW));

			String[] attrs = new String[1];
			attrs[0] = "gecos"; /* users full name */

			String searchString = "uid=" + ldapId + ",ou=People";
			Attributes answer = context.getAttributes(searchString, attrs);

			if (answer != null) {
				Attribute attr = answer.get("gecos");

				if (attr != null) {
					for (NamingEnumeration enum = attr.getAll(); enum.hasMore(); enum.next()) {
						name = (String) attr.get();
					}
				}
			}

			this.ldapName = name;
		} catch (Exception e) {
			throw new ConfigurationException("Could not read properties", e);
		}
		finally {
			try {
				if (context != null) {
					close(context);
				}
			}
			catch (NamingException e) {
				throw new ConfigurationException("Closing context failed: ", e);
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lenya.cms.ac.FileUser#createConfiguration()
	 */
	protected Configuration createConfiguration() {
		DefaultConfiguration config = (DefaultConfiguration) super.createConfiguration();

		// add ldap_id node
		DefaultConfiguration child = new DefaultConfiguration(LDAP_ID);
		child.setValue(ldapId);
		config.addChild(child);

		return config;
	}

	/**
	 * Get the ldap id
	 * 
	 * @return the ldap id
	 */
	public String getLdapId() {
		return ldapId;
	}

	/**
	 * Set the ldap id
	 * 
	 * @param string
	 *            the new ldap id
	 */
	public void setLdapId(String string) {
		ldapId = string;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lenya.cms.ac.User#authenticate(java.lang.String)
	 */
	public boolean authenticate(String password) {

		String principal =
			"uid=" + getLdapId() + "," + defaultProperties.getProperty(PARTIAL_USER_DN);
		Context ctx = null;

        if (log.isDebugEnabled()) {
            log.debug("Authenticating with principal [" + principal + "]");
        }
        
        boolean authenticated = false;

		try {
			ctx = bind(principal, password);
            authenticated = true;
            close(ctx);
            if (log.isDebugEnabled()) {
                log.debug("Context closed.");
            }
		} catch (NamingException e) {
			// log this failure
			// StringWriter writer = new StringWriter();
			// e.printStackTrace(new PrintWriter(writer));
            if (log.isInfoEnabled()) {
                log.info("Bind for user " + principal + " to Ldap server failed: ", e);
            }
		}

		return authenticated;
	}

	/**
	 * @see org.apache.lenya.cms.ac.Item#getName()
	 */
	public String getName() {
		return ldapName;
	}

	/**
	 * LDAP Users fetch their name information from the LDAP server, so we don't store it locally.
	 * Since we only have read access we basically can't set the name, i.e. any request to change
	 * the name is ignored.
	 * 
	 * @param string
	 *            is ignored
	 */
	public void setName(String string) {
		// we do not have write access to LDAP, so we ignore
		// change request to the name.
	}

	/**
	 * The LDAPUser doesn't store any passwords as they are handled by LDAP
	 * 
	 * @param plainTextPassword
	 *            is ignored
	 */
	public void setPassword(String plainTextPassword) {
		setEncryptedPassword(null);
	}

	/**
	 * The LDAPUser doesn't store any passwords as they are handled by LDAP
	 * 
	 * @param encryptedPassword
	 *            is ignored
	 */
	protected void setEncryptedPassword(String encryptedPassword) {
		encryptedPassword = null;
	}

	/**
	 * Connect to the LDAP server
	 * 
	 * @param principal
	 *            the principal string for the LDAP connection
	 * @param credentials
	 *            the credentials for the LDAP connection
	 * @return a <code>LdapContext</code>
	 * @throws NamingException
	 *             if there are problems establishing the Ldap connection
	 */
	private LdapContext bind(String principal, String credentials) throws NamingException {

		log.info("Binding principal: [" + principal + "]");

		Hashtable env = new Hashtable();

		System.setProperty(
			"javax.net.ssl.trustStore",
			getConfigurationDirectory().getAbsolutePath()
				+ File.separator
				+ defaultProperties.getProperty(KEY_STORE));

		env.put(Context.INITIAL_CONTEXT_FACTORY, LdapCtxFactory.class.getName());
		env.put(Context.PROVIDER_URL, defaultProperties.getProperty(PROVIDER_URL));
		env.put(Context.SECURITY_PROTOCOL, defaultProperties.getProperty(SECURITY_PROTOCOL));
		env.put(
			Context.SECURITY_AUTHENTICATION,
			defaultProperties.getProperty(SECURITY_AUTHENTICATION));
		env.put(Context.SECURITY_PRINCIPAL, principal);
		env.put(Context.SECURITY_CREDENTIALS, credentials);

		LdapContext ctx = new InitialLdapContext(env, null);

		log.info("Finished binding principal.");

		return ctx;
	}

	/**
	 * Close the connection to the LDAP server
	 * 
	 * @param ctx
	 *            the context that was returned from the bind
	 * @throws NamingException
	 *             if there is a problem communicating to the LDAP server
	 */
	private void close(Context ctx) throws NamingException {
		ctx.close();
	}

	/**
	 * Read the properties
	 * 
	 * @throws IOException
	 *             if the properties cannot be found.
	 */
	private void readProperties() throws IOException {
		// create and load default properties
		File propertiesFile = new File(getConfigurationDirectory(), "ldap.properties");

		if (defaultProperties == null) {
			defaultProperties = new Properties();

			FileInputStream in;
			in = new FileInputStream(propertiesFile);
			defaultProperties.load(in);
			in.close();
		}
	}
}
