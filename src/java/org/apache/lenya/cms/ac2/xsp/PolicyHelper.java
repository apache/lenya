/*
$Id: PolicyHelper.java,v 1.2 2003/07/30 13:19:22 andreas Exp $
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
import org.apache.lenya.cms.ac2.AccessControllerResolver;
import org.apache.lenya.cms.ac2.Accreditable;
import org.apache.lenya.cms.ac2.AccreditableManager;
import org.apache.lenya.cms.ac2.Credential;
import org.apache.lenya.cms.ac2.DefaultAccessController;
import org.apache.lenya.cms.ac2.DefaultPolicy;
import org.apache.lenya.cms.ac2.InheritingPolicyManager;
import org.apache.lenya.cms.ac2.file.FilePolicyManager;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;

/**
 * @author andreas
 */
public class PolicyHelper {

    protected static final String DEFAULT_RESOLVER = "composable";

    public static CredentialWrapper[] getURICredentials(
        Map objectModel,
        String area,
        ComponentManager manager)
        throws ProcessingException {
        return getCredentials(objectModel, area, manager, true);
    }

    public static CredentialWrapper[] getParentCredentials(
        Map objectModel,
        String area,
        ComponentManager manager)
        throws ProcessingException {
        return getCredentials(objectModel, area, manager, false);
    }

    public static CredentialWrapper[] getCredentials(
        Map objectModel,
        String area,
        ComponentManager manager,
        boolean urlOnly)
        throws ProcessingException {

        DefaultAccessController accessController = null;
        ComponentSelector selector = null;
        AccessControllerResolver resolver = null;
        FilePolicyManager policyManager = null;
        List credentials = new ArrayList();

        String url = computeUrl(objectModel, area);

        try {
            selector =
                (ComponentSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");
            resolver = (AccessControllerResolver) selector.select(DEFAULT_RESOLVER);

            accessController = (DefaultAccessController) resolver.resolveAccessController(url);

            AccreditableManager accreditableManager = accessController.getAccreditableManager();
            policyManager = (FilePolicyManager) accessController.getPolicyManager();

            DefaultPolicy policies[] =
                getPolicies(accreditableManager, policyManager, url, urlOnly);

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

        } catch (Exception e) {
            throw new ProcessingException("Obtaining credentials failed: ", e);
        } finally {
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
        return (CredentialWrapper[]) credentials.toArray(new CredentialWrapper[credentials.size()]);
    }

    private static String computeUrl(Map objectModel, String area) throws ProcessingException {
        PageEnvelope envelope;
        try {
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        } catch (PageEnvelopeException e) {
            throw new ProcessingException(e);
        }
        String url =
            "/" + envelope.getPublication().getId() + "/" + area + envelope.getDocumentId();
        return url;
    }

    protected static DefaultPolicy[] getPolicies(
        AccreditableManager accreditableManager,
        InheritingPolicyManager policyManager,
        String url,
        boolean onlyUrl)
        throws AccessControlException {

        DefaultPolicy[] policies;

        if (onlyUrl) {
            policies = new DefaultPolicy[1];
            policies[0] = policyManager.buildSubtreePolicy(accreditableManager, url);
        } else {
            int lastSlashIndex = url.lastIndexOf("/");
            if (lastSlashIndex != -1) {
                url = url.substring(0, lastSlashIndex);
            } else {
                url = "";
            }
            policies = policyManager.getPolicies(accreditableManager, url);
        }

        return policies;
    }
    
    public static final String ADD = "add";
    public static final String DELETE = "delete";

    public static void manipulateCredential(
        Map objectModel,
        Item item,
        Role role,
        String area,
        String operation,
        ComponentManager manager)
        throws ProcessingException {

        DefaultAccessController accessController = null;
        ComponentSelector selector = null;
        AccessControllerResolver resolver = null;
        FilePolicyManager policyManager = null;

        String url = computeUrl(objectModel, area);

        try {
            selector =
                (ComponentSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");
            resolver = (AccessControllerResolver) selector.select(DEFAULT_RESOLVER);

            accessController = (DefaultAccessController) resolver.resolveAccessController(url);

            AccreditableManager accreditableManager = accessController.getAccreditableManager();
            policyManager = (FilePolicyManager) accessController.getPolicyManager();

            DefaultPolicy policy = policyManager.buildSubtreePolicy(accreditableManager, url);

            Accreditable accreditable = (Accreditable) item;
            
            if (operation.equals(ADD)) {
                policy.addRole(accreditable, role);
            }
            else if (operation.equals(DELETE)) {
                policy.removeRole(accreditable, role);
            }
            
            policyManager.saveSubtreePolicy(url, policy);
            

        } catch (Exception e) {
            throw new ProcessingException("Manipulating credential failed: ", e);
        } finally {
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
    }

}
