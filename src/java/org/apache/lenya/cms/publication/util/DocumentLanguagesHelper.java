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

package org.apache.lenya.cms.publication.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;

/**
 * Helper class for the policy GUI.
 */
public class DocumentLanguagesHelper {

    private DocumentFactory factory;
    private ServiceManager manager;
    private Publication pub;
    private String url;
    private String contextPath;

    /**
     * Create a new DocumentlanguageHelper.
     * @param objectModel the objectModel
     * @param manager The service manager.
     * @throws ProcessingException if the page envelope could not be created.
     */
    public DocumentLanguagesHelper(Map objectModel, ServiceManager manager)
            throws ProcessingException {

        this.manager = manager;
        Request request = ObjectModelHelper.getRequest(objectModel);
        this.url = ServletHelper.getWebappURI(request);
        this.contextPath = request.getContextPath();

        try {
            Session session = RepositoryUtil.getSession(manager, request);
            this.factory = DocumentUtil.createDocumentFactory(this.manager, session);

            this.pub = PublicationUtil.getPublication(manager, objectModel);
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }

    /**
     * @return The requested language.
     * @throws ProcessingException if an error occurs.
     */
    public String getLanguage() throws ProcessingException {
        try {
            return getLocator().getLanguage();
        } catch (DocumentBuildException e) {
            throw new ProcessingException(e);
        }
    }

    /**
     * All available languages for the current URL.
     * @return A string array.
     * @throws ProcessingException
     */
    public String[] getLanguages() throws ProcessingException {
        List availableLanguages = new ArrayList();

        try {
            DocumentLocator locator = getLocator();
            String[] languages = pub.getLanguages();
            for (int i = 0; i < languages.length; i++) {
                DocumentLocator version = locator.getLanguageVersion(languages[i]);
                Publication pub = factory.getPublication(locator.getPublicationId());
                if (pub.getArea(version.getArea()).getSite().contains(version.getPath(), version.getLanguage())) {
                    availableLanguages.add(languages[i]);
                }
            }

        } catch (Exception e) {
            throw new ProcessingException(e);
        }
        return (String[]) availableLanguages.toArray(new String[availableLanguages.size()]);
    }

    /**
     * Compute the URL for a given language and the parameters given in the contructor.
     * @param language the language
     * @return the url for the given language
     * @throws ProcessingException if the document for the given language could not be created.
     */
    public String getUrl(String language) throws ProcessingException {
        Document doc = getDocument(language);
        return this.contextPath + doc.getCanonicalWebappURL();
    }

    /**
     * Create a document for a given language and the parameters given in the contructor.
     * @param language the language
     * @return the document with the given language
     * @throws ProcessingException if the document for the given language could not be created.
     */
    protected Document getDocument(String language) throws ProcessingException {
        Document document;
        try {
            DocumentLocator locator = getLocator();
            DocumentLocator version = locator.getLanguageVersion(language);
            document = this.factory.get(version);
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
        return document;
    }

    protected DocumentLocator getLocator() throws DocumentBuildException {
        DocumentLocator locator = this.pub.getDocumentBuilder().getLocator(this.factory, this.url);
        return locator;
    }
}