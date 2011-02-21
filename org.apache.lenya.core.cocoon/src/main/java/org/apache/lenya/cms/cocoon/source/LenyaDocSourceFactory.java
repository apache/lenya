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
package org.apache.lenya.cms.cocoon.source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceFactory;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Repository;
import org.apache.lenya.cms.publication.ResourceNotFoundException;
import org.apache.lenya.cms.publication.Session;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.NodeFactory;
import org.apache.lenya.utils.ServletHelper;

/**
 * A factory for the "lenyadoc" scheme (virtual protocol), which is used to resolve any
 * src="lenyadoc:<...>" attributes in sitemaps.
 * 
 * <code>lenyadoc://<publication>/<area>/<language>/<uuid></code>
 * <code>lenyadoc:/<language>/<uuid></code>
 * 
 * If we want to request the meta data for a document
 * instead of the document itself, we need to use
 * 
 * <code>lenyadoc:meta:/<language>/<uuid></code>
 * <code>lenyadoc:meta://<publication>/<area>/<language>/<uuid></code>
 * 
 * @version $Id:$
 * @deprecated Use <code>lenya-document</code> instead (see {@link org.apache.lenya.cms.cocoon.source.DocumentSourceFactory}.
 */
public class LenyaDocSourceFactory extends AbstractLogEnabled implements SourceFactory {

    protected static final String SCHEME = "lenyadoc";
    
    private Repository repository;
    private NodeFactory nodeFactory;

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException {
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#getSource(java.lang.String, java.util.Map)
     */
    public Source getSource(String location, Map parameters) throws MalformedURLException,
            IOException, SourceException {
        String scheme = null;
        String area = null;
        String language = null;
        String uuid = null;
        Publication pub;

        // Parse the url
        int start = 0;
        int end;

        // Scheme
        end = location.indexOf(':', start);
        if (end == -1) {
            throw new MalformedURLException("Malformed lenyadoc: URI: can not find scheme part ["
                    + location + "]");
        }
        scheme = location.substring(start, end);
        if (!SCHEME.equals(scheme)) {
            throw new MalformedURLException("Malformed lenyadoc: URI: unknown scheme [" + location
                    + "]");
        }

        ProcessInfoProvider processInfoProvider = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        HttpServletRequest request = processInfoProvider.getRequest();
        Session session = this.repository.getSession(request);
        start = end + 1;
        
        // Absolute vs. relative
        if (location.startsWith("//", start)) {
            // Absolute: get publication id
            start += 2;
            end = location.indexOf('/', start);
            if (end == -1) {
                throw new MalformedURLException("Malformed lenyadoc: URI: publication part not found ["
                        + location + "]");
            }
            String publicationId = location.substring(start, end);
            try {
                pub = session.getPublication(publicationId);
            } catch (ResourceNotFoundException e) {
                throw new MalformedURLException("Malformed lenyadoc: Publication [" + publicationId
                        + "] does not exist or could not be initialized");
            }
            if (pub == null || !pub.exists()) {
                throw new SourceException("The publication [" + publicationId + "] does not exist!");
            }

            // Area
            start = end + 1;
            end = location.indexOf('/', start);
            if (end == -1) {
                throw new MalformedURLException("Malformed lenyadoc: URI: cannot find area ["
                        + location + "]");
            }
            area = location.substring(start, end);

        } else if (location.startsWith("/", start)) {
            end += 1;
            // Relative: get publication id and area from page envelope
            try {
                String id = new URLInformation(ServletHelper.getWebappURI(request)).getPublicationId();
                pub = session.getPublication(id);
            } catch (ResourceNotFoundException e) {
                throw new SourceException("Error getting publication id / area from page envelope ["
                        + location + "]");
            }
            if (pub != null && pub.exists()) {
                String url = ServletHelper.getWebappURI(request);
                area = new URLInformation(url).getArea();
            } else {
                throw new SourceException("Error getting publication id / area from page envelope ["
                        + location + "]");
            }
        } else {
            throw new MalformedURLException("Malformed lenyadoc: URI [" + location + "]");
        }

        // Language
        start = end + 1;
        end = location.indexOf('/', start);
        if (end == -1) {
            throw new MalformedURLException("Malformed lenyadoc: URI: cannot find language ["
                    + location + "]");
        }
        language = location.substring(start, end);

        // UUID
        start = end + 1;
        uuid = location.substring(start);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Creating repository source for URI [" + location + "]");
        }
        Document document;
        try {
            document = pub.getArea(area).getDocument(uuid, language);
        } catch (ResourceNotFoundException e) {
            throw new MalformedURLException("Malformed lenyadoc: Document [" + uuid + ":"
                    + language + "] could not be created.");
        }

        String lenyaURL = document.getSourceURI();

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Mapping 'lenyadoc:' URL [" + location + "] to 'lenya:' URL ["
                    + lenyaURL + "]");
            getLogger().debug("Creating repository source for URI [" + lenyaURL + "]");
        }

        return new RepositorySource(getNodeFactory(), lenyaURL, session, getLogger());
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source)
     */
    public void release(Source source) {
        // Source will be released by delegated source factory.
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setNodeFactory(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public NodeFactory getNodeFactory() {
        return nodeFactory;
    }
}