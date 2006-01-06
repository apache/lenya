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

/* $Id$  */

package org.apache.lenya.cms.ac;

import java.io.File;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.impl.AbstractAccessControllerResolver;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repo.Publication;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.impl.RepositoryUtil;

/**
 * Resolves the access controller according to the <code>ac.xconf</code> file of a publication.
 */
public class PublicationAccessControllerResolver extends AbstractAccessControllerResolver implements
        Initializable {

    protected static final String CONFIGURATION_FILE = "/config/ac/ac.xconf";
    protected static final String TYPE_ATTRIBUTE = "type";

    /**
     * This implementation uses the publication ID in combination with the context path as cache
     * key.
     * @see org.apache.lenya.ac.impl.AbstractAccessControllerResolver#generateCacheKey(java.lang.String,
     *      org.apache.excalibur.source.SourceResolver)
     */
    protected Object generateCacheKey(String webappUrl, SourceResolver resolver)
            throws AccessControlException {

        URLInformation info = new URLInformation(webappUrl);

        String publicationId = info.getPublicationId();
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Using first URL step (might be publication ID) as cache key: ["
                    + publicationId + "]");
        }

        return super.generateCacheKey(publicationId, resolver);
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
            try {
                String publicationUrl = webappUrl.substring(("/" + publication.getPublicationId()).length());
                controller = resolveAccessController(publication, publicationUrl);
            } catch (RepositoryException e) {
                throw new AccessControlException(e);
            }
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

        ContextUtility contextUtility = null;
        try {
            contextUtility = (ContextUtility) this.manager.lookup(ContextUtility.ROLE);
            Session session = RepositoryUtil.getSession(this.manager,
                    contextUtility.getRequest(),
                    getLogger());
            String pubId = new URLInformation(webappUrl).getPublicationId();
            if (session.existsPublication(pubId)) {
                getLogger().debug("Publication [" + pubId + "] exists.");
                return session.getPublication(pubId);
            } else {
                getLogger().debug("Publication [" + pubId + "] does not exist.");
                return null;
            }

        } catch (ServiceException e) {
            throw new AccessControlException("Error looking up ContextUtility component", e);
        } catch (Exception e) {
            throw new AccessControlException(e);
        } finally {
            if (contextUtility != null) {
                this.manager.release(contextUtility);
            }
        }

    }

    /**
     * Returns the servlet context.
     * @return A file.
     */
    protected File getContext() {
        return this.context;
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
            String configUrl = "context://lenya/pubs/" + publication.getPublicationId()
                    + CONFIGURATION_FILE;
            Configuration configuration = new DefaultConfigurationBuilder().build(configUrl);
            String type = configuration.getAttribute(TYPE_ATTRIBUTE);

            accessController = (AccessController) getManager().lookup(AccessController.ROLE + "/"
                    + type);

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
        SourceResolver resolver = null;
        Source contextSource = null;
        File contextDir;
        try {
            resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);
            contextSource = resolver.resolveURI("context:///");
            contextDir = SourceUtil.getFile(contextSource);

            if (contextDir == null || !contextDir.isDirectory()) {
                throw new AccessControlException("The servlet context is not a directory!");
            }

        } finally {
            if (resolver != null) {
                if (contextSource != null) {
                    resolver.release(contextSource);
                }
                getManager().release(resolver);
            }
        }
        this.context = contextDir;
    }

}