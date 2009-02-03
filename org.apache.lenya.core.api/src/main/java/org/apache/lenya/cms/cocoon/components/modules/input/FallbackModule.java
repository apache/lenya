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

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.store.impl.MRUMemoryStore;
import org.apache.lenya.cms.cocoon.source.FallbackSourceFactory;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;

/**
 * <p>
 * This module returns the actual source URI of a fallback:// source. The protocol (fallback,
 * template-fallback, ...) is configurable via the <em>protocol</em> parameter.
 * </p>
 */
public class FallbackModule extends AbstractInputModule implements Serviceable, Parameterizable {

    protected static final String PARAM_PROTOCOL = "protocol";
    protected ServiceManager manager;
    private String protocol;
    protected static MRUMemoryStore store;
    private static Boolean useCache = null;

    protected boolean useCache() {
        if (useCache == null) {
            useCache = Boolean.valueOf(this.manager.hasService(FallbackSourceFactory.STORE_ROLE));
        }
        return useCache.booleanValue();
    }

    protected MRUMemoryStore getStore() {
        if (store == null) {
            try {
                store = (MRUMemoryStore) this.manager.lookup(FallbackSourceFactory.STORE_ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return store;
    }

    protected String getPublicationId(Map objectModel) {
        Request request = ObjectModelHelper.getRequest(objectModel);
        String webappUri = ServletHelper.getWebappURI(request);
        URLInformation info = new URLInformation(webappUri);
        String pubId = null;
        try {
            Session session = RepositoryUtil.getSession(this.manager, request);
            DocumentFactory factory = DocumentUtil.createDocumentFactory(this.manager, session);
            String pubIdCandidate = info.getPublicationId();
            if (pubIdCandidate != null && factory.existsPublication(pubIdCandidate)) {
                pubId = pubIdCandidate;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return pubId;
    }

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        String uri;
        String fallbackUri = getFallbackUri(name);
        if (useCache()) {
            final String pubId = getPublicationId(objectModel);
            String cacheKey = FallbackSourceFactory.getCacheKey(pubId, fallbackUri);
            MRUMemoryStore store = getStore();
            if (store.containsKey(cacheKey)) {
                uri = (String) store.get(cacheKey);
            }
            else {
                uri = resolveSourceUri(name);
            }
        }
        else {
            uri = resolveSourceUri(name);
        }
        return uri;
    }

    protected String resolveSourceUri(String name) throws ConfigurationException {
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(getFallbackUri(name));
            return source.getURI();
        } catch (Exception e) {
            throw new ConfigurationException("Resolving fallback source [" + name + "] failed: ", e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

    protected String getFallbackUri(String name) {
        return this.protocol + "://" + name;
    }

    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        return Collections.EMPTY_SET.iterator();
    }

    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel) };
        return objects;
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
        
    }

    public void parameterize(Parameters params) throws ParameterException {
        this.protocol = params.getParameter(PARAM_PROTOCOL);
        
    }

}