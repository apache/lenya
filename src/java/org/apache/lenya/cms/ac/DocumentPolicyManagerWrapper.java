/*
 * $Id: DocumentPolicyManagerWrapper.java,v 1.2 2004/02/02 02:50:38 stefano Exp $ <License>
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
 * 
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
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
	 * @see org.apache.lenya.cms.ac2.InheritingPolicyManager#buildURLPolicy(org.apache.lenya.cms.ac2.AccreditableManager,
	 *      java.lang.String)
	 */
    public DefaultPolicy buildURLPolicy(AccreditableManager controller, String url)
        throws AccessControlException {
        return getPolicyManager().buildURLPolicy(controller, getPolicyURL(url));
    }

    /**
	 * @see org.apache.lenya.cms.ac2.InheritingPolicyManager#buildSubtreePolicy(org.apache.lenya.cms.ac2.AccreditableManager,
	 *      java.lang.String)
	 */
    public DefaultPolicy buildSubtreePolicy(AccreditableManager controller, String url)
        throws AccessControlException {
        return getPolicyManager().buildSubtreePolicy(controller, getPolicyURL(url));
    }

    /**
	 * @see org.apache.lenya.cms.ac2.InheritingPolicyManager#getPolicies(org.apache.lenya.cms.ac2.AccreditableManager,
	 *      java.lang.String)
	 */
    public DefaultPolicy[] getPolicies(AccreditableManager controller, String url)
        throws AccessControlException {
        return getPolicyManager().getPolicies(controller, getPolicyURL(url));
    }

    /**
	 * @see org.apache.lenya.cms.ac2.InheritingPolicyManager#saveURLPolicy(java.lang.String,
	 *      org.apache.lenya.cms.ac2.DefaultPolicy)
	 */
    public void saveURLPolicy(String url, DefaultPolicy policy) throws AccessControlException {
        getPolicyManager().saveURLPolicy(getPolicyURL(url), policy);

    }

    /**
	 * @see org.apache.lenya.cms.ac2.InheritingPolicyManager#saveSubtreePolicy(java.lang.String,
	 *      org.apache.lenya.cms.ac2.DefaultPolicy)
	 */
    public void saveSubtreePolicy(String url, DefaultPolicy policy) throws AccessControlException {
        getPolicyManager().saveSubtreePolicy(getPolicyURL(url), policy);
    }

    /**
	 * @see org.apache.lenya.cms.ac2.PolicyManager#getPolicy(org.apache.lenya.cms.ac2.AccreditableManager,
	 *      java.lang.String)
	 */
    public Policy getPolicy(AccreditableManager controller, String url)
        throws AccessControlException {
        return getPolicyManager().getPolicy(controller, getPolicyURL(url));
    }

    /**
	 * @see org.apache.lenya.cms.ac2.PolicyManager#accreditableRemoved(org.apache.lenya.cms.ac2.AccreditableManager,
	 *      org.apache.lenya.cms.ac2.Accreditable)
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
