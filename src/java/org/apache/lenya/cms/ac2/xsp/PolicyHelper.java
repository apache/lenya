/*
$Id: PolicyHelper.java,v 1.7 2003/08/20 18:52:11 andreas Exp $
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

package org.apache.lenya.cms.ac2.xsp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.Item;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.ac.UserManager;
import org.apache.lenya.cms.ac2.AccessControllerResolver;
import org.apache.lenya.cms.ac2.Accreditable;
import org.apache.lenya.cms.ac2.Credential;
import org.apache.lenya.cms.ac2.DefaultAccessController;
import org.apache.lenya.cms.ac2.DefaultPolicy;
import org.apache.lenya.cms.ac2.Identity;
import org.apache.lenya.cms.ac2.InheritingPolicyManager;
import org.apache.lenya.cms.ac2.Policy;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;

/**
 * Helper class for the policy GUI.
 * 
 * @author andreas
 */
public class PolicyHelper {

    /**
     * Ctor.
     */
    public PolicyHelper() {
    }

    private DefaultAccessController accessController;
    private ComponentSelector selector;
    private AccessControllerResolver resolver;
    private InheritingPolicyManager policyManager;
    private ComponentManager manager;
    private String url;

    /**
     * Initializes this helper.
     * @param objectModel The Cocoon object model.
     * @param manager The component manager.
     * @param area The selected area.
     * @throws ProcessingException when something went wrong.
     */
    public void setup(Map objectModel, ComponentManager manager, String area)
        throws ProcessingException {

        this.manager = manager;

        accessController = null;
        selector = null;
        resolver = null;
        policyManager = null;

        url = computeUrl(objectModel, area);

        try {
            selector =
                (ComponentSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");
            resolver =
                (AccessControllerResolver) selector.select(
                    AccessControllerResolver.DEFAULT_RESOLVER);

            accessController = (DefaultAccessController) resolver.resolveAccessController(url);

            policyManager = (InheritingPolicyManager) accessController.getPolicyManager();
        } catch (Exception e) {
            throw new ProcessingException("Obtaining credentials failed: ", e);
        }
    }

    /**
     * Releases all obtained components.
     */
    public void tearDown() {
        if (selector != null) {
            if (resolver != null) {
                if (accessController != null) {
                    resolver.release(accessController);
                }
                selector.release(resolver);
            }
            manager.release(selector);
        }
    }

    /**
     * Returns the URI credential wrappers for the request of this object model.
     * @return An array of CredentialWrappers.
     * @throws ProcessingException when something went wrong.
     */
    public CredentialWrapper[] getURICredentials() throws ProcessingException {
        return getCredentials(true);
    }

    /**
     * Returns the credential wrappers for the parent URI of the URL
     * belonging to the request of this object model.
     * @return An array of CredentialWrappers.
     * @throws ProcessingException when something went wrong.
     */
    public CredentialWrapper[] getParentCredentials() throws ProcessingException {
        return getCredentials(false);
    }

    /**
     * Returns the credentials of the policy of the selected URL.
     * @param urlOnly If true, the URL policy credentials are returned.
     * If false, the credentials of all ancestor policies are returned.
     * @return An array of CredentialWrappers.
     * @throws ProcessingException when something went wrong.
     */
    public CredentialWrapper[] getCredentials(boolean urlOnly) throws ProcessingException {

        List credentials = new ArrayList();

        DefaultPolicy policies[] = getPolicies(urlOnly);
        List policyCredentials = new ArrayList();
        for (int i = 0; i < policies.length; i++) {
            Credential[] creds = policies[i].getCredentials();
            for (int j = 0; j < creds.length; j++) {
                policyCredentials.add(creds[j]);
            }
        }
        for (Iterator i = policyCredentials.iterator(); i.hasNext();) {
            Credential credential = (Credential) i.next();
            Accreditable accreditable = credential.getAccreditable();
            Role[] roles = credential.getRoles();
            for (int j = 0; j < roles.length; j++) {
                credentials.add(new CredentialWrapper(accreditable, roles[j]));
            }
        }
        return (CredentialWrapper[]) credentials.toArray(new CredentialWrapper[credentials.size()]);
    }

    /**
     * Computes the webapp URL belonging to an object model with respect to the selected
     * area.
     * @param objectModel The Cocoon object model.
     * @param area The selected area.
     * @return A string.
     * @throws ProcessingException when something went wrong.
     */
    private static String computeUrl(Map objectModel, String area) throws ProcessingException {
        PageEnvelope envelope;
        try {
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        } catch (PageEnvelopeException e) {
            throw new ProcessingException(e);
        }
        String url =
            "/" + envelope.getPublication().getId() + "/" + area + envelope.getDocument().getId();
        return url;
    }

    /**
     * Returns the policies for a certain URL.
     * @param onlyUrl If true, only the URL policies are returned.
     * Otherwise, all ancestor policies are returned.
     * @return An array of DefaultPolicy objects.
     * @throws ProcessingException when something went wrong.
     */
    protected DefaultPolicy[] getPolicies(boolean onlyUrl) throws ProcessingException {

        DefaultPolicy[] policies;

        try {
            if (onlyUrl) {
                policies = new DefaultPolicy[1];
                policies[0] =
                    policyManager.buildSubtreePolicy(
                        accessController.getAccreditableManager(),
                        url);
            } else {
                String ancestorUrl = "";
                int lastSlashIndex = url.lastIndexOf("/");
                if (lastSlashIndex != -1) {
                    ancestorUrl = url.substring(0, lastSlashIndex);
                }
                policies =
                    policyManager.getPolicies(accessController.getAccreditableManager(), ancestorUrl);
            }
        } catch (AccessControlException e) {
            throw new ProcessingException(e);
        }

        return policies;
    }

    public static final String ADD = "add";
    public static final String DELETE = "delete";

    /**
     * Changes a credential by adding or deleting an item for a role.
     * @param item The item to add or delete.
     * @param role The role.
     * @param operation The operation, either {@link #ADD} or {@link #DELETE}.
     * @throws ProcessingException when something went wrong.
     */
    public void manipulateCredential(Item item, Role role, String operation)
        throws ProcessingException {

        try {
            DefaultPolicy policy = policyManager.buildSubtreePolicy(accessController.getAccreditableManager(), url);
            Accreditable accreditable = (Accreditable) item;

            if (operation.equals(ADD)) {
                policy.addRole(accreditable, role);
            } else if (operation.equals(DELETE)) {
                policy.removeRole(accreditable, role);
            }

            policyManager.saveSubtreePolicy(url, policy);

        } catch (Exception e) {
            throw new ProcessingException("Manipulating credential failed: ", e);
        }
    }

    /**
     * Returns if one of the ancestors of this URL is SSL protected.
     * @return A boolean value.
     * @throws ProcessingException when something went wrong.
     */
    public boolean isAncestorSSLProtected() throws ProcessingException {
        boolean ssl;
        try {
            String ancestorUrl = "";
            int lastSlashIndex = url.lastIndexOf("/");
            if (lastSlashIndex != -1) {
                ancestorUrl = url.substring(0, lastSlashIndex);
            }
            Policy policy = policyManager.getPolicy(accessController.getAccreditableManager(), ancestorUrl);
            ssl = policy.isSSLProtected();
        } catch (AccessControlException e) {
            throw new ProcessingException("Resolving policy failed: ", e);
        }
        return ssl;
    }

    /**
     * Returns if this URL is SSL protected.
     * @return A boolean value.
     * @throws ProcessingException when something went wrong.
     */
    public boolean isUrlSSLProtected() throws ProcessingException {
        boolean ssl;
        try {
            DefaultPolicy policy = policyManager.buildSubtreePolicy(accessController.getAccreditableManager(), url);
            ssl = policy.isSSLProtected();
        } catch (AccessControlException e) {
            throw new ProcessingException("Resolving policy failed: ", e);
        }
        return ssl;
    }

    /**
     * Sets if this URL is SSL protected.
     * @param ssl A boolean value.
     * @throws ProcessingException when something went wrong.
     */
    public void setUrlSSLProtected(boolean ssl) throws ProcessingException {
        try {
            DefaultPolicy policy = policyManager.buildSubtreePolicy(accessController.getAccreditableManager(), url);
            policy.setSSL(ssl);
            policyManager.saveSubtreePolicy(url, policy);
        } catch (AccessControlException e) {
            throw new ProcessingException("Resolving policy failed: ", e);
        }
    }
    
    /**
     * Returns the users with a certain role on the current URL.
     * @param roleId The role ID.
     * @return An array of users.
     * @throws ProcessingException when something went wrong.
     */
    public User[] getUsersWithRole(String roleId) throws ProcessingException {
        List users = new ArrayList();
        try {
            Policy policy = policyManager.getPolicy(accessController.getAccreditableManager(), getUrl());
            UserManager userManager = accessController.getAccreditableManager().getUserManager();
            for (Iterator i = userManager.getUsers(); i.hasNext(); ) {
                User user = (User) i.next();
                Identity identity = new Identity();
                identity.addIdentifiable(user);
                Role[] roles = policy.getRoles(identity);
                for (int roleIndex = 0; roleIndex < roles.length; roleIndex++) {
                    if (roles[roleIndex].getId().equals(roleId)) {
                        users.add(user);
                    }
                }
            }
        } catch (AccessControlException e) {
            throw new ProcessingException(e);
        }
        return (User[]) users.toArray(new User[users.size()]);
    }

    /**
     * Returns the URL.
     * @return A string.
     */
    public String getUrl() {
        return url;
    }

}
