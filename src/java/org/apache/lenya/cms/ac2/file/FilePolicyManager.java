/*
$Id: FilePolicyManager.java,v 1.13 2003/08/07 10:23:02 andreas Exp $
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
import org.apache.lenya.cms.ac2.AccreditableManager;
import org.apache.lenya.cms.ac2.DefaultPolicy;
import org.apache.lenya.cms.ac2.InheritingPolicyManager;
import org.apache.lenya.cms.ac2.Policy;
import org.apache.lenya.cms.ac2.PolicyBuilder;
import org.apache.lenya.cms.ac2.URLPolicy;
import org.apache.lenya.util.CacheMap;
import org.apache.lenya.xml.DocumentHelper;

import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A PolicyBuilder is used to build policies.
 * @author andreas
 */
public class FilePolicyManager
    extends AbstractLogEnabled
    implements InheritingPolicyManager, Parameterizable, Serviceable {

    /**
     * Creates a new PolicyBuilder.
     */
    public FilePolicyManager() {
    }

    private File policyDirectory;

    protected static final String URL_FILENAME = "url-policy.acml";
    protected static final String SUBTREE_FILENAME = "subtree-policy.acml";

    /**
     * Builds the URL policy for a URL from a file.
     * When the file is not present, an empty policy is returned.
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
     * Builds a subtree policy from a file. When the file is not present, an empty policy is returned.
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
        DefaultPolicy policy;
        getLogger().debug("Building policy for URL [" + url + "]");
        File policyFile = getPolicyFile(url, policyFilename);
        getLogger().debug("Policy file resolved to: " + policyFile.getAbsolutePath());
        //        getLogger().debug("", new IllegalStateException());

        if (policyFile.exists()) {
            policy = PolicyBuilder.getInstance().buildPolicy(controller, policyFile);
        } else {
            policy = new DefaultPolicy();
        }

        return policy;
    }

    /**
     * Returns the policy file for a URL and a policy filename.
     * @param url The url to get the file for.
     * @param policyFilename The name of the policy file.
     * @return A file object.
     * 
     * @throws AccessControlException if an error occurs
     */
    protected File getPolicyFile(String url, String policyFilename) throws AccessControlException {
        if (url.startsWith("/")) {
            url = url.substring(1);
        }

        String path = url.replace('/', File.separatorChar) + File.separator + policyFilename;
        File policyFile = new File(getPoliciesDirectory(), path);

        return policyFile;
    }

    /**
     * Saves a URL policy.
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
     * @param url The URL to save the policy for.
     * @param policy The policy.
     * @param filename The file.
     * @throws AccessControlException if something goes wrong.
     */
    protected void savePolicy(String url, DefaultPolicy policy, String filename)
        throws AccessControlException {
            
        String key = getCacheKey(url);
        cache.remove(key);

        Document document = PolicyBuilder.getInstance().savePolicy(policy);
        File file = getPolicyFile(url, filename);

        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            DocumentHelper.writeDocument(document, file);
        } catch (Exception e) {
            throw new AccessControlException("Path: [" + file.getAbsolutePath() + "]", e);
        }
    }

    protected static final int CACHE_CAPACITY = 1000;
    private static Map cache = new CacheMap(CACHE_CAPACITY);

    /**
     * @see org.apache.lenya.cms.ac2.PolicyManager#getPolicy(AccreditableManager, Publication, java.lang.String)
     */
    public Policy getPolicy(AccreditableManager controller, String url)
        throws AccessControlException {

        String key = getCacheKey(url);
        Policy policy = (Policy) cache.get(key);
        if (policy == null) {
            policy = new URLPolicy(controller, url, this);
            cache.put(key, policy);
        }

        return policy;
    }

    /**
     * Creates a cache key for a URL.
     * @param url The url.
     * @return A string value.
     * @throws AccessControlException if something went wrong.
     */
    protected String getCacheKey(String url) throws AccessControlException {
        String key;
        try {
            key = getPoliciesDirectory().getCanonicalPath() + ":" + url;
        } catch (IOException e) {
            throw new AccessControlException(e);
        }
        return key;
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

            getLogger().debug(
                "Policies directory resolved to [" + directory.getAbsolutePath() + "]");
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
     * 
     * @throws AccessControlException if the directory is not a directory
     */
    public void setPoliciesDirectory(File directory) throws AccessControlException {
        if (!directory.isDirectory()) {
            throw new AccessControlException(
                "Policies directory invalid: [" + directory.getAbsolutePath() + "]");
        }
        policiesDirectory = directory;
    }

    /**
     * @see org.apache.lenya.cms.ac2.InheritingPolicyManager#getPolicies(org.apache.lenya.cms.ac2.AccreditableManager, java.lang.String)
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

}
