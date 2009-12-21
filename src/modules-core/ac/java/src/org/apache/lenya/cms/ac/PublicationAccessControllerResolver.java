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

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.impl.AbstractAccessControllerResolver;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;

/**
 * Resolves the access controller according to the <code>access-control.xml</code> file of a
 * publication.
 */
public class PublicationAccessControllerResolver extends AbstractAccessControllerResolver implements
        Initializable {

    protected static final String AC_CONFIGURATION_FILE = "config/access-control/access-control.xml"
            .replace('/', File.separatorChar);
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
            getLogger().debug(
                    "Using first URL step (might be publication ID) as cache key: ["
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
        Publication publication = null;

        assert webappUrl.startsWith("/");
        // remove leading slash
        String url = webappUrl.substring(1);

        if (url.length() > 0) {

            URLInformation info = new URLInformation(webappUrl);
            String pubId = info.getPublicationId();

            ContextUtility util = null;
            try {
                util = (ContextUtility) this.manager.lookup(ContextUtility.ROLE);
                Request request = util.getRequest();
                DocumentFactory factory = DocumentUtil.getDocumentFactory(manager, request);
                if (pubId != null && factory.existsPublication(pubId)) {
                    publication = factory.getPublication(pubId);
                }
            } catch (Exception e) {
                throw new AccessControlException(e);
            } finally {
                if (util != null) {
                    this.manager.release(util);
                }
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
        File configurationFile = new File(publication.getDirectory(), AC_CONFIGURATION_FILE);

        if (configurationFile.isFile()) {
            try {
                Configuration configuration = new DefaultConfigurationBuilder()
                        .buildFromFile(configurationFile);
                return configuration;
            } catch (Exception e) {
                throw new AccessControlException(e);
            }
        } else {
            throw new AccessControlException("No such file or directory: " + configurationFile);
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

            accessController = (AccessController) getManager().lookup(
                    AccessController.ROLE + "/" + type);

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

            if (contextDir == null) {
                throw new AccessControlException("The servlet context is null!");
            } else if(!contextDir.isDirectory()) {
                throw new AccessControlException("The servlet context ["
                        + contextDir.getAbsolutePath() + "] is not a directory!");
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
