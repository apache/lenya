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
package org.apache.lenya.cms.admin;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.GroupManager;
import org.apache.lenya.ac.IPRangeManager;
import org.apache.lenya.ac.RoleManager;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.cms.usecase.AbstractUsecase;

/**
 * Super class for access-control related usecases.
 * 
 * @version $Id$
 */
public class AccessControlUsecase extends AbstractUsecase {

    /**
     * Ctor.
     */
    public AccessControlUsecase() {
        super();
    }
    
    private UserManager userManager;
    private GroupManager groupManager;
    private IPRangeManager ipRangeManager;
    private RoleManager roleManager;
    private AccessController accessController;

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doInitialize()
     */
    protected void doInitialize() throws Exception {
        super.doInitialize();
        
        accessController = null;
        ServiceSelector selector = null;
        AccessControllerResolver resolver = null;
        
        try {
            selector = (ServiceSelector) this.manager.lookup(AccessControllerResolver.ROLE + "Selector");
            resolver =
                (AccessControllerResolver) selector.select(
                    AccessControllerResolver.DEFAULT_RESOLVER);

            accessController = resolver.resolveAccessController(getSourceURL());

            AccreditableManager accreditableManager =
                ((DefaultAccessController) accessController).getAccreditableManager();

            this.userManager = accreditableManager.getUserManager();
            this.groupManager = accreditableManager.getGroupManager();
            this.roleManager = accreditableManager.getRoleManager();
            this.ipRangeManager = accreditableManager.getIPRangeManager();

        } catch (Exception e) {
            throw new ConfigurationException("Initialization failed: ", e);
        } finally {
            if (selector != null) {
                if (resolver != null) {
                    selector.release(resolver);
                }
                this.manager.release(selector);
            }
        }

    }
    
    
    /**
     * @return Returns the groupManager.
     */
    protected GroupManager getGroupManager() {
        return this.groupManager;
    }
    /**
     * @return Returns the ipRangeManager.
     */
    protected IPRangeManager getIpRangeManager() {
        return this.ipRangeManager;
    }
    /**
     * @return Returns the roleManager.
     */
    protected RoleManager getRoleManager() {
        return this.roleManager;
    }
    /**
     * @return Returns the userManager.
     */
    protected UserManager getUserManager() {
        return this.userManager;
    }
    /**
     * @return Returns the accessController.
     */
    protected AccessController getAccessController() {
        return this.accessController;
    }
}
