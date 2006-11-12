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

package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.cms.publication.file.FilePublication;
import org.apache.lenya.util.ServletHelper;
import org.apache.log4j.Logger;

/**
 * Factory for creating publication objects.
 */
public final class PublicationFactory {

    private static Logger log = Logger.getLogger(PublicationFactory.class);

    /**
     * Create a new <code>PublicationFactory</code>.
     */
    private PublicationFactory() {
    }

    private static Map keyToPublication = new HashMap();

    /**
     * Creates a new publication.
     * The publication ID is resolved from the request URI.
     * The servlet context path is resolved from the context object.
    
     * @param objectModel The object model of the Cocoon component.
     * 
     * @return a <code>Publication</code>
     * 
     * @throws PublicationException if there was a problem creating the publication.
     */
    public static Publication getPublication(Map objectModel) throws PublicationException {

        assert objectModel != null;
        Request request = ObjectModelHelper.getRequest(objectModel);
        Context context = ObjectModelHelper.getContext(objectModel);
        return getPublication(request, context);
    }

    /**
     * Create a new publication with the given publication-id and servlet context path.
     * These publications are cached and reused for similar requests.
     *
     * @param id the publication id
     * @param servletContextPath the servlet context path of the publication
     *
     * @return a <code>Publication</code>
     * 
     * @throws PublicationException if there was a problem creating the publication.
     */
    public static Publication getPublication(String id, String servletContextPath)
        throws PublicationException {

        assert id != null;
        assert servletContextPath != null;

        String key = generateKey(id, servletContextPath);
        Publication publication = null;

        if (keyToPublication.containsKey(key)) {
            publication = (Publication) keyToPublication.get(key);
        } else {
            if (PublicationFactory.existsPublication(id, servletContextPath)) {
                publication = new FilePublication(id, servletContextPath);
                keyToPublication.put(key, publication);
            }
        }

        if (publication == null) {
            throw new PublicationException("The publication for ID [" + id + "] could not be created.");
        }
        return publication;
    }

    /**
     * Generates a key to cache a publication.
     * The cache key is constructed using the canonical servlet context path
     * and the publication ID.
     * 
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
     * 
     * @param request A request.
     * @param context A context.
     * 
     * @return A publication.
     * 
     * @throws PublicationException if there was a problem creating the publication.
     */
    public static Publication getPublication(Request request, Context context)
        throws PublicationException {

        log.debug("Creating publication from Cocoon object model");
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
    public static Publication getPublication(String webappUrl, File servletContext)
        throws PublicationException {
        log.debug("Creating publication from webapp URL and servlet context");

        log.debug("    Webapp URL:       [" + webappUrl + "]");
        String publicationId = new URLInformation(webappUrl).getPublicationId();
        Publication publication = getPublication(publicationId, servletContext.getAbsolutePath());
        return publication;
    }

    /**
     * Checks if a publication with a certain ID exists in a certain context.
     * @param id The publication ID.
     * @param servletContextPath The webapp context path.
     * @return <code>true</code> if the publication exists, <code>false</code> otherwise.
     */
    public static boolean existsPublication(String id, String servletContextPath) {

        if (servletContextPath.endsWith("/")) {
            servletContextPath = servletContextPath.substring(0, servletContextPath.length() - 1);
        }

        File publicationDirectory =
            new File(
                servletContextPath
                    + File.separator
                    + Publication.PUBLICATION_PREFIX
                    + File.separator
                    + id);

        boolean exists = true;
        exists = exists && publicationDirectory.isDirectory();
        exists = exists && new File(publicationDirectory, Publication.CONFIGURATION_FILE).exists();

        return exists;
    }

    /**
     * Creates a publication using a source resolver and a request.
     * @param resolver The source resolver.
     * @param request The request.
     * @return A publication.
     * @throws PublicationException when something went wrong.
     */
    public static Publication getPublication(SourceResolver resolver, Request request)
        throws PublicationException {
        log.debug("Creating publication from resolver and request");
        Publication publication;
        String webappUri = ServletHelper.getWebappURI(request);
        Source source = null;
        try {
            source = resolver.resolveURI("context:///");
            File servletContext = SourceUtil.getFile(source);
            publication = PublicationFactory.getPublication(webappUri, servletContext);
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (source != null) {
                resolver.release(source);
            }
        }
        return publication;
    }
    
}
