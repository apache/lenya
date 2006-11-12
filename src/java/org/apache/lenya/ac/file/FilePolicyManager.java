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
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.util.NetUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.cache.CachingException;
import org.apache.lenya.ac.cache.SourceCache;
import org.apache.lenya.ac.impl.Credential;
import org.apache.lenya.ac.impl.DefaultPolicy;
import org.apache.lenya.ac.impl.InheritingPolicyManager;
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

    /**
     * Creates a new FilePolicyManager.
     */
    public FilePolicyManager() {
    }

    /**
     * Returns the source cache.
     * 
     * @return A source cache.
     */
    protected SourceCache getCache() {
        return cache;
    }

    private SourceCache cache;

    protected static final String URL_FILENAME = "url-policy.acml";
    protected static final String SUBTREE_FILENAME = "subtree-policy.acml";
    protected static final String USER_ADMIN_URL = "/admin/users/";

    private static final FileFilter POLICY_ACML_FILEFILTER = 
        FileFilterUtils.orFileFilter(FileFilterUtils.nameFileFilter(SUBTREE_FILENAME), FileFilterUtils.nameFileFilter(URL_FILENAME));

    /**
     * Builds the URL policy for a URL from a file. When the file is not present, an empty policy is
     * returned.
     * 
     * @param controller The access controller to use.
     * @param url The URL inside the web application.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    public DefaultPolicy buildURLPolicy(AccreditableManager controller, String url)
            throws AccessControlException {
        return buildPolicy(controller, url, URL_FILENAME);
    }

    /**
     * Builds a subtree policy from a file. When the file is not present, an empty policy is
     * returned.
     * 
     * @param controller The access controller to use.
     * @param url The URL inside the web application.
     * @return A policy.
     * @throws AccessControlException when something went wrong.
     */
    public DefaultPolicy buildSubtreePolicy(AccreditableManager controller, String url)
            throws AccessControlException {
        return buildPolicy(controller, url, SUBTREE_FILENAME);
    }

    /**
     * Builds a policy from a file. When the file is not present, an empty policy is returned.
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
     * 
     * @throws AccessControlException if an error occurs
     */
    protected String getPolicySourceURI(String url, String policyFilename)
            throws AccessControlException {
        if (url.startsWith("/")) {
            url = url.substring(1);
        }

        File policyFile = new File(getPoliciesDirectory(), url + File.separator + policyFilename);
        String policyUri = policyFile.toURI().toString();
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
        File file;
        try {
            file = new File(new URI(NetUtils.encodePath(fileUri)));
        } catch (final Exception e) {
            throw new AccessControlException(e);
        }
        return file;
    }

    /**
     * Saves a URL policy.
     * 
     * @param url The URL to save the policy for.
     * @param policy The policy to save.
     * @throws AccessControlException when something went wrong.
     */
    public void saveURLPolicy(String url, DefaultPolicy policy) throws AccessControlException {
        getLogger().debug("Saving URL policy for URL [" + url + "]");
        savePolicy(url, policy, URL_FILENAME);
    }

    /**
     * Saves a Subtree policy.
     * 
     * @param url The url to save the policy for.
     * @param policy The policy to save.
     * @throws AccessControlException when something went wrong.
     */
    public void saveSubtreePolicy(String url, DefaultPolicy policy) throws AccessControlException {
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
    protected void savePolicy(String url, DefaultPolicy policy, String filename)
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
    protected void savePolicy(DefaultPolicy policy, File file) throws AccessControlException {
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
            policiesDirectoryUri = parameters.getParameter(DIRECTORY_PARAMETER);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Policies directory URI: " + policiesDirectoryUri);
            }
        }
    }

    /**
     * Get the path to the policies directory.
     * 
     * @return the path to the policies directory
     * 
     * @throws AccessControlException if an error occurs
     */
    public File getPoliciesDirectory() throws AccessControlException {

        if (policiesDirectory == null) {
            SourceResolver resolver = null;
            Source source = null;
            File directory;

            try {
                resolver = (SourceResolver) getServiceManager().lookup(SourceResolver.ROLE);
                source = resolver.resolveURI(policiesDirectoryUri);
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

        return policiesDirectory;
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
     * 
     * @throws AccessControlException if the directory is not a directory
     */
    public void setPoliciesDirectory(File directory) throws AccessControlException {
        getLogger().debug("Setting policies directory [" + directory.getAbsolutePath() + "]");
        if (!directory.isDirectory()) {
            throw new AccessControlException("Policies directory invalid: ["
                    + directory.getAbsolutePath() + "]");
        }
        policiesDirectory = directory;
    }

    /**
     * @see org.apache.lenya.ac.impl.InheritingPolicyManager#getPolicies(org.apache.lenya.ac.AccreditableManager,
     *      java.lang.String)
     */
    public DefaultPolicy[] getPolicies(AccreditableManager controller, String url)
            throws AccessControlException {

        List policies = new ArrayList();

        Policy policy = buildURLPolicy(controller, url);
        policies.add(policy);

        String[] directories = url.split("/");
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < directories.length; i++) {
            buf.append(directories[i]).append("/");
            policy = buildSubtreePolicy(controller, buf.toString());
            policies.add(policy);
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
     * Removes an accreditable from all policies within a certain directory tree.
     * 
     * @param manager The accreditable manager which owns the accreditable.
     * @param accreditable The accreditable to remove.
     * @param policyDirectory The directory where the policies are located.
     * @throws AccessControlException when an error occurs.
     */
    protected void removeAccreditable(AccreditableManager manager, Accreditable accreditable,
            File policyDirectory) throws AccessControlException {

        File[] policyFiles = policyDirectory.listFiles(POLICY_ACML_FILEFILTER);

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
                DefaultPolicy policy = builder.buildPolicy(stream);
                policy.removeRoles(accreditable);
                savePolicy(policy, policyFiles[i]);
            }
        } catch (Exception e) {
            throw new AccessControlException(e);
        }

        File[] directories = policyDirectory.listFiles((FileFilter)FileFilterUtils.directoryFileFilter());

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

        if (accreditable instanceof User) {
            Role role = URLPolicy.getAuthorRole(manager);
            if (role != null) {
                String url = USER_ADMIN_URL + ((User) accreditable).getId() + ".html";
                DefaultPolicy policy = buildSubtreePolicy(manager, url);
                Credential credential = policy.getCredential(accreditable);
                if (credential != null && credential.contains(role)) {
                    policy.removeRole(accreditable, role);
                }
                saveSubtreePolicy(url, policy);
            }
        }
    }

    private ServiceManager serviceManager;

    /**
     * Returns the service manager.
     * 
     * @return A service manager.
     */
    protected ServiceManager getServiceManager() {
        return serviceManager;
    }

    /**
     * @see org.apache.lenya.ac.PolicyManager#accreditableAdded(org.apache.lenya.ac.AccreditableManager,
     *      org.apache.lenya.ac.Accreditable)
     */
    public void accreditableAdded(AccreditableManager manager, Accreditable accreditable)
            throws AccessControlException {
        if (accreditable instanceof User) {
            Role role = URLPolicy.getAuthorRole(manager);
            if (role != null) {
                String url = USER_ADMIN_URL + ((User) accreditable).getId() + ".html";
                DefaultPolicy policy = buildSubtreePolicy(manager, url);
                policy.addRole(accreditable, role);
                saveSubtreePolicy(url, policy);
            }
        }
    }
}
