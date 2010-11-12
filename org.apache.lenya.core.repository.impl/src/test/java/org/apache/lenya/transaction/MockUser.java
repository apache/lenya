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
package org.apache.lenya.transaction;

import java.io.File;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.ItemManager;
import org.apache.lenya.ac.User;

public class MockUser implements User {
    
    private String id;
    
    public MockUser(String id) {
        this.id = id;
    }

    public boolean authenticate(String password) {
        // TODO Auto-generated method stub
        return false;
    }

    public void delete() throws AccessControlException {
        // TODO Auto-generated method stub
        
    }

    public String getDefaultDocumentLocale() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDefaultMenuLocale() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getEmail() {
        // TODO Auto-generated method stub
        return null;
    }

    public void save() throws AccessControlException {
        // TODO Auto-generated method stub
        
    }

    public void setDefaultDocumentLocale(String documentLocale) {
        // TODO Auto-generated method stub
        
    }

    public void setDefaultMenuLocale(String menuLocale) {
        // TODO Auto-generated method stub
        
    }

    public void setEmail(String email) {
        // TODO Auto-generated method stub
        
    }

    public void setPassword(String plainTextPassword) {
        // TODO Auto-generated method stub
        
    }

    public Accreditable[] getAccreditables() {
        // TODO Auto-generated method stub
        return null;
    }

    public void configure(Configuration configuration) throws ConfigurationException {
        // TODO Auto-generated method stub
        
    }

    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setConfigurationDirectory(File configurationDirectory) {
        // TODO Auto-generated method stub
        
    }

    public void setDescription(String description) {
        // TODO Auto-generated method stub
        
    }

    public void setName(String name) {
        // TODO Auto-generated method stub
        
    }

    public void enableLogging(Logger arg0) {
        // TODO Auto-generated method stub
        
    }

    public void addedToGroup(Group group) {
        // TODO Auto-generated method stub
        
    }

    public Group[] getGroups() {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeFromAllGroups() {
        // TODO Auto-generated method stub
        
    }

    public void removedFromGroup(Group group) {
        // TODO Auto-generated method stub
        
    }

    public AccreditableManager getAccreditableManager() {
        // TODO Auto-generated method stub
        return null;
    }

    public ItemManager getItemManager() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean canChangePassword() {
        // TODO Auto-generated method stub
        return false;
    }

}
