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
import org.apache.lenya.cms.module.ModuleManager;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.publication.templating.ExistingSourceResolver;
import org.apache.lenya.cms.publication.templating.PublicationTemplateManager;
import org.apache.lenya.cms.publication.templating.VisitingSourceResolver;

/**
 * Source factory following the fallback principle.
 * 
 * @version $Id$
 */
public class FallbackSourceFactory extends AbstractLogEnabled implements SourceFactory,
        Serviceable, Contextualizable, URIAbsolutizer {

    /**
     * Ctor.
     */
    public FallbackSourceFactory() {
        super();
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#getSource(java.lang.String, java.util.Map)
     */
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
        
        //allow for template-fallback://{pubid}//{path} for the sake of the
        //cocoon use-store
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

        PublicationTemplateManager templateManager = null;
        SourceResolver sourceResolver = null;
        Source source = null;
        try {
            sourceResolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            templateManager = (PublicationTemplateManager) this.manager.lookup(PublicationTemplateManager.ROLE);

            
            Request request = ContextHelper.getRequest(this.context);
            
            if (publicationId == null) {
                String webappUrl = request.getRequestURI().substring(request.getContextPath().length());

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
                        final String modulePath = path.substring(("lenya/modules/" + moduleShortcut).length());
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

    private org.apache.avalon.framework.context.Context context;

    /** The ServiceManager */
    private ServiceManager manager;

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