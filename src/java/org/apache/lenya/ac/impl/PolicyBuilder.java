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

/* $Id: PolicyBuilder.java,v 1.3 2004/03/08 16:48:20 gregor Exp $  */

package org.apache.lenya.ac.impl;

import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.World;
import org.apache.lenya.ac.cache.BuildException;
import org.apache.lenya.ac.cache.InputStreamBuilder;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PolicyBuilder implements InputStreamBuilder {

    /**
     * Ctor.
     * @param accreditableManager An accreditable manager.
     */
    public PolicyBuilder(AccreditableManager accreditableManager) {
        assert accreditableManager != null;
        this.accreditableManager = accreditableManager;
    }
    
    /**
     * Returns the accreditable manager.
     * @return An accreditable manager.
     */
    public AccreditableManager getAccreditableManager() {
        return accreditableManager;
    }

    private AccreditableManager accreditableManager;

    protected static final String POLICY_ELEMENT = "policy";
    protected static final String GROUP_ELEMENT = "group";
    protected static final String USER_ELEMENT = "user";
    protected static final String ROLE_ELEMENT = "role";
    protected static final String WORLD_ELEMENT = "world";
    protected static final String IP_RANGE_ELEMENT = "ip-range";
    protected static final String ID_ATTRIBUTE = "id";
    protected static final String SSL_ATTRIBUTE = "ssl";
    
    /**
     * Builds a policy from an input stream.
     * @param stream The input stream to read the policy from.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    public DefaultPolicy buildPolicy(InputStream stream)
        throws AccessControlException {

        Document document;

        try {
            document = DocumentHelper.readDocument(stream);
        } catch (Exception e) {
            throw new AccessControlException(e);
        }

        return buildPolicy(document);
    }

    /**
     * Builds a policy from an XML document.
     * @param document The XML document.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    public DefaultPolicy buildPolicy(Document document)
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
            accreditable = getAccreditable(credentialElements[i].getLocalName(), id);

            Credential credential = new Credential(accreditable);

            Element[] roleElements = helper.getChildren(credentialElements[i], ROLE_ELEMENT);

            for (int j = 0; j < roleElements.length; j++) {
                String roleId = roleElements[j].getAttribute(ID_ATTRIBUTE);
                Role role = getAccreditableManager().getRoleManager().getRole(roleId);
                credential.addRole(role);
            }

            policy.addCredential(credential);
        }
        
        boolean ssl = false;
        String sslString = policyElement.getAttribute(SSL_ATTRIBUTE);
        if (sslString != null) {
            ssl = Boolean.valueOf(sslString).booleanValue();
        }
        policy.setSSL(ssl);

        return policy;
    }

    /**
     * Creates an accredtiable for an element.
     * @param elementName The elment name.
     * @param id The ID of the accreditable.
     * @return An accreditable.
     * @throws AccessControlException when something went wrong.
     */
    protected Accreditable getAccreditable(
        String elementName,
        String id)
        throws AccessControlException {
        Accreditable accreditable = null;

        if (elementName.equals(USER_ELEMENT)) {
            accreditable = getAccreditableManager().getUserManager().getUser(id);
        } else if (elementName.equals(GROUP_ELEMENT)) {
            accreditable = getAccreditableManager().getGroupManager().getGroup(id);
        } else if (elementName.equals(WORLD_ELEMENT)) {
            accreditable = World.getInstance();
        } else if (elementName.equals(IP_RANGE_ELEMENT)) {
            accreditable = getAccreditableManager().getIPRangeManager().getIPRange(id);
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
    public static Document savePolicy(DefaultPolicy policy) throws AccessControlException {
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
        Element policyElement = helper.getDocument().getDocumentElement();

        for (int i = 0; i < credentials.length; i++) {
            Accreditable accreditable = credentials[i].getAccreditable();
            Element accreditableElement = save(accreditable, helper);
            
            Role[] roles = credentials[i].getRoles();
            for (int j = 0; j < roles.length; j++) {
                Element roleElement = helper.createElement(ROLE_ELEMENT);
                roleElement.setAttribute(ID_ATTRIBUTE, roles[j].getId());
                accreditableElement.appendChild(roleElement);
            }
            
            policyElement.appendChild(accreditableElement);
        }
        
        policyElement.setAttribute(SSL_ATTRIBUTE, Boolean.toString(policy.isSSLProtected()));

        return helper.getDocument();
    }

    /**
     * Saves an accreditable to an XML element.
     * @param accreditable The accreditable.
     * @param helper The namespace helper to be used.
     * @return An XML element.
     * @throws AccessControlException when something went wrong.
     */
    protected static Element save(Accreditable accreditable, NamespaceHelper helper)
        throws AccessControlException {
        String localName = null;
        String id = null;

        if (accreditable instanceof User) {
            localName = USER_ELEMENT;
            id = ((User) accreditable).getId();
        } else if (accreditable instanceof AbstractGroup) {
            localName = GROUP_ELEMENT;
            id = ((AbstractGroup) accreditable).getId();
        } else if (accreditable instanceof World) {
            localName = WORLD_ELEMENT;
        } else if (accreditable instanceof AbstractIPRange) {
            localName = IP_RANGE_ELEMENT;
            id = ((AbstractIPRange) accreditable).getId();
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

    /**
     * @see org.apache.lenya.ac.cache.InputStreamBuilder#build(org.apache.excalibur.source.Source)
     */
    public Object build(InputStream stream) throws BuildException {
        Object value = null;
        try {
            value = buildPolicy(stream);
        } catch (AccessControlException e) {
            throw new BuildException(e);
        }
        return value;
    }

}
