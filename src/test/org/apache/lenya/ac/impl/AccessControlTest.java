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
 * @author andreas
 *
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
