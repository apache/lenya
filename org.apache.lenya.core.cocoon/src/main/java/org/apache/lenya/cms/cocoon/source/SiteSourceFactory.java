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

import javax.servlet.http.HttpServletRequest;

import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Repository;
import org.apache.lenya.cms.publication.Session;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.utils.ServletHelper;

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
public class SiteSourceFactory extends AbstractLogEnabled implements SourceFactory {

    private SourceResolver sourceResolver;
    private Repository repository;

    /**
     * @see org.apache.excalibur.source.SourceFactory#getSource(java.lang.String, java.util.Map)
     */
    public Source getSource(String location, Map parameters) throws MalformedURLException,
            IOException, SourceException {
        ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        HttpServletRequest request = process.getRequest();

        String areaName = null;
        String pubId;

        StringTokenizer locationSteps = new StringTokenizer(location, "?");
        String completePath = locationSteps.nextToken();

        String relativePath;
        try {

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

            Session session = this.repository.getSession(request);
            SiteStructure site = session.getPublication(pubId).getArea(areaName).getSite();

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
                return this.sourceResolver.resolveURI(docUri);
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
        this.sourceResolver.release(source);
    }

    public SourceResolver getSourceResolver() {
        return sourceResolver;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

}
