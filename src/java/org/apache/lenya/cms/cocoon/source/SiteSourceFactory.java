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
import java.util.StringTokenizer;

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
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
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
        Contextualizable, Serviceable {

    private Context context;
    private ServiceManager manager;
    private SourceResolver resolver;

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
     * @see org.apache.excalibur.source.SourceFactory#getSource(java.lang.String, java.util.Map)
     */
    public Source getSource(String location, Map parameters) throws MalformedURLException,
            IOException, SourceException {
        Map objectModel = ContextHelper.getObjectModel(this.context);
        Request request = ObjectModelHelper.getRequest(objectModel);

        String areaName = null;
        String pubId;

        StringTokenizer locationSteps = new StringTokenizer(location, "?");
        String completePath = locationSteps.nextToken();

        String relativePath;
        try {
            this.resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            String scheme = completePath.substring(0, completePath.indexOf(":") + 1);
            final String absolutePath = completePath.substring(scheme.length());
            if (absolutePath.startsWith("//")) {
                final String fullPath = absolutePath.substring(2);
                StringTokenizer steps = new StringTokenizer(fullPath, "/");
                pubId = steps.nextToken();
                areaName = steps.nextToken();
                String prefix = pubId + "/" + areaName;
                relativePath = fullPath.substring(prefix.length());
            } else if (absolutePath.startsWith("/")) {
                String webappUrl = ServletHelper.getWebappURI(request);
                URLInformation info = new URLInformation(webappUrl);
                pubId = info.getPublicationId();
                areaName = info.getArea();
                relativePath = absolutePath;
            } else {
                throw new MalformedURLException("The path [" + absolutePath
                        + "] must start with at least one slash.");
            }

            DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);
            Publication pub = factory.getPublication(pubId);
            SiteStructure site = pub.getArea(areaName).getSite();

            String[] steps = relativePath.substring(1).split("/");

            String language = steps[0];
            String prefix = "/" + language;
            String path = relativePath.substring(prefix.length());

            if (site.contains(path, language)) {
                Document doc = site.getNode(path).getLink(language).getDocument();
                String docUri = "lenya-document:" + doc.getUUID() + ",lang=" + doc.getLanguage()
                        + ",area=" + doc.getArea() + ",pub=" + doc.getPublication().getId();

                if (locationSteps.hasMoreTokens()) {
                    String queryString = locationSteps.nextToken();
                    docUri = docUri + "?" + queryString;
                }
                return this.resolver.resolveURI(docUri);
            } else {
                throw new SourceNotFoundException("The source [" + location + "] doesn't exist.");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source)
     */
    public void release(Source source) {
        this.resolver.release(source);
    }

}
