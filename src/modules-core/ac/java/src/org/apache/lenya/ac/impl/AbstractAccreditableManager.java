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

/* $Id$  */

package org.apache.lenya.ac.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.GroupManager;
import org.apache.lenya.ac.IPRangeManager;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.ItemManagerListener;
import org.apache.lenya.ac.RoleManager;
import org.apache.lenya.ac.UserManager;

/**
 * Abstract base class for accreditable managers.
 */
public abstract class AbstractAccreditableManager
    extends AbstractLogEnabled
    implements AccreditableManager, ItemManagerListener {
    
    /**
     * @param logger The logger.
     */
    public AbstractAccreditableManager(Logger logger) {
        ContainerUtil.enableLogging(this, logger);
    }

    private UserManager userManager = null;
    private GroupManager groupManager = null;
    private IPRangeManager ipRangeManager = null;
    private RoleManager roleManager = null;

    private List itemManagerListeners = new ArrayList();

    /**
	 * Attaches an item manager listener to this accreditable manager.
	 * @param listener An item manager listener.
	 */
    public void addItemManagerListener(ItemManagerListener listener) {
        if (!this.itemManagerListeners.contains(listener)) {
            
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Adding listener: [" + listener + "]");
            }
            
            this.itemManagerListeners.add(listener);
        }
    }

    /**
	 * Removes an item manager listener from this accreditable manager.
	 * @param listener An item manager listener.
	 */
    public void removeItemManagerListener(ItemManagerListener listener) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Removing listener: [" + listener + "]");
        }
            
        this.itemManagerListeners.remove(listener);
    }

    /**
	 * Notifies the listeners that an item was added.
	 * @param item The item that was added.
	 * @throws AccessControlException when a notified listener threw this exception.
	 */
    protected void notifyAdded(Item item) throws AccessControlException {
        List clone = new ArrayList(this.itemManagerListeners);
        for (Iterator i = clone.iterator(); i.hasNext();) {
            ItemManagerListener listener = (ItemManagerListener) i.next();
            listener.itemAdded(item);
        }
    }

    /**
	 * Notifies the listeners that an item was removed.
	 * @param item The item that was removed.
	 * @throws AccessControlException when a notified listener threw this exception.
	 */
    protected void notifyRemoved(Item item) throws AccessControlException {
        List clone = new ArrayList(this.itemManagerListeners);
        for (Iterator i = clone.iterator(); i.hasNext();) {
            ItemManagerListener listener = (ItemManagerListener) i.next();
            listener.itemRemoved(item);
        }
    }

    /**
	 * @see org.apache.lenya.ac.ItemManagerListener#itemAdded(org.apache.lenya.ac.Item)
	 */
    public void itemAdded(Item item) throws AccessControlException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Item was added: [" + item + "] - notifying listeners");
        }
        notifyAdded(item);
    }

    /**
	 * @see org.apache.lenya.ac.ItemManagerListener#itemRemoved(org.apache.lenya.ac.Item)
	 */
    public void itemRemoved(Item item) throws AccessControlException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Item was removed: [" + item + "] - notifying listeners");
        }
        notifyRemoved(item);
    }

    /**
	 * @see org.apache.lenya.ac.AccreditableManager#getUserManager()
	 */
    public UserManager getUserManager() throws AccessControlException {
        if (this.userManager == null) {
            this.userManager = initializeUserManager();
            this.userManager.addItemManagerListener(this);
        }
        return this.userManager;
    }

    /**
	 * @see org.apache.lenya.ac.AccreditableManager#getGroupManager()
	 */
    public GroupManager getGroupManager() throws AccessControlException {
        if (this.groupManager == null) {
            this.groupManager = initializeGroupManager();
            this.groupManager.addItemManagerListener(this);
        }
        return this.groupManager;
    }

    /**
	 * @see org.apache.lenya.ac.AccreditableManager#getRoleManager()
	 */
    public RoleManager getRoleManager() throws AccessControlException {
        if (this.roleManager == null) {
            this.roleManager = initializeRoleManager();
            this.roleManager.addItemManagerListener(this);
        }
        return this.roleManager;
    }

    /**
	 * @see org.apache.lenya.ac.AccreditableManager#getIPRangeManager()
	 */
    public IPRangeManager getIPRangeManager() throws AccessControlException {
        if (this.ipRangeManager == null) {
            this.ipRangeManager = initializeIPRangeManager();
            this.ipRangeManager.addItemManagerListener(this);
        }
        return this.ipRangeManager;
    }

    /**
	 * Initializes the group manager.
	 * 
     * @return A group manager.
	 * @throws AccessControlException when something went wrong.
	 */
    protected abstract GroupManager initializeGroupManager() throws AccessControlException;

    /**
	 * Initializes the IP range manager.
	 * 
     * @return An IP range manager.
	 * @throws AccessControlException when something went wrong.
	 */
    protected abstract IPRangeManager initializeIPRangeManager() throws AccessControlException;

    /**
	 * Initializes the role manager.
	 * 
     * @return A role manager.
	 * @throws AccessControlException when something went wrong.
	 */
    protected abstract RoleManager initializeRoleManager() throws AccessControlException;

    /**
	 * Initializes the user manager.
	 * 
     * @return A user manager.
	 * @throws AccessControlException when something went wrong.
	 */
    protected abstract UserManager initializeUserManager() throws AccessControlException;

}
