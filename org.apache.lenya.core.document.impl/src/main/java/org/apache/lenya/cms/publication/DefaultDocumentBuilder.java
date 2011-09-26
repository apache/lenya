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

package org.apache.lenya.cms.publication;

import java.net.MalformedURLException;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.utils.URLInformation;
/**
 * Default document builder implementation.
 */
public class DefaultDocumentBuilder extends AbstractLogEnabled implements DocumentBuilder,
        Serviceable, ThreadSafe {

    /**
     * Ctor.
     */
    public DefaultDocumentBuilder() {
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) {
        this.manager = manager;
    }

    protected ServiceManager manager;

    /**
     * Removes all "."-separated extensions from a URL (e.g.,
     * <code>/foo.print.html</code> is transformed to <code>/foo</code>).
     * @param url The URL to trim.
     * @return A URL string.
     */
    protected String removeExtensions(String url) {
        int dotIndex = url.indexOf(".");
        if (dotIndex > -1) {
            url = url.substring(0, dotIndex);
        }
        return url;
    }

    /**
     * Returns the language of a URL.
     * @param urlWithoutSuffix The URL without the suffix.
     * @return A string.
     */
    protected String getLanguage(String urlWithoutSuffix) {

        String language = "";
        String url = urlWithoutSuffix;

        int languageSeparatorIndex = url.lastIndexOf("_");
        if (languageSeparatorIndex > -1) {
            String suffix = url.substring(languageSeparatorIndex + 1);
            if (suffix.length() <= 5) {
                language = suffix;
            }
        }
        return language;
    }

    /**
     * Returns the extension of a URL.
     * @param url The URL.
     * @return The extension.
     */
    protected String getExtension(String url) {
        int startOfSuffix = url.lastIndexOf('.');
        String suffix = "";

        if ((startOfSuffix > -1) && !url.endsWith(".")) {
            suffix = url.substring(startOfSuffix + 1);
        }

        return suffix;
    }

    //florent : public boolean isDocument(Session session, String url) {
    public boolean isDocument(String url) {
        try {
            //DocumentLocator locator = getLocatorWithoutCheck(session, url);
        	DocumentLocator locator = getLocatorWithoutCheck(url);
            if (locator != null) {
                //Publication pub = session.getPublication(locator.getPublicationId());
            	Publication pub = (Publication)WebAppContextUtils.getCurrentWebApplicationContext().getBean(Publication.class.getName()+"/"+locator.getPublicationId());
                String path = locator.getPath();
                Area area = pub.getArea(locator.getArea());
                if (area.getSite().contains(path)) {
                    SiteNode node = area.getSite().getNode(path);
                    if (node.hasLink(locator.getLanguage())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    /**
     * Builds the canonical document URL.
     * @param session The document factory.
     * @param locator The document locator.
     * @return A string.
     */
    //florent : protected String buildCanonicalDocumentUrl(Session session, DocumentLocator locator) {
    protected String buildCanonicalDocumentUrl(DocumentLocator locator) {

        String languageSuffix = "";
        String language = locator.getLanguage();

        //florent : Publication pub = session.getPublication(locator.getPublicationId());
        Publication pub = (Publication)WebAppContextUtils.getCurrentWebApplicationContext().getBean(Publication.class.getName()+"/"+locator.getPublicationId());

        if (!language.equals(pub.getDefaultLanguage())) {
            languageSuffix = "_" + language;
        }

        return locator.getPath() + languageSuffix + ".html";
    }

    //florent public String buildCanonicalUrl(Session session, DocumentLocator doc) {
    public String buildCanonicalUrl(DocumentLocator doc) {

        //String documentUrl = buildCanonicalDocumentUrl(session, doc);
    	String documentUrl = buildCanonicalDocumentUrl(doc);
        String url = "/" + doc.getPublicationId() + "/" + doc.getArea() + documentUrl;
        return url;
    }

    //florent public DocumentLocator getLocator(Session session, String webappUrl) throws MalformedURLException {
    public DocumentLocator getLocator(String webappUrl) throws MalformedURLException {

        //DocumentLocator locator = getLocatorWithoutCheck(session, webappUrl);
    	DocumentLocator locator = getLocatorWithoutCheck(webappUrl);
        if (locator == null) {
            throw new ResourceNotFoundException("The webapp URL [" + webappUrl
                    + "] does not refer to a document!");
        }
        return locator;
    }

    /**
     * Creates a document locator for a webapp URL without checking if the
     * webapp URL refers to a locator first.
     * @param session The document factory.
     * @param webappUrl The webapp URL.
     * @return A document locator or <code>null</code> if the URL doesn't
     *         refer to a locator.
     * @throws MalformedURLException if the URL is not a webapp URL. 
     */
    //florent : protected DocumentLocator getLocatorWithoutCheck(Session session, String webappUrl) throws MalformedURLException {
    //TODO : remove the webappUrl param, not used
    protected DocumentLocator getLocatorWithoutCheck(String webappUrl) throws MalformedURLException {

        if (!webappUrl.startsWith("/")) {
            return null;
        }
        if (webappUrl.substring(1).split("/").length < 3) {
            return null;
        }

        URLInformation info = new URLInformation();

        //Publication publication = session.getPublication(info.getPublicationId());
        Publication publication = (Publication)WebAppContextUtils.getCurrentWebApplicationContext().getBean(Publication.class.getName()+"/"+info.getPublicationId());
        String documentURL = info.getDocumentUrl();
        documentURL = removeExtensions(documentURL);

        String language = getLanguage(documentURL);
        String fullLanguage = "".equals(language) ? "" : ("_" + language);
        documentURL = documentURL.substring(0, documentURL.length() - fullLanguage.length());

        if ("".equals(language)) {
            language = publication.getDefaultLanguage();
        }

        String path = documentURL;

        if (!path.startsWith("/")) {
            throw new MalformedURLException("Path [" + path + "] does not start with '/'!");
        }

        return DocumentLocatorImpl.getLocator(publication.getId(), info.getArea(), path, language);
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentBuilder#isValidDocumentName(java.lang.String)
     */
    public boolean isValidDocumentName(String documentName) {
        return documentName.matches("[a-zA-Z0-9\\-]+");
    }

}
