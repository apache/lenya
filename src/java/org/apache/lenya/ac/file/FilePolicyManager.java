/*
 * $Id: FilePolicyManager.java,v 1.3 2004/02/05 08:50:57 andreas Exp $ <License>
 * 
 * ============================================================================ The Apache Software
 * License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica- tion, are permitted
 * provided that the following conditions are met: 1. Redistributions of source code must retain
 * the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)." Alternately, this acknowledgment may
 * appear in the software itself, if and wherever such third-party acknowledgments normally appear. 4.
 * The names "Apache Lenya" and "Apache Software Foundation" must not be used to endorse or promote
 * products derived from this software without prior written permission. For written permission,
 * please contact apache@apache.org. 5. Products derived from this software may not be called
 * "Apache", nor may "Apache" appear in their name, without prior written permission of the Apache
 * Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU- DING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation and was originally created by Michael Wechner <michi@apache.org> .
 * For more information on the Apache Soft- ware Foundation, please see <http://www.apache.org/> .
 * 
 * Lenya includes software developed by the Apache Software Foundation, W3C, DOM4J Project,
 * BitfluxEditor, Xopus, and WebSHPINX. </License>
 */
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
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.cache.CachingException;
import org.apache.lenya.ac.cache.SourceCache;
import org.apache.lenya.ac.impl.DefaultPolicy;
import org.apache.lenya.ac.impl.InheritingPolicyManager;
import org.apache.lenya.ac.impl.PolicyBuilder;
import org.apache.lenya.ac.impl.RemovedAccreditablePolicyBuilder;
import org.apache.lenya.ac.impl.URLPolicy;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;

/**
 * A PolicyBuilder is used to build policies.
 * 
 * @author andreas
 */
public class FilePolicyManager
    extends AbstractLogEnabled
    implements InheritingPolicyManager, Parameterizable, Disposable, Serviceable {

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

    /**
	 * Builds the URL policy for a URL from a file. When the file is not present, an empty policy
	 * is returned.
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
    protected DefaultPolicy buildPolicy(
        AccreditableManager controller,
        String url,
        String policyFilename)
        throws AccessControlException {

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

        File policyFile = new File(getPoliciesDirectory(), url + "/" + policyFilename);
        String policyUri = policyFile.toURI().toString(); 
        getLogger().debug("Computing policy URI [" + policyUri + "]");
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
            file = new File(new URI(fileUri));
        } catch (Exception e) {
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
            file.getParentFile().mkdirs();
            file.createNewFile();
            DocumentHelper.writeDocument(document, file);
        } catch (Exception e) {
            throw new AccessControlException("Path: [" + file.getAbsolutePath() + "]", e);
        }
    }

    /**
	 * @see org.apache.lenya.cms.ac2.PolicyManager#getPolicy(AccreditableManager, Publication,
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
                directory = new File(new URI(source.getURI()));
            } catch (Exception e) {
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
            throw new AccessControlException(
                "Policies directory invalid: [" + directory.getAbsolutePath() + "]");
        }
        policiesDirectory = directory;
    }

    /**
	 * @see org.apache.lenya.cms.ac2.InheritingPolicyManager#getPolicies(org.apache.lenya.cms.ac2.AccreditableManager,
	 *      java.lang.String)
	 */
    public DefaultPolicy[] getPolicies(AccreditableManager controller, String url)
        throws AccessControlException {
        
        List policies = new ArrayList();

        Policy policy = buildURLPolicy(controller, url);
        policies.add(policy);

        String[] directories = url.split("/");
        url = "";

        for (int i = 0; i < directories.length; i++) {
            url += directories[i] + "/";
            policy = buildSubtreePolicy(controller, url);
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
    protected void removeAccreditable(
        AccreditableManager manager,
        Accreditable accreditable,
        File policyDirectory)
        throws AccessControlException {

        File[] policyFiles = policyDirectory.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.getName().equals(SUBTREE_FILENAME)
                    || file.getName().equals(URL_FILENAME);
            }
        });

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

        File[] directories = policyDirectory.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });

        for (int i = 0; i < directories.length; i++) {
            removeAccreditable(manager, accreditable, directories[i]);
        }

    }

    /**
	 * @see org.apache.lenya.cms.ac2.PolicyManager#accreditableRemoved(org.apache.lenya.cms.ac2.AccreditableManager,
	 *      org.apache.lenya.cms.ac2.Accreditable)
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
        return serviceManager;
    }

}
