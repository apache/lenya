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

/* $Id: DocumentPolicyManagerWrapper.java,v 1.4 2004/03/08 16:48:20 gregor Exp $  */

package org.apache.lenya.cms.ac;

import java.io.File;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.ac.impl.DefaultPolicy;
import org.apache.lenya.ac.impl.InheritingPolicyManager;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

/**
 * A PolicyManager which is capable of mapping all URLs of a document to the appropriate canonical
 * URL, e.g. <code>/foo/bar_de.print.html</code> is mapped to <code>/foo/bar</code>.
 */
public class DocumentPolicyManagerWrapper
    extends AbstractLogEnabled
    implements InheritingPolicyManager, Serviceable, Configurable, Disposable {

    /**
	 * Ctor.
	 */
    public DocumentPolicyManagerWrapper() {
    }

    private InheritingPolicyManager policyManager;
    private ServiceSelector policyManagerSelector;

    /**
	 * Returns the URI which is used to obtain the policy for a webapp URL.
	 * 
	 * @param webappUrl The webapp URL.
	 * @return A string.
	 * @throws AccessControlException when something went wrong.
	 */
    protected String getPolicyURL(String webappUrl) throws AccessControlException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Resolving policy for webapp URL [" + webappUrl + "]");
        }

        Publication publication = getPublication(webappUrl);
        DocumentBuilder builder = publication.getDocumentBuilder();
        String url = null;
        try {
            if (builder.isDocument(publication, webappUrl)) {
                Document document = builder.buildDocument(publication, webappUrl);
                if (document.existsInAnyLanguage()) {
                    url = "/" + document.getArea() + document.getId();
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("    Document exists");
                        getLogger().debug("    Document ID: [" + document.getId() + "]");
                    }
                }
            }
        } catch (Exception e) {
            throw new AccessControlException(e);
        }

        if (url == null) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("    Document does not exist.");
            }
            url = webappUrl.substring(("/" + publication.getId()).length());
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("    Using URL: [" + url + "]");
        }
        return url;
    }

    /**
	 * Returns the publication for a certain URL.
	 * 
	 * @param url The webapp url.
	 * @return A publication.
	 * @throws AccessControlException when the publication could not be created.
	 */
    protected Publication getPublication(String url) throws AccessControlException {
        getLogger().debug("Building publication");

        Publication publication;
        Source source = null;
        SourceResolver resolver = null;

        try {
            resolver = (SourceResolver) serviceManager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI("context:///");
            File servletContext = SourceUtil.getFile(source);
            getLogger().debug("    Webapp URL:      [" + url + "]");
            getLogger().debug("    Serlvet context: [" + servletContext.getAbsolutePath() + "]");
            publication = PublicationFactory.getPublication(url, servletContext);
        } catch (Exception e) {
            throw new AccessControlException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                serviceManager.release(resolver);
            }
        }
        return publication;
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
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 */
    public void service(ServiceManager manager) throws ServiceException {
        this.serviceManager = manager;
    }

    /**
	 * @return Returns the policyManager.
	 */
    public InheritingPolicyManager getPolicyManager() {
        return policyManager;
    }

    /**
	 * @param policyManager The policyManager to set.
	 */
    public void setPolicyManager(InheritingPolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    /**
	 * @see org.apache.lenya.ac.impl.InheritingPolicyManager#buildURLPolicy(org.apache.lenya.ac.AccreditableManager,
	 *      java.lang.String)
	 */
    public DefaultPolicy buildURLPolicy(AccreditableManager controller, String url)
        throws AccessControlException {
        return getPolicyManager().buildURLPolicy(controller, getPolicyURL(url));
    }

    /**
	 * @see org.apache.lenya.ac.impl.InheritingPolicyManager#buildSubtreePolicy(org.apache.lenya.ac.AccreditableManager,
	 *      java.lang.String)
	 */
    public DefaultPolicy buildSubtreePolicy(AccreditableManager controller, String url)
        throws AccessControlException {
        return getPolicyManager().buildSubtreePolicy(controller, getPolicyURL(url));
    }

    /**
	 * @see org.apache.lenya.ac.impl.InheritingPolicyManager#getPolicies(org.apache.lenya.ac.AccreditableManager,
	 *      java.lang.String)
	 */
    public DefaultPolicy[] getPolicies(AccreditableManager controller, String url)
        throws AccessControlException {
        return getPolicyManager().getPolicies(controller, getPolicyURL(url));
    }

    /**
	 * @see org.apache.lenya.ac.impl.InheritingPolicyManager#saveURLPolicy(java.lang.String,
	 *      org.apache.lenya.ac.impl.DefaultPolicy)
	 */
    public void saveURLPolicy(String url, DefaultPolicy policy) throws AccessControlException {
        getPolicyManager().saveURLPolicy(getPolicyURL(url), policy);

    }

    /**
	 * @see org.apache.lenya.ac.impl.InheritingPolicyManager#saveSubtreePolicy(java.lang.String,
	 *      org.apache.lenya.ac.impl.DefaultPolicy)
	 */
    public void saveSubtreePolicy(String url, DefaultPolicy policy) throws AccessControlException {
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
        Configuration policyManagerConfiguration =
            configuration.getChild(ELEMENT_POLICY_MANAGER, false);
        if (policyManagerConfiguration != null) {
            String type = policyManagerConfiguration.getAttribute(ATTRIBUTE_TYPE);
            try {
                policyManagerSelector =
                    (ServiceSelector) getServiceManager().lookup(PolicyManager.ROLE + "Selector");

                PolicyManager policyManager = (PolicyManager) policyManagerSelector.select(type);

                if (!(policyManager instanceof InheritingPolicyManager)) {
                    throw new AccessControlException(
                        "The "
                            + getClass().getName()
                            + " can only be used with an "
                            + InheritingPolicyManager.class.getName()
                            + ".");
                }

                DefaultAccessController.configureOrParameterize(policyManager, policyManagerConfiguration);
                setPolicyManager((InheritingPolicyManager) policyManager);
            } catch (Exception e) {
                throw new ConfigurationException(
                    "Obtaining policy manager for type [" + type + "] failed: ",
                    e);
            }
        }
    } /**
	   * @see org.apache.avalon.framework.activity.Disposable#dispose()
	   */
    public void dispose() {
        if (policyManagerSelector != null) {
            if (getPolicyManager() != null) {
                policyManagerSelector.release(getPolicyManager());
            }
            getServiceManager().release(policyManagerSelector);
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Disposing [" + this +"]");
        }

    }

}
