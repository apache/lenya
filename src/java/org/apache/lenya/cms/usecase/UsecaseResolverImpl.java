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

import java.util.Map;

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
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;

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
    }

    private ServiceSelector selector;

    /**
     * @see org.apache.lenya.cms.usecase.UsecaseResolver#resolve(java.lang.String)
     */
    public Usecase resolve(String name) throws ServiceException {
        Usecase usecase = null;
        String publicationUsecaseName = getPublicationUsecaseName(name);
        if (this.selector.isSelectable(publicationUsecaseName)) {
            usecase = (Usecase) this.selector.select(publicationUsecaseName);
        }
        else {
            usecase = (Usecase) this.selector.select(name);
            
        }
        return usecase;
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
        this.selector = (ServiceSelector) manager.lookup(Usecase.ROLE + "Selector");
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
        return this.selector.isSelectable(getPublicationUsecaseName(name))
                || this.selector.isSelectable(name);
    }

    /**
     * Returns the name of the publication-overridden usecase to be resolved.
     * @param name The plain usecase name.
     * @return A string.
     */
    protected String getPublicationUsecaseName(String name) {
        return getPublication().getId() + "/" + name;
    }

    private Context context;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException {
        this.context = context;
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
     * @return A publication.
     */
    protected Publication getPublication() {
        Map objectModel = ContextHelper.getObjectModel(getContext());
        Publication publication;
        try {
            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            publication = factory.getPublication(objectModel);
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }
        return publication;
    }

}