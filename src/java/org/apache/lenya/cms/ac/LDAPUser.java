/*
$Id
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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

import org.apache.log4j.Category;

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
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;


/**
 * @author egli
 *
 *
 */
public class LDAPUser extends FileUser {
    private static Properties defaultProperties = null;
    private static Category log = Category.getInstance(LDAPUser.class);

    public static final String LDAP_ID = "ldapid";
    public static final String CONFIG_PATH = File.separator + "config" + File.separator + "ac" +
        File.separator;
    private static String PROVIDER_URL = "provider-url";
    private static String MGR_DN = "mgr-dn";
    private static String MGR_PW = "mgr-pw";
    private static String PARTIAL_USER_DN = "partial-user-dn";
    private static String KEY_STORE = "key-store";
    private static String SECURITY_PROTOCOL = "security-protocol";
    private static String SECURITY_AUTHENTICATION = "security-authentication";
    private String ldapId;

    /**
     * Creates a new LDAPUser object.
     */
    public LDAPUser() {
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

        try {
            readProperties();
        } catch (IOException e) {
            throw new ConfigurationException("Could not read properties", e);
        }
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

        try {
            readProperties();
        } catch (IOException e) {
            throw new ConfigurationException("Could not read properties", e);
        }
    }

    /** (non-Javadoc)
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
     * @param string the new ldap id
     */
    public void setLdapId(String string) {
        ldapId = string;
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.ac.User#authenticate(java.lang.String)
     */
    public boolean authenticate(String password) {
        String principal = "uid=" + getLdapId() + "," +
            defaultProperties.getProperty(PARTIAL_USER_DN);
        Context ctx;

        try {
            ctx = bind(principal, password);
            close(ctx);
        } catch (NamingException e) {
            // log this failure
            log.info("Bind for user " + principal + " to Ldap server failed: ", e);

            return false;
        }

        return true;
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.ac.User#getName()
     */
    public String getName() {
        String fullName = null;
        LdapContext ctx;

        try {
            ctx = bind(defaultProperties.getProperty(MGR_DN), defaultProperties.getProperty(MGR_PW));

            String[] attrs = new String[1];
            attrs[0] = "gecos"; /* users full name */

            Attributes answer = ctx.getAttributes("uid=m400032,ou=People", attrs);

            if (answer != null) {
                Attribute attr = answer.get("gecos");

                if (attr != null) {
                    for (NamingEnumeration enum = attr.getAll(); enum.hasMore(); enum.next()) {
                        fullName = (String) attr.get();
                    }
                }
            }

            close(ctx);

            return fullName;
        } catch (NamingException e) {
            // log this failure
            e.printStackTrace();

            return null;
        }
    }

    /**
     * LDAP Users fetch their name information from the LDAP server,
     * so we don't store it locally. Since we only have read access we basically
     * can't set the name, i.e. any request to change the name is ignored.
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
     * @return a <code>LdapContext</code>
     * @throws NamingException if there are problems establishing the Ldap connection
     */
    private LdapContext bind(String principal, String credentials)
        throws NamingException {
        Hashtable env = new Hashtable();

        System.setProperty("javax.net.ssl.trustStore",
            getConfigurationDirectory().getAbsolutePath() + File.separator +
            defaultProperties.getProperty(KEY_STORE));

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, defaultProperties.getProperty(PROVIDER_URL));
        env.put(Context.SECURITY_PROTOCOL, defaultProperties.getProperty(SECURITY_PROTOCOL));
        env.put(Context.SECURITY_AUTHENTICATION,
            defaultProperties.getProperty(SECURITY_AUTHENTICATION));
        env.put(Context.SECURITY_PRINCIPAL, principal);
        env.put(Context.SECURITY_CREDENTIALS, credentials);

        LdapContext ctx = new InitialLdapContext(env, null);

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
