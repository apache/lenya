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

/* $Id$  */

package org.apache.lenya.cms.ac;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.commons.lang.Validate;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.impl.AbstractAccessControllerResolver;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Repository;
import org.apache.lenya.cms.publication.Session;
//import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.utils.URLInformation;

/**
 * Resolves the access controller according to the <code>access-control.xml</code> file of a
 * publication.
 */
public class PublicationAccessControllerResolver extends AbstractAccessControllerResolver {

    protected static final String AC_CONFIGURATION_URI = "config/access-control/access-control.xml";
    protected static final String TYPE_ATTRIBUTE = "type";
    protected static final String GLOBAL_CACHE_KEY = "";

    private Repository repository;
    private SourceResolver sourceResolver;

    public SourceResolver getSourceResolver() {
        return sourceResolver;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

    /**
     * This implementation uses the publication ID in combination with the context path as cache
     * key.
     * @see org.apache.lenya.ac.impl.AbstractAccessControllerResolver#generateCacheKey(java.lang.String,
     *      org.apache.excalibur.source.SourceResolver)
     */
    protected Object generateCacheKey(String webappUrl, SourceResolver resolver)
            throws AccessControlException {
        Validate.isTrue(webappUrl.startsWith("/"), "Webapp URL must start with a slash.");

        //TODO : florent : remove comment when ok 
        //URLInformation info = new URLInformation(webappUrl);
        URLInformation info = new URLInformation();

        String publicationId = info.getPublicationId();
        String cacheKey = publicationId == null ? GLOBAL_CACHE_KEY : publicationId;

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                    "Using first URL step (might be publication ID) as cache key: ["
                            + publicationId + "]");
        }

        return super.generateCacheKey(cacheKey, resolver);
    }

    /**
     * @see org.apache.lenya.ac.impl.AbstractAccessControllerResolver#doResolveAccessController(java.lang.String)
     */
    public AccessController doResolveAccessController(String webappUrl)
            throws AccessControlException {
        getLogger().debug("Resolving controller for URL [" + webappUrl + "]");

        AccessController controller = null;
        Publication publication = getPublication(webappUrl);

        if (publication != null) {
            String publicationUrl = webappUrl.substring(("/" + publication.getId()).length());
            controller = resolveAccessController(publication, publicationUrl);
        }
        return controller;
    }

    /**
     * Returns the publication for the webapp URL or null if the URL is not included in a
     * publication.
     * @param webappUrl The webapp URL.
     * @return A publication.
     * @throws AccessControlException when something went wrong.
     */
    protected Publication getPublication(String webappUrl) throws AccessControlException {
        Validate.isTrue(webappUrl.startsWith("/"), "Webapp URL must start with a slash.");

        Publication publication = null;

        // remove leading slash
        String url = webappUrl.substring(1);

        if (url.length() > 0) {

        	//TODO : florent : remove comment when ok 
          //URLInformation info = new URLInformation(webappUrl);
          URLInformation info = new URLInformation();
            String pubId = info.getPublicationId();

            try {
                ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                        .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
                HttpServletRequest request = process.getRequest();
                Session session = this.repository.getSession(request);
                if (pubId != null && session.existsPublication(pubId)) {
                    publication = session.getPublication(pubId);
                }
            } catch (Exception e) {
                throw new AccessControlException(e);
            }
            if (publication != null) {
                getLogger().debug("Publication [" + pubId + "] exists.");
            }
        }
        return publication;
    }

    /**
     * Returns the servlet context.
     * @return A file.
     */
    protected File getContext() {
        return this.context;
    }

    /**
     * Retrieves access control configuration of a specific publication.
     * @param publication The publication.
     * @return Configuration
     * @throws AccessControlException when something went wrong.
     */
    public Configuration getConfiguration(Publication publication) throws AccessControlException {
        String uri = publication.getPubBaseUri() + "/" + publication.getId() + "/"
                + AC_CONFIGURATION_URI;
        Source source = null;
        try {
            source = this.sourceResolver.resolveURI(uri);
            if (source.exists()) {

                Configuration configuration = new DefaultConfigurationBuilder().build(source
                        .getInputStream());
                return configuration;
            } else {
                throw new AccessControlException("No such file or directory: " + uri);
            }
        } catch (AccessControlException e) {
            throw e;
        } catch (Exception e) {
            throw new AccessControlException(e);
        }
    }

    private File context;

    /**
     * Resolves an access controller for a certain URL within a publication.
     * @param publication The publication.
     * @param url The url within the publication.
     * @return An access controller.
     * @throws AccessControlException when something went wrong.
     */
    public AccessController resolveAccessController(Publication publication, String url)
            throws AccessControlException {

        assert publication != null;

        AccessController accessController = null;

        try {
            Configuration configuration = getConfiguration(publication);
            String type = configuration.getAttribute(TYPE_ATTRIBUTE);

            accessController = (AccessController) WebAppContextUtils
                    .getCurrentWebApplicationContext().getBean(AccessController.ROLE + "/" + type);

            if (accessController instanceof Configurable) {
                ((Configurable) accessController).configure(configuration);
            }

        } catch (Exception e) {
            throw new AccessControlException(e);
        }

        return accessController;
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception {
        Source contextSource = null;
        File contextDir;
        SourceResolver resolver = getSourceResolver();
        try {
            contextSource = resolver.resolveURI("context:///");
            contextDir = SourceUtil.getFile(contextSource);

            if (contextDir == null || !contextDir.isDirectory()) {
                throw new AccessControlException("The servlet context is not a directory!");
            }

        } finally {
            if (contextSource != null) {
                resolver.release(contextSource);
            }
        }
        this.context = contextDir;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

}
