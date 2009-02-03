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

package org.apache.lenya.ac.impl;

import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Credential;
import org.apache.lenya.ac.ModifiablePolicy;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.World;
import org.apache.lenya.ac.cache.BuildException;
import org.apache.lenya.ac.cache.InputStreamBuilder;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Builds policies from input streams.
 * @version $Id$
 */
public class PolicyBuilder implements InputStreamBuilder {

    /**
     * Ctor.
     * @param _accreditableManager An accreditable manager.
     */
    public PolicyBuilder(AccreditableManager _accreditableManager) {
        assert _accreditableManager != null;
        this.accreditableManager = _accreditableManager;
    }
    
    /**
     * Returns the accreditable manager.
     * @return An accreditable manager.
     */
    public AccreditableManager getAccreditableManager() {
        return this.accreditableManager;
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
    protected static final String METHOD_ATTRIBUTE = "method";
    
    /**
     * Builds a policy from an input stream.
     * @param stream The input stream to read the policy from.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    public ModifiablePolicy buildPolicy(InputStream stream)
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
    public ModifiablePolicy buildPolicy(Document document)
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

            Element[] roleElements = helper.getChildren(credentialElements[i], ROLE_ELEMENT);

            for (int j = 0; j < roleElements.length; j++) {
                String roleId = roleElements[j].getAttribute(ID_ATTRIBUTE);
                Role role = getAccreditableManager().getRoleManager().getRole(roleId);
                if (role == null) {
                    throw new AccessControlException("The role '" + roleId + "' does not exist.");
                }
                CredentialImpl credential = new CredentialImpl(accreditable, role);
                String method = roleElements[j].getAttribute(METHOD_ATTRIBUTE);
                // If method is not set, we assume DENY 
                if (method.length() == 0) method = CredentialImpl.DENY;
                credential.setMethod(method);
                policy.addCredential(credential);
            }

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
    public static Document savePolicy(Policy policy) throws AccessControlException {
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

        Credential[] credentials = ((DefaultPolicy) policy).getCredentials();
        Element policyElement = helper.getDocument().getDocumentElement();

        for (int i = 0; i < credentials.length; i++) {
            Accreditable accreditable = credentials[i].getAccreditable();
            Element accreditableElement = save(accreditable, helper);
            
            Role role = credentials[i].getRole();
            Element roleElement = helper.createElement(ROLE_ELEMENT);
            roleElement.setAttribute(ID_ATTRIBUTE, role.getId());
            roleElement.setAttribute(METHOD_ATTRIBUTE, credentials[i].getMethod());
            accreditableElement.appendChild(roleElement);
            
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
     * @see org.apache.lenya.ac.cache.InputStreamBuilder#build(java.io.InputStream)
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
