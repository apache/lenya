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

import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.publication.Publication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A policy at a certain URL. The final policy is computed by merging the subtree
 * policies of all ancestor-or-self directories with the URL policy of the actual URL.  
 * @author andreas
 */
public class URLPolicy implements Policy {

	/**
	 * Returns the resulting policy for a certain URL.
	 * @param controller The acccess controller.
	 * @param publication The publication.
	 * @param url The URL.
	 * @param manager The policy manager.
	 */
	public URLPolicy(
		AccreditableManager controller,
		Publication publication,
		String url,
		PolicyManager manager) {
		assert url != null;
		this.url = url;

		assert manager != null;
		policyManager = manager;

		assert publication != null;
		this.publication = publication;

		assert controller != null;
		this.controller = controller;
	}

	private String url;
	private PolicyManager policyManager;
	private Publication publication;
	private AccreditableManager controller;

	/**
	 * @see org.apache.lenya.cms.ac2.Policy#getRoles(org.apache.lenya.cms.ac2.Identity)
	 */
	public Role[] getRoles(Identity identity) throws AccessControlException {
		Set roles = new HashSet();
		Policy urlPolicy =
			getPolicyManager().buildURLPolicy(
				getAccessController(),
				getPublication(),
				getUrl());
		addRoles(urlPolicy, identity, roles);

		String url = "";
		String[] directories = getUrl().split("/");

		for (int i = 0; i < directories.length; i++) {
			url += (directories[i] + "/");
			addRoles(identity, url, roles);
		}

		return (Role[]) roles.toArray(new Role[roles.size()]);
	}

	/**
	 * Adds roles of a certain URL to a set.
	 * @param identity The identity.
	 * @param url The URL.
	 * @param roles The roles set.
	 * @throws AccessControlException when something went wrong.
	 */
	protected void addRoles(Identity identity, String url, Set roles)
		throws AccessControlException {
		addRoles(
			getPolicyManager().buildSubtreePolicy(
				getAccessController(),
				getPublication(),
				url),
			identity,
			roles);
	}

	/**
	 * Adds the roles of an identity of a policy to a role set.
	 * @param policy The policy.
	 * @param identity The identity.
	 * @param roles The role set.
	 * @throws AccessControlException when something went wrong.
	 */
	protected void addRoles(Policy policy, Identity identity, Set roles)
		throws AccessControlException {
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
	/**
	 * 
	 * Returns the publication.
	 * @return The publication.
	 */
	public Publication getPublication() {
		return publication;
	}

	/**
	 * Returns the access controller.
	 * @return An access controller.
	 */
	public AccreditableManager getAccessController() {
		return controller;
	}

}
