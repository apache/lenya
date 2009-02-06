/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
package org.apache.lenya.cms.ac.usecases;

import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.GroupManager;
import org.apache.lenya.ac.IPRangeManager;
import org.apache.lenya.ac.RoleManager;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.cms.usecase.AbstractUsecase;

/**
 * Super class for access-control related usecases.
 * 
 * @version $Id: AccessControlUsecase.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class AccessControlUsecase extends AbstractUsecase {

    private UserManager userManager;
    private GroupManager groupManager;
    private IPRangeManager ipRangeManager;
    private RoleManager roleManager;
    private AccessController accessController;
    private AccessControllerResolver accessControllerResolver;

    /**
     * Initializes the accreditable managers. FIXME: This method resolves the
     * AccessController, it has to be released after it is used!
     */
    protected void initializeAccessController() {
        super.doInitialize();

        if (getLogger().isDebugEnabled())
            getLogger().debug("initializeAccessController() called");

        this.accessController = null;

        try {

            this.accessController = getAccessControllerResolver().resolveAccessController(getSourceURL());

            if (this.accessController == null) {
                throw new RuntimeException("No access controller could be resolved for URL ["
                        + getSourceURL() + "].");
            }

            AccreditableManager accreditableManager = this.accessController
                    .getAccreditableManager();

            this.userManager = accreditableManager.getUserManager();
            this.groupManager = accreditableManager.getGroupManager();
            this.roleManager = accreditableManager.getRoleManager();
            this.ipRangeManager = accreditableManager.getIPRangeManager();

        } catch (Exception e) {
            throw new RuntimeException("Initialization failed: ", e);
        }

    }

    /**
     * @return Returns the groupManager.
     */
    protected GroupManager getGroupManager() {
        if (this.groupManager == null) {
            initializeAccessController();
        }
        return this.groupManager;
    }

    /**
     * @return Returns the ipRangeManager.
     */
    protected IPRangeManager getIpRangeManager() {
        if (this.ipRangeManager == null) {
            initializeAccessController();
        }
        return this.ipRangeManager;
    }

    /**
     * @return Returns the roleManager.
     */
    protected RoleManager getRoleManager() {
        if (this.roleManager == null) {
            initializeAccessController();
        }
        return this.roleManager;
    }

    /**
     * @return Returns the userManager.
     */
    protected UserManager getUserManager() {
        if (this.userManager == null) {
            if (getLogger().isDebugEnabled())
                getLogger()
                        .debug(
                                "getUserManager() accessed, is null, so calling initializeAccessController");
            initializeAccessController();
        }
        return this.userManager;
    }

    /**
     * @return Returns the accessController.
     */
    protected AccessController getAccessController() {
        if (this.accessController == null) {
            initializeAccessController();
        }
        return this.accessController;
    }

    protected AccessControllerResolver getAccessControllerResolver() {
        return accessControllerResolver;
    }

    public void setAccessControllerResolver(AccessControllerResolver accessControllerResolver) {
        this.accessControllerResolver = accessControllerResolver;
    }
    
    
}
