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

/* $Id: AccessControlTest.java,v 1.3 2004/03/04 15:40:19 egli Exp $  */

package org.apache.lenya.ac.impl;

import java.io.File;

import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.file.FileAccreditableManager;
import org.apache.lenya.ac.file.FilePolicyManager;
import org.apache.lenya.cms.ExcaliburTest;
import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.ac.PublicationAccessControllerResolver;

/**
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AccessControlTest extends ExcaliburTest {

    private ComponentSelector accessControllerResolverSelector;
    private AccessControllerResolver accessControllerResolver;
    private DefaultAccessController accessController;
    
    private File accreditablesDirectory;

    /**
     * @param test The test.
     */
    public AccessControlTest(String test) {
        super(test);
    }

    /**
     * Returns the access controller.
     * @return An access controller.
     */
    public DefaultAccessController getAccessController() {
        return accessController;
    }

    protected static final String URL = "/test/authoring/index.html";

    /** @see junit.framework.TestCase#setUp() */
    protected void setUp() throws Exception {

        if (PublicationHelper.getPublication() == null) {
            String[] args = { "D:\\Development\\build\\tomcat-4.1.24\\webapps\\lenya", "test" };
            PublicationHelper.extractPublicationArguments(args);
        }

        super.setUp();

        accessControllerResolverSelector =
            (ComponentSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");
        assertNotNull(accessControllerResolverSelector);

        accessControllerResolver =
            (AccessControllerResolver) accessControllerResolverSelector.select(
                AccessControllerResolver.DEFAULT_RESOLVER);

        assertNotNull(accessControllerResolver);
        getLogger().info(
            "Using access controller resolver: [" + accessControllerResolver.getClass() + "]");

        accessController =
            (DefaultAccessController)
                (
                    (
                        PublicationAccessControllerResolver) accessControllerResolver)
                            .resolveAccessController(
                URL);

        assertNotNull(accessController);
        getLogger().info("Resolved access controller: [" + accessController.getClass() + "]");

        File servletContext = PublicationHelper.getPublication().getServletContext();
        ((FilePolicyManager) accessController.getPolicyManager()).setPoliciesDirectory(
            servletContext);

        accreditablesDirectory =
            new File(
                PublicationHelper.getPublication().getDirectory(),
                "config/ac/passwd".replace('/', File.separatorChar));
        (
            (FileAccreditableManager) accessController
                .getAccreditableManager())
                .setConfigurationDirectory(
            accreditablesDirectory);

    }

    /**
     * The teardown method for JUnit
     *
     * @exception  Exception  Description of Exception
     * @since
     */
    public void tearDown() throws Exception {
        super.tearDown();

        if (accessControllerResolverSelector != null) {
            if (accessControllerResolver != null) {
                if (accessController != null) {
                    accessControllerResolver.release(accessController);
                }
                accessControllerResolverSelector.release(accessControllerResolver);
            }
            manager.release(accessControllerResolver);
        }
    }

    protected static final String USERNAME = "lenya";

    /**
     * Returns the identity.
     * @return The identity.
     * @throws AccessControlException when something went wrong.
     */
    protected Identity getIdentity() throws AccessControlException {
        DefaultAccessController controller = getAccessController();
        User user = controller.getAccreditableManager().getUserManager().getUser(USERNAME);
        assertNotNull(user);

        Identity identity = new Identity();
        identity.addIdentifiable(user);

        return identity;
    }

    /**
     * Returns the policy manager.
     * @return A policy manager.
     */
    protected FilePolicyManager getPolicyManager() {
        return (FilePolicyManager) getAccessController().getPolicyManager();
    }

    /**
     * Returns the accreditable manager.
     * @return An accreditable manager.
     */
    protected AccreditableManager getAccreditableManager() {
        return getAccessController().getAccreditableManager();
    }

    /**
     * Returns the directories where accreditables are stored.
     * @return A file.
     */
    protected File getAccreditablesDirectory() {
        return accreditablesDirectory;
    }

}
