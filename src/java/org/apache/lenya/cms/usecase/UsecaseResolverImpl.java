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
package org.apache.lenya.cms.usecase;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.URLInformation;

/**
 * Usecase resolver implementation.
 * 
 * @version $Id$
 */
public class UsecaseResolverImpl extends AbstractLogEnabled implements UsecaseResolver,
        Serviceable, Disposable, Contextualizable {

    /**
     * Ctor.
     */
    public UsecaseResolverImpl() {
        // do nothing
    }

    private ServiceSelector selector;

    /**
     * @see org.apache.lenya.cms.usecase.UsecaseResolver#resolve(java.lang.String)
     */
    public Usecase resolve(String name) throws ServiceException {
        return this.resolve(getWebappURL(), name);
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager _manager) throws ServiceException {
        this.manager = _manager;
        this.selector = (ServiceSelector) _manager.lookup(Usecase.ROLE + "Selector");
    }

    /**
     * @see org.apache.lenya.cms.usecase.UsecaseResolver#release(org.apache.lenya.cms.usecase.Usecase)
     */
    public void release(Usecase usecase) throws ServiceException {
        if (usecase == null) {
            throw new IllegalArgumentException("The usecase to release must not be null.");
        }
        this.selector.release(usecase);

    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        if (this.selector != null) {
            this.manager.release(this.selector);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.UsecaseResolver#isRegistered(java.lang.String)
     */
    public boolean isRegistered(String name) throws ServiceException {
        return this.isRegistered(getWebappURL(), name);
    }

    protected String getWebappURL() {
        Request request = ContextHelper.getRequest(getContext());
        String context = request.getContextPath();
        String webappUrl = request.getRequestURI().substring(context.length());
        return webappUrl;
    }

    /**
     * Returns the name of the publication-overridden usecase to be resolved.
     * @param webappUrl The web application URL.
     * @param name The plain usecase name.
     * @return A string.
     */
    protected String getPublicationUsecaseName(String webappUrl, final String name) {
        String newName = null;
        Publication publication = getPublication(webappUrl);
        if (publication != null) {
            newName = publication.getId() + "/" + name;
        }
        return newName;
    }

    private Context context;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context _context) throws ContextException {
        this.context = _context;
    }

    /**
     * Returns the context.
     * @return A context.
     */
    protected Context getContext() {
        return this.context;
    }

    /**
     * Returns the publication the usecase was invoked in.
     * @param webappUrl The web application URL.
     * @return A publication.
     */
    protected Publication getPublication(String webappUrl) {

        SourceResolver resolver = null;
        Source source = null;
        Publication publication = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI("context://");
            String contextPath = SourceUtil.getFile(source).getAbsolutePath();

            URLInformation info = new URLInformation(webappUrl);
            String publicationId = info.getPublicationId();

            if (publicationId != null
                    && PublicationFactory.existsPublication(publicationId, contextPath)) {
                PublicationFactory factory = PublicationFactory.getInstance(getLogger());
                publication = factory.getPublication(webappUrl, SourceUtil.getFile(source));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
        return publication;
    }

    /**
     * @see org.apache.lenya.cms.usecase.UsecaseResolver#resolve(java.lang.String, java.lang.String)
     */
    public Usecase resolve(String webappUrl, String name) throws ServiceException {
        Usecase usecase = null;
        String publicationUsecaseName = getPublicationUsecaseName(webappUrl, name);
        if (publicationUsecaseName != null && this.selector.isSelectable(publicationUsecaseName)) {
            usecase = (Usecase) this.selector.select(publicationUsecaseName);
        } else {
            usecase = (Usecase) this.selector.select(name);

        }
        return usecase;
    }

    /**
     * @see org.apache.lenya.cms.usecase.UsecaseResolver#isRegistered(java.lang.String,
     *      java.lang.String)
     */
    public boolean isRegistered(String webappUrl, String name) throws ServiceException {
        String pubName = getPublicationUsecaseName(webappUrl, name);
        return (pubName != null && this.selector.isSelectable(pubName))
                || this.selector.isSelectable(name);
    }

}