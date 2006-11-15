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

import org.apache.avalon.framework.context.Context;
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
import org.apache.excalibur.source.SourceUtil;
import org.apache.excalibur.source.URIAbsolutizer;
import org.apache.lenya.cms.module.ModuleManager;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationManager;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.publication.templating.AllExistingSourceResolver;
import org.apache.lenya.cms.publication.templating.PublicationTemplateManager;

/**
 * Aggregate all existing fallback URIs by merging their XML content under
 * the document element of the first encountered source.
 */
public class AggregatingFallbackSourceFactory extends AbstractLogEnabled implements SourceFactory,
        Serviceable, Contextualizable, URIAbsolutizer {

    public Source getSource(final String location, Map parameters) throws IOException,
            MalformedURLException {

        // Remove the protocol and the first '//'
        int pos = location.indexOf("://");

        if (pos == -1) {
            throw new RuntimeException("The location [" + location
                    + "] does not contain the string '://'");
        }

        String path = location.substring(pos + 3);
        String publicationId = null;

        // allow for template-fallback://{pubid}//{path} for the sake of the
        // cocoon use-store
        if (path.indexOf("//") > 1) {
            pos = path.indexOf("//");
            publicationId = path.substring(0, pos);
            path = path.substring(pos + 2, path.length());
        }

        if (path.length() == 0) {
            throw new RuntimeException("The path after the protocol must not be empty!");
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Location:     [" + location + "]");
            getLogger().debug("Path:         [" + path + "]");
        }

        PublicationManager pubMgr = null;
        PublicationTemplateManager templateManager = null;
        try {
            templateManager = (PublicationTemplateManager) this.manager
                    .lookup(PublicationTemplateManager.ROLE);

            Request request = ContextHelper.getRequest(this.context);

            if (publicationId == null) {
                String webappUrl = request.getRequestURI().substring(
                        request.getContextPath().length());

                URLInformation info = new URLInformation(webappUrl);
                publicationId = info.getPublicationId();
            }

            pubMgr = (PublicationManager) this.manager.lookup(PublicationManager.ROLE);
            DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);
            Publication pub = pubMgr.getPublication(factory, publicationId);

            String[] uris;

            if (pub.exists()) {
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

            String[] aggregateUris = (String[]) allUris.toArray(new String[allUris.size()]); 
            return new AggregatingSource(location, aggregateUris, this.manager);

        } catch (Exception e) {
            throw new RuntimeException("Resolving path [" + location + "] failed: ", e);
        } finally {
            if (templateManager != null) {
                this.manager.release(templateManager);
            }
            if (pubMgr != null) {
                this.manager.release(pubMgr);
            }
        }
    }

    public void release(Source source) {
    }

    private ServiceManager manager;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    private Context context;

    public void contextualize(Context context) throws ContextException {
        this.context = context;
    }

    public String absolutize(String baseURI, String location) {
        return SourceUtil.absolutize(baseURI, location, true);
    }

}
