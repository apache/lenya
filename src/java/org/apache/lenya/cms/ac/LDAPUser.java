/*
 * $Id: LDAPUser.java,v 1.2 2003/06/18 18:55:29 egli Exp $
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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.lenya.cms.publication.Publication;
import org.apache.log4j.Category;

/**
 * @author egli
 * 
 * 
 */
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
        DefaultConfiguration config =
            (DefaultConfiguration) super.createConfiguration();
        // add ldap_id node
        DefaultConfiguration child = new DefaultConfiguration(LDAP_ID);
        child.setValue(ldapId);
        config.addChild(child);

        return config;
    }

    /**
     * @return
     */
    public String getLdapId() {
        return ldapId;
    }

    /**
     * @param string
     */
    public void setLdapId(String string) {
        ldapId = string;
    }

    /* (non-Javadoc)
     * @see org.apache.lenya.cms.ac.User#authenticate(java.lang.String)
     */
    public boolean authenticate(String password) {
        String principal =
            "uid="
                + getLdapId()
                + ","
                + defaultProperties.getProperty(PARTIAL_USER_DN);
        Context ctx;
        try {
            ctx = bind(principal, password);
            close(ctx);
        } catch (NamingException e) {
            // log this failure
            log.info(
                "Bind for user " + principal + " to Ldap server failed: ",
                e);
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see org.apache.lenya.cms.ac.User#getFullName()
     */
    public String getFullName() {
        String fullName = null;
        LdapContext ctx;

        try {
            ctx =
                bind(
                    defaultProperties.getProperty(MGR_DN),
                    defaultProperties.getProperty(MGR_PW));
            String[] attrs = new String[1];
            attrs[0] = "gecos"; /* users full name */

            Attributes answer =
                ctx.getAttributes("uid=m400032,ou=People", attrs);
            if (answer != null) {
                Attribute attr = answer.get("gecos");
                if (attr != null) {
                    for (NamingEnumeration enum = attr.getAll();
                        enum.hasMore();
                        enum.next()) {
                        fullName = attr.getID();
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

    /* 
     * LDAP Users fetch their full name information from the LDAP server,
     * so we don't store it locally. Since we only have read access we basically
     * can't set the full name, i.e. any request to change the full name 
     * is ignored.
     */
    public void setFullName(String string) {
        // we do not have write access to LDAP, so we ignore 
        // change request to the full name.
    }

    private LdapContext bind(String principal, String credentials)
        throws NamingException {
        Hashtable env = new Hashtable();

        System.setProperty(
            "javax.net.ssl.trustStore",
            defaultProperties.getProperty(KEY_STORE));

        env.put(
            Context.INITIAL_CONTEXT_FACTORY,
            "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(
            Context.PROVIDER_URL,
            defaultProperties.getProperty(PROVIDER_URL));
        env.put(
            Context.SECURITY_PROTOCOL,
            defaultProperties.getProperty(SECURITY_PROTOCOL));
        env.put(
            Context.SECURITY_AUTHENTICATION,
            defaultProperties.getProperty(SECURITY_AUTHENTICATION));
        env.put(Context.SECURITY_PRINCIPAL, principal);
        env.put(Context.SECURITY_CREDENTIALS, credentials);

        LdapContext ctx = new InitialLdapContext(env, null);
        return ctx;
    }

    private void close(Context ctx) throws NamingException {
        ctx.close();
    }

    private void readProperties() {
        // create and load default properties
        if (defaultProperties == null) {
            defaultProperties = new Properties();
            FileInputStream in;
            try {
                in = new FileInputStream("ldap.properties");
                defaultProperties.load(in);
                in.close();
            } catch (IOException e) {
                log.error("Could not load properties", e);
            }
        }
    }
}
