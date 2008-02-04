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
import java.util.StringTokenizer;

import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.excalibur.source.URIAbsolutizer;
import org.apache.excalibur.store.impl.MRUMemoryStore;
import org.apache.lenya.cms.module.ModuleManager;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.publication.templating.ExistingSourceResolver;
import org.apache.lenya.cms.publication.templating.PublicationTemplateManager;
import org.apache.lenya.cms.publication.templating.VisitingSourceResolver;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;

/**
 * <p>
 * Source factory following the fallback principle.
 * </p>
 * <p>
 * The ID of the current publication can be passed in the URL (<code>fallback:pub://path</code),
 * this is necessary as a workaround for bug 40564.
 * </p>
 * 
 * @version $Id$
 */
public class FallbackSourceFactory extends AbstractLogEnabled implements SourceFactory,
        Serviceable, Contextualizable, URIAbsolutizer {

    protected static MRUMemoryStore store;
    private boolean useCache = true;
    
    protected static final String STORE_ROLE = FallbackSourceFactory.class.getName() + "Store";
    
    protected MRUMemoryStore getStore() {
        if (store == null) {
            try {
                store = (MRUMemoryStore) this.manager.lookup(STORE_ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return store;
    }
    
    /**
     * @see org.apache.excalibur.source.SourceFactory#getSource(java.lang.String, java.util.Map)
     */
    public Source getSource(final String location, Map parameters) throws IOException,
            MalformedURLException {

        MRUMemoryStore store = getStore();
        Source source;
        final String cacheKey = getCacheKey(location);
        final String cachedSourceUri = (String) store.get(cacheKey);

        if (!useCache || cachedSourceUri == null) {
            source = findSource(location, parameters);
            final String resolvedSourceUri = source.getURI();
            store.hold(cacheKey, resolvedSourceUri);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("No cached source URI for key " + cacheKey + ", caching URI " + resolvedSourceUri);
            }
        } else {
            SourceResolver resolver = null;
            try {
                resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                source = resolver.resolveURI(cachedSourceUri);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            } finally {
                if (resolver != null) {
                    this.manager.release(resolver);
                }
            }
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Using cached source URI " + cachedSourceUri + " for key " + cacheKey);
            }
        }
        return source;
    }

    protected String getCacheKey(final String location) {
        String pubId = getPublicationId();
        String cacheKey = pubId == null ? location : pubId + ":" + location;
        return cacheKey;
    }

    protected String getPublicationId() {
        Request request = ContextHelper.getRequest(this.context);
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

    protected Source findSource(final String location, Map parameters) throws IOException,
            MalformedURLException {

        // Remove the protocol and the first '//'
        int pos = location.indexOf("://");

        if (pos == -1) {
            throw new RuntimeException("The location [" + location
                    + "] does not contain the string '://'");
        }

        String path = location.substring(pos + 3);

        String publicationId = null;

        // extract publication ID
        String prefix = location.substring(0, pos);
        StringTokenizer tokens = new StringTokenizer(prefix, ":");
        if (tokens.countTokens() > 1) {
            tokens.nextToken();
            publicationId = tokens.nextToken();
        }

        // remove query string
        int questionMarkIndex = path.indexOf("?");
        if (questionMarkIndex > -1) {
            path = path.substring(0, questionMarkIndex);
        }

        if (path.length() == 0) {
            throw new RuntimeException("The path after the protocol must not be empty!");
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Location:     [" + location + "]");
            getLogger().debug("Path:         [" + path + "]");
        }

        PublicationTemplateManager templateManager = null;
        SourceResolver sourceResolver = null;
        Source source = null;
        try {
            sourceResolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            templateManager = (PublicationTemplateManager) this.manager
                    .lookup(PublicationTemplateManager.ROLE);

            Request request = ContextHelper.getRequest(this.context);

            if (publicationId == null) {
                String webappUrl = request.getRequestURI().substring(
                        request.getContextPath().length());

                URLInformation info = new URLInformation(webappUrl);
                publicationId = info.getPublicationId();
            }

            DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);
            if (factory.existsPublication(publicationId)) {
                Publication pub = factory.getPublication(publicationId);
                VisitingSourceResolver resolver = getSourceVisitor();
                templateManager.visit(pub, path, resolver);
                source = resolver.getSource();
            }

            if (source == null) {
                if (path.startsWith("lenya/modules/")) {
                    ModuleManager moduleMgr = null;
                    try {
                        moduleMgr = (ModuleManager) this.manager.lookup(ModuleManager.ROLE);
                        final String moduleShortcut = path.split("/")[2];
                        String baseUri = moduleMgr.getBaseURI(moduleShortcut);
                        final String modulePath = path
                                .substring(("lenya/modules/" + moduleShortcut).length());
                        source = sourceResolver.resolveURI(baseUri + modulePath);
                    } finally {
                        if (moduleMgr != null) {
                            this.manager.release(moduleMgr);
                        }
                    }
                } else {
                    String contextUri = "context://" + path;
                    source = sourceResolver.resolveURI(contextUri);
                }
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Resolved source:  [" + source.getURI() + "]");
            }

        } catch (Exception e) {
            throw new RuntimeException("Resolving path [" + location + "] failed: ", e);
        } finally {
            if (templateManager != null) {
                this.manager.release(templateManager);
            }
            if (sourceResolver != null) {
                this.manager.release(sourceResolver);
            }
        }

        return source;
    }

    protected VisitingSourceResolver getSourceVisitor() {
        return new ExistingSourceResolver();
    }

    protected org.apache.avalon.framework.context.Context context;

    /** The ServiceManager */
    protected ServiceManager manager;
    
    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager _manager) throws ServiceException {
        this.manager = _manager;
    }

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(org.apache.avalon.framework.context.Context _context)
            throws ContextException {
        this.context = _context;
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
            SourceResolver resolver = null;
            try {
                resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                resolver.release(source);
            } catch (ServiceException ignore) {
                // ignore the exception
            } finally {
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.excalibur.source.URIAbsolutizer#absolutize(java.lang.String,
     *      java.lang.String)
     */
    public String absolutize(String baseURI, String location) {
        return SourceUtil.absolutize(baseURI, location, true);
    }

}