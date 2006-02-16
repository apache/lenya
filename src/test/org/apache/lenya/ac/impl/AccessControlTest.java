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

/* $Id$  */

package org.apache.lenya.ac.impl;

import java.io.File;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.file.FileAccreditableManager;
import org.apache.lenya.ac.file.FilePolicyManager;
import org.apache.lenya.cms.LenyaTestCase;
import org.apache.lenya.cms.ac.DocumentPolicyManagerWrapper;
import org.apache.lenya.cms.ac.PublicationAccessControllerResolver;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;

/**
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class AccessControlTest extends LenyaTestCase {

    private ServiceSelector accessControllerResolverSelector;
    private AccessControllerResolver accessControllerResolver;
    private DefaultAccessController accessController;

    private File accreditablesDirectory;

    /**
     * Returns the access controller.
     * @return An access controller.
     */
    public DefaultAccessController getAccessController() {
        return this.accessController;
    }

    protected static final String URL = "/test/authoring/index.html";

    public void setUp() throws Exception {

        super.setUp();

        this.accessControllerResolverSelector = (ServiceSelector) getManager().lookup(AccessControllerResolver.ROLE
                + "Selector");
        assertNotNull(this.accessControllerResolverSelector);

        this.accessControllerResolver = (AccessControllerResolver) this.accessControllerResolverSelector.select(AccessControllerResolver.DEFAULT_RESOLVER);

        assertNotNull(this.accessControllerResolver);
        getLogger().info("Using access controller resolver: ["
                + this.accessControllerResolver.getClass() + "]");
        
        Publication pub = PublicationUtil.getPublication(getManager(), "test");
        getLogger().info("Resolve access controller");
        getLogger().info("Publication directory: [" + pub.getDirectory().getAbsolutePath() + "]");
        
        this.accessController = (DefaultAccessController) ((PublicationAccessControllerResolver) this.accessControllerResolver).resolveAccessController(URL);

        assertNotNull(this.accessController);
        getLogger().info("Resolved access controller: [" + this.accessController.getClass() + "]");

        File servletContext = pub.getServletContext();
        DocumentPolicyManagerWrapper wrapper = (DocumentPolicyManagerWrapper) this.accessController.getPolicyManager();
        FilePolicyManager policyManager = (FilePolicyManager) wrapper.getPolicyManager();
        policyManager.setPoliciesDirectory(servletContext);

        this.accreditablesDirectory = new File(pub.getDirectory(), "config/ac/passwd".replace('/',
                File.separatorChar));
        ((FileAccreditableManager) this.accessController.getAccreditableManager()).setConfigurationDirectory(this.accreditablesDirectory);

    }

    /**
     * The teardown method for JUnit
     * @exception Exception if an error occurs
     */
    public void tearDown() throws Exception {

        if (this.accessControllerResolverSelector != null) {
            if (this.accessControllerResolver != null) {
                if (this.accessController != null) {
                    this.accessControllerResolver.release(this.accessController);
                }
                this.accessControllerResolverSelector.release(this.accessControllerResolver);
            }
            getManager().release(this.accessControllerResolver);
        }
        super.tearDown();
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
        identity.enableLogging(getLogger());
        identity.addIdentifiable(user);

        return identity;
    }

    /**
     * Returns the policy manager.
     * @return A policy manager.
     */
    protected PolicyManager getPolicyManager() {
        return getAccessController().getPolicyManager();
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
        return this.accreditablesDirectory;
    }

}