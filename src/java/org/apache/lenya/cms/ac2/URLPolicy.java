/*
 * $Id
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.ac2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.Role;

/**
 * A policy at a certain URL.
 * @author andreas
 */
public class URLPolicy implements Policy {
    
    /**
     * Returns the resulting policy for a certain URL.
     * @param url The URL.
     * @param manager The policy manager.
     */
    public URLPolicy(String url, PolicyManager manager) {
        assert url != null;
        this.url = url;
        
        assert manager != null;
        policyManager = manager;
    }
    
    private String url;
    private PolicyManager policyManager;

    /**
     * @see org.apache.lenya.cms.ac2.Policy#getRoles(org.apache.lenya.cms.ac2.Identity)
     */
    public Role[] getRoles(Identity identity) throws AccessControlException {
        Set roles = new HashSet();
        Policy urlPolicy = getPolicyManager().buildURLPolicy(getUrl());
        addRoles(urlPolicy, identity, roles);
        
        String url = "";
        String[] directories = url.split("/");
        for (int i = 0; i < directories.length; i++) {
            
        }
        addRoles(identity, url, roles);
        return (Role[]) roles.toArray(new Role[roles.size()]);
    }
    
    /**
     * Adds roles of a certain URL to a set.
     * @param identity The identity.
     * @param url The URL.
     * @param roles The roles set.
     * @throws AccessControlException when something went wrong.
     */
    protected void addRoles(Identity identity, String url, Set roles) throws AccessControlException {
        addRoles(getPolicyManager().buildSubtreePolicy(url), identity, roles);
    }
    
    /**
     * Adds the roles of an identity of a policy to a role set.
     * @param policy The policy.
     * @param identity The identity.
     * @param roles The role set.
     * @throws AccessControlException when something went wrong.
     */
    protected void addRoles(Policy policy, Identity identity, Set roles) throws AccessControlException {
        roles.addAll(Arrays.asList(policy.getRoles(identity)));
    }

    /**
     * Returns the URL of this policy.
     * @return The URL of this policy.
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Returns the policy builder.
     * @return A policy builder.
     */
    public PolicyManager getPolicyManager() {
        return policyManager;
    }

}
