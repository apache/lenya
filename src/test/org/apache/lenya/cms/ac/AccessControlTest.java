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
package org.apache.lenya.cms.ac;

import java.io.File;

import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.ac2.AccessController;
import org.apache.lenya.cms.ac2.PolicyTest;
import org.apache.lenya.cms.ac2.file.FileAccessController;
import org.apache.lenya.cms.publication.Publication;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AccessControlTest extends TestCase {
    
    /**
     * Constructor.
     * @param test The test to execute.
     */
    public AccessControlTest(String test) {
        super(test);
    }

    /**
     * The main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        TestRunner.run(getSuite());
    }
    
    protected static final Class classes[] = {
        FileGroupTest.class,
        FileRoleTest.class,
        FileUserTest.class,
//        LDAPUserTest.class,
        GroupManagerTest.class,
        RoleManagerTest.class,
        UserManagerTest.class,
        PolicyTest.class
    };
    
    /**
     * Creates a test suite.
     * @return a test suite.
     */
    public static Test getSuite() {
        TestSuite suite = new TestSuite();
        for (int i = 0; i < classes.length; i++) {
            suite.addTestSuite(classes[i]);
        }
        return suite;
    }

    /**
     * Returns the configuration directory.
     * @return A file object.
     */
    public static final File getConfigurationDirectory() {
        Publication publication = PublicationHelper.getPublication();
        return new File(publication.getDirectory(), ItemManager.PATH);
    }
    
    /**
     * Returns the access controller.
     * @return An access controller object.
     */
    public static final AccessController getAccessController() {
        Publication publication = PublicationHelper.getPublication();
        File configDir = new File(publication.getDirectory(), "config/ac");
        return new FileAccessController(configDir);
    }
    
    /** @see junit.framework.TestCase#setUp() */
    protected void setUp() throws Exception {
        if (PublicationHelper.getPublication() == null) {
            String args[] = {
                "D:\\Development\\build\\tomcat-4.1.24\\webapps\\lenya",
                "test"
            };
            PublicationHelper.extractPublicationArguments(args);
        }
    }
    
}
