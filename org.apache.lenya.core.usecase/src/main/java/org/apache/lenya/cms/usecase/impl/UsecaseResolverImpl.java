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
package org.apache.lenya.cms.usecase.impl;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Repository;
import org.apache.lenya.cms.publication.Session;
import org.apache.lenya.cms.publication.templating.PublicationTemplateManager;
import org.apache.lenya.cms.usecase.Usecase;
import org.apache.lenya.cms.usecase.UsecaseResolver;
import org.apache.lenya.utils.URLInformation;

/**
 * Usecase resolver implementation.
 * 
 * @version $Id$
 */
public class UsecaseResolverImpl extends AbstractLogEnabled implements UsecaseResolver {

    private PublicationTemplateManager templateManager;
    private Repository repository;

    /**
     * @see org.apache.lenya.cms.usecase.UsecaseResolver#release(org.apache.lenya.cms.usecase.Usecase)
     */
    public void release(Usecase usecase) throws ServiceException {
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
            newName = (String) this.templateManager.getSelectableHint(publication, Usecase.ROLE, name);
        } else {
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
        ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        HttpServletRequest request = process.getRequest();
        Publication publication = null;
        try {

            Session session = this.repository.getSession(request);

            String uri = new URLInformation().getWebappUrl();
            if (session.getUriHandler().isPublication(uri)) {
                publication = session.getUriHandler().getPublication(uri);
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
        Usecase usecase = (Usecase) WebAppContextUtils.getCurrentWebApplicationContext().getBean(
                Usecase.ROLE + "/" + usecaseName);
        usecase.setName(name);
        /**The "webappUrl pass to this function is not always the "real" webappUrl cause of "servlet" protocol
         * TODO : be sure to always pass a "real" url to resolve and suppress code behond.
         */
        String realWebappUrl = new URLInformation().getWebappUrl();
        usecase.setSourceURL(realWebappUrl);
        return usecase;
    }

    /**
     * @see org.apache.lenya.cms.usecase.UsecaseResolver#isRegistered(java.lang.String,
     *      java.lang.String)
     */
    public boolean isRegistered(String webappUrl, String name) throws ServiceException {
        String usecaseName = getUsecaseName(webappUrl, name);
        return WebAppContextUtils.getCurrentWebApplicationContext().containsBean(
                Usecase.ROLE + "/" + usecaseName);
    }

    /**
     * @return The names of all registered usecases in alphabetical order.
     */
    public String[] getUsecaseNames() {
        if (this.usecaseNames == null) {
            throw new IllegalStateException("No usecase registered!");
        }
        return (String[]) this.usecaseNames.toArray(new String[this.usecaseNames.size()]);
    }

    private SortedSet usecaseNames;

    public void register(String usecaseName) {
        if (this.usecaseNames == null) {
            this.usecaseNames = new TreeSet();
        }
        this.usecaseNames.add(usecaseName);
    }

    public void setTemplateManager(PublicationTemplateManager templateManager) {
        this.templateManager = templateManager;
    }

    public PublicationTemplateManager getTemplateManager() {
        return templateManager;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

}