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
package org.apache.shibboleth.impl;

import java.util.Iterator;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.shibboleth.ShibbolethManager;
import org.apache.shibboleth.ShibbolethModule;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLResponse;
import org.opensaml.TrustException;

import edu.internet2.middleware.shibboleth.common.provider.ShibbolethTrust;
import edu.internet2.middleware.shibboleth.metadata.AttributeAuthorityDescriptor;

/**
 * Shibboleth manager.
 */
public class ShibbolethManagerImpl extends AbstractLogEnabled implements ShibbolethManager, ThreadSafe,
        Serviceable {

    /**
     * The service role.
     */
    public static final String ROLE = ShibbolethManager.class.getName();
    private ShibbolethModule shibbolethModule;
    protected ServiceManager manager;

    /**
     * @param name The name.
     * @return An identity provider.
     */
    public String lookupIdentityProvider(String name) {
        try {
            return getShibbolethModule().getMetadata().lookup(name).getIDPSSODescriptor(
                    edu.internet2.middleware.shibboleth.common.XML.SHIB_NS)
                    .getSingleSignOnServiceManager().getDefaultEndpoint().getLocation();
        } catch (Exception e) {
            getLogger().error("Unable to lookup handle service with name: " + name);
            return null;
        }
    }

    protected ShibbolethModule getShibbolethModule() {
        if (this.shibbolethModule == null) {
            try {
                this.shibbolethModule = (ShibbolethModule) this.manager
                        .lookup(ShibbolethModule.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return this.shibbolethModule;
    }

    /**
     * Given a SAMLResponse, check the Response itself and every Assertion it
     * contains for a digital signature. If signed, call Trust to verify the
     * signature against the configured Certificates for this Role in the
     * Metadata.
     * 
     * @param role OriginSite
     * @param resp SAML response
     * @throws TrustException on failure
     */
    public void validateResponseSignatures(AttributeAuthorityDescriptor role, SAMLResponse resp)
            throws TrustException {

        // If the entire Response is signed, check it
        ShibbolethTrust shibTrust = new ShibbolethTrust();
        if (resp.isSigned() && !shibTrust.validate(resp, role)) {
            throw new TrustException("Unable to validate signature of response");
        }

        // Now check each Assertion in the Response for a signature
        Iterator assertions = resp.getAssertions();
        while (assertions.hasNext()) {
            SAMLAssertion assertion = (SAMLAssertion) assertions.next();
            if (assertion.isSigned() && !shibTrust.validate(assertion, role)) {
                throw new TrustException("Unable to validate signature of assertion in response");
            }
        }
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
