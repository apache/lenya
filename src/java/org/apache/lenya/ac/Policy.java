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
 * A policy assigns roles to accreditables using credentials.
 * Additionally, SSL protection is defined.
 * 
 * @version $Id$
 */
public interface Policy {
    
    /**
     * The identity was not matched in this policy.
     */
    int RESULT_NOT_MATCHED = 0;
    
    /**
     * The role is denied for the identity.
     */
    int RESULT_DENIED = 1;
    
    /**
     * The role is granted for the identity.
     */
    int RESULT_GRANTED = 2;
    
    /**
     * Checks if a certain role is granted for a certain policy.
     * @param identity The identity.
     * @param role The role to check.
     * @return A result code.
     * @throws AccessControlException when something went wrong.
     */
    int check(Identity identity, Role role) throws AccessControlException;
    
    /**
     * Returns if this policy requires SSL protection.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    boolean isSSLProtected() throws AccessControlException;
    
    /**
     * Returns if the policy is empty. A policy is empty if it does
     * not contain any credentials.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    boolean isEmpty() throws AccessControlException;
    
    /**
     * @param identity The identity.
     * @return All credentials defined by this policy for this identity.
     * @throws AccessControlException if an error occurs.
     */
    Credential[] getCredentials(Identity identity) throws AccessControlException;
    
    /**
     * @return All credentials defined by this policy.
     * @throws AccessControlException if an error occurs.
     */
    Credential[] getCredentials() throws AccessControlException;

}
