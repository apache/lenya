/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.ac2;

import junit.textui.TestRunner;

import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac2.AccessControlTest;
import org.apache.lenya.cms.ac2.file.FileAccessController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


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

    protected static final String URL = "/authoring/index.html";
    protected static final String SAVE_URL = "/authoring/tutorial.html";

    /**
     * A test.
     * @throws AccessControlException when something went wrong.
     */
    public void testLoadPolicy() throws AccessControlException {
        String url = "/" + PublicationHelper.getPublication().getId() + URL;
        Policy policy = getPolicy(url);
        Role[] roles = policy.getRoles(getIdentity());
        System.out.print("Roles: ");

        for (int i = 0; i < roles.length; i++) {
            System.out.print(roles[i]);
        }

        System.out.println();
    }

    /**
     * Returns the policy for a URL.
     * @param url The URL.
     * @return The policy.
     * @throws AccessControlException when something went wrong.
     */
    protected Policy getPolicy(String url) throws AccessControlException {
        Policy policy = ((FileAccessController) getAccessController()).getPolicy(PublicationHelper.getPublication(),
                url);

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

        Credential[] credentials = urlPolicy.getCredentials();

        for (int i = 0; i < credentials.length; i++) {
            Credential credential = new Credential(credentials[i].getAccreditable());
            Role[] roles = credentials[i].getRoles();

            for (int j = 0; j < roles.length; j++) {
                credential.addRole(roles[j]);
            }

            newPolicy.addCredential(credential);
        }

        assertEquals(urlPolicy.getCredentials().length, newPolicy.getCredentials().length);

        getAccessController().getPolicyManager().saveURLPolicy(SAVE_URL, newPolicy);

        newPolicy = manager.buildURLPolicy(SAVE_URL);
        assertEquals(urlPolicy.getCredentials().length, newPolicy.getCredentials().length);

        Credential[] newCredentials = newPolicy.getCredentials();

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
