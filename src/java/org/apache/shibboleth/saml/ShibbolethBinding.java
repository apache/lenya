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
package org.apache.shibboleth.saml;

import java.util.Iterator;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.shibboleth.ShibbolethManager;
import org.opensaml.SAMLAuthorityBinding;
import org.opensaml.SAMLBinding;
import org.opensaml.SAMLBindingFactory;
import org.opensaml.SAMLException;
import org.opensaml.SAMLRequest;
import org.opensaml.SAMLResponse;
import org.opensaml.SAMLSOAPHTTPBinding;
import org.opensaml.TrustException;

import edu.internet2.middleware.shibboleth.metadata.AttributeAuthorityDescriptor;
import edu.internet2.middleware.shibboleth.metadata.Endpoint;

/**
 * Description:<br>
 * Some code borrowed and adapted from Internet2 Shibboleth SP implementation.
 * 
 * <P>
 * Initial Date: 24.08.2005 <br>
 * @author Mike Stock
 */
public class ShibbolethBinding extends AbstractLogEnabled {
    
    private ServiceManager manager;
    private ShibbolethManager shibManager;

    /**
     * @param manager The service manager.
     * @param logger The logger.
     */
    public ShibbolethBinding(ServiceManager manager, Logger logger) {
        this.manager = manager;
        ContainerUtil.enableLogging(this, logger);
    }
    
    protected ShibbolethManager getShibbolethManager() {
        if (this.shibManager == null) {
            try {
                this.shibManager = (ShibbolethManager) this.manager.lookup(ShibbolethManager.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return this.shibManager;
    }

    /**
     * Send a SAMLRequest and get back a SAMLResponse.
     * <p>
     * Although this logic could be generalized, this version declares the
     * arguments to be of specific types (an AA role) so it can only be used to
     * send the Attribute Query and get back the Attribute Assertions.
     * 
     * @param req SAMLRequest to send
     * @param role AttributeAuthorityRole representing destination
     * @param audiences Audience strings to check SAML conditions
     * @param bindings Stupid idea. Don't use this parameter
     * @return The SAMLResponse
     * @throws SAMLException
     */
    public SAMLResponse send(SAMLRequest req, AttributeAuthorityDescriptor role,
            String[] audiences, SAMLAuthorityBinding[] bindings) throws SAMLException {

        SAMLBinding sbinding = null;
        SAMLResponse resp = null;
        String prevBinding = null;
        
        ShibbolethManager shibManager = getShibbolethManager();

        /*
         * Try any inline bindings provided by 1.0/1.1 IdPs.
         */
        if (bindings != null) {
            for (int ibinding = 0; ibinding < bindings.length; ibinding++) {
                try {
                    SAMLAuthorityBinding binding = bindings[ibinding];
                    if (!binding.getBinding().equals(prevBinding)) {
                        prevBinding = binding.getBinding();
                        sbinding = SAMLBindingFactory.getInstance(binding.getBinding());
                    }
                    resp = sbinding.send(binding.getLocation(), req);
                    shibManager.validateResponseSignatures(role, resp);
                    return resp;
                } catch (TrustException e) {
                    getLogger().error("Unable to validate signatures on attribute response: ", e);
                    continue;
                } catch (SAMLException e) {
                    getLogger().error("Unable to query attributes: ", e);
                    continue;
                }
            }
        }

        /*
         * Try each metadata endpoint...
         */
        Iterator ends = role.getAttributeServiceManager().getEndpoints();
        while (ends.hasNext()) {
            Endpoint endpoint = (Endpoint) ends.next();
            try {
                if (!endpoint.getBinding().equals(prevBinding)) {
                    prevBinding = endpoint.getBinding();
                    sbinding = SAMLBindingFactory.getInstance(endpoint.getBinding());
                }
                if (sbinding instanceof SAMLSOAPHTTPBinding) {
                    SAMLSOAPHTTPBinding httpbind = (SAMLSOAPHTTPBinding) sbinding;
                    httpbind.addHook(new HttpHookImpl(this.manager, getLogger()));
                }
                resp = sbinding.send(endpoint.getLocation(), req);
                shibManager.validateResponseSignatures(role, resp);
                return resp;
            } catch (TrustException e) {
                getLogger().error("Unable to validate signatures on attribute response: ", e);
                continue;
            } catch (SAMLException e) {
                getLogger().error("Unable to query attributes: ", e);
                continue;
            }
        }
        return null;
    }
}
