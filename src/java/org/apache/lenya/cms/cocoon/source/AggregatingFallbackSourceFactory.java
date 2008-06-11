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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.store.impl.MRUMemoryStore;
import org.apache.lenya.cms.module.ModuleManager;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.publication.templating.AllExistingSourceResolver;
import org.apache.lenya.cms.publication.templating.PublicationTemplateManager;
import org.apache.lenya.util.ServletHelper;

/**
 * <p>
 * Aggregate all existing fallback sources by adding their XML content under the document element of
 * the first encountered source. The document element of all subsequent sources is stripped.
 * </p>
 * <p>
 * The fallback sources are resolved in bottom-up order, i.e.
 * </p>
 * <ul>
 * <li>current publication</li>
 * <li>template of the current publication</li>
 * <li>template of the template publication</li>
 * <li>...</li>
 * <li>core</li>
 * </ul>
 * <p>
 * If one of the fallback sources is not a well-formed XML document, a RuntimeException is thrown.
 * </p>
 */
public class AggregatingFallbackSourceFactory extends FallbackSourceFactory {
    
    /**
     * @see org.apache.excalibur.source.SourceFactory#getSource(java.lang.String, java.util.Map)
     */
    public Source getSource(final String location, Map parameters) throws IOException,
            MalformedURLException {

        String[] uris;

        if (useCache()) {
            MRUMemoryStore store = getStore();
            final String cacheKey = getCacheKey(getPublicationId(), location);
            final String[] cachedUris = (String[]) store.get(cacheKey);
            if (cachedUris == null) {
                uris = findUris(location, parameters);
                store.hold(cacheKey, uris);
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("No cached source URI for key " + cacheKey + ", caching resolved URIs.");
                }
            } else {
                uris = cachedUris;
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Using cached source URIs for key " + cacheKey);
                }
            }
        }
        else {
            uris = findUris(location, parameters);
        }
        return new AggregatingSource(location, uris, this.manager);
    }

    protected String[] findUris(final String location, Map parameters) throws IOException,
            MalformedURLException {
        
        FallbackUri uri = new FallbackUri(location);
        String pubId = uri.getPubId();
        String path = uri.getPath();

        PublicationTemplateManager templateManager = null;
        try {
            templateManager = (PublicationTemplateManager) this.manager
                    .lookup(PublicationTemplateManager.ROLE);

            Request request = ContextHelper.getRequest(this.context);

            if (pubId == null) {
                String webappUrl = ServletHelper.getWebappURI(request);
                URLInformation info = new URLInformation(webappUrl);
                pubId = info.getPublicationId();
            }

            DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);

            String[] uris;

            if (factory.existsPublication(pubId)) {
                Publication pub = factory.getPublication(pubId);
                AllExistingSourceResolver resolver = new AllExistingSourceResolver();
                templateManager.visit(pub, path, resolver);
                uris = resolver.getUris();
            } else {
                uris = new String[0];
            }
            
            List allUris = new ArrayList();
            allUris.addAll(Arrays.asList(uris));
            
            String contextSourceUri = null;
            if (path.startsWith("lenya/modules/")) {
                ModuleManager moduleMgr = null;
                try {
                    moduleMgr = (ModuleManager) this.manager.lookup(ModuleManager.ROLE);
                    final String moduleShortcut = path.split("/")[2];
                    String baseUri = moduleMgr.getBaseURI(moduleShortcut);
                    final String modulePath = path.substring(("lenya/modules/" + moduleShortcut).length());
                    contextSourceUri = baseUri + modulePath;
                } finally {
                    if (moduleMgr != null) {
                        this.manager.release(moduleMgr);
                    }
                }
            } else {
                contextSourceUri = "context://" + path;
            }
            if (org.apache.lenya.cms.cocoon.source.SourceUtil.exists(contextSourceUri, this.manager)) {
                allUris.add(contextSourceUri);
            }

            return (String[]) allUris.toArray(new String[allUris.size()]); 

        } catch (Exception e) {
            throw new RuntimeException("Resolving path [" + location + "] failed: ", e);
        } finally {
            if (templateManager != null) {
                this.manager.release(templateManager);
            }
        }
    }

}
