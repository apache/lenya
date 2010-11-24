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

package org.apache.lenya.cms.ac;

import javax.servlet.http.HttpServletRequest;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Credential;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.InheritingPolicyManager;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Repository;
import org.apache.lenya.cms.publication.Session;
//import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.utils.URLInformation;

/**
 * A PolicyManager which is capable of mapping all URLs of a document to the appropriate canonical
 * URL, e.g. <code>/foo/bar_de.print.html</code> is mapped to <code>/foo/bar</code>.
 */
public class DocumentPolicyManagerWrapper extends AbstractLogEnabled implements
        InheritingPolicyManager, Configurable {

    private InheritingPolicyManager policyManager;
    private Repository repository;

    /**
     * Returns the URI which is used to obtain the policy for a webapp URL.
     * @param webappUrl The web application URL.
     * @return A string.
     * @throws AccessControlException when something went wrong.
     */
    protected String getPolicyURL(String webappUrl) throws AccessControlException {
        return getPolicyUrlCorrect(webappUrl);
    }

    /**
     * Returns the URI which is used to obtain the policy for a webapp URL.
     * @param webappUrl The web application URL.
     * @return A string.
     * @throws AccessControlException when something went wrong.
     */
    protected String getPolicyUrlCorrect(String webappUrl) throws AccessControlException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Resolving policy for webapp URL [" + webappUrl + "]");
        }

        String url = null;
        //URLInformation info = new URLInformation(webappUrl);
        URLInformation info = new URLInformation();
        String pubId = info.getPublicationId();
        String area = info.getArea();

        if (pubId != null && area != null && info.getDocumentUrl().length() > 1) {
            try {
                HttpServletRequest request = getRequest();
                Session session = this.repository.getSession(request);
                Publication pub = session.getPublication(pubId);
                DocumentLocator loc = pub.getDocumentBuilder().getLocator(session, webappUrl);
                url = "/" + pubId + "/" + area + loc.getPath();
            } catch (Exception e) {
                throw new AccessControlException(e);
            }
        }

        if (url == null) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("    URL does not refer to a document.");
            }
            url = webappUrl;
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("    Using URL: [" + url + "]");
        }
        return url;
    }

    protected HttpServletRequest getRequest() {
        ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        HttpServletRequest request = process.getRequest();
        return request;
    }

    protected String getPolicyUrlFast(String webappUrl) throws AccessControlException {
        String strippedUrl = strip(strip(webappUrl, '.'), '_');
        return strippedUrl;
    }

    protected String strip(String strippedUrl, char delimiter) {
        int lastDotIndex = strippedUrl.lastIndexOf(delimiter);
        if (lastDotIndex != -1) {
            strippedUrl = strippedUrl.substring(0, lastDotIndex);
        }
        return strippedUrl;
    }

    /**
     * Returns the publication for a certain URL.
     * @param url The webapp url.
     * @return A publication.
     * @throws AccessControlException when the publication could not be created.
     */
    protected Publication getPublication(String url) throws AccessControlException {
        getLogger().debug("Building publication");

        try {
            Session session = this.repository.getSession(getRequest());
            return session.getUriHandler().getPublication(url);
        } catch (Exception e) {
            throw new AccessControlException(e);
        }
    }

    /**
     * @return Returns the policyManager.
     */
    public InheritingPolicyManager getPolicyManager() {
        return this.policyManager;
    }

    /**
     * @param _policyManager The policyManager to set.
     */
    public void setPolicyManager(InheritingPolicyManager _policyManager) {
        this.policyManager = _policyManager;
    }

    public Policy buildSubtreePolicy(AccreditableManager controller, String url)
            throws AccessControlException {
        return getPolicyManager().buildSubtreePolicy(controller, getPolicyURL(url));
    }

    public Policy[] getPolicies(AccreditableManager controller, String url)
            throws AccessControlException {
        return getPolicyManager().getPolicies(controller, getPolicyURL(url));
    }

    public void saveSubtreePolicy(String url, Policy policy) throws AccessControlException {
        getPolicyManager().saveSubtreePolicy(getPolicyURL(url), policy);
    }

    /**
     * @see org.apache.lenya.ac.PolicyManager#getPolicy(org.apache.lenya.ac.AccreditableManager,
     *      java.lang.String)
     */
    public Policy getPolicy(AccreditableManager controller, String url)
            throws AccessControlException {
        return getPolicyManager().getPolicy(controller, getPolicyURL(url));
    }

    /**
     * @see org.apache.lenya.ac.PolicyManager#accreditableRemoved(org.apache.lenya.ac.AccreditableManager,
     *      org.apache.lenya.ac.Accreditable)
     */
    public void accreditableRemoved(AccreditableManager manager, Accreditable accreditable)
            throws AccessControlException {
        getPolicyManager().accreditableRemoved(manager, accreditable);

    }

    String ELEMENT_POLICY_MANAGER = "policy-manager";
    String ATTRIBUTE_TYPE = "type";

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException {
        Configuration policyManagerConfiguration = configuration.getChild(
                this.ELEMENT_POLICY_MANAGER, false);
        if (policyManagerConfiguration != null) {
            String type = null;
            try {
                type = policyManagerConfiguration.getAttribute(this.ATTRIBUTE_TYPE);

                PolicyManager _policyManager = (PolicyManager) WebAppContextUtils
                        .getCurrentWebApplicationContext().getBean(PolicyManager.ROLE + "/" + type);

                if (!(_policyManager instanceof InheritingPolicyManager)) {
                    throw new AccessControlException("The " + getClass().getName()
                            + " can only be used with an "
                            + InheritingPolicyManager.class.getName() + ".");
                }

                DefaultAccessController.configureOrParameterize(_policyManager,
                        policyManagerConfiguration);
                setPolicyManager((InheritingPolicyManager) _policyManager);
            } catch (final ConfigurationException e1) {
                throw new ConfigurationException("Obtaining policy manager for type [" + type
                        + "] failed: ", e1);
            } catch (final ParameterException e1) {
                throw new ConfigurationException("Obtaining policy manager for type [" + type
                        + "] failed: ", e1);
            } catch (final AccessControlException e1) {
                throw new ConfigurationException("Obtaining policy manager for type [" + type
                        + "] failed: ", e1);
            }
        }
    }

    /**
     * @see org.apache.lenya.ac.PolicyManager#accreditableAdded(org.apache.lenya.ac.AccreditableManager,
     *      org.apache.lenya.ac.Accreditable)
     */
    public void accreditableAdded(AccreditableManager manager, Accreditable accreditable)
            throws AccessControlException {
        getPolicyManager().accreditableAdded(manager, accreditable);
    }

    public Credential[] getCredentials(AccreditableManager controller, String url)
            throws AccessControlException {
        return getPolicyManager().getCredentials(controller, getPolicyURL(url));
    }

    public Role[] getGrantedRoles(AccreditableManager accreditableManager, Identity identity,
            String url) throws AccessControlException {
        return getPolicyManager().getGrantedRoles(accreditableManager, identity, getPolicyURL(url));
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

}