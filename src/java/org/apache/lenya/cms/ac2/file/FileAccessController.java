/*
 * $Id
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.ac2.file;

import java.io.File;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.GroupManager;
import org.apache.lenya.cms.ac.RoleManager;
import org.apache.lenya.cms.ac.UserManager;
import org.apache.lenya.cms.ac2.AccessController;
import org.apache.lenya.cms.ac2.PolicyManager;
import org.apache.lenya.cms.ac2.URLPolicy;
import org.apache.lenya.cms.ac2.Policy;
import org.apache.lenya.cms.publication.PageEnvelope;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FileAccessController implements AccessController {

    /**
     * Creates a new FileAccessController.
     * If you use this constructor, you have to set the configuration directory either
     * by calling {@link #setConfigurationDirectory(File)} or by calling
     * {@link #configure(Configuration)}.
     */
    public FileAccessController() {
    }

    /**
     * Creates a new FileAccessController based on a configuration directory.
     * @param configurationDirectory The configuration directory.
     */
    public FileAccessController(File configurationDirectory) {
        assert configurationDirectory != null;
        assert configurationDirectory.exists();
        assert configurationDirectory.isDirectory();
        this.configurationDirectory = configurationDirectory;
    }

    private File configurationDirectory;

    protected static final String POLICY_PATH = "policies";
    protected static final String ACCREDITABLE_PATH = "passwd";

    /**
     * Returns the configuration directory.
     * @return The configuration directory.
     */
    public File getConfigurationDirectory() {
        return configurationDirectory;
    }

    /**
     * Returns the directory where accreditables are configured.
     * @return A file.
     */
    protected File getAccreditableConfigurationDirectory() {
        return new File(getConfigurationDirectory(), ACCREDITABLE_PATH);
    }

    /**
     * @see org.apache.lenya.cms.ac2.AccessController#getUserManager()
     */
    public UserManager getUserManager() throws AccessControlException {
        return UserManager.instance(getAccreditableConfigurationDirectory());
    }

    /**
     * @see org.apache.lenya.cms.ac2.AccessController#getGroupManager()
     */
    public GroupManager getGroupManager() throws AccessControlException {
        return GroupManager.instance(getAccreditableConfigurationDirectory());
    }

    /**
     * @see org.apache.lenya.cms.ac2.AccessController#getRoleManager()
     */
    public RoleManager getRoleManager() throws AccessControlException {
        return RoleManager.instance(getAccreditableConfigurationDirectory());
    }

    /**
     * @see org.apache.lenya.cms.ac2.AccessController#getPolicy(java.lang.String)
     */
    public Policy getPolicy(PageEnvelope envelope) throws AccessControlException {
        assert envelope != null;
        if (getConfigurationDirectory() == null) {
            if (configurationDirectoryPath == null) {
                throw new AccessControlException("Configuration directory not set!");
            }
            File directory =
                new File(envelope.getPublication().getDirectory(), configurationDirectoryPath);
            setConfigurationDirectory(directory);
        }
        return getPolicy(envelope.getDocumentURL());
    }
    
    /**
     * Returns the policy for a given URL.
     * @param url The URL.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    protected Policy getPolicy(String url) throws AccessControlException {
        URLPolicy policy = new URLPolicy(url, getPolicyManager());
        return policy;
    }
    
    private PolicyManager policyManager;

    /**
     * @see org.apache.lenya.cms.ac2.AccessController#getPolicyManager()
     */
    public PolicyManager getPolicyManager() throws AccessControlException {
        if (policyManager == null) {
            policyManager =
                new FilePolicyManager(
                    new File(getConfigurationDirectory(), POLICY_PATH),
                    getUserManager(),
                    getGroupManager(),
                    getRoleManager());
        }
        return policyManager;
    }

    protected static final String DIRECTORY_ELEMENT = "directory";
    protected static final String SRC_ATTRIBUTE = "src";

    /**
     * @see org.apache.lenya.cms.ac2.AccessController#configure(File, org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException {
        Configuration directoryConfiguration = configuration.getChild(DIRECTORY_ELEMENT);
        configurationDirectoryPath = directoryConfiguration.getAttribute(SRC_ATTRIBUTE);
    }

    private String configurationDirectoryPath;

    /**
     * Sets the configuration directory.
     * @param file The configuration directory.
     */
    public void setConfigurationDirectory(File file) {
        assert file != null && file.isDirectory();
        configurationDirectory = file;
    }

}
