/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
 * FIXME: jwkaltz@yahoo.com please add parameter descriptions to getters and setters.
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
     * Get the key
     *
     * @return a <code>String</code>
     */
    public String getKey() {
	  return key;
    }
    
    /**
     * Set the key
     *
     * @param key the new key
     */
    public void setKey(String key) {
	  this.key = key;
    }
    
    /**
     * Get the className
     *
     * @return a <code>String</code>
     */
    public String getClassName() {
	  return className;
    }
    
    /**
     * Set the className
     *
     * @param className the new className
     */
    public void setClassName(String className) {
	  this.className = className;
    }
    
    /**
     * Get the createUseCase
     *
     * @return a <code>String</code>
     */
    public String getCreateUseCase() {
	  return createUseCase;
    }
    
    /**
     * Set the createUseCase
     *
     * @param createUseCase the new createUseCase
     */
    public void setCreateUseCase(String createUseCase) {
	  this.createUseCase = createUseCase;
    }
    
}
