/*
$Id: PolicyBuilder.java,v 1.4 2003/07/30 15:10:08 andreas Exp $
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

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.Group;
import org.apache.lenya.cms.ac.IPRange;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public class PolicyBuilder {

    /**
     * Ctor.
     */
    protected PolicyBuilder() {
    }

    /**
     * Returns the PolicyBuilder instance.
     * @return A policy builder.
     */
    public static PolicyBuilder getInstance() {
        if (instance == null) {
            instance = new PolicyBuilder();
        }
        return instance;
    }

    private static PolicyBuilder instance;

    protected static final String POLICY_ELEMENT = "policy";
    protected static final String GROUP_ELEMENT = "group";
    protected static final String USER_ELEMENT = "user";
    protected static final String ROLE_ELEMENT = "role";
    protected static final String WORLD_ELEMENT = "world";
    protected static final String IP_RANGE_ELEMENT = "ip-range";
    protected static final String ID_ATTRIBUTE = "id";

    /**
     * Builds a policy from a file. When the file is not present, an empty policy is returned.
     * @param controller An access controller.
     * @param file The file.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    public DefaultPolicy buildPolicy(AccreditableManager controller, File file)
        throws AccessControlException {
        assert(null != file) && file.isFile();

        Document document;

        try {
            document = DocumentHelper.readDocument(file);
        } catch (Exception e) {
            throw new AccessControlException(e);
        }

        return buildPolicy(controller, document);
    }

    /**
     * Builds a policy from an XML document.
     * @param controller An access controller.
     * @param document The XML document.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    public DefaultPolicy buildPolicy(AccreditableManager controller, Document document)
        throws AccessControlException {

        DefaultPolicy policy = new DefaultPolicy();
        Element policyElement = document.getDocumentElement();
        assert policyElement.getLocalName().equals(POLICY_ELEMENT);

        NamespaceHelper helper =
            new NamespaceHelper(
                AccessController.NAMESPACE,
                AccessController.DEFAULT_PREFIX,
                document);

        Element[] credentialElements = helper.getChildren(policyElement);

        for (int i = 0; i < credentialElements.length; i++) {
            Accreditable accreditable = null;

            String id = credentialElements[i].getAttribute(ID_ATTRIBUTE);
            accreditable = getAccreditable(controller, credentialElements[i].getLocalName(), id);

            Credential credential = new Credential(accreditable);

            Element[] roleElements = helper.getChildren(credentialElements[i], ROLE_ELEMENT);

            for (int j = 0; j < roleElements.length; j++) {
                String roleId = roleElements[j].getAttribute(ID_ATTRIBUTE);
                Role role = controller.getRoleManager().getRole(roleId);
                credential.addRole(role);
            }

            policy.addCredential(credential);
        }

        return policy;
    }

    /**
     * Creates an accredtiable for an element.
     * @param controller An access controller.
     * @param elementName The elment name.
     * @param id The ID of the accreditable.
     * @return An accreditable.
     * @throws AccessControlException when something went wrong.
     */
    protected Accreditable getAccreditable(
        AccreditableManager controller,
        String elementName,
        String id)
        throws AccessControlException {
        Accreditable accreditable = null;

        if (elementName.equals(USER_ELEMENT)) {
            accreditable = controller.getUserManager().getUser(id);
        } else if (elementName.equals(GROUP_ELEMENT)) {
            accreditable = controller.getGroupManager().getGroup(id);
        } else if (elementName.equals(WORLD_ELEMENT)) {
            accreditable = World.getInstance();
        } else if (elementName.equals(IP_RANGE_ELEMENT)) {
            accreditable = controller.getIPRangeManager().getIPRange(id);
        }

        if (accreditable == null) {
            throw new AccessControlException(
                "Unknown accreditable [" + elementName + "] with ID [" + id + "]");
        }

        return accreditable;
    }

    /**
     * Saves a policy to an XML document.
     * @param policy The policy to save.
     * @return A DOM document.
     * @throws AccessControlException when something went wrong.
     */
    public Document savePolicy(DefaultPolicy policy) throws AccessControlException {
        NamespaceHelper helper;

        try {
            helper =
                new NamespaceHelper(
                    AccessController.NAMESPACE,
                    AccessController.DEFAULT_PREFIX,
                    POLICY_ELEMENT);
        } catch (ParserConfigurationException e) {
            throw new AccessControlException(e);
        }

        Credential[] credentials = policy.getCredentials();

        for (int i = 0; i < credentials.length; i++) {
            Accreditable accreditable = credentials[i].getAccreditable();
            Element accreditableElement = save(accreditable, helper);
            
            Role[] roles = credentials[i].getRoles();
            for (int j = 0; j < roles.length; j++) {
                Element roleElement = helper.createElement(ROLE_ELEMENT);
                roleElement.setAttribute(ID_ATTRIBUTE, roles[j].getId());
                accreditableElement.appendChild(roleElement);
            }
            
            helper.getDocument().getDocumentElement().appendChild(accreditableElement);
        }

        return helper.getDocument();
    }

    /**
     * Saves an accreditable to an XML element.
     * @param accreditable The accreditable.
     * @param helper The namespace helper to be used.
     * @return An XML element.
     * @throws AccessControlException when something went wrong.
     */
    protected Element save(Accreditable accreditable, NamespaceHelper helper)
        throws AccessControlException {
        String localName = null;
        String id = null;

        if (accreditable instanceof User) {
            localName = USER_ELEMENT;
            id = ((User) accreditable).getId();
        } else if (accreditable instanceof Group) {
            localName = GROUP_ELEMENT;
            id = ((Group) accreditable).getId();
        } else if (accreditable instanceof World) {
            localName = WORLD_ELEMENT;
        } else if (accreditable instanceof IPRange) {
            localName = IP_RANGE_ELEMENT;
            id = ((IPRange) accreditable).getId();
        }

        if (localName == null) {
            throw new AccessControlException("Could not save accreditable [" + accreditable + "]");
        }

        Element element = helper.createElement(localName);

        if (id != null) {
            element.setAttribute(ID_ATTRIBUTE, id);
        }

        return element;
    }

}
