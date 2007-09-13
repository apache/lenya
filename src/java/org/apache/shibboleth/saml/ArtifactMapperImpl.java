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

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.shibboleth.ShibbolethModule;
import org.opensaml.NoSuchProviderException;
import org.opensaml.ProfileException;
import org.opensaml.SAMLBinding;
import org.opensaml.SAMLBindingFactory;
import org.opensaml.SAMLException;
import org.opensaml.SAMLRequest;
import org.opensaml.SAMLResponse;
import org.opensaml.SAMLSOAPHTTPBinding;
import org.opensaml.UnsupportedExtensionException;
import org.opensaml.SAMLBrowserProfile.ArtifactMapper;
import org.opensaml.artifact.SAMLArtifact;
import org.opensaml.artifact.SAMLArtifactType0001;
import org.opensaml.artifact.SAMLArtifactType0002;

import edu.internet2.middleware.shibboleth.metadata.Endpoint;
import edu.internet2.middleware.shibboleth.metadata.EndpointManager;
import edu.internet2.middleware.shibboleth.metadata.EntityDescriptor;
import edu.internet2.middleware.shibboleth.metadata.IDPSSODescriptor;
import edu.internet2.middleware.shibboleth.metadata.MetadataException;

/**
 * Description:<br>
 * Some code borrowed and adapted from Internet2 Shibboleth SP implementation.
 * 
 * <P>
 * Initial Date: 24.08.2005 <br>
 */
public class ArtifactMapperImpl extends AbstractLogEnabled implements Component, ArtifactMapper,
        Serviceable {

    /**
     * The service role.
     */
    public static final String ROLE = ArtifactMapperImpl.class.getName();
    private ServiceManager manager;
    private ShibbolethModule shibbolethModule;

    /**
     * The Callback routine from SAML to direct a Request containing the
     * Artifact to the IdP.
     * 
     * @param request A SAMLRequest to resolve the Artifact
     * @return The SAMLResponse from the IdP
     * @throws SAMLException
     */
    public SAMLResponse resolve(SAMLRequest request) throws SAMLException {
        SAMLResponse response = null;

        // Ok, so what is this Artifact anyway
        Iterator artifacts = request.getArtifacts();
        if (!artifacts.hasNext())
            throw new SAMLException("SPArtifactMapper was passed no artifact.");
        EntityDescriptor entity = null;
        SAMLArtifact artifact = null;
        while (artifacts.hasNext()) {
            artifact = (SAMLArtifact) artifacts.next();
            entity = getShibbolethModule().getMetadata().lookup(artifact);
            if (entity != null)
                break;
        }
        if (entity == null) {
            throw new MetadataException("Unable to find Artifact issuer in Metadata.");
        }
        String entityId = entity.getId();
        getLogger().info("Processing Artifact issued by " + entityId);

        IDPSSODescriptor idp = entity
                .getIDPSSODescriptor(request.getMinorVersion() == 1 ? org.opensaml.XML.SAML11_PROTOCOL_ENUM
                        : org.opensaml.XML.SAML10_PROTOCOL_ENUM);
        if (idp == null) {
            throw new MetadataException("Entity " + entityId + " has no usable IDPSSODescriptor.");
        }

        // TODO: Sign the Request if so configured
        // String credentialId = appinfo.getCredentialIdForEntity(entity);
        // if (credentialId != null)
        // AttributeRequestor.possiblySignRequest(config.getCredentials(),
        // request, credentialId);

        if (artifact instanceof SAMLArtifactType0001) {
            // A Type1 Artifact takes any usable SOAP Endpoint
            EndpointManager endpointManager = idp.getArtifactResolutionServiceManager();
            Iterator endpoints = endpointManager.getEndpoints();
            while (endpoints.hasNext()) {
                // Search for an Endpoint with a SOAP Binding
                Endpoint endpoint = (Endpoint) endpoints.next();
                String binding = endpoint.getBinding();
                if (!binding.equals(SAMLBinding.SOAP))
                    continue; // The C++ code is
                // more elaborate here

                response = resolveArtifact(request, idp, endpoint);
                break; // Got response, stop scanning endpoints
            }
        } else if (artifact instanceof SAMLArtifactType0002) {
            // A Type2 Artifact carries an Endpoint location
            SAMLArtifactType0002 type2 = (SAMLArtifactType0002) artifact;
            EndpointManager endpointManager = idp.getArtifactResolutionServiceManager();
            Iterator endpoints = endpointManager.getEndpoints();
            while (endpoints.hasNext()) {
                // Search for an Endpoint matching the Artifact
                Endpoint endpoint = (Endpoint) endpoints.next();
                String binding = endpoint.getBinding();
                if (!binding.equals(SAMLBinding.SOAP))
                    continue; // The C++ code is
                // more elaborate here
                String location = endpoint.getLocation();
                if (!location.equals(type2.getSourceLocation()))
                    continue;

                response = resolveArtifact(request, idp, endpoint);
                break; // Got response, stop scanning endpoints
            }
        } else {
            throw new UnsupportedExtensionException("Unrecognized Artifact type.");
        }
        if (response == null) {
            throw new MetadataException(
                    "Unable to locate acceptable binding/endpoint to resolve artifact.");
        }
        return response;
    }

    /**
     * Call back into SAML to transmit the Request to the IdP Enpoint and get
     * back the Response represented by the Artifact.
     * 
     * @param request A SAMLRequest containing the Artifact
     * @param idp The IdP entity
     * @param endpoint The IdP Endpoint
     * @return The SAMLResponse returned by the IdP
     * @throws NoSuchProviderException
     * @throws SAMLException
     * @throws ProfileException if the response has no assertions
     */
    private SAMLResponse resolveArtifact(SAMLRequest request, IDPSSODescriptor idp,
            Endpoint endpoint) throws NoSuchProviderException, SAMLException, ProfileException {
        SAMLResponse response;
        SAMLBinding sbinding = SAMLBindingFactory.getInstance(endpoint.getBinding());
        if (sbinding instanceof SAMLSOAPHTTPBinding) { // I shure hope so
            SAMLSOAPHTTPBinding httpbind = (SAMLSOAPHTTPBinding) sbinding;
            httpbind.addHook(new HttpHookImpl(this.manager, getLogger()));
        }
        response = sbinding.send(endpoint.getLocation(), request);
        if (!response.getAssertions().hasNext()) {
            throw new ProfileException(
                    "No SAML assertions returned in response to artifact profile request.");
        }
        return response;
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    protected ShibbolethModule getShibbolethModule() {
        if (this.shibbolethModule == null) {
            try {
                this.shibbolethModule = (ShibbolethModule) this.manager.lookup(ShibbolethModule.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return this.shibbolethModule;
    }

}
