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

/* $Id$  */

package org.apache.lenya.ac.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.util.NetUtils;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Credential;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.InheritingPolicyManager;
import org.apache.lenya.ac.ModifiablePolicy;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.cache.CachingException;
import org.apache.lenya.ac.cache.SourceCache;
import org.apache.lenya.ac.impl.CredentialImpl;
import org.apache.lenya.ac.impl.DefaultPolicy;
import org.apache.lenya.ac.impl.PolicyBuilder;
import org.apache.lenya.ac.impl.RemovedAccreditablePolicyBuilder;
import org.apache.lenya.ac.impl.URLPolicy;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;

/**
 * A PolicyBuilder is used to build policies.
 */
public class FilePolicyManager extends AbstractLogEnabled implements InheritingPolicyManager,
        Parameterizable, Disposable, Serviceable {

    private static final class SubtreeFileFilter implements FileFilter {

        private final String subtree;

        private SubtreeFileFilter(String _subtree) {
            super();
            this.subtree = _subtree;
        }

        /**
         * @see java.io.FileFilter#accept(java.io.File)
         */
        public boolean accept(File file) {
            return file.getName().equals(this.subtree);
        }
    }

    private static final class IsDirectoryFileFilter implements FileFilter {
        /**
         * @see java.io.FileFilter#accept(java.io.File)
         */
        public boolean accept(File file) {
            return file.isDirectory();
        }
    }

    /**
     * Creates a new FilePolicyManager.
     */
    public FilePolicyManager() {
        // do nothing
    }

    /**
     * Returns the source cache.
     * 
     * @return A source cache.
     */
    protected SourceCache getCache() {
        return this.cache;
    }

    private SourceCache cache;

    protected static final String SUBTREE_FILENAME = "subtree-policy.acml";

    /**
     * Builds a subtree policy from a file. When the file is not present, an
     * empty policy is returned.
     * 
     * @param controller The access controller to use.
     * @param url The URL inside the web application.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    public Policy buildSubtreePolicy(AccreditableManager controller, String url)
            throws AccessControlException {
        return buildPolicy(controller, url, SUBTREE_FILENAME);
    }

    /**
     * Builds a policy from a file. When the file is not present, an empty
     * policy is returned.
     * 
     * @param controller The access controller to use.
     * @param url The url.
     * @param policyFilename The policy filename.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    protected DefaultPolicy buildPolicy(AccreditableManager controller, String url,
            String policyFilename) throws AccessControlException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Building policy for URL [" + url + "]");
        }

        DefaultPolicy policy = null;

        String policyUri = getPolicySourceURI(url, policyFilename);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Policy source URI resolved to: " + policyUri);
        }

        try {
            PolicyBuilder builder = new PolicyBuilder(controller);
            policy = (DefaultPolicy) getCache().get(policyUri, builder);
        } catch (CachingException e) {
            throw new AccessControlException(e);
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Policy exists: [" + (policy != null) + "]");
        }

        if (policy == null) {
            policy = new DefaultPolicy();
        }
        return policy;
    }

    /**
     * Returns the policy file URI for a URL and a policy filename.
     * 
     * @param url The url to get the file for.
     * @param policyFilename The name of the policy file.
     * @return A String.
     * @throws AccessControlException if an error occurs
     */
    protected String getPolicySourceURI(String url, String policyFilename)
            throws AccessControlException {
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        
        // remove publication ID
        if (url.indexOf("/") > -1) {
            url = url.substring(url.indexOf("/") + 1);
        }
        else {
            url = "";
        }

        final String policyUri = this.policiesDirectoryUri + "/" + url + "/" + policyFilename;
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Computing policy URI [" + policyUri + "]");
        }
        return policyUri;
    }

    /**
     * Returns the policy file for a certain URL.
     * 
     * @param url The URL to get the policy for.
     * @param policyFilename The policy filename.
     * @return A file.
     * @throws AccessControlException when an error occurs.
     */
    protected File getPolicyFile(String url, String policyFilename) throws AccessControlException {
        String fileUri = getPolicySourceURI(url, policyFilename);
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.serviceManager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(fileUri);
            return SourceUtil.getFile(source);
        } catch (final Exception e) {
            throw new AccessControlException(e);
        }
        finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.serviceManager.release(resolver);
            }
        }
    }

    /**
     * Saves a Subtree policy.
     * 
     * @param url The url to save the policy for.
     * @param policy The policy to save.
     * @throws AccessControlException when something went wrong.
     */
    public void saveSubtreePolicy(String url, Policy policy) throws AccessControlException {
        getLogger().debug("Saving subtree policy for URL [" + url + "]");
        savePolicy(url, policy, SUBTREE_FILENAME);
    }

    /**
     * Saves a policy to a file.
     * 
     * @param url The URL to save the policy for.
     * @param policy The policy.
     * @param filename The file.
     * @throws AccessControlException if something goes wrong.
     */
    protected void savePolicy(String url, Policy policy, String filename)
            throws AccessControlException {

        File file = getPolicyFile(url, filename);
        savePolicy(policy, file);
    }

    /**
     * Saves a policy to a file.
     * 
     * @param policy The policy to save.
     * @param file The file.
     * @throws AccessControlException when an error occurs.
     */
    protected void savePolicy(Policy policy, File file) throws AccessControlException {
        Document document = PolicyBuilder.savePolicy(policy);

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                if (!file.createNewFile()) {
                    throw new AccessControlException("File [" + file + "] could not be created.");
                }
            }
            DocumentHelper.writeDocument(document, file);
        } catch (AccessControlException e) {
            throw e;
        } catch (Exception e) {
            throw new AccessControlException("Path: [" + file.getAbsolutePath() + "]", e);
        }
    }

    /**
     * @see org.apache.lenya.ac.PolicyManager#getPolicy(org.apache.lenya.ac.AccreditableManager,
     *      java.lang.String)
     */
    public Policy getPolicy(AccreditableManager controller, String url)
            throws AccessControlException {

        return new URLPolicy(controller, url, this);
    }

    protected static final String DIRECTORY_PARAMETER = "directory";

    private String policiesDirectoryUri;

    private File policiesDirectory;

    /**
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters parameters) throws ParameterException {
        if (parameters.isParameter(DIRECTORY_PARAMETER)) {
            this.policiesDirectoryUri = parameters.getParameter(DIRECTORY_PARAMETER);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Policies directory URI: " + this.policiesDirectoryUri);
            }
        }
    }

    /**
     * Get the path to the policies directory.
     * 
     * @return the path to the policies directory
     * @throws AccessControlException if an error occurs
     */
    public File getPoliciesDirectory() throws AccessControlException {

        if (this.policiesDirectory == null) {
            SourceResolver resolver = null;
            Source source = null;
            File directory;

            try {
                resolver = (SourceResolver) getServiceManager().lookup(SourceResolver.ROLE);
                source = resolver.resolveURI(this.policiesDirectoryUri);
                getLogger().debug("Policies directory source: [" + source.getURI() + "]");
                directory = new File(new URI(NetUtils.encodePath(source.getURI())));
            } catch (final Exception e) {
                throw new AccessControlException("Resolving policies directory failed: ", e);
            } finally {
                if (resolver != null) {
                    if (source != null) {
                        resolver.release(source);
                    }
                    getServiceManager().release(resolver);
                }
            }

            getLogger().debug(
                    "Policies directory resolved to [" + directory.getAbsolutePath() + "]");
            setPoliciesDirectory(directory);
        }

        return this.policiesDirectory;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.serviceManager = manager;
        this.cache = (SourceCache) manager.lookup(SourceCache.ROLE);
    }

    /**
     * Sets the policies directory.
     * 
     * @param directory The directory.
     * @throws AccessControlException if the directory is not a directory
     */
    public void setPoliciesDirectory(File directory) throws AccessControlException {
        getLogger().debug("Setting policies directory [" + directory.getAbsolutePath() + "]");
        if (!directory.isDirectory()) {
            throw new AccessControlException("Policies directory invalid: ["
                    + directory.getAbsolutePath() + "]");
        }
        this.policiesDirectory = directory;
    }

    /**
     * @see org.apache.lenya.ac.InheritingPolicyManager#getPolicies(org.apache.lenya.ac.AccreditableManager,
     *      java.lang.String)
     */
    public Policy[] getPolicies(AccreditableManager controller, String url)
            throws AccessControlException {
        
        if (!url.startsWith("/")) {
            throw new IllegalArgumentException("The URL [" + url + "] doesn't start with a slash!");
        }
        
        url = url.substring(1);

        List policies = new LinkedList();
        HashMap orderedPolicies = new LinkedHashMap();
        int position = 1;
        Policy policy;
        String[] directories = url.split("/");
        url = directories[0] + "/";

        for (int i = 1; i < directories.length; i++) {
            url += directories[i] + "/";
            policy = buildSubtreePolicy(controller, url);
            orderedPolicies.put(String.valueOf(position), policy);
            position++;
        }
        for (int i = orderedPolicies.size(); i > 0; i--) {
            policies.add(orderedPolicies.get(String.valueOf(i)));
        }
        return (DefaultPolicy[]) policies.toArray(new DefaultPolicy[policies.size()]);
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {

        if (getCache() != null) {
            getServiceManager().release(getCache());
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Disposing [" + this + "]");
        }
    }

    /**
     * Removes an accreditable from all policies within a certain directory
     * tree.
     * 
     * @param manager The accreditable manager which owns the accreditable.
     * @param accreditable The accreditable to remove.
     * @param policyDirectory The directory where the policies are located.
     * @throws AccessControlException when an error occurs.
     */
    protected void removeAccreditable(AccreditableManager manager, Accreditable accreditable,
            File policyDirectory) throws AccessControlException {

        File[] policyFiles = policyDirectory.listFiles(new SubtreeFileFilter(SUBTREE_FILENAME));

        try {
            RemovedAccreditablePolicyBuilder builder = new RemovedAccreditablePolicyBuilder(manager);
            builder.setRemovedAccreditable(accreditable);
            for (int i = 0; i < policyFiles.length; i++) {

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Removing roles");
                    getLogger().debug("    Accreditable: [" + accreditable + "]");
                    getLogger().debug(
                            "    File:         [" + policyFiles[i].getAbsolutePath() + "]");
                }

                InputStream stream = new FileInputStream(policyFiles[i]);
                ModifiablePolicy policy = builder.buildPolicy(stream);
                policy.removeRoles(accreditable);
                savePolicy(policy, policyFiles[i]);
            }
        } catch (final FileNotFoundException e1) {
            throw new AccessControlException(e1);
        }

        File[] directories = policyDirectory.listFiles(new IsDirectoryFileFilter());

        for (int i = 0; i < directories.length; i++) {
            removeAccreditable(manager, accreditable, directories[i]);
        }

    }

    /**
     * @see org.apache.lenya.ac.PolicyManager#accreditableRemoved(org.apache.lenya.ac.AccreditableManager,
     *      org.apache.lenya.ac.Accreditable)
     */
    public void accreditableRemoved(AccreditableManager manager, Accreditable accreditable)
            throws AccessControlException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("An accreditable was removed: [" + accreditable + "]");
        }

        removeAccreditable(manager, accreditable, getPoliciesDirectory());
    }

    private ServiceManager serviceManager;

    /**
     * Returns the service manager.
     * 
     * @return A service manager.
     */
    protected ServiceManager getServiceManager() {
        return this.serviceManager;
    }

    /**
     * @see org.apache.lenya.ac.PolicyManager#accreditableAdded(org.apache.lenya.ac.AccreditableManager,
     *      org.apache.lenya.ac.Accreditable)
     */
    public void accreditableAdded(AccreditableManager manager, Accreditable accreditable)
            throws AccessControlException {
    }

    public Credential[] getCredentials(AccreditableManager controller, String url)
            throws AccessControlException {

        if (!url.startsWith("/")) {
            throw new IllegalArgumentException("The URL [" + url + "] doesn't start with a slash!");
        }
        
        url = url.substring(1);

        HashMap orderedCredential = new LinkedHashMap();
        int position = 1;

        String[] directories = url.split("/");
        url = directories[0] + "/";

        for (int i = 1; i < directories.length; i++) {
            url += directories[i] + "/";
            Policy policy = buildSubtreePolicy(controller, url);
            Credential[] tmp = policy.getCredentials();
            // we need to revert the order of the credentials
            // to keep the most important policy on top
            for (int j = tmp.length - 1; j >= 0; j--) {
                Credential credential = tmp[j];
                orderedCredential.put(String.valueOf(position), credential);
                position++;
            }
        }
        Credential[] returnCredential = new CredentialImpl[orderedCredential.size()];
        int y = 0;
        for (int i = orderedCredential.size(); i > 0; i--) {
            returnCredential[y] = (Credential) orderedCredential.get(String.valueOf(i));
            y++;
        }

        return returnCredential;
    }

    public Role[] getGrantedRoles(AccreditableManager accreditableManager, Identity identity,
            String url) throws AccessControlException {
        Role[] roles = accreditableManager.getRoleManager().getRoles();
        Set grantedRoles = new HashSet();
        Policy policy = getPolicy(accreditableManager, url);
        for (int i = 0; i < roles.length; i++) {
            if (policy.check(identity, roles[i]) == Policy.RESULT_GRANTED) {
                grantedRoles.add(roles[i]);
            }
        }
        return (Role[]) grantedRoles.toArray(new Role[grantedRoles.size()]);
    }

}