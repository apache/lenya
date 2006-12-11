/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *  
 */

package org.apache.lenya.ac.cifs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.lenya.ac.ItemManager;
import org.apache.lenya.ac.file.FileUser;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbSession;

import jcifs.UniAddress;
import java.net.UnknownHostException;

/**
 * CIFS user.
 * @version $Id$
 */
public class CIFSUser extends FileUser {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static Properties defaultProperties = null;

    // The name for the cifs.properties domain controller lookup
    private static final String DOMAIN_CONTROLLER = "domain-controller";

    // The name for the cifs.properties domain name lookup
    private static final String DOMAIN = "domain";

    /**
     * Creates a new CIFSUser object.
     * @param itemManager The item manager.
     * @param logger The logger.
     */
    public CIFSUser(ItemManager itemManager, Logger logger) {
        super(itemManager, logger);

    }

    /**
     * Create a CIFSUser
     * @param itemManager The item manager.
     * @param logger The logger.
     * @param id The user ID.
     * @param fullName The user's name.
     * @param email The e-mail address.
     * @param password The password.
     */
    public CIFSUser(ItemManager itemManager, Logger logger, String id, String fullName,
            String email, String password) {
        super(itemManager, logger, id, fullName, email, password);

    }

    /**
     * Initializes this user.
     * @throws ConfigurationException when something went wrong.
     */
    protected void initialize() throws ConfigurationException {
        try {
            readProperties(super.getConfigurationDirectory());
        } catch (final IOException ioe) {
            throw new ConfigurationException("Reading cifs.properties file in ["
                    + super.getConfigurationDirectory() + "] failed", ioe);
        }
    }

    /**
     * Create a new CIFSUser from a configuration
     * @param config the <code>Configuration</code> specifying the user
     *        details
     * @throws ConfigurationException if the user could not be instantiated
     */
    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);
        initialize();
    }

    /**
     * Authenticate a user. This is done by NTDomain Authentication using jcifs
     * @param password to authenticate with
     * @return true if the given password matches the password for this user
     */
    public boolean authenticate(String password) {

        System.setProperty("jcifs.smb.client.disablePlainTextPasswords", "true");
        try {
            UniAddress mydomaincontroller = UniAddress.getByName(getDomainController());
            NtlmPasswordAuthentication mycreds = new NtlmPasswordAuthentication(getDomainName(),
                    super.getId(), password);
            SmbSession.logon(mydomaincontroller, mycreds);
            // SUCCESS
            return true;
        } catch (final SmbAuthException sae) {
            // AUTHENTICATION FAILURE
            if (getLogger().isInfoEnabled()) {
                getLogger().info(
                        "Authentication against [" + getDomainController() + "]" + " failed for "
                                + getDomainName() + "/" + super.getId());
            }
            return false;
        } catch (final SmbException se) {
            // NETWORK PROBLEMS?
            return false;
        } catch (final UnknownHostException unho) {
            return false;
        }

    }

    /**
     * Read the properties
     * @param configurationDirectory The configuration directory.
     * @throws IOException if the properties cannot be found.
     */
    private void readProperties(File configurationDirectory) throws IOException {
        // create and load default properties
        File propertiesFile = new File(configurationDirectory, "cifs.properties");

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
     * Get the domain controller we want to authenticate against
     * @return the name of the domain controller
     */
    private String getDomainController() {
        return (String) defaultProperties.get(DOMAIN_CONTROLLER);
    }

    /**
     * Get the domain name
     * @return the domain name
     */
    private String getDomainName() {
        return (String) defaultProperties.get(DOMAIN);
    }

}
