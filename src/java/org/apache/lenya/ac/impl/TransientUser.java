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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.User;

/**
 * Class for users which are not stored in the CMS, but in an external directory
 * like LDAP or a Shibboleth identity provider.
 */
public class TransientUser implements User {

    /**
     * @param id The user ID.
     */
    public TransientUser(String id) {
        this.id = id;
    }

    private String id;
    
    public String getId() {
        return this.id;
    }

    private Map attributes = new HashMap();

    public String[] getAttributeValues(String name) throws AccessControlException {
        return (String[]) this.attributes.get(name);
    }

    public String[] getAttributeNames() {
        Set names = this.attributes.keySet();
        return (String[]) names.toArray(new String[names.size()]);
    }

    /**
     * Sets an attribute.
     * @param name The name.
     * @param values The values.
     * @throws AccessControlException if the attribute name is not supported.
     */
    public void setAttributeValues(String name, String[] values) throws AccessControlException {
        this.attributes.put(name, values);
    }

    protected boolean hasAttributes() {
        return !this.attributes.isEmpty();
    }
    
    private String email;
    private String description;
    private String name;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return this.description;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

}
