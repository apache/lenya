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

/* $Id$  */

package org.apache.lenya.ac.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Credential;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.InheritingPolicyManager;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;
import org.apache.lenya.util.Assert;

/**
 * A policy at a certain URL. The final policy is computed by merging the
 * subtree policies of all ancestor-or-self directories with the URL policy of
 * the actual URL.
 */
public class URLPolicy implements Policy {

    /**
     * Returns the resulting policy for a certain URL.
     * 
     * @param controller
     *            The acccess controller.
     * @param _url
     *            The URL.
     * @param manager
     *            The policy manager.
     */
    public URLPolicy(AccreditableManager controller, String _url,
            InheritingPolicyManager manager) {
        assert _url != null;
        this.url = _url;

        assert manager != null;
        this.policyManager = manager;

        assert controller != null;
        this.accreditableManager = controller;
    }

    private String url;

    private InheritingPolicyManager policyManager;

    private AccreditableManager accreditableManager;

    private Policy[] policies = null;

    private Credential[] credentials = null;

    /**
     * Obtains the policies from the policy manager. This method is expensive
     * and therefore only called when needed.
     * 
     * @throws AccessControlException
     *             when something went wrong.
     */
    protected void obtainPolicies() throws AccessControlException {
        if (this.policies == null) {
            this.policies = getPolicyManager().getPolicies(
                    getAccreditableManager(), getUrl());
        }
    }

    /**
     * Obtains the credentials from the policy manager. This method is expensive
     * and therefore only called when needed.
     * 
     * @throws AccessControlException
     *             when something went wrong.
     */
    protected void obtainCredentials() throws AccessControlException {
        if (this.credentials == null) {
            this.credentials = getPolicyManager().getCredentials(
                    getAccreditableManager(), getUrl());
        }
    }

    static final String[] VISITOR_ROLES = { "visitor", "visit" };

    static final String[] ADMINISTRATOR_ROLES = { "administrator", "admin",
            "organize" };

    static final String[] AUTHOR_ROLES = { "author", "edit" };

    /**
     * @see org.apache.lenya.ac.Policy#check(org.apache.lenya.ac.Identity, org.apache.lenya.ac.Role)
     * Iterate the policy tree bottom-up.
     */
    public int check(Identity identity, Role role) throws AccessControlException {
        Assert.notNull("identity", identity);
        Assert.notNull("role", role);
        obtainPolicies();
        
        for (int i = 0; i < this.policies.length; i++) {
            int result = this.policies[i].check(identity, role);
            if (result == Policy.RESULT_GRANTED || result == Policy.RESULT_DENIED) {
                return result;
            }
        }
        return Policy.RESULT_NOT_MATCHED;
    }

    /**
     * Returns the visitor role.
     * 
     * @param manager
     *            The accreditable manager.
     * @return A role.
     * @throws AccessControlException
     *             when something went wrong.
     */
    public static Role getVisitorRole(AccreditableManager manager)
            throws AccessControlException {
        Role visitorRole = null;
        for (int i = 0; i < VISITOR_ROLES.length; i++) {
            Role role = manager.getRoleManager().getRole(VISITOR_ROLES[i]);
            if (role != null) {
                visitorRole = role;
            }
        }
        return visitorRole;
    }

    /**
     * Returns the administrator role.
     * 
     * @param manager
     *            The accreditable manager.
     * @return A role.
     * @throws AccessControlException
     *             when something went wrong.
     */
    public static Role getAdministratorRole(AccreditableManager manager)
            throws AccessControlException {
        Role administratorRole = null;
        for (int i = 0; i < ADMINISTRATOR_ROLES.length; i++) {
            Role role = manager.getRoleManager()
                    .getRole(ADMINISTRATOR_ROLES[i]);
            if (role != null) {
                administratorRole = role;
            }
        }
        return administratorRole;
    }

    /**
     * Returns the author role.
     * 
     * @param manager
     *            The accreditable manager.
     * @return A role.
     * @throws AccessControlException
     *             when something went wrong.
     */
    public static Role getAuthorRole(AccreditableManager manager)
            throws AccessControlException {
        Role administratorRole = null;
        for (int i = 0; i < AUTHOR_ROLES.length; i++) {
            Role role = manager.getRoleManager().getRole(AUTHOR_ROLES[i]);
            if (role != null) {
                administratorRole = role;
            }
        }
        return administratorRole;
    }

    /**
     * Returns the URL of this policy.
     * 
     * @return The URL of this policy.
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Returns the policy builder.
     * 
     * @return A policy builder.
     */
    public InheritingPolicyManager getPolicyManager() {
        return this.policyManager;
    }

    /**
     * Returns the access controller.
     * 
     * @return An access controller.
     */
    public AccreditableManager getAccreditableManager() {
        return this.accreditableManager;
    }

    /**
     * The URL policy requires SSL protection if one of its member policies
     * requires SSL protection.
     * 
     * @see org.apache.lenya.ac.Policy#isSSLProtected()
     */
    public boolean isSSLProtected() throws AccessControlException {
        obtainPolicies();

        boolean ssl = false;

        int i = 0;
        while (!ssl && i < this.policies.length) {
            ssl = ssl || this.policies[i].isSSLProtected();
            i++;
        }

        return ssl;
    }

    /**
     * @see org.apache.lenya.ac.Policy#isEmpty()
     */
    public boolean isEmpty() throws AccessControlException {
        boolean empty = true;

        int i = 0;
        while (empty && i < this.policies.length) {
            empty = empty && this.policies[i].isEmpty();
            i++;
        }

        return empty;
    }

    public Credential[] getCredentials() throws AccessControlException {
        obtainCredentials();
        Set credentials = new LinkedHashSet();

        for (int accrIndex = 0; accrIndex < this.credentials.length; accrIndex++) {
            credentials.add(this.credentials[accrIndex]);
        }
        return (Credential[]) credentials.toArray(new Credential[credentials
                .size()]);
    }

    public Credential[] getCredentials(Identity identity)
            throws AccessControlException {
        Accreditable[] accreditables = identity.getAccreditables();
        Credential[] credentials = getCredentials();
        Set returnCredential = new LinkedHashSet();

        for (int credIndex = 0; credIndex < credentials.length; credIndex++) {
            Credential credential = credentials[credIndex];
            
            for (int accrIndex = 0; accrIndex < accreditables.length; accrIndex++) {
                Accreditable accreditable = accreditables[accrIndex];

                if (credential.getAccreditable().equals(accreditable)) {
                    returnCredential.add(credential);
                }
            }
        }

        return (Credential[]) returnCredential
                .toArray(new Credential[returnCredential.size()]);
    }

}
