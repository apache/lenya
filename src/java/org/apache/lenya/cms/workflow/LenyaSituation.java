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

/* $Id: CMSSituation.java 157324 2005-03-13 09:41:14Z andreas $  */

package org.apache.lenya.cms.workflow;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.workflow.Situation;

import sun.security.action.GetLongAction;

/**
 * The CMS situation
 */
public class LenyaSituation extends AbstractLogEnabled implements Situation {

    /**
     * Returns the machine IP address.
     * @return A string.
     */
    public String getMachineIp() {
        return this.machineIp;
    }

    /**
     * Returns the user ID.
     * @return A string.
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Creates a new instance of Situation
     * @param manager The service manager.
     */
    protected LenyaSituation(Identity identity, ServiceManager manager, Logger logger) {
        this.manager = manager;
        this.identity = identity;
        ContainerUtil.enableLogging(this, logger);
    }

    private Identity identity;

    private ServiceManager manager;

    protected ServiceManager getServiceManager() {
        return this.manager;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.userId + " " + this.machineIp;
    }

    private String userId;
    private String machineIp;

    public Identity getIdentity() {
        return this.identity;
    }
}
