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
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.publication.templating.PublicationTemplateManager;

/**
 * Usecase resolver implementation.
 * 
 * @version $Id$
 */
public class UsecaseResolverImpl extends AbstractLogEnabled implements UsecaseResolver,
        Serviceable, Disposable, ThreadSafe {

    /**
     * Ctor.
     */
    public UsecaseResolverImpl() {
        // do nothing
    }

    private ServiceSelector selector;

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
     * Returns the name of the publication-overridden usecase to be resolved.
     * @param webappUrl The web application URL.
     * @param name The plain usecase name.
     * @return A string.
     * @throws ServiceException if an error occurs.
     */
    protected String getUsecaseName(String webappUrl, final String name) throws ServiceException {
        String newName = null;

        Publication publication = getPublication(webappUrl);
        if (publication != null) {
            PublicationTemplateManager templateManager = null;
            try {
                templateManager = (PublicationTemplateManager) this.manager
                        .lookup(PublicationTemplateManager.ROLE);
                newName = (String) templateManager.getSelectableHint(publication,
                        this.selector,
                        name);
            } finally {
                if (templateManager != null) {
                    this.manager.release(templateManager);
                }
            }
        }
        else {
            newName = name;
        }

        return newName;
    }

    /**
     * Returns the publication the usecase was invoked in.
     * @param webappUrl The web application URL.
     * @return A publication.
     */
    protected Publication getPublication(String webappUrl) {
        Publication publication = null;
        try {
            URLInformation info = new URLInformation(webappUrl);
            String publicationId = info.getPublicationId();

            if (publicationId != null) {
                Publication pub = PublicationUtil.getPublicationFromUrl(this.manager, webappUrl);
                if (pub.exists()) {
                    publication = pub;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return publication;
    }

    /**
     * @see org.apache.lenya.cms.usecase.UsecaseResolver#resolve(java.lang.String, java.lang.String)
     */
    public Usecase resolve(String webappUrl, String name) throws ServiceException {
        Object usecaseName = getUsecaseName(webappUrl, name);
        return (Usecase) this.selector.select(usecaseName);
    }

    /**
     * @see org.apache.lenya.cms.usecase.UsecaseResolver#isRegistered(java.lang.String,
     *      java.lang.String)
     */
    public boolean isRegistered(String webappUrl, String name) throws ServiceException {
        String usecaseName = getUsecaseName(webappUrl, name);
        return this.selector.isSelectable(usecaseName);
    }

}