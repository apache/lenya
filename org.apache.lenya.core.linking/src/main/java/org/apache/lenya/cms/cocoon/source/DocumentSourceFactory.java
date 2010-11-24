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

import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.linking.Link;
import org.apache.lenya.cms.linking.LinkResolver;
import org.apache.lenya.cms.linking.LinkTarget;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.Repository;
import org.apache.lenya.cms.publication.Session;
//import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.utils.URLInformation;
import org.apache.lenya.util.Query;
//import org.apache.lenya.util.ServletHelper;
import org.apache.lenya.utils.ServletHelper;

/**
 * <p>
 * This source factory allows to access documents using the link syntax of the
 * {@link org.apache.lenya.cms.linking.LinkResolver}.
 * </p>
 * <p>
 * Additional optional parameters, separated using <code>...?f=foo&amp;b=bar</code>:
 * </p>
 * <ul>
 * <li><strong>format</strong> - the resource type format</li>
 * <li><strong>session</strong> - the session. To use the session of the current usecase, specify
 * <code>session=usecase</code></li>
 * </ul>
 */
public class DocumentSourceFactory extends AbstractLogEnabled implements SourceFactory {

    /**
     * The URI scheme.
     */
    public static final String SCHEME = "lenya-document";

    private SourceResolver sourceResolver;
    private LinkResolver linkResolver;
    private Repository repository;

    /**
     * @see org.apache.excalibur.source.SourceFactory#getSource(java.lang.String, java.util.Map)
     */
    public Source getSource(String location, Map parameters) throws MalformedURLException,
            IOException, SourceException {

        ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        HttpServletRequest request = process.getRequest();

        String[] uriAndQuery = location.split("\\?");

        String linkUri = uriAndQuery[0];
        String queryString = null;
        if (uriAndQuery.length > 1) {
            queryString = uriAndQuery[1];
        }

        try {

            Session session = this.repository.getSession(request);
            //String webappUrl = ServletHelper.getWebappURI(request);
            String webappUrl = new URLInformation().getWebappUrl();
            LinkTarget target;
            if (session.getUriHandler().isDocument(webappUrl)) {
                Document currentDoc = session.getUriHandler().getDocument(webappUrl);
                target = getLinkResolver().resolve(session, currentDoc, linkUri);
            } else {
                Link link = new Link(linkUri);
                contextualize(link, webappUrl);
                target = getLinkResolver().resolve(session, link.getUri());
            }

            if (!target.exists()) {
                throw new SourceNotFoundException("Source not found: [" + location + "]");
            }

            Document doc = target.getDocument();

            if (target.isRevisionSpecified()) {
                if (queryString == null) {
                    queryString = "";
                }
                queryString += "rev=" + target.getRevisionNumber();
            }

            String format = null;
            if (queryString != null) {
                Query query = new Query(queryString);
                format = query.getValue("format");
            }
            if (format != null) {
                return getFormatSource(doc, format);
            } else {
                String lenyaURL = doc.getSourceURI();
                if (queryString != null) {
                    lenyaURL += "?" + queryString;
                }
                return this.sourceResolver.resolveURI(lenyaURL);
            }
        } catch (SourceException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * If the link doesn't contain context information (publication ID, area), provide it.
     * @param link The link.
     * @param webappUrl The web application URL to extract the context information from..
     */
    protected void contextualize(Link link, String webappUrl) {
        //URLInformation url = new URLInformation(webappUrl);
    	URLInformation url = new URLInformation();
        if (link.getPubId() == null) {
            link.setPubId(url.getPublicationId());
        }
        if (link.getArea() == null) {
            link.setArea(url.getArea());
        }
    }

    protected Source getFormatSource(Document doc, String format) throws DocumentException,
            ServiceException, IOException {
        String formatBaseUri = doc.getResourceType().getFormatURI(format);
        String formatUri = formatBaseUri + "/" + doc.getPublicationId() + "/" + doc.getArea()
                + "/" + doc.getUUID() + "/" + doc.getLanguage();

        return this.sourceResolver.resolveURI(formatUri);
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source)
     */
    public void release(Source source) {
        this.sourceResolver.release(source);
    }

    public void setLinkResolver(LinkResolver linkResolver) {
        this.linkResolver = linkResolver;
    }

    public LinkResolver getLinkResolver() {
        return linkResolver;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public SourceResolver getSourceResolver() {
        return sourceResolver;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

}
