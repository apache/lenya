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

package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.util.ServletHelper;

/**
 * Factory for creating publication objects.
 */
public final class PublicationFactory extends AbstractLogEnabled {

    /**
     * Create a new <code>PublicationFactory</code>.
     * @param logger The logger to use.
     */
    private PublicationFactory(Logger logger) {
        ContainerUtil.enableLogging(this, logger);
    }

    private static PublicationFactory instance;

    /**
     * Returns the publication factory instance.
     * @param logger The logger to use.
     * @return A publication factory.
     */
    public static PublicationFactory getInstance(Logger logger) {
        if (instance == null) {
            instance = new PublicationFactory(logger);
        }
        return instance;
    }

    private static Map keyToPublication = new HashMap();

    /**
     * Creates a new publication. The publication ID is resolved from the request URI. The servlet
     * context path is resolved from the context object.
     * @param objectModel The object model of the Cocoon component.
     * @return a <code>Publication</code>
     * @throws PublicationException if there was a problem creating the publication.
     */
    public Publication getPublication(Map objectModel) throws PublicationException {

        assert objectModel != null;
        Request request = ObjectModelHelper.getRequest(objectModel);
        Context context = ObjectModelHelper.getContext(objectModel);
        return getPublication(request, context);
    }

    /**
     * Create a new publication with the given publication-id and servlet context path. These
     * publications are cached and reused for similar requests.
     * @param id the publication id
     * @param servletContextPath the servlet context path of the publication
     * @return a <code>Publication</code>
     * @throws PublicationException if there was a problem creating the publication.
     */
    public Publication getPublication(String id, String servletContextPath)
            throws PublicationException {

        assert id != null;
        assert servletContextPath != null;

        String key = generateKey(id, servletContextPath);
        Publication publication = null;

        if (keyToPublication.containsKey(key)) {
            publication = (Publication) keyToPublication.get(key);
        } else {
            publication = new PublicationImpl(id, servletContextPath);
            ContainerUtil.enableLogging(publication, getLogger());
            keyToPublication.put(key, publication);
        }

        if (publication == null) {
            throw new PublicationException("The publication for ID [" + id
                    + "] could not be created.");
        }
        return publication;
    }

    /**
     * Generates a key to cache a publication. The cache key is constructed using the canonical
     * servlet context path and the publication ID.
     * @param publicationId The publication ID.
     * @param servletContextPath The servlet context path.
     * @return A cache key.
     * @throws PublicationException when the servlet context does not exist.
     */
    protected static String generateKey(String publicationId, String servletContextPath)
            throws PublicationException {
        String key;
        File servletContext = new File(servletContextPath);
        String canonicalPath;
        try {
            canonicalPath = servletContext.getCanonicalPath();
        } catch (IOException e) {
            throw new PublicationException(e);
        }
        key = canonicalPath + "_" + publicationId;
        return key;
    }

    /**
     * Creates a new publication based on a request and a context.
     * @param request A request.
     * @param context A context.
     * @return A publication.
     * @throws PublicationException if there was a problem creating the publication.
     */
    public Publication getPublication(Request request, Context context) throws PublicationException {

        getLogger().debug("Creating publication from Cocoon object model");
        String webappUrl = ServletHelper.getWebappURI(request);
        String servletContextPath = context.getRealPath("");
        return getPublication(webappUrl, new File(servletContextPath));
    }

    /**
     * Creates a publication from a webapp URL and a servlet context directory.
     * @param webappUrl The URL within the web application (without context prefix)
     * @param servletContext The Lenya servlet context directory
     * @return A publication
     * @throws PublicationException when something went wrong
     */
    public Publication getPublication(String webappUrl, File servletContext)
            throws PublicationException {
        getLogger().debug("Creating publication from webapp URL and servlet context");

        getLogger().debug("    Webapp URL:       [" + webappUrl + "]");
        String publicationId = new URLInformation(webappUrl).getPublicationId();
        Publication publication = getPublication(publicationId, servletContext.getAbsolutePath());
        return publication;
    }

    /**
     * Creates a publication using a source resolver and a request.
     * @param resolver The source resolver.
     * @param request The request.
     * @return A publication.
     * @throws PublicationException when something went wrong.
     */
    public Publication getPublication(SourceResolver resolver, Request request)
            throws PublicationException {
        getLogger().debug("Creating publication from resolver and request");
        String webappUrl = ServletHelper.getWebappURI(request);
        return getPublication(resolver, webappUrl);
    }

    /**
     * @param resolver A source resolver.
     * @param webappUrl A webapp URL.
     * @return A publication.
     * @throws PublicationException if an error occurs.
     */
    public Publication getPublication(SourceResolver resolver, String webappUrl)
            throws PublicationException {
        Publication publication;
        Source source = null;
        try {
            source = resolver.resolveURI("context:///");
            File servletContext = SourceUtil.getFile(source);
            publication = getPublication(webappUrl, servletContext);
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (source != null) {
                resolver.release(source);
            }
        }
        return publication;
    }

    /**
     * @param manager A service manager.
     * @param webappUrl A webapp URL.
     * @return A publication.
     * @throws PublicationException if an error occurs.
     */
    public Publication getPublication(ServiceManager manager, String webappUrl)
            throws PublicationException {
        Publication publication = null;
        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            publication = getPublication(resolver, webappUrl);
        } catch (ServiceException e) {
            throw new PublicationException(e);
        } finally {
            if (resolver != null) {
                manager.release(resolver);
            }
        }
        return publication;
    }

    /**
     * Returns a list of all available publications.
     * @param manager The service manager to use for source resolving.
     * @return An array of publications.
     * @throws PublicationException if an error occurs.
     */
    public Publication[] getPublications(ServiceManager manager) throws PublicationException {
        List publications = new ArrayList();

        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI("context:///");
            File servletContext = SourceUtil.getFile(source);
            String servletContextPath = servletContext.getAbsolutePath();

            File publicationsDirectory = new File(servletContext,
                    PublicationImpl.PUBLICATION_PREFIX);
            File[] publicationDirectories = publicationsDirectory.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return file.isDirectory();
                }
            });

            for (int i = 0; i < publicationDirectories.length; i++) {
                String publicationId = publicationDirectories[i].getName();
                Publication publication = getPublication(publicationId, servletContextPath);
                publications.add(publication);
            }

        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }

        return (Publication[]) publications.toArray(new Publication[publications.size()]);
    }

}