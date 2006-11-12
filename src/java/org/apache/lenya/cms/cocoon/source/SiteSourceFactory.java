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

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.util.ServletHelper;

/**
 * <p>
 * This source factory allows to access documents based on their path in the site structure.
 * </p>
 * <p>
 * Relative addressing refers to the current publication and area.
 * </p>
 * <p>
 * Syntax:
 * </p>
 * <ul>
 * <li>Absolute: <code>site://{pubId}/{area}/{language}{path}</code></li>
 * <li>Relative: <code>site:/{language}{path}</code></li>
 * </ul>
 * <p>
 * Usage examples:
 * </p>
 * <ul>
 * <li><code>site://default/authoring/en/news/today</code></li>
 * <li><code>site:/en/news/today</code></li>
 * </ul>
 */
public class SiteSourceFactory extends AbstractLogEnabled implements SourceFactory, ThreadSafe,
        Contextualizable, Serviceable, Configurable {

    protected static final String SCHEME = "site";

    private Context context;
    private ServiceManager manager;

    /**
     * Used for resolving the object model.
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException {
        this.context = context;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

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
        String area = null;
        String pubId;

        Map objectModel = ContextHelper.getObjectModel(this.context);
        Request request = ObjectModelHelper.getRequest(objectModel);

        String completePath = location.split("\\?")[0];
        String queryString = null;
        if (location.indexOf("?") > -1) {
            queryString = location.split("\\?")[1];
        }

        String relativePath;
        try {

            final String scheme = completePath.split(":")[0] + ":";
            final String absolutePath = completePath.substring(scheme.length());
            if (absolutePath.startsWith("//")) {
                final String fullPath = absolutePath.substring(2);
                String[] steps = fullPath.split("/");
                pubId = steps[0];
                area = steps[1];
                String prefix = pubId + "/" + area;
                relativePath = fullPath.substring(prefix.length());
            } else if (absolutePath.startsWith("/")) {
                String webappUrl = ServletHelper.getWebappURI(request);
                URLInformation info = new URLInformation(webappUrl);
                pubId = info.getPublicationId();
                area = info.getArea();
                relativePath = absolutePath;
            } else {
                throw new MalformedURLException("The path [" + absolutePath
                        + "] must start with at least one slash.");
            }

            DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);
            Publication pub = factory.getPublication(pubId);
            String[] steps = relativePath.substring(1).split("/");

            String language = steps[0];
            String prefix = "/" + language;
            String path = relativePath.substring(prefix.length());

            ServiceSelector selector = null;
            SiteManager siteManager = null;

            Document doc;

            try {
                selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
                siteManager = (SiteManager) selector.select(pub.getSiteManagerHint());
                SiteStructure structure = siteManager.getSiteStructure(factory, pub, area);
                doc = structure.getNode(path).getLink(language).getDocument();
            } finally {
                if (selector != null) {
                    if (siteManager != null) {
                        selector.release(siteManager);
                    }
                    this.manager.release(selector);
                }
            }

            if (queryString != null && queryString.length() > 0) {
                return getFormatSource(doc, queryString);
            } else {
                String lenyaURL = doc.getSourceURI();
                Session session = RepositoryUtil.getSession(this.manager, request);
                return new RepositorySource(manager, lenyaURL, session, getLogger());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected Source getFormatSource(Document doc, String queryString) throws DocumentException, ServiceException, MalformedURLException, IOException {
        String name = queryString.split("=")[0];
        String value = queryString.split("=")[1];

        if (name.equals("format")) {
            String format = value;
            String formatBaseUri = doc.getResourceType().getFormatURI(format);
            String formatUri = formatBaseUri + "/" + doc.getPublication().getId() + "/"
                    + doc.getArea() + "/" + doc.getUUID() + "/" + doc.getLanguage();
            
            SourceResolver resolver = null;
            try {
                resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                return resolver.resolveURI(formatUri);
            }
            finally {
                if (resolver != null) {
                    this.manager.release(resolver);
                }
            }
            
        } else {
            throw new MalformedURLException("The parameter [" + name
                    + "] is not supported.");
        }
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source)
     */
    public void release(Source source) {
        // Source will be released by delegated source factory.
    }

}
