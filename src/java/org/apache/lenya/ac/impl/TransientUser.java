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

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.ItemManager;

/**
 * Class for users which are not stored in the CMS, but in an external directory
 * like LDAP.
 */
public class TransientUser extends AbstractUser {

    public void save() throws AccessControlException {
        throw new UnsupportedOperationException();
    }

    public void configure(Configuration configuration) throws ConfigurationException {
        throw new UnsupportedOperationException();
    }

    public void setConfigurationDirectory(File configurationDirectory) {
    }

    public boolean authenticate(String password) {
        return false;
    }
    
    private String id;

    public String getId() {
        return this.id;
    }

    public void setId(String string) {
        this.id = string;
    }

    public boolean isPersistent() {
        return false;
    }

    public void delete() throws AccessControlException {
        throw new UnsupportedOperationException();
    }

    public ItemManager getItemManager() {
        throw new UnsupportedOperationException();
    }

    public void setItemManager(ItemManager manager) {
        throw new UnsupportedOperationException();
    }

    public void addedToGroup(Group group) {
        throw new UnsupportedOperationException();
    }

    public Group[] getGroups() {
        throw new UnsupportedOperationException();
    }

    public void removeFromAllGroups() {
        throw new UnsupportedOperationException();
    }

    public void removedFromGroup(Group group) {
        throw new UnsupportedOperationException();
    }

    public Accreditable[] getAccreditables() {
        Set accrs = new HashSet();
        accrs.add(this);

        Group[] groups = getRuleGroups();
        for (int i = 0; i < groups.length; i++) {
            Accreditable[] groupAccreditables = groups[i].getAccreditables();
            accrs.addAll(Arrays.asList(groupAccreditables));
        }

        return (Accreditable[]) accrs.toArray(new Accreditable[accrs.size()]);
    }

}
