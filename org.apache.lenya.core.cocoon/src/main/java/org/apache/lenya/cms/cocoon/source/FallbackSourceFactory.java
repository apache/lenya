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
package org.apache.lenya.cms.cocoon.source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.commons.lang.Validate;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.excalibur.source.URIAbsolutizer;
import org.apache.excalibur.store.impl.MRUMemoryStore;
import org.apache.lenya.cms.module.Module;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Repository;
//florent import org.apache.lenya.cms.publication.Session;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.publication.templating.ExistingSourceResolver;
import org.apache.lenya.cms.publication.templating.PublicationTemplateManager;
import org.apache.lenya.cms.publication.templating.VisitingSourceResolver;
//florent import org.apache.lenya.util.ServletHelper;
import org.apache.lenya.utils.ServletHelper;

/**
 * <p>
 * Source factory following the fallback principle.
 * </p>
 * <p>
 * The ID of the current publication can be passed in the URL (
 * <code>fallback:pub://path</code),
 * this is necessary as a workaround for bug 40564.
 * </p>
 * 
 */
public class FallbackSourceFactory extends AbstractLogEnabled implements SourceFactory,
        URIAbsolutizer {

    protected MRUMemoryStore store;
    private SourceResolver resolver;
    private Repository repository;
    private PublicationTemplateManager templateManager;
    private Map modules;

    /**
     * Configure the spring bean accordingly if you want to use a store.
     * @param store The store.
     */
    public void setStore(MRUMemoryStore store) {
        Validate.notNull(store);
        this.store = store;
    }

    protected boolean useCache() {
        return this.store != null;
    }

    protected MRUMemoryStore getStore() {
        return this.store;
    }

    public void setSourceResolver(SourceResolver resolver) {
        this.resolver = resolver;
    }

    protected SourceResolver getSourceResolver() {
        return this.resolver;
    }

    public void setTemplateManager(PublicationTemplateManager mgr) {
        this.templateManager = mgr;
    }

    protected PublicationTemplateManager getTemplateManager() {
        return this.templateManager;
    }

    public void setModules(Map modules) {
        this.modules = modules;
    }

    protected Map getModules() {
        return this.modules;
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#getSource(java.lang.String, java.util.Map)
     */
    public Source getSource(final String location, Map parameters) throws IOException,
            MalformedURLException {

        Source source;

        if (useCache()) {
            MRUMemoryStore store = getStore();
            final String pubId = getPublicationId();
            final String cacheKey = getCacheKey(pubId, location);
            final String cachedSourceUri = (String) store.get(cacheKey);

            if (cachedSourceUri == null) {
                source = findSource(location, parameters);
                final String resolvedSourceUri = source.getURI();
                store.hold(cacheKey, resolvedSourceUri);
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(
                            "No cached source URI for key " + cacheKey + ", caching URI "
                                    + resolvedSourceUri);
                }
            } else {
                source = this.resolver.resolveURI(cachedSourceUri);
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(
                            "Using cached source URI " + cachedSourceUri + " for key " + cacheKey);
                }
            }

        } else {
            source = findSource(location, parameters);
        }

        return source;
    }

    /**
     * @param pubId The publication ID.
     * @param fallbackUri The fallback:// (or template-fallback:// etc.) URI.
     * @return A string.
     */
    public static String getCacheKey(final String pubId, final String fallbackUri) {
        String cacheKey = pubId == null ? fallbackUri : pubId + ":" + fallbackUri;
        return cacheKey;
    }

    protected String getPublicationId() {
        final ProcessInfoProvider processInfo = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        /*florent HttpServletRequest request = processInfo.getRequest();
        String webappUri = ServletHelper.getWebappURI(request);
        URLInformation info = new URLInformation(webappUri);*/
        HttpServletRequest request = processInfo.getRequest();
        URLInformation info = new URLInformation();
        String pubId = null;
        try {
            Session session = this.repository.getSession(request);
            String pubIdCandidate = info.getPublicationId();
            if (pubIdCandidate != null && session.existsPublication(pubIdCandidate)) {
                pubId = pubIdCandidate;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return pubId;
    }

    protected Source findSource(final String location, Map parameters) throws IOException,
            MalformedURLException {

        FallbackUri uri = new FallbackUri(location);

        String pubId = uri.getPubId();
        String path = uri.getPath();

        Source source = null;
        try {
            final ProcessInfoProvider processInfo = (ProcessInfoProvider) WebAppContextUtils
                    .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
            HttpServletRequest request = processInfo.getRequest();

            if (pubId == null) {
                String webappUrl = request.getRequestURI().substring(
                        request.getContextPath().length());

                URLInformation info = new URLInformation(webappUrl);
                pubId = info.getPublicationId();
            }

            Session session = this.repository.getSession(request);
            if (session.existsPublication(pubId)) {
                Publication pub = session.getPublication(pubId);
                VisitingSourceResolver resolver = getSourceVisitor();
                this.templateManager.visit(pub, path, resolver);
                source = resolver.getSource();
            }

            if (source == null) {
                if (path.startsWith("lenya/modules/")) {
                    final String moduleShortcut = path.split("/")[2];
                    if (!this.modules.containsKey(moduleShortcut)) {
                        throw new RuntimeException("The module '" + moduleShortcut + "' is not registered.");
                    }
                    Module module = (Module) this.modules.get(moduleShortcut);
                    String baseUri = module.getBaseUri();
                    final String modulePath = path.substring(("lenya/modules/" + moduleShortcut)
                            .length());
                    source = this.resolver.resolveURI(baseUri + modulePath);
                } else {
                    String contextUri = "context://" + path;
                    source = this.resolver.resolveURI(contextUri);
                }
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Resolved source:  [" + source.getURI() + "]");
            }

        } catch (Exception e) {
            throw new RuntimeException("Resolving path [" + location + "] failed: ", e);
        }

        return source;
    }

    protected VisitingSourceResolver getSourceVisitor() {
        return new ExistingSourceResolver();
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source)
     */
    public void release(Source source) {
        // In fact, this method should never be called as this factory
        // returns a source object from a different factory. So that
        // factory should release the source
        if (null != source) {
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("Releasing source " + source.getURI());
            }
            this.resolver.release(source);
        }
    }

    /**
     * @see org.apache.excalibur.source.URIAbsolutizer#absolutize(java.lang.String,
     *      java.lang.String)
     */
    public String absolutize(String baseURI, String location) {
        return SourceUtil.absolutize(baseURI, location, true);
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

}