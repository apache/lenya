/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.apache.lenya.cms.publication.PublicationManager;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.publication.templating.ExistingSourceResolver;
import org.apache.lenya.cms.publication.templating.PublicationTemplateManager;
import org.apache.lenya.cms.publication.templating.URIResolver;

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

        String resolvedUri = null;

        long startTime = new GregorianCalendar().getTimeInMillis();

        // Remove the protocol and the first '//'
        final int pos = location.indexOf("://");

        if (pos == -1) {
            throw new RuntimeException("The location [" + location
                    + "] does not contain the string '://'");
        }

        final String path = location.substring(pos + 3);

        if (path.length() == 0) {
            throw new RuntimeException("The path after the protocol must not be empty!");
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Location:     [" + location + "]");
            getLogger().debug("Path:         [" + path + "]");
        }

        PublicationManager pubMgr = null;
        PublicationTemplateManager templateManager = null;
        SourceResolver sourceResolver = null;
        Source source;
        try {
            sourceResolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            templateManager = (PublicationTemplateManager) this.manager.lookup(PublicationTemplateManager.ROLE);

            Request request = ContextHelper.getRequest(this.context);
            String webappUrl = request.getRequestURI().substring(request.getContextPath().length());

            URLInformation info = new URLInformation(webappUrl);
            String publicationId = info.getPublicationId();

            pubMgr = (PublicationManager) this.manager.lookup(PublicationManager.ROLE);
            DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);
            Publication pub = pubMgr.getPublication(factory, publicationId);
            if (pub.exists()) {
                URIResolver resolver = getSourceVisitor();
                templateManager.visit(pub, path, resolver);
                resolvedUri = resolver.getURI();
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Resolved URI:  [" + resolvedUri + "]");
            }

            if (resolvedUri == null) {
                if (path.startsWith("lenya/modules/")) {
                    ModuleManager moduleMgr = null;
                    try {
                        moduleMgr = (ModuleManager) this.manager.lookup(ModuleManager.ROLE);
                        final String moduleShortcut = path.split("/")[2];
                        String baseUri = moduleMgr.getBaseURI(moduleShortcut);
                        final String modulePath = path.substring(("lenya/modules/" + moduleShortcut).length());
                        resolvedUri = baseUri + modulePath;
                    } finally {
                        if (moduleMgr != null) {
                            this.manager.release(moduleMgr);
                        }
                    }
                } else {
                    String contextUri = "context://" + path;
                    resolvedUri = contextUri;
                }
            }

            source = sourceResolver.resolveURI(resolvedUri);

        } catch (Exception e) {
            throw new RuntimeException("Resolving path [" + location + "] failed: ", e);
        } finally {
            if (templateManager != null) {
                this.manager.release(templateManager);
            }
            if (pubMgr != null) {
                this.manager.release(pubMgr);
            }
            if (sourceResolver != null) {
                this.manager.release(sourceResolver);
            }
        }

        if (getLogger().isDebugEnabled()) {
            long endTime = new GregorianCalendar().getTimeInMillis();
            long time = endTime - startTime;
            getLogger().debug("Processing time: "
                    + new SimpleDateFormat("hh:mm:ss.S").format(new Date(time)));
        }

        return source;
    }

    protected URIResolver getSourceVisitor() {
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