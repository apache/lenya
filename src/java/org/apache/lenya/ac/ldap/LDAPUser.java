/*
 * Copyright 1999-2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *  
 */

package org.apache.lenya.ac.ldap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.file.FileUser;
import org.apache.log4j.Category;

import com.sun.jndi.ldap.LdapCtxFactory;

/**
 * LDAP user.
 * @version $Id$
 */
public class LDAPUser extends FileUser {
    private static Properties defaultProperties = null;
    private static Category log = Category.getInstance(LDAPUser.class);

    public static final String LDAP_ID = "ldapid";
    private static String LDAP_PROPERTIES_FILE = "ldap.properties"; 
    private static String PROVIDER_URL_PROP = "provider-url";
    private static String MGR_DN_PROP = "mgr-dn";
    private static String MGR_PW_PROP = "mgr-pw";
    private static String KEY_STORE_PROP = "key-store";
    private static String SECURITY_PROTOCOL_PROP = "security-protocol";
    private static String SECURITY_AUTHENTICATION_PROP = "security-authentication";
    private static String USR_ATTR_PROP = "usr-attr";
    private static String USR_ATTR_DEFAULT = "uid";
    private static String USR_NAME_ATTR_PROP = "usr-name-attr";
    private static String USR_NAME_ATTR_DEFAULT = "gecos";
    private static String USR_BRANCH_PROP = "usr-branch";
    private static String USR_BRANCH_DEFAULT = "ou=People";
    private static String USR_AUTH_TYPE_PROP = "usr-authentication";
    private static String USR_AUTH_TYPE_DEFAULT = "simple";
    private static String BASE_DN_PROP = "base-dn";
    private static String DOMAIN_NAME_PROP = "domain-name";

    private String ldapId;
    private String ldapName;

    // deprecated: for backwards compatibility only !
    private static String PARTIAL_USER_DN_PROP = "partial-user-dn";

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
     * @param configurationDirectory where the user will be attached to
     * @param id user id of LDAPUser
     * @param email of LDAPUser
     * @param ldapId of LDAPUser
     * @throws ConfigurationException if the properties could not be read
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
     * @param config the <code>Configuration</code> specifying the user details
     * @throws ConfigurationException if the user could not be instantiated
     */
    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);
        ldapId = config.getChild(LDAP_ID).getValue();

        initialize();
    }

    /**
     * Checks if a user exists.
     * 
     * @param ldapId The LDAP id.
     * @return A boolean value indicating whether the user is found in the directory
     * @throws AccessControlException when an error occurs. 
     */
    public boolean existsUser(String ldapId) throws AccessControlException {

	if (log.isDebugEnabled())
	    log.debug("existsUser() checking id " + ldapId);

        boolean exists = false;

        try {
            readProperties();
	    SearchResult entry = getDirectoryEntry(ldapId);

	    exists = (entry != null);

        } catch (Exception e) {
	    if (log.isDebugEnabled())
		log.debug("existsUser() for id " + ldapId + " got exception: " + e);
            throw new AccessControlException("Exception during search: ", e);
        } 

        return exists;
    }

    /**
     * Initializes this user.
     *
     * The current (already authenticated) ldapId is queried in the directory, 
     * in order to retrieve additional information, such as the user name.
     * In current implementation, only the user name is actually retrieved, but
     * other attributes may be used in the future (such as groups ?)
     *
     * TODO: should the code be changed to not throw an exception when something
     * goes wrong ? After all, it's only used to get additional info for display?
     * This is a design decision, I'm not sure what's best.
     * 
     * @throws ConfigurationException when something went wrong.
     */
    protected void initialize() throws ConfigurationException {
        DirContext context = null;
        try {
	    if (log.isDebugEnabled())
		log.debug("initialize() getting entry ...");

	    SearchResult entry = getDirectoryEntry(ldapId);
	    StringBuffer name = new StringBuffer();

	    if (entry != null) {
		/* users full name */
		String usrNameAttr = 
		    defaultProperties.getProperty(USR_NAME_ATTR_PROP, USR_NAME_ATTR_DEFAULT);

		if (log.isDebugEnabled())
		    log.debug("initialize() got entry, going to look for attribute " + usrNameAttr + " in entry, which is: " + entry);
		
		Attributes attributes = entry.getAttributes();
		if (attributes != null) {
		    Attribute userNames = attributes.get(usrNameAttr);
		    if (userNames != null) {
			for (NamingEnumeration nenum = userNames.getAll(); nenum.hasMore(); nenum.next()) {
			    name.append((String)userNames.get());
			}
		    }
		}
	    }
	    ldapName = name.toString();
	    if (log.isDebugEnabled())
		log.debug("initialize() set name to " + ldapName);

        } catch (Exception e) {
            throw new ConfigurationException("Could not read properties", e);
        } finally {
            try {
                if (context != null) {
                    close(context);
                }
            } catch (NamingException e) {
                throw new ConfigurationException("Closing context failed: ", e);
            }
        }
    }

    /**
     * @see org.apache.lenya.ac.file.FileUser#createConfiguration()
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
     * @param string the new ldap id
     */
    public void setLdapId(String string) {
        ldapId = string;
    }

    /**
     * Authenticate a user against the directory.
     *
     * The principal to be authenticated is either constructed by use of the
     * configured properties, or by lookup of this ID in the directory. 
     * This principal then attempts to authenticate against the directory with
     * the provided password.
     * 
     * @see org.apache.lenya.ac.User#authenticate(java.lang.String)
     */
    public boolean authenticate(String password) {

	boolean authenticated = false;
	String principal = "";
	Context ctx = null;

        try {
	    principal = getPrincipal();
	    
	    if (log.isDebugEnabled())
		log.debug("Authenticating with principal [" + principal + "]");

            ctx = bind(principal, password,
		       defaultProperties.getProperty(USR_AUTH_TYPE_PROP, 
						     USR_AUTH_TYPE_DEFAULT));
            authenticated = true;
            close(ctx);
            if (log.isDebugEnabled()) {
                log.debug("Context closed.");
            }
        } catch (IOException e) {
	    log.warn("authenticate handling IOException, check your setup: " + e);
        } catch (AuthenticationException e) {
	    log.info("authenticate failed for principal " + principal + ", exception " + e);
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
     * @see org.apache.lenya.ac.Item#getName()
     */
    public String getName() {
        return ldapName;
    }

    /**
     * LDAP Users fetch their name information from the LDAP server, so we don't store it locally.
     * Since we only have read access we basically can't set the name, i.e. any request to change
     * the name is ignored.
     * 
     * @param string is ignored
     */
    public void setName(String string) {
        // we do not have write access to LDAP, so we ignore
        // change request to the name.
    }

    /**
     * The LDAPUser doesn't store any passwords as they are handled by LDAP
     * 
     * @param plainTextPassword is ignored
     */
    public void setPassword(String plainTextPassword) {
        setEncryptedPassword(null);
    }

    /**
     * The LDAPUser doesn't store any passwords as they are handled by LDAP
     * 
     * @param encryptedPassword is ignored
     */
    protected void setEncryptedPassword(String encryptedPassword) {
        encryptedPassword = null;
    }

    /**
     * Connect to the LDAP server
     * 
     * @param principal the principal string for the LDAP connection
     * @param credentials the credentials for the LDAP connection
     * @param authMethod the authentication method
     * @return a <code>DirContext</code>
     * @throws NamingException if there are problems establishing the Ldap connection
     */
    private DirContext bind(String principal, String credentials,
			    String authMethod) throws NamingException {

        log.info("Binding principal: [" + principal + "]");

        Hashtable env = new Hashtable();

        System.setProperty("javax.net.ssl.trustStore", getConfigurationDirectory()
                .getAbsolutePath()
                + File.separator + defaultProperties.getProperty(KEY_STORE_PROP));

        env.put(Context.INITIAL_CONTEXT_FACTORY, LdapCtxFactory.class.getName());
        env.put(Context.PROVIDER_URL, defaultProperties.getProperty(PROVIDER_URL_PROP));
        env.put(Context.SECURITY_PROTOCOL, defaultProperties.getProperty(SECURITY_PROTOCOL_PROP));

        env.put(Context.SECURITY_AUTHENTICATION, authMethod);
	if (authMethod != null && ! authMethod.equals("none")) {
	    env.put(Context.SECURITY_PRINCIPAL, principal);
	    env.put(Context.SECURITY_CREDENTIALS, credentials);
	}

        DirContext ctx = new InitialLdapContext(env, null);

        log.info("Finished binding principal.");

        return ctx;
    }

    /**
     * Close the connection to the LDAP server
     * 
     * @param ctx the context that was returned from the bind
     * @throws NamingException if there is a problem communicating to the LDAP server
     */
    private void close(Context ctx) throws NamingException {
        ctx.close();
    }

    /**
     * Read the properties
     * 
     * @throws IOException if the properties cannot be found.
     */
    private void readProperties() throws IOException {
        // create and load default properties
        File propertiesFile = new File(getConfigurationDirectory(), LDAP_PROPERTIES_FILE);

        if (defaultProperties == null) {
            defaultProperties = new Properties();

            FileInputStream in = null;
            try {
                in = new FileInputStream(propertiesFile);
                defaultProperties.load(in);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
    }

    /** 
     * Wrapping of the decision whether a recursive search is wanted or not.
     * Implementation: 
     * If the USR_BRANCH_PROP is present, this is the new style of configuration
     * (starting Lenya 1.2.2); if it has a value, then a specific branch is wanted:
     * no recursive search. If the property is present, but has no value,
     * search recursively.
     */
    private boolean isSubtreeSearch() {
	boolean recurse = false;
	String usrBranchProp = defaultProperties.getProperty(USR_BRANCH_PROP);
	if (usrBranchProp != null)
	    if (usrBranchProp.trim().length() == 0)
		recurse = true;
	
	return recurse;
    }


    private SearchResult getDirectoryEntry(String userId) 
	throws NamingException, IOException
    {
	DirContext context = null;
	String searchFilter = "";
	String objectName = "";
	boolean recursiveSearch;
	SearchResult result = null;
	
	try {
            readProperties();
	    
            context = bind(defaultProperties.getProperty(MGR_DN_PROP), 
			   defaultProperties.getProperty(MGR_PW_PROP),
			   defaultProperties.getProperty(SECURITY_AUTHENTICATION_PROP));

	    // Get search information and user attribute from properties
	    // provide defaults if not present (backward compatibility)
	    String userAttribute = 
		defaultProperties.getProperty(USR_ATTR_PROP, USR_ATTR_DEFAULT);
	    searchFilter = "(" + userAttribute + "=" + userId + ")";
	    SearchControls scope = new SearchControls();
	    NamingEnumeration results;

	    recursiveSearch = isSubtreeSearch();
	    if (recursiveSearch) {
		scope.setSearchScope(SearchControls.SUBTREE_SCOPE);
		objectName = defaultProperties.getProperty(PROVIDER_URL_PROP);
	    }
	    else {
		scope.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		objectName =  
		    defaultProperties.getProperty(USR_BRANCH_PROP, USR_BRANCH_DEFAULT);
	    }
	
	    if (log.isDebugEnabled())
		log.debug("searching object " + objectName + " filtering with " + searchFilter + ", recursive search ? " + recursiveSearch);

	    results = context.search(objectName, searchFilter, scope);

	    if (results != null && results.hasMore()) {
		result = (SearchResult)results.next();

		// sanity check: if more than one entry is returned
		// for a user-id, then the directory is probably flawed,
		// so it would be nice to warn the administrator.
		//
		// This block is commented out for now, because of possible
		// side-effects, such as unexpected exceptions.
// 		try {
// 		    if (results.hasMore()) {
// 			log.warn("Found more than one entry in the directory for user " + userId + ". You probably should deactivate recursive searches. The first entry was used as a work-around.");
// 		    }
// 		}
// 		catch (javax.naming.PartialResultException e) {
// 		    if (log.isDebugEnabled())
// 			log.debug("Catching and ignoring PartialResultException, as this means LDAP server does not support our sanity check");
// 		}
		
	    }
	}
        catch (NamingException e) {
	    if (log.isDebugEnabled())
		log.debug("NamingException caught when searching on objectName = " + objectName + " and searchFilter=" + searchFilter + ", this exception will be propagated: " + e);
            throw e;
        } 
	finally {
            try {
                if (context != null) {
                    close(context);
                }
            } catch (NamingException e) {
		log.warn("this should not happen: exception closing context " + e);
            }
        }
	return result;
    }

    /**
     * Encapsulation of the creation of a principal: we need to distinguish
     * three cases, in order to support different modes of using a directory.
     * The first is the use of a domain-name (requirement of MS Active Directory):
     * if this property is set, this is used to construct the principal.
     * The second case is where a user-id is somewhere in a domain, but not in a
     * specific branch: in this case, a subtree search is performed to retrieve
     * the complete path.
     * The third case is where a specific branch of the directory is to be used;
     * this is the case where usr-branch is set to a value. In this case, this branch
     * is used to construct the principal.
     */
    private String getPrincipal() throws IOException, NamingException {

	String principal;

	// 1. Check if domain-name is to be supported
	String domainProp = defaultProperties.getProperty(DOMAIN_NAME_PROP);
	if (domainProp != null && domainProp.trim().length() > 0) {
	    principal = domainProp + "\\" + getLdapId();
	}
	else {
	    if (isSubtreeSearch()) {
		// 2. Principal is constructed from directory entry
		SearchResult entry = getDirectoryEntry(getLdapId());
		principal = entry.getName();
		if (entry.isRelative()) {
		    if (principal.length()>0){
			principal = principal +","+ defaultProperties.getProperty(BASE_DN_PROP);
		    }
		}
	    }
	    else {
		// 3. Principal is constructed from properties
		principal = constructPrincipal(getLdapId());
	    }
	}

	return principal;
    }

    /**
     * Construct the principal for a user, by using the given userId along
     * with the configured properties.
     *
     */
    private String constructPrincipal(String userId) {
	StringBuffer principal = new StringBuffer();
	principal
	    .append(defaultProperties.getProperty(USR_ATTR_PROP, USR_ATTR_DEFAULT))
	    .append("=")
	    .append(userId)
	    .append(",");

	String baseDn = defaultProperties.getProperty(BASE_DN_PROP);
	if (baseDn != null && baseDn.length() > 0) {
	    // USR_BRANCH_PROP may be empty, so only append when not-empty
	    String usrBranch = defaultProperties.getProperty(USR_BRANCH_PROP);
	    if (usrBranch != null) {
		if (usrBranch.trim().length() > 0)
		    principal.append(usrBranch).append(",");
	    }
	    else
		principal.append(USR_BRANCH_DEFAULT).append(",");
		
	    principal.append(defaultProperties.getProperty(BASE_DN_PROP));
	}
	else {
	    // try for backwards compatibility of ldap properties
	    log.warn("getPrincipal() read a deprecated format in ldap properties, please update");
	    principal.append(defaultProperties.getProperty(PARTIAL_USER_DN_PROP));
	}

	if (log.isDebugEnabled())
	    log.debug("getPrincipal() returning " + principal.toString());

	return principal.toString();
    }


}
