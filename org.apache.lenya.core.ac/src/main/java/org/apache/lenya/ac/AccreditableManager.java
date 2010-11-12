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

package org.apache.lenya.ac;

import org.apache.avalon.framework.component.Component;

/**
 * An AccreditableManager combines a UserManager, a GroupManager, an IPRangeManager and a
 * RoleManager.
 * @version $Id$
 */
public interface AccreditableManager extends Component {

    /**
     * Avalon role.
     */
    String ROLE = AccreditableManager.class.getName();

    /**
     * Returns the user manager of this access controller.
     * @return A user manager.
     * @throws AccessControlException when something went wrong.
     */
    UserManager getUserManager() throws AccessControlException;

    /**
     * Returns the group manager of this access controller.
     * @return A group manager.
     * @throws AccessControlException when something went wrong.
     */
    GroupManager getGroupManager() throws AccessControlException;

    /**
     * Returns the role manager of this access controller.
     * @return A role manager.
     * @throws AccessControlException when something went wrong.
     */
    RoleManager getRoleManager() throws AccessControlException;

    /**
     * Returns the IP range manager of this access controller.
     * @return An IP range manager.
     * @throws AccessControlException when something went wrong.
     */
    IPRangeManager getIPRangeManager() throws AccessControlException;

    /**
     * Attaches an item manager listener to this accreditable manager.
     * @param listener An item manager listener.
     */
    void addItemManagerListener(ItemManagerListener listener);

    /**
     * Removes an item manager listener from this accreditable manager.
     * @param listener An item manager listener.
     */
    void removeItemManagerListener(ItemManagerListener listener);
    
    /**
     * @return A source URI to store configuration sources. This URI
     * must point to a collection.
     */
    String getConfigurationCollectionUri();

    /**
     * @return The unique ID of this accreditable manager. It is used to check
     * if two accreditables belong to the same accreditable manager.
     */
    String getId();

}