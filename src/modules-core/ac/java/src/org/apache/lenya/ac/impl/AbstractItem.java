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

package org.apache.lenya.ac.impl;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.ItemManager;
import org.apache.lenya.ac.ItemUtil;
import org.apache.lenya.util.Assert;

/**
 * Abstract superclass for all access control objects that can be managed by an
 * {@link org.apache.lenya.ac.ItemManager}. It is only used for code reuse.
 * @version $Id$
 */
public abstract class AbstractItem extends AbstractLogEnabled implements Item, Comparable {

    private String id;
    private String description = "";
    private String name = "";

    private ItemManager itemManager;

    /**
     * Ctor.
     * @param itemManager The item manager this item belongs to.
     * @param logger The logger.
     */
    public AbstractItem(ItemManager itemManager, Logger logger) {
        Assert.notNull("item manager", itemManager);
        this.itemManager = itemManager;

        Assert.notNull("logger", logger);
        ContainerUtil.enableLogging(this, logger);
    }

    /**
     * @return The accreditable manager.
     */
    public AccreditableManager getAccreditableManager() {
        return getItemManager().getAccreditableManager();
    }

    /**
     * Sets the ID.
     * @param string The ID.
     */
    protected void setId(String string) {
        assert ItemUtil.isValidId(string);
        this.id = string;
    }

    /**
     * Returns the ID.
     * @return The ID.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the description of this object.
     * @return A string.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of this object.
     * @param _description A string.
     */
    public void setDescription(String _description) {
        assert _description != null;
        this.description = _description;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getId();

    }

    /**
     * Returns the name of this object.
     * @return A <code>String</code>.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the full name
     * @param _name the new full name
     */
    public void setName(String _name) {
        assert _name != null;
        this.name = _name;
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object otherObject) {
        boolean equals = false;

        if (getClass().isInstance(otherObject)) {
            AbstractItem otherManageable = (AbstractItem) otherObject;
            String thisMgrId = getAccreditableManager().getId();
            String otherMgrId = otherManageable.getAccreditableManager().getId();
            equals = getId().equals(otherManageable.getId()) && thisMgrId.equals(otherMgrId);
        }
        return equals;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getId().hashCode();
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj) {
        if (obj instanceof AbstractItem) {
            return getId().compareTo(((AbstractItem) obj).getId());
        }
        return 0;
    }

    public ItemManager getItemManager() {
        return this.itemManager;
    }

}