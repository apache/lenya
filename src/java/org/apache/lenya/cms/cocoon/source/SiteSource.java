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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.StringTokenizer;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceValidity;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.util.Query;
import org.apache.lenya.util.ServletHelper;

/**
 * Source for the site:/ protocol.
 */
public class SiteSource extends AbstractLogEnabled implements Source {

    private ServiceManager manager;
    private Source delegate;
    private Source repositorySource;
    private String scheme;
    private String uri;

    /**
     * @param manager The service manager.
     * @param request The cocoon request.
     * @param location The source URI.
     * @param logger The logger.
     */
    public SiteSource(ServiceManager manager, Request request, String location, Logger logger) {
        ContainerUtil.enableLogging(this, logger);

        this.manager = manager;
        this.uri = location;

        String areaName = null;
        String pubId;

        StringTokenizer locationSteps = new StringTokenizer(location, "?");
        String completePath = locationSteps.nextToken();

        String relativePath;
        try {

            this.scheme = completePath.substring(0, completePath.indexOf(":") + 1);
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
                String lenyaURL = doc.getSourceURI();
                Session session = RepositoryUtil.getSession(this.manager, request);
                this.repositorySource = new RepositorySource(manager, lenyaURL, session, getLogger());

                if (locationSteps.hasMoreTokens()) {
                    Query query = new Query(locationSteps.nextToken());
                    String format = query.getValue("format");
                    if (format != null) {
                        this.delegate = getFormatSource(doc, format);
                    }
                }
                if (this.delegate == null) {
                    this.delegate = this.repositorySource;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public boolean exists() {
        return this.repositorySource != null;
    }

    public long getContentLength() {
        return !exists() ? 0 : this.delegate.getContentLength();
    }

    public InputStream getInputStream() throws IOException, SourceNotFoundException {
        if (!exists()) {
            throw new SourceNotFoundException("The source [" + getURI() + "] does not exist!");
        }
        return this.delegate.getInputStream();
    }

    public long getLastModified() {
        return !exists() ? 0 : this.delegate.getLastModified();
    }

    public String getMimeType() {
        return !exists() ? "" : this.delegate.getMimeType();
    }

    public String getScheme() {
        return this.scheme;
    }

    public String getURI() {
        return this.uri;
    }

    public SourceValidity getValidity() {
        return this.delegate.getValidity();
    }

    public void refresh() {
        if (exists()) {
            this.delegate.refresh();
        }
    }

    protected Source getFormatSource(Document doc, String format) throws DocumentException,
            ServiceException, MalformedURLException, IOException {
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

}
