/*
 * $Id: AbstractAccreditableManager.java,v 1.2 2004/02/05 08:50:57 andreas Exp $ <License>
 * 
 * ============================================================================ The Apache Software
 * License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica- tion, are permitted
 * provided that the following conditions are met: 1. Redistributions of source code must retain
 * the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)." Alternately, this acknowledgment may
 * appear in the software itself, if and wherever such third-party acknowledgments normally appear. 4.
 * The names "Apache Lenya" and "Apache Software Foundation" must not be used to endorse or promote
 * products derived from this software without prior written permission. For written permission,
 * please contact apache@apache.org. 5. Products derived from this software may not be called
 * "Apache", nor may "Apache" appear in their name, without prior written permission of the Apache
 * Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU- DING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation and was originally created by Michael Wechner <michi@apache.org> .
 * For more information on the Apache Soft- ware Foundation, please see <http://www.apache.org/> .
 * 
 * Lenya includes software developed by the Apache Software Foundation, W3C, DOM4J Project,
 * BitfluxEditor, Xopus, and WebSHPINX. </License>
 */
package org.apache.lenya.ac.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
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
 * 
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public abstract class AbstractAccreditableManager
    extends AbstractLogEnabled
    implements AccreditableManager, ItemManagerListener, Disposable {

    private UserManager userManager = null;
    private GroupManager groupManager = null;
    private IPRangeManager ipRangeManager = null;
    private RoleManager roleManager = null;

    private List itemManagerListeners = new ArrayList();

    /**
	 * Attaches an item manager listener to this accreditable manager.
	 * 
	 * @param listener An item manager listener.
	 */
    public void addItemManagerListener(ItemManagerListener listener) {
        if (!itemManagerListeners.contains(listener)) {
            
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Adding listener: [" + listener + "]");
            }
            
            itemManagerListeners.add(listener);
        }
    }

    /**
	 * Removes an item manager listener from this accreditable manager.
	 * 
	 * @param listener An item manager listener.
	 */
    public void removeItemManagerListener(ItemManagerListener listener) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Removing listener: [" + listener + "]");
        }
            
        itemManagerListeners.remove(listener);
    }

    /**
	 * Notifies the listeners that an item was added.
	 * 
	 * @param item The item that was added.
	 * @throws AccessControlException when a notified listener threw this exception.
	 */
    protected void notifyAdded(Item item) throws AccessControlException {
        List clone = new ArrayList(itemManagerListeners);
        for (Iterator i = clone.iterator(); i.hasNext();) {
            ItemManagerListener listener = (ItemManagerListener) i.next();
            listener.itemAdded(item);
        }
    }

    /**
	 * Notifies the listeners that an item was removed.
	 * 
	 * @param item The item that was removed.
	 * @throws AccessControlException when a notified listener threw this exception.
	 */
    protected void notifyRemoved(Item item) throws AccessControlException {
        
        List clone = new ArrayList(itemManagerListeners);
        for (Iterator i = clone.iterator(); i.hasNext();) {
            ItemManagerListener listener = (ItemManagerListener) i.next();
            listener.itemRemoved(item);
        }
    }

    /**
	 * @see org.apache.lenya.cms.ac.ItemManagerListener#itemAdded(org.apache.lenya.cms.ac.Item)
	 */
    public void itemAdded(Item item) throws AccessControlException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Item was added: [" + item + "] - notifying listeners");
        }
        notifyAdded(item);
    }

    /**
	 * @see org.apache.lenya.cms.ac.ItemManagerListener#itemRemoved(org.apache.lenya.cms.ac.Item)
	 */
    public void itemRemoved(Item item) throws AccessControlException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Item was removed: [" + item + "] - notifying listeners");
        }
        notifyRemoved(item);
    }

    /**
	 * @see org.apache.avalon.framework.activity.Disposable#dispose()
	 */
    public void dispose() {
        if (userManager != null) {
            userManager.removeItemManagerListener(this);
        }
        if (groupManager != null) {
            groupManager.removeItemManagerListener(this);
        }
        if (ipRangeManager != null) {
            this.ipRangeManager.removeItemManagerListener(this);
        }
        if (roleManager != null) {
            this.roleManager.removeItemManagerListener(this);
        }
        
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Disposing [" + this + "]");
        }
    }

    /**
	 * @see org.apache.lenya.cms.ac2.AccreditableManager#getUserManager()
	 */
    public UserManager getUserManager() throws AccessControlException {
        if (userManager == null) {
            userManager = initializeUserManager();
            userManager.addItemManagerListener(this);
        }
        return userManager;
    }

    /**
	 * @see org.apache.lenya.cms.ac2.AccreditableManager#getGroupManager()
	 */
    public GroupManager getGroupManager() throws AccessControlException {
        if (groupManager == null) {
            groupManager = initializeGroupManager();
            groupManager.addItemManagerListener(this);
        }
        return groupManager;
    }

    /**
	 * @see org.apache.lenya.cms.ac2.AccreditableManager#getRoleManager()
	 */
    public RoleManager getRoleManager() throws AccessControlException {
        if (roleManager == null) {
            roleManager = initializeRoleManager();
            roleManager.addItemManagerListener(this);
        }
        return roleManager;
    }

    /**
	 * @see org.apache.lenya.cms.ac2.AccreditableManager#getIPRangeManager()
	 */
    public IPRangeManager getIPRangeManager() throws AccessControlException {
        if (ipRangeManager == null) {
            ipRangeManager = initializeIPRangeManager();
            ipRangeManager.addItemManagerListener(this);
        }
        return ipRangeManager;
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
