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

package org.apache.lenya.cms.ac;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Credential;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.impl.PolicyBuilder;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Policy manager based on Cocoon sitemaps.
 * @version $Id$
 */
public class SitemapPolicyManager extends AbstractLogEnabled implements PolicyManager, Serviceable {

    private Credential[] credentials;

	/**
     * @see org.apache.lenya.ac.PolicyManager#getPolicy(org.apache.lenya.ac.AccreditableManager,
     *      java.lang.String)
     */
    public Policy getPolicy(AccreditableManager accreditableManager, String url)
            throws AccessControlException {

        url = url.substring(1);

        int slashIndex = url.indexOf("/");
        if (slashIndex == -1) {
            slashIndex = url.length();
        }

        String publicationId = url.substring(0, slashIndex);
        url = url.substring(publicationId.length());

        SourceResolver resolver = null;
        Policy policy = null;
        Source source = null;
        try {
            resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);

            String policyUrl = publicationId + "/policies" + url + ".acml";
            getLogger().debug("Policy URL: " + policyUrl);
            source = resolver.resolveURI("cocoon://" + policyUrl);
            Document document = DocumentHelper.readDocument(source.getInputStream());
            policy = new PolicyBuilder(accreditableManager).buildPolicy(document);
            this.credentials=policy.getCredentials();
        } catch (SourceNotFoundException e) {
            throw new AccessControlException(e);
        } catch (ServiceException e) {
            throw new AccessControlException(e);
        } catch (MalformedURLException e) {
            throw new AccessControlException(e);
        } catch (IOException e) {
            throw new AccessControlException(e);
        } catch (ParserConfigurationException e) {
            throw new AccessControlException(e);
        } catch (SAXException e) {
            throw new AccessControlException(e);
        } catch (AccessControlException e) {
            throw new AccessControlException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                getManager().release(resolver);
            }
        }

        return policy;
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager _manager) throws ServiceException {
        this.manager = _manager;
    }

    /**
     * Returns the service manager.
     * @return A service manager.
     */
    public ServiceManager getManager() {
        return this.manager;
    }

    /**
     * @see org.apache.lenya.ac.PolicyManager#accreditableRemoved(org.apache.lenya.ac.AccreditableManager,
     *      org.apache.lenya.ac.Accreditable)
     */
    public void accreditableRemoved(AccreditableManager _manager, Accreditable accreditable)
            throws AccessControlException {
	    // do nothing
    }

    /**
     * @see org.apache.lenya.ac.PolicyManager#accreditableAdded(org.apache.lenya.ac.AccreditableManager,
     *      org.apache.lenya.ac.Accreditable)
     */
    public void accreditableAdded(AccreditableManager _manager, Accreditable accreditable)
            throws AccessControlException {
	    // do nothing
    }

	public Credential[] getCredentials(AccreditableManager controller, String url) throws AccessControlException {
		Credential[] copy = new Credential[credentials.length];
		for (int i = 0; i < credentials.length; i++) {
			copy[i]=credentials[i];
		}
		return copy;
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