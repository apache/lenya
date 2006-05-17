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

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.PolicyManager;

/**
 * Policy manager which inherits credentials in a policy tree.
 * @version $Id$
 */
public interface InheritingPolicyManager extends PolicyManager {

    /**
     * Builds the URL policy for a URL from a file. When the file is not present, an empty policy is
     * returned.
     * @param controller The access controller to use.
     * @param url The URL inside the web application.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    Policy buildURLPolicy(AccreditableManager controller, String url) throws AccessControlException;

    /**
     * Builds a subtree policy from a file. When the file is not present, an empty policy is
     * returned.
     * @param controller The access controller to use.
     * @param url The URL inside the web application.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    Policy buildSubtreePolicy(AccreditableManager controller, String url)
            throws AccessControlException;

    /**
     * Returns the existing policies for all URL steps, beginning with the root policy.
     * @param controller The accreditable manager to use.
     * @param url The URL inside the web application.
     * @return An array of policies.
     * @throws AccessControlException when something went wrong.
     */
    Policy[] getPolicies(AccreditableManager controller, String url) throws AccessControlException;

    /**
     * Saves a URL policy.
     * @param url The URL to save the policy for.
     * @param policy The policy to save.
     * @throws AccessControlException when something went wrong.
     */
    void saveURLPolicy(String url, Policy policy) throws AccessControlException;

    /**
     * Saves a Subtree policy.
     * @param url The url to save the policy for.
     * @param policy The policy to save.
     * @throws AccessControlException when something went wrong.
     */
    void saveSubtreePolicy(String url, Policy policy) throws AccessControlException;

}
