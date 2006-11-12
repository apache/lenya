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
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.linking.LinkResolver;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.Query;
import org.apache.lenya.util.ServletHelper;

/**
 * <p>
 * This source factory allows to access documents using the
 * link syntax of the {@link org.apache.lenya.cms.linking.LinkResolver}.
 * </p>
 * <p>
 * Additional parameters:
 * </p>
 * <ul>
 * <li><strong>format</strong> - the resource type format</li>
 * </ul>
 */
public class DocumentSourceFactory extends AbstractLogEnabled implements SourceFactory, ThreadSafe,
        Contextualizable, Serviceable, Configurable {

    public static final String SCHEME = "lenya-document";

    private Context context;
    private ServiceManager manager;

    /**
     * Used for resolving the object model.
     * 
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
     * @see org.apache.excalibur.source.SourceFactory#getSource(java.lang.String,
     *      java.util.Map)
     */
    public Source getSource(String location, Map parameters) throws MalformedURLException,
            IOException, SourceException {

        Map objectModel = ContextHelper.getObjectModel(this.context);
        Request request = ObjectModelHelper.getRequest(objectModel);

        String[] uriAndQuery = location.split("\\?");

        String linkUri = uriAndQuery[0];
        String queryString = null;
        if (uriAndQuery.length > 1) {
            queryString = uriAndQuery[1];
        }

        LinkResolver resolver = null;
        try {
            resolver = (LinkResolver) this.manager.lookup(LinkResolver.ROLE);
            DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);
            String webappUrl = ServletHelper.getWebappURI(request);
            Document currentDoc = factory.getFromURL(webappUrl);

            Document doc = resolver.resolve(currentDoc, linkUri);

            String format = null;
            if (queryString != null) {
                Query query = new Query(queryString);
                format = query.getValue("format");
            }
            if (format != null) {
                return getFormatSource(doc, format);
            } else {
                String lenyaURL = doc.getSourceURI();
                Session session = RepositoryUtil.getSession(this.manager, request);
                return new RepositorySource(manager, lenyaURL, session, getLogger());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected Source getFormatSource(Document doc, String format) throws DocumentException,
            ServiceException, IOException {
        String formatBaseUri = doc.getResourceType().getFormatURI(format);
        String formatUri = formatBaseUri + "/" + doc.getPublication().getId() + "/" + doc.getArea()
                + "/" + doc.getUUID() + "/" + doc.getLanguage();

        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            return resolver.resolveURI(formatUri);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source)
     */
    public void release(Source source) {
        // Source will be released by delegated source factory.
    }

}
