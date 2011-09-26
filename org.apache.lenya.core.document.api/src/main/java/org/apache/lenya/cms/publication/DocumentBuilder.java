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

import java.net.MalformedURLException;
//import org.apache.lenya.cms.publication.Session;
//import from core-document-api
import org.apache.lenya.cms.publication.DocumentLocator;
/**
 * A document builder builds a document from a URL.
 */
public interface DocumentBuilder {

    /**
     * The Avalon role.
     */
    String ROLE = DocumentBuilder.class.getName();
    
    /**
     * Returns a document for a web application URL.
     * @param factory The factory.
     * @param webappUrl The web application URL.
     * @return A document identifier.
     * @throws MalformedURLException if the URL is not a webapp URL. 
     */
    //florent DocumentLocator getLocator(Session session, String webappUrl) throws MalformedURLException;
    DocumentLocator getLocator(String webappUrl) throws MalformedURLException;

    /**
     * Checks if an URL corresponds to a CMS document.
     * @param factory The document factory.
     * @param url The URL of the form /{publication-id}/...
     * @return A boolean value.
     * @throws DocumentBuildException when something went wrong.
     */
    //boolean isDocument(Session session, String url);
    boolean isDocument(String url);

    /**
     * Builds an URL corresponding to a CMS document.
     * @param factory The document factory.
     * @param locator The locator.
     * @return a String The corresponding URL.
     */
    //String buildCanonicalUrl(Session session, DocumentLocator locator);
    String buildCanonicalUrl(DocumentLocator locator);

    /**
     * Checks if a document name is valid.
     * @param documentName The document name.
     * @return A boolean value.
     */
    boolean isValidDocumentName(String documentName);

}