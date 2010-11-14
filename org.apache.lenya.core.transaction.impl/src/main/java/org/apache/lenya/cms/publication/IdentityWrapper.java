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
package org.apache.lenya.cms.publication;

import org.apache.lenya.transaction.Identity;

public class IdentityWrapper implements Identity {
    
    private org.apache.lenya.ac.Identity identity;
    
    public IdentityWrapper(org.apache.lenya.ac.Identity identity) {
        this.setIdentity(identity);
    }

    public void setIdentity(org.apache.lenya.ac.Identity identity) {
        this.identity = identity;
    }

    public org.apache.lenya.ac.Identity getIdentity() {
        return identity;
    }

    public String getUserId() {
        return getIdentity().getUser().getId();
    }
    
}
