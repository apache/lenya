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
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.apache.lenya.cms.PublicationHelper;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;


/**
 * @author egli
 *
 *
 */
public class FileGroupTest extends AccessControlTest {
    /**
     * Constructor for FileGroupTest.
     * @param arg0 command line args
     */
    public FileGroupTest(String arg0) {
        super(arg0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        junit.textui.TestRunner.run(FileGroupTest.class);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     * @throws ConfigurationException DOCUMENT ME!
     * @throws SAXException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    final public void testFileGroup()
        throws AccessControlException, ConfigurationException, SAXException, IOException {
        String groupId = "testGroup";
        String roleId = "testRole";
        File configurationDirectory = getConfigurationDirectory();

        System.out.println("Configuration directory: " + configurationDirectory);

        FileGroup group = new FileGroup(configurationDirectory, groupId);
        FileRole role = new FileRole(configurationDirectory, roleId);

        //		group.addRole(role);
        role.save();
        group.save();

        File path = null;
        path = RoleManager.instance(configurationDirectory).getConfigurationDirectory();

        File groupFile = new File(path, groupId + GroupManager.SUFFIX);
        assertNotNull(groupFile);
        assertTrue(groupFile.exists());

        Configuration config = null;
        config = new DefaultConfigurationBuilder().buildFromFile(groupFile);
        assertNotNull(config);

        FileGroup newGroup = null;
        newGroup = new FileGroup();
        newGroup.setConfigurationDirectory(configurationDirectory);
        newGroup.configure(config);
        assertNotNull(newGroup);

        assertTrue(newGroup.getId().equals(groupId));

        /*
                int roleCount = 0;
                for (Iterator roles = newGroup.getRoles(); roles.hasNext();) {
                        Role newRole = (Role) roles.next();
                        roleCount = roleCount + 1;
                        assertTrue(newRole.getName().equals(roleName));
                }
                assertEquals(1, roleCount);
        */
    }
}
