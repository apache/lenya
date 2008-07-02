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

/* $Id: FileGroupManager.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.ac.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.GroupManager;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.attr.AttributeRuleEvaluator;
import org.apache.lenya.ac.attr.AttributeRuleEvaluatorFactory;

/**
 * File-based group manager.
 */
public final class FileGroupManager extends FileItemManager implements GroupManager {

    private FileGroupManager(ServiceManager manager, AccreditableManager accreditableManager,
            Logger logger) throws AccessControlException {
        super(manager, accreditableManager, logger);
    }

    static final String SUFFIX = ".gml";

    private static Map instances = new HashMap();

    /**
     * Return the <code>GroupManager</code> for the given publication. The
     * <code>GroupManager</code> is a singleton.
     * @param manager The service manager.
     * @param accrMgr The accreditable manager the group manager belongs to.
     * @param logger The logger.
     * @return a <code>GroupManager</code>
     * @throws AccessControlException if no GroupManager could be instanciated
     */
    public static synchronized FileGroupManager instance(ServiceManager manager, FileAccreditableManager accrMgr,
            Logger logger) throws AccessControlException {
        File configDir = accrMgr.getConfigurationDirectory();

        if (!instances.containsKey(configDir)) {
            instances.put(configDir, new FileGroupManager(manager, accrMgr, logger));
        }

        return (FileGroupManager) instances.get(configDir);
    }

    /**
     * Get all groups
     * 
     * @return an array of groups.
     */
    public Group[] getGroups() {
        Item[] items = super.getItems();
        Group[] groups = new Group[items.length];
        for (int i = 0; i < groups.length; i++) {
            groups[i] = (Group) items[i];
        }
        return groups;
    }

    /**
     * Add a group to this manager
     * 
     * @param group the group to be added
     * @throws AccessControlException when the notification failed.
     */
    public void add(Group group) throws AccessControlException {
        super.add(group);
    }

    /**
     * Remove a group from this manager
     * 
     * @param group the group to be removed
     * @throws AccessControlException when the notification failed.
     */
    public void remove(Group group) throws AccessControlException {
        super.remove(group);
    }

    /**
     * Get the group with the given group name.
     * 
     * @param groupId the id of the requested group
     * @return a <code>Group</code> or null if there is no group with the
     *         given name
     */
    public Group getGroup(String groupId) {
        return (Group) getItem(groupId);
    }

    /**
     * @see org.apache.lenya.ac.file.FileItemManager#getSuffix()
     */
    protected String getSuffix() {
        return SUFFIX;
    }

    public boolean contains(String groupId) {
        return containsItem(groupId);
    }

    private AttributeRuleEvaluator evaluator;

    public AttributeRuleEvaluator getAttributeRuleEvaluator() {
        if (this.evaluator == null) {
            AttributeRuleEvaluatorFactory factory = null;
            try {
                factory = (AttributeRuleEvaluatorFactory) this.manager
                        .lookup(AttributeRuleEvaluatorFactory.ROLE);
                this.evaluator = factory.getEvaluator();
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            } finally {
                if (factory != null) {
                    this.manager.release(factory);
                }
            }
        }
        return this.evaluator;
    }

}
