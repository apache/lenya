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

/**
 * A user type to be supported by the UserManager
 * Note: the types are configured through access control (ac.xconf)
 * 
 * @version $Id:$
 */
public class UserType  {
    private String key;
    private String className;
    private String createUseCase;

    /**
     * Ctor.
     */
    public UserType() {
    }

    /**
     * Ctor.
     * 
     * @param key The key.
     * @param className The class name.
     * @param createUseCase The create usecase.
     */
    public UserType(String key, String className, String createUseCase) {
	  this.key = key;
	  this.className = className;
	  this.createUseCase = createUseCase;
    }

    /**
     * Get the key to be used for this type. 
     * This key can be used for a dictionary entry for the user interface.
     *
     * @return a <code>String</code>
     */
    public String getKey() {
	  return key;
    }
    
    /**
     * Set the key to be used for this type. 
     * This key can be used for a dictionary entry for the user interface.
     *
     * @param key the new key
     */
    public void setKey(String key) {
	  this.key = key;
    }
    
    /**
     * Get the name of the class responsible for implementing this
     * type of user. Note that in current version, this field is for
     * information only; in later versions, it might be used for
     * introspection and dynamic script creation.
     *
     * @see org.apache.lenya.ac.file.FileUser
     * @see org.apache.lenya.ac.ldap.LDAPUser
     * @return a <code>String</code> the name of the class
     */
    public String getClassName() {
	  return className;
    }
    
    /**
     * Set the name of the class responsible for implementing this
     * type of user. Note that in current version, this field is for
     * information only; in later versions, it might be used for
     * introspection and dynamic flowscript creation.
     *
     * @param className the new className
     */
    public void setClassName(String className) {
	  this.className = className;
    }
    
    /**
     * Get the createUseCase name to be used when a user of this type
     * is to be created. 
     * This name will be used in the flowscript (currently: user-admin.js),
     * in order to distinguish between different types.
     *
     * @return a <code>String</code> the name of the use case in the flowscript
     */
    public String getCreateUseCase() {
	  return createUseCase;
    }
    
    /**
     * Set the createUseCase name to be used when a user of this type
     * is to be created. 
     * This name will be used in the flowscript (currently: user-admin.js),
     * in order to distinguish between different types.
     *
     * @param createUseCase the new createUseCase
     */
    public void setCreateUseCase(String createUseCase) {
	  this.createUseCase = createUseCase;
    }
    
}
