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

import junit.textui.TestRunner;

import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.AccessControlTest;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.ac2.file.FileAccessController;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.TestPageEnvelope;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PolicyTest extends AccessControlTest {

    /**
     * Executes this test.
     * @param test The test.
     */
    public PolicyTest(String test) {
        super(test);
    }

    /**
     * The main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        TestRunner.run(PolicyTest.class);
    }
    
    protected static final String URL = "index.html";
    protected static final String SAVE_URL = "tutorial.html";
    protected static final String USERNAME = "lenya";
    
    /**
     * A test.
     * @throws AccessControlException when something went wrong.
     */
    public void testLoadPolicy() throws AccessControlException {
        
        Policy policy = getPolicy(URL);
        Role roles[] = policy.getRoles(getIdentity());
        System.out.print("Roles: ");
        for (int i = 0; i < roles.length; i++) {
            System.out.print(roles[i]);
        }
        System.out.println();
    }
    
    /**
     * Returns the identity.
     * @return The identity.
     * @throws AccessControlException when something went wrong.
     */
    protected Identity getIdentity() throws AccessControlException {
        User user = getAccessController().getUserManager().getUser(USERNAME);
        assertNotNull(user);
        Identity identity = new Identity();
        identity.addIdentifiable(user);
        return identity;
    }
    
    /**
     * Returns the policy for a URL.
     * @param url The URL.
     * @return The policy.
     * @throws AccessControlException when something went wrong.
     */
    protected Policy getPolicy(String url) throws AccessControlException {
        PageEnvelope envelope;
        try {
            envelope = new TestPageEnvelope(PublicationHelper.getPublication(), url);
        } catch (PageEnvelopeException e) {
            throw new AccessControlException(e);
        }
        Policy policy = ((FileAccessController) getAccessController()).getPolicy(envelope);
        return policy;
    }
    
    /**
     * A test.
     * @throws AccessControlException when something went wrong.
     */
    public void testSavePolicy() throws AccessControlException {
        
        PolicyManager manager = getAccessController().getPolicyManager();
        DefaultPolicy urlPolicy = manager.buildURLPolicy(URL);
        DefaultPolicy newPolicy = new DefaultPolicy();
        
        Credential credentials[] = urlPolicy.getCredentials();
        for (int i = 0; i < credentials.length; i++) {
            Credential credential = new Credential(credentials[i].getAccreditable());
            Role roles[] = credentials[i].getRoles();
            for (int j = 0; j < roles.length; j++) {
                credential.addRole(roles[j]);
            }
            newPolicy.addCredential(credential);
        }
        
        assertEquals(urlPolicy.getCredentials().length, newPolicy.getCredentials().length);
        
        getAccessController().getPolicyManager().saveURLPolicy(SAVE_URL, newPolicy);
        
        newPolicy = manager.buildURLPolicy(SAVE_URL);
        assertEquals(urlPolicy.getCredentials().length, newPolicy.getCredentials().length);
        
        Credential newCredentials[] = newPolicy.getCredentials();
        for (int i = 0; i < credentials.length; i++) {
            Credential credential = new Credential(credentials[i].getAccreditable());

            Credential newCredential = null;            
            for (int k = 0; k < newCredentials.length; k++) {
                if (newCredentials[k].getAccreditable().equals(credential.getAccreditable())) {
                    newCredential = newCredentials[k];
                }
            }
            System.out.println("Accreditable: [" + credential.getAccreditable() + "]");
            assertNotNull(newCredential);
                
            Set oldRoles = new HashSet(Arrays.asList(credential.getRoles()));
            Set newRoles = new HashSet(Arrays.asList(newCredential.getRoles()));
            assertEquals(oldRoles, newRoles);
            /*
            for (int j = 0; j < roles.length; j++) {
                assertEquals(roles[j], newRoles[j]);
                System.out.println("  Role: [" + roles[j] + "]");
            }
            */
        }
    }
    
}
