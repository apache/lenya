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

/* $Id: AbstractGroup.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.ac.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Groupable;
import org.apache.lenya.ac.attr.AttributeOwner;
import org.apache.lenya.ac.attr.AttributeRule;
import org.apache.lenya.ac.attr.AttributeRuleEvaluator;
import org.apache.lenya.util.Assert;

/**
 * A group is a set of {@link Groupable}s.
 */
public abstract class AbstractGroup extends AbstractItem implements Accreditable, Group {
    /**
     * Creates a new group.
     */
    public AbstractGroup() {
    }

    /**
     * Creates a new group.
     * @param id The group ID.
     */
    public AbstractGroup(String id) {
        setId(id);
    }

    /**
     * Returns the members of this group.
     * @return An array of {@link Groupable}s.
     */
    public Groupable[] getMembers() {
        Set members = new HashSet();
        Set groupables = new HashSet();
        AccreditableManager accrMgr = getItemManager().getAccreditableManager();
        try {
            groupables.addAll(Arrays.asList(accrMgr.getUserManager().getUsers()));
            groupables.addAll(Arrays.asList(accrMgr.getIPRangeManager().getIPRanges()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (Iterator i = groupables.iterator(); i.hasNext();) {
            Groupable groupable = (Groupable) i.next();
            if (Arrays.asList(groupable.getGroups()).contains(this)) {
                members.add(groupable);
            }
        }
        return (Groupable[]) members.toArray(new Groupable[members.size()]);
    }

    /**
     * Adds a member to this group.
     * @param member The member to add.
     */
    public void add(Groupable member) {
        Assert.notNull("member", member);
        member.addedToGroup(this);
    }

    /**
     * Removes a member from this group.
     * @param member The member to remove.
     */
    public void remove(Groupable member) {
        Assert.notNull("member", member);
        member.removedFromGroup(this);
    }

    /**
     * Removes all members from this group.
     */
    public void removeAllMembers() {
        Groupable[] members = getMembers();
        for (int i = 0; i < members.length; i++) {
            remove(members[i]);
        }
    }

    public boolean contains(Groupable member) {
        return Arrays.asList(getMembers()).contains(member);
    }

    public boolean matches(AttributeOwner user) {
        AttributeRule rule = getRule();
        return rule == null ? false : rule.matches(user);
    }

    protected AttributeRuleEvaluator getAttributeRuleEvaluator() {
        return getItemManager().getAccreditableManager().getAttributeManager().getEvaluator();
    }

    /**
     * Delete a group
     * 
     * @throws AccessControlException if the delete failed
     */
    public void delete() throws AccessControlException {
        Groupable[] members = getMembers();
        for (int i = 0; i < members.length; i++) {
            remove(members[i]);
        }
    }

    private AttributeRule rule;

    public void setRule(AttributeRule rule) {
        this.rule = rule;
    }

    public AttributeRule getRule() {
        return this.rule;
    }

}
