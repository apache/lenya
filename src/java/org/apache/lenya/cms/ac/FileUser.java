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
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;

import org.apache.log4j.Category;

import java.io.File;


/**
 * @author egli
 *
 *
 */
public class FileUser extends User implements Item {
    private Category log = Category.getInstance(FileUser.class);

    public static final String ID = "identity";
    public static final String FULL_NAME = "fullname";
    public static final String EMAIL = "email";
    public static final String DESCRIPTION = "description";
    public static final String PASSWORD = "password";
    public static final String GROUPS = "groups";
    public static final String GROUP = "group";
    public static final String PASSWORD_ATTRIBUTE = "type";
    public static final String ID_ATTRIBUTE = "id";
    public static final String CLASS_ATTRIBUTE = "class";
    private File configurationDirectory;

    /**
     * Creates a new FileUser object.
     */
    public FileUser() {
    }

    /**
     * Create a FileUser
     *
     * @param configurationDirectory where the user will be attached to
     * @param id the user id
     * @param fullName the full name of the user
     * @param email the users email address
     * @param password the users password
     */
    public FileUser(File configurationDirectory, String id, String fullName, String email,
        String password) {
        super(id, fullName, email, password);
        setConfigurationDirectory(configurationDirectory);
    }

    /**
     * Configure this FileUser.
     *
     * @param config where the user details are specified
     * @throws ConfigurationException if the necessary details aren't specified in the config
     */
    public void configure(Configuration config) throws ConfigurationException {
        setId(config.getAttribute(ID_ATTRIBUTE));
        setFullName(config.getChild(FULL_NAME).getValue(null));
        setEmail(config.getChild(EMAIL).getValue());
        setDescription(config.getChild(DESCRIPTION).getValue(""));
        setEncryptedPassword(config.getChild(PASSWORD).getValue(null));

        Configuration[] groups = config.getChildren(GROUPS);

        if (groups.length == 1) {
            groups = groups[0].getChildren(GROUP);

            GroupManager manager = null;

            try {
                manager = GroupManager.instance(configurationDirectory);
            } catch (AccessControlException e) {
                throw new ConfigurationException(
                    "Exception when trying to fetch GroupManager for directory: [" +
                    configurationDirectory + "]", e);
            }

            for (int i = 0; i < groups.length; i++) {
                String groupName = groups[i].getValue();
                Group group = manager.getGroup(groupName);

                if (!group.contains(this)) {
                    group.add(this);
                }

                if (group == null) {
                    log.error("Couldn't find Group for group name [" + groupName + "]");
                }
            }
        } else {
            // strange, it should have groups
            log.error("User " + config.getAttribute(ID_ATTRIBUTE) +
                " doesn't seem to have any groups");
        }
    }

    /**
     * Create a configuration from the current user details. Can
     * be used for saving.
     *
    * @return a <code>Configuration</code>
    */
    protected Configuration createConfiguration() {
        DefaultConfiguration config = new DefaultConfiguration(ID);
        config.setAttribute(ID_ATTRIBUTE, getId());
        config.setAttribute(CLASS_ATTRIBUTE, this.getClass().getName());

        DefaultConfiguration child = null;

        // add fullname node
        child = new DefaultConfiguration(FULL_NAME);
        child.setValue(getFullName());
        config.addChild(child);

        // add email node
        child = new DefaultConfiguration(EMAIL);
        child.setValue(getEmail());
        config.addChild(child);

        // add description node
        child = new DefaultConfiguration(DESCRIPTION);
        child.setValue(getDescription());
        config.addChild(child);

        // add password node
        child = new DefaultConfiguration(PASSWORD);
        child.setValue(getEncryptedPassword());
        child.setAttribute(PASSWORD_ATTRIBUTE, "md5");
        config.addChild(child);

        // add group nodes
        child = new DefaultConfiguration(GROUPS);
        config.addChild(child);

        Group[] groups = getGroups();

        for (int i = 0; i < groups.length; i++) {
            DefaultConfiguration groupNode = new DefaultConfiguration(GROUP);
            groupNode.setValue(groups[i].getName());
            child.addChild(groupNode);
        }

        return config;
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.ac.User#save()
     */
    public void save() throws AccessControlException {
        DefaultConfigurationSerializer serializer = new DefaultConfigurationSerializer();
        Configuration config = createConfiguration();
        UserManager manager = UserManager.instance(configurationDirectory);
        manager.add(this);

        File xmlPath = manager.getConfigurationDirectory();
        File xmlfile = new File(xmlPath, getId() + UserManager.SUFFIX);

        try {
            serializer.serializeToFile(xmlfile, config);
        } catch (Exception e) {
            throw new AccessControlException(e);
        }
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.ac.User#delete()
     */
    public void delete() throws AccessControlException {
        super.delete();

        UserManager manager = UserManager.instance(configurationDirectory);
        manager.remove(this);

        File xmlPath = manager.getConfigurationDirectory();
        File xmlfile = new File(xmlPath, getId() + UserManager.SUFFIX);
        xmlfile.delete();
    }

    /**
     * Returns the configuration directory.
     * @return A file object.
     */
    protected File getConfigurationDirectory() {
        return configurationDirectory;
    }

    /**
     * @see org.apache.lenya.cms.ac.Item#setConfigurationDirectory(java.io.File)
     */
    public void setConfigurationDirectory(File configurationDirectory) {
        assert (configurationDirectory != null) && configurationDirectory.isDirectory();
        this.configurationDirectory = configurationDirectory;
    }
}
