/*
$Id: FilePolicyManager.java,v 1.8 2003/07/14 18:05:34 andreas Exp $
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
package org.apache.lenya.cms.ac2.file;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.Group;
import org.apache.lenya.cms.ac.Machine;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.ac2.AccreditableManager;
import org.apache.lenya.cms.ac2.Accreditable;
import org.apache.lenya.cms.ac2.Credential;
import org.apache.lenya.cms.ac2.DefaultPolicy;
import org.apache.lenya.cms.ac2.Policy;
import org.apache.lenya.cms.ac2.PolicyManager;
import org.apache.lenya.cms.ac2.URLPolicy;
import org.apache.lenya.cms.ac2.World;
import org.apache.lenya.util.CacheMap;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

/**
 * A PolicyBuilder is used to build policies.
 * @author andreas
 */
public class FilePolicyManager
    extends AbstractLogEnabled
    implements PolicyManager, Parameterizable, Serviceable {

    /**
     * Creates a new PolicyBuilder.
     */
    public FilePolicyManager() {
    }

    private File policyDirectory;

    /**
     * Builds a policy from a file. When the file is not present, an empty policy is returned.
     * @param controller An access controller.
     * @param file The file.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    protected DefaultPolicy buildPolicy(AccreditableManager controller, File file)
        throws AccessControlException {
        assert(null != file) && file.isFile();

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
        } else if (elementName.equals(MACHINE_ELEMENT)) {
            accreditable = new Machine(id);
        }

        if (accreditable == null) {
            throw new AccessControlException(
                "Unknown accreditable [" + elementName + "] with ID [" + id + "]");
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
     * @see org.apache.lenya.cms.ac2.PolicyManager#buildURLPolicy(org.apache.lenya.cms.publication.Publication, java.lang.String)
     */
    public DefaultPolicy buildURLPolicy(AccreditableManager controller, String url)
        throws AccessControlException {
        return buildPolicy(controller, url, URL_FILENAME);
    }

    /**
     * Builds a subtree policy from a file. When the file is not present, an empty policy is returned.
     * @see org.apache.lenya.cms.ac2.PolicyManager#buildSubtreePolicy(org.apache.lenya.cms.publication.Publication, java.lang.String)
     */
    public DefaultPolicy buildSubtreePolicy(AccreditableManager controller, String url)
        throws AccessControlException {
        return buildPolicy(controller, url, SUBTREE_FILENAME);
    }

    /**
     * Builds a policy from a file. When the file is not present, an empty policy is returned.
     * @param controller The access controller to use.
     * @param publication The publication.
     * @param url The url.
     * @param policyFilename The policy filename.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    protected DefaultPolicy buildPolicy(
        AccreditableManager controller,
        String url,
        String policyFilename)
        throws AccessControlException {
        DefaultPolicy policy;
        File policyFile = getPolicyFile(url, policyFilename);
        getLogger().debug("Policy file resolved to: " + policyFile.getAbsolutePath());
//        getLogger().debug("", new IllegalStateException());

        if (policyFile.exists()) {
            policy = buildPolicy(controller, policyFile);
        } else {
            policy = new DefaultPolicy();
        }

        return policy;
    }

    /**
     * Returns the policy file for a URL and a policy filename.
     * @param publication The publication.
     * @param url The url to get the file for.
     * @param policyFilename The name of the policy file.
     * @return A file object.
     */
    protected File getPolicyFile(String url, String policyFilename) throws AccessControlException {
        assert url.startsWith("/");
        url = url.substring(1);

        String path = url.replace('/', File.separatorChar) + File.separator + policyFilename;
        File policyFile = new File(getPoliciesDirectory(), path);

        return policyFile;
    }

    /**
     * @see org.apache.lenya.cms.ac2.PolicyManager#saveURLPolicy(Publication, java.lang.String, org.apache.lenya.cms.ac2.Policy)
     */
    public void saveURLPolicy(String url, DefaultPolicy policy) throws AccessControlException {
        savePolicy(url, policy, URL_FILENAME);
    }

    /**
     * @see org.apache.lenya.cms.ac2.PolicyManager#saveSubtreePolicy(Publication, java.lang.String, Policy)
     */
    public void saveSubtreePolicy(String url, DefaultPolicy policy) throws AccessControlException {
        savePolicy(url, policy, SUBTREE_FILENAME);
    }

    /**
     * Saves a policy to a file.
     * @param publication The publication.
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

        Credential[] credentials = policy.getCredentials();

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
        } else if (accreditable instanceof Machine) {
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

    protected static final int CACHE_CAPACITY = 1000;
    private static Map cache = new CacheMap(CACHE_CAPACITY);

    /**
     * @see org.apache.lenya.cms.ac2.PolicyManager#getPolicy(AccreditableManager, Publication, java.lang.String)
     */
    public Policy getPolicy(AccreditableManager controller, String url)
        throws AccessControlException {

        String key;
        try {
            key = getPoliciesDirectory().getCanonicalPath() + ":" + url;
        } catch (IOException e) {
            throw new AccessControlException(e);
        }
        Policy policy = (Policy) cache.get(key);
        if (policy == null) {
            policy = new URLPolicy(controller, url, this);
            cache.put(key, policy);
        }

        return policy;
    }

    protected static final String DIRECTORY_PARAMETER = "directory";

    private String policiesDirectoryUri;
    private File policiesDirectory;

    /**
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters parameters) throws ParameterException {
        policiesDirectoryUri = parameters.getParameter(DIRECTORY_PARAMETER);
        getLogger().debug("Policies directory URI: " + policiesDirectoryUri);
    }

    /**
     * @return
     */
    public File getPoliciesDirectory() throws AccessControlException {
        
        if (policiesDirectory == null) {
            SourceResolver resolver = null;
            Source source = null;
            File directory;

            try {
                resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);
                source = resolver.resolveURI(policiesDirectoryUri);
                getLogger().debug("Policies directory source: [" + source.getURI() + "]");
                directory = new File(new URI(source.getURI()));
            } catch (Exception e) {
                throw new AccessControlException("Resolving policies directory failed: ", e);
            } finally {
                if (resolver != null) {
                    if (source != null) {
                        resolver.release(source);
                    }
                    getManager().release(resolver);
                }
            }

            getLogger().debug("Policies directory resolved to [" + directory.getAbsolutePath() + "]");
            setPoliciesDirectory(directory);
        }
        
        return policiesDirectory;
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /**
     * Returns the service manager of this Serviceable.
     * @return A service manager.
     */
    public ServiceManager getManager() {
        return manager;
    }

    /**
     * Sets the policies directory.
     * @param directory The directory.
     */
    public void setPoliciesDirectory(File directory) throws AccessControlException {
        if (!directory.isDirectory()) {
            throw new AccessControlException("Policies directory invalid: [" + directory.getAbsolutePath() + "]");
        }
        policiesDirectory = directory;
    }

}
