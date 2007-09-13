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
 * @version $Id: Policy.java 473841 2006-11-12 00:46:38Z gregor $
 */
public interface Policy {
    /**
     * Returns all roles of a certain identity.
     * @param identity The identity.
     * @return An array of roles.
     * @throws AccessControlException when something went wrong.
     */
    Role[] getRoles(Identity identity) throws AccessControlException;
    
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
     * @param role The role.
     * @return All accreditables wich are granted this role.
     */
    Accreditable[] getAccreditables(Role role);
}
