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
package org.apache.lenya.cms.ac2.file;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.GroupManager;
import org.apache.lenya.cms.ac.RoleManager;
import org.apache.lenya.cms.ac.UserManager;
import org.apache.lenya.cms.ac2.AccessController;
import org.apache.lenya.cms.ac2.Policy;
import org.apache.lenya.cms.ac2.PolicyManager;
import org.apache.lenya.cms.ac2.URLPolicy;
import org.apache.lenya.cms.publication.Publication;

import java.io.File;


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
    public Policy getPolicy(Publication publication, String url)
        throws AccessControlException {
        assert publication != null;

        if (getConfigurationDirectory() == null) {
            if (configurationDirectoryPath == null) {
                throw new AccessControlException("Configuration directory not set!");
            }

            File directory = new File(publication.getDirectory(), configurationDirectoryPath);
            setConfigurationDirectory(directory);
        }

        return getPolicy(url);
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
            policyManager = new FilePolicyManager(new File(getConfigurationDirectory(), POLICY_PATH),
                    getUserManager(), getGroupManager(), getRoleManager());
        }

        return policyManager;
    }

    protected static final String DIRECTORY_ELEMENT = "directory";
    protected static final String SRC_ATTRIBUTE = "src";

    /**
     * @see org.apache.lenya.cms.ac2.AccessController#configure(File, org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration)
        throws ConfigurationException {
        Configuration directoryConfiguration = configuration.getChild(DIRECTORY_ELEMENT);
        configurationDirectoryPath = directoryConfiguration.getAttribute(SRC_ATTRIBUTE);
    }

    private String configurationDirectoryPath;

    /**
     * Sets the configuration directory.
     * @param file The configuration directory.
     */
    public void setConfigurationDirectory(File file) {
        assert (file != null) && file.isDirectory();
        configurationDirectory = file;
    }
}
