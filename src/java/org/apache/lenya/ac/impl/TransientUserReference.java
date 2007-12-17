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

import java.util.HashSet;
import java.util.Set;

import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserReference;
import org.apache.lenya.util.Assert;

public class TransientUserReference extends UserReference {
    
    private TransientUser user;
    
    public TransientUserReference(TransientUser user) {
        super(user.getId(), "");
        Assert.notNull(user);
        this.user = user;
    }

    public Accreditable[] getAccreditables(AccreditableManager accrMgr) {
        Set accrs = new HashSet();
        accrs.addAll(getMatchingGroups(accrMgr, this.user));
        return (Accreditable[]) accrs.toArray(new Accreditable[accrs.size()]);
    }

    public boolean canGetUserFrom(AccreditableManager accrMgr) {
        return true;
    }

    public User getUser(AccreditableManager accrMgr) {
        return this.user;
    }

}
