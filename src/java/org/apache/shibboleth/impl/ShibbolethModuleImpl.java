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

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.shibboleth.ShibbolethModule;
import org.apache.shibboleth.saml.ArtifactMapperImpl;
import org.apache.shibboleth.util.CredentialsManager;
import org.apache.shibboleth.util.UniqueIdentifierMapper;
import org.opensaml.MalformedException;
import org.opensaml.NoSuchProviderException;
import org.opensaml.ReplayCache;
import org.opensaml.ReplayCacheFactory;
import org.opensaml.SAMLBrowserProfile.ArtifactMapper;

import edu.internet2.middleware.shibboleth.aap.AAP;
import edu.internet2.middleware.shibboleth.aap.provider.XMLAAP;
import edu.internet2.middleware.shibboleth.common.ShibResource.ResourceNotAvailableException;
import edu.internet2.middleware.shibboleth.metadata.Metadata;
import edu.internet2.middleware.shibboleth.metadata.MetadataException;
import edu.internet2.middleware.shibboleth.metadata.provider.XMLMetadata;

/**
 * Initial Date: 16.07.2004
 * 
 * @author Mike Stock
 * 
 * Comment:
 * 
 */
public class ShibbolethModuleImpl extends AbstractLogEnabled implements ShibbolethModule,
        Configurable, Initializable, Serviceable, ThreadSafe, Disposable {

    private static final String WAYF_SERVER = "WayfServer";
    private static final String CONF_PROVIDER_ID = "ProviderId";
	private static final String CONF_SHIRE = "Shire";
    private static final String CONF_METADATA = "Metadata";
    private static final String CONF_AAP = "AAP";
    private static final String CONF_CHECK_CERTIFICATES = "CheckCertificateValidity";
    private static final String CONF_CHECK_ISSUERIP = "CheckIssuerIP";
    private static final String CONF_RELOAD_DELAY = "ReloadDelayMinutes";
    private static final String CONF_USELANGUAGEINREQ = "UseLanguageInRequest";
    private static final String CONF_LANGUAGEPARAMNAM = "LanguageParamName";

    private boolean checkCertificates = false;
    private boolean checkIssuerIP = true;
    private String providerId;
    private String shire;

    private XMLMetadata metadata;
    private XMLAAP aap;
    private ReplayCache replayCache;
    private ArtifactMapper artifactMapper;
    private UniqueIdentifierMapper uidMapper;
    private CredentialsManager credentialsManager;
    private String wayfServerUrl;

    private static boolean useLanguageInReq = false;

    private static String languageParamName;

    private ServiceManager manager;

    public UniqueIdentifierMapper getUidMapper() {
        if (this.uidMapper == null) {
            try {
                this.uidMapper = (UniqueIdentifierMapper) this.manager
                        .lookup(UniqueIdentifierMapper.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return this.uidMapper;
    }

    public ArtifactMapper getArtifactMapper() {
        if (this.artifactMapper == null) {
            try {
                this.artifactMapper = (ArtifactMapper) this.manager.lookup(ArtifactMapperImpl.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return this.artifactMapper;
    }

    public CredentialsManager getCredentialsManager() {
        if (this.credentialsManager == null) {
            try {
                this.credentialsManager = (CredentialsManager) this.manager
                        .lookup(CredentialsManager.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
            if (!credentialsManager.checkServerCertValidity(0)) {
                throw new RuntimeException(
                        "Shibboleth enabled but no valid server certificate found. Please fix!");
            }
            if (!credentialsManager.checkServerCertValidity(30)) {
                getLogger().warn("Server Certificate will expire in less than 30 days.");
            }
        }
        return this.credentialsManager;
    }

    public void configure(Configuration moduleConfig) throws ConfigurationException {

        setGlobalProperties(moduleConfig);

        // Configure OpenSAML and load metadata (both used by the following
        // method calls...)
        loadShibbolethMetadata(moduleConfig);
        loadAAP(moduleConfig);
        loadWayfServerUrl(moduleConfig);
    }

    protected void loadWayfServerUrl(Configuration moduleConfig) throws ConfigurationException {
        Configuration wayfConfig = moduleConfig.getChild(WAYF_SERVER);
        String wayfUrl = wayfConfig.getValue();
        this.wayfServerUrl = wayfUrl;
    }

    public void initialize() {
        // initialize ReplayCache
        try {
            replayCache = ReplayCacheFactory.getInstance();
        } catch (NoSuchProviderException e) {
            throw new RuntimeException("Error initializing ReplayCache.", e);
        }
    }

    public void dispose() {
        if (metadata != null && metadata.isAlive()) {
            metadata.interrupt();
        }
        if (aap != null && aap.isAlive()) {
            aap.interrupt();
        }
    }

    private void setGlobalProperties(Configuration config) throws ConfigurationException {
        // Check certificates?
        String sCheckCertificates = config.getChild(CONF_CHECK_CERTIFICATES).getValue();
        if (sCheckCertificates.equalsIgnoreCase("true")
                || sCheckCertificates.equalsIgnoreCase("yes"))
            checkCertificates = true;
        else
            checkCertificates = false;

        // Check issuer ?
        String sCheckIssuer = config.getChild(CONF_CHECK_ISSUERIP).getValue();
        if (sCheckIssuer.equalsIgnoreCase("true") || sCheckIssuer.equalsIgnoreCase("yes"))
            checkIssuerIP = true;
        else
            checkIssuerIP = false;

        // Out providerID
        providerId = config.getChild(CONF_PROVIDER_ID).getValue();
        if (providerId == null || providerId.length() == 0)
            providerId = null;
        if (providerId != null)
            getLogger().info("Using providerId: " + providerId);
        else
            getLogger().info("Not using any providerId.");
        
        shire = config.getChild(CONF_SHIRE).getValue();

        // use language parameter
        String sUseLanguageInReq = config.getChild(CONF_USELANGUAGEINREQ).getValue();
        if (sUseLanguageInReq != null
                && (sUseLanguageInReq.equalsIgnoreCase("true") || sUseLanguageInReq
                        .equalsIgnoreCase("yes"))) {
            useLanguageInReq = true;
            languageParamName = config.getChild(CONF_LANGUAGEPARAMNAM).getValue();
            if (languageParamName == null || languageParamName.length() == 0)
                languageParamName = null;
            if (languageParamName != null)
                getLogger().info("Language code is sent as parameter in the AAI request");
            else
                getLogger().info(
                        "Language code was enabled to be sent, "
                                + "but no parameter name defined! "
                                + "Please fix in the configuration.");
        } else {
            useLanguageInReq = false;
            languageParamName = null;
            getLogger().info("Language code is not sent with AAI request.");
        }
    }

    /**
     * @param moduleConfig The configuration.
     * @throws ConfigurationException if an error occurs.
     */
    private void loadShibbolethMetadata(Configuration moduleConfig) throws ConfigurationException {

        getLogger().info("Initializing metadata & watchdog");
        String metadataConfigUri = moduleConfig.getChild(CONF_METADATA).getValue();
        if (!SourceUtil.exists(this.manager, metadataConfigUri))
            throw new ConfigurationException("Invalid metadata file: " + metadataConfigUri);
        try {
            metadata = new XMLMetadata(getFileUri(metadataConfigUri));
        } catch (ResourceNotAvailableException e) {
            throw new ConfigurationException("Metadata file watchdog could not be initialized.", e);
        } catch (MetadataException e) {
            throw new ConfigurationException("Metadata file could not be parsed.", e);
        }

        String delayMinutes = moduleConfig.getChild(CONF_RELOAD_DELAY).getValue();
        if (delayMinutes != null) {
            try {
                metadata.setDelay(Long.parseLong(delayMinutes) * 60 * 1000);
            } catch (NumberFormatException nfe) {
                getLogger().warn(
                        "ShibbolethModule: Delay configuration parameter has invalid value. "
                                + "Reverting to default.");
            }
        }
        getLogger().info("Metadata watchdog initialization completed.");
    }

    protected String getFileUri(String sourceUri) throws ConfigurationException {
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(sourceUri);
            File file = org.apache.excalibur.source.SourceUtil.getFile(source);
            return file.toURI().toString();
        } catch (Exception e) {
            throw new ConfigurationException("Could not resolve source [" + sourceUri + "]", e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @param moduleConfig The configuration.
     * @throws ConfigurationException
     */
    private void loadAAP(Configuration moduleConfig) throws ConfigurationException {

        getLogger().info("Initializing AAP & watchdog");
        String aapConfigUri = moduleConfig.getChild(CONF_AAP).getValue();
        if (aapConfigUri == null || aapConfigUri.length() == 0) {
            getLogger().info("No AAP defined.");
            return;
        }

        if (!SourceUtil.exists(this.manager, aapConfigUri))
            throw new ConfigurationException("Invalid AAP configuration file: " + aapConfigUri);
        try {
            aap = new XMLAAP(getFileUri(aapConfigUri));
        } catch (ResourceNotAvailableException e) {
            throw new ConfigurationException("AAP file watchdog could not be initialized.", e);
        } catch (MalformedException e) {
            throw new ConfigurationException("AAP file could not be parsed.", e);
        }

        String delayMinutes = moduleConfig.getChild(CONF_RELOAD_DELAY).getValue();
        if (delayMinutes != null) {
            try {
                aap.setDelay(Long.parseLong(delayMinutes) * 60 * 1000);
            } catch (NumberFormatException nfe) {
                getLogger().warn(
                        "ShibboletsModule: Delay configuration parameter has invalid value."
                                + " Reverting to default.");
            }
        }
        getLogger().info("AAP watchdog initialization completed.");
    }

    /**
     * @return true if the language should be sent in the aai request
     */
    public boolean useLanguageInReq() {
        return useLanguageInReq;
    }

    /**
     * 
     * @return the get request paramter name to be used sending the language
     *         code.
     */
    public String getLanguageParamName() {
        return languageParamName;
    }

    /**
     * @return True if to check certificates.
     */
    public boolean checkCertificates() {
        return checkCertificates;
    }

    /**
     * @return True if to check issuer IPs.
     */
    public boolean checkIssuerIP() {
        return checkIssuerIP;
    }

    /**
     * @return The provider ID for this shibboleth resource.
     */
    public String getProviderId() {
        return providerId;
    }

    /**
     * @return The Shibboleth metadata object.
     */
    public Metadata getMetadata() {
        return metadata;
    }

    public AAP getAttributeAcceptancePolicy() {
        return aap;
    }

    /**
     * @return The replay cache.
     */
    public ReplayCache getReplayCache() {
        return replayCache;
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public String getWayfServerUrl() {
        return this.wayfServerUrl;
    }

	public String getShireUrl(String baseUrl) {
		return baseUrl + this.shire;
	}
	
	public String getTargetBaseUrl(String targetUrl){
		try {
            URL url = new URL(targetUrl);
            return url.getProtocol() + "://" + url.getAuthority();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
	}

}