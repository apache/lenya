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
package org.apache.lenya.cms.ac2.file;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.Group;
import org.apache.lenya.cms.ac.GroupManager;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac.RoleManager;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.ac.UserManager;
import org.apache.lenya.cms.ac2.Accreditable;
import org.apache.lenya.cms.ac2.Credential;
import org.apache.lenya.cms.ac2.DefaultPolicy;
import org.apache.lenya.cms.ac2.Machine;
import org.apache.lenya.cms.ac2.PolicyManager;
import org.apache.lenya.cms.ac2.World;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A PolicyBuilder is used to build policies.
 * @author andreas
 */
public class FilePolicyManager implements PolicyManager {

    /**
     * Creates a new PolicyBuilder.
     * @param policyDirectory The directory where policies are stored.
     * @param userManager The user manager.
     * @param groupManager The group manager.
     * @param roleManager The role manager.
     */
    protected FilePolicyManager(
        File policyDirectory,
        UserManager userManager,
        GroupManager groupManager,
        RoleManager roleManager) {

        assert policyDirectory != null && policyDirectory.isDirectory();
        this.policyDirectory = policyDirectory;

        assert userManager != null;
        this.userManager = userManager;

        assert groupManager != null;
        this.groupManager = groupManager;

        assert roleManager != null;
        this.roleManager = roleManager;
    }

    private UserManager userManager;
    private GroupManager groupManager;
    private RoleManager roleManager;
    private File policyDirectory;

    /**
     * Builds a policy from a file. When the file is not present, an empty policy is returned.
     * @param file The file.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    protected DefaultPolicy buildPolicy(File file) throws AccessControlException {
        
        assert null != file && file.isFile();

        DefaultPolicy policy = new DefaultPolicy();

        Document document;
        try {
            document = DocumentHelper.readDocument(file);
        } catch (Exception e) {
            throw new AccessControlException(e);
        }

        Element policyElement = document.getDocumentElement();
        assert policyElement.getLocalName().equals(POLICY_ELEMENT);

        NamespaceHelper helper = new NamespaceHelper(NAMESPACE, DEFAULT_PREFIX, document);

        Element credentialElements[] = helper.getChildren(policyElement);
        for (int i = 0; i < credentialElements.length; i++) {

            Accreditable accreditable = null;

            String id = credentialElements[i].getAttribute(ID_ATTRIBUTE);
            accreditable = getAccreditable(credentialElements[i].getLocalName(), id);

            Credential credential = new Credential(accreditable);

            Element roleElements[] = helper.getChildren(credentialElements[i], ROLE_ELEMENT);
            for (int j = 0; j < roleElements.length; j++) {
                String roleId = roleElements[j].getAttribute(ID_ATTRIBUTE);
                Role role = roleManager.getRole(roleId);
                credential.addRole(role);
            }

            policy.addCredential(credential);

        }

        return policy;
    }
    
    /**
     * Creates an accredtiable for an element.
     * @param elementName The elment name.
     * @param id The ID of the accreditable.
     * @return An accreditable.
     * @throws AccessControlException when something went wrong.
     */
    protected Accreditable getAccreditable(String elementName, String id) throws AccessControlException {
        Accreditable accreditable = null;
        if (elementName.equals(USER_ELEMENT)) {
            accreditable = userManager.getUser(id);
        } else if (elementName.equals(GROUP_ELEMENT)) {
            accreditable = groupManager.getGroup(id);
        } else if (elementName.equals(WORLD_ELEMENT)) {
            accreditable = World.getInstance();
        } else if (elementName.equals(MACHINE_ELEMENT)) {
            accreditable = new Machine(id);
        }
        if (accreditable == null) {
            throw new AccessControlException("Unknown accreditable [" + elementName + "]");
        }
        return accreditable;
    }

    protected static final String NAMESPACE = "http://apache.org/cocoon/lenya/ac/1.0";
    protected static final String DEFAULT_PREFIX = "ac";
    protected static final String POLICY_ELEMENT = "policy";
    protected static final String GROUP_ELEMENT = "group";
    protected static final String USER_ELEMENT = "user";
    protected static final String ROLE_ELEMENT = "role";
    protected static final String WORLD_ELEMENT = "world";
    protected static final String MACHINE_ELEMENT = "machine";
    protected static final String ID_ATTRIBUTE = "id";
    protected static final String URL_FILENAME = "url-policy.acml";
    protected static final String SUBTREE_FILENAME = "subtree-policy.acml";

    /**
     * Builds a URL policy from a file. When the file is not present, an empty policy is returned.
     * @see org.apache.lenya.cms.ac2.PolicyBuilder#buildURLPolicy(java.lang.String)
     */
    public DefaultPolicy buildURLPolicy(String url) throws AccessControlException {
        return buildPolicy(url, URL_FILENAME);
    }

    /**
     * Builds a subtree policy from a file. When the file is not present, an empty policy is returned.
     * @see org.apache.lenya.cms.ac2.PolicyBuilder#buildSubtreePolicy(java.lang.String)
     */
    public DefaultPolicy buildSubtreePolicy(String url) throws AccessControlException {
        return buildPolicy(url, SUBTREE_FILENAME);
    }

    /**
     * Builds a policy from a file. When the file is not present, an empty policy is returned.  
     * @param url The url.
     * @param policyFilename The policy filename.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    protected DefaultPolicy buildPolicy(String url, String policyFilename)
        throws AccessControlException {
        DefaultPolicy policy;
        File policyFile = getPolicyFile(url, policyFilename);
        if (policyFile.exists()) {
            policy = buildPolicy(policyFile);
        }
        else {
            policy = new DefaultPolicy();
        }
        return policy;
    }
    
    /**
     * Returns the policy file for a URL and a policy filename.
     * @param url The url to get the file for: /{area}/...
     * @param policyFilename The name of the policy file.
     * @return A file object.
     */
    protected File getPolicyFile(String url, String policyFilename) {
        assert url.startsWith("/");
        url = url.substring(1);
        String path = url.replace('/', File.separatorChar) + File.separator + policyFilename;
        File policyFile = new File(getPolicyDirectory(), path);
        return policyFile;
    }
    
    /**
     * Returns the policy directory.
     * @return A file object.
     */
    public File getPolicyDirectory() {
        return policyDirectory;
    }

    /**
     * @see org.apache.lenya.cms.ac2.PolicyManager#saveURLPolicy(org.apache.lenya.cms.ac2.Policy)
     */
    public void saveURLPolicy(String url, DefaultPolicy policy) throws AccessControlException {
        savePolicy(url, policy, URL_FILENAME);
    }

    /**
     * @see org.apache.lenya.cms.ac2.PolicyManager#saveSubtreePolicy(org.apache.lenya.cms.ac2.Policy)
     */
    public void saveSubtreePolicy(String url, DefaultPolicy policy) throws AccessControlException {
        savePolicy(url, policy, SUBTREE_FILENAME);
    }

    /**
     * Saves a policy to a file.
     * @param url The URL to save the policy for.
     * @param policy The policy.
     * @param filename The file.
     * @throws AccessControlException if something goes wrong.
     */
    protected void savePolicy(String url, DefaultPolicy policy, String filename)
        throws AccessControlException {

        NamespaceHelper helper;
        try {
            helper = new NamespaceHelper(NAMESPACE, DEFAULT_PREFIX, POLICY_ELEMENT);
        } catch (ParserConfigurationException e) {
            throw new AccessControlException(e);
        }
        Credential credentials[] = policy.getCredentials();
        for (int i = 0; i < credentials.length; i++) {
            Accreditable accreditable = credentials[i].getAccreditable();
            Element accreditableElement = save(accreditable, helper);
            helper.getDocument().getDocumentElement().appendChild(accreditableElement);
        }
        
        File file = getPolicyFile(url, filename);
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            DocumentHelper.writeDocument(helper.getDocument(), file);
        } catch (Exception e) {
            throw new AccessControlException("Path: [" + file.getAbsolutePath() + "]", e);
        }

    }
    
    /**
     * Saves an accreditable to an XML element.
     * @param accreditable The accreditable.
     * @param helper The namespace helper to be used.
     * @return An XML element.
     * @throws AccessControlException when something went wrong.
     */
    protected Element save(Accreditable accreditable, NamespaceHelper helper) throws AccessControlException {
        String localName = null;
        String id = null;
        if (accreditable instanceof User) {
            localName = USER_ELEMENT;
            id = ((User) accreditable).getId();
        }
        else if (accreditable instanceof Group) {
            localName = GROUP_ELEMENT;
            id = ((Group) accreditable).getName();
        }
        else if (accreditable instanceof World) {
            localName = WORLD_ELEMENT;
        }
        else if (accreditable instanceof Machine) {
            localName = MACHINE_ELEMENT;
            id = ((Machine) accreditable).getIp();
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
