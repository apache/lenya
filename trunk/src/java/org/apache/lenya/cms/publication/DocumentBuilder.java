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
     * @throws DocumentBuildException if an error occurs.
     */
    DocumentLocator getLocator(DocumentFactory factory, String webappUrl) throws DocumentBuildException;

    /**
     * Checks if an URL corresponds to a CMS document.
     * @param factory The document factory.
     * @param url The URL of the form /{publication-id}/...
     * @return A boolean value.
     * @throws DocumentBuildException when something went wrong.
     */
    boolean isDocument(DocumentFactory factory, String url) throws DocumentBuildException;

    /**
     * Builds an URL corresponding to a CMS document.
     * @param factory The document factory.
     * @param locator The locator.
     * @return a String The corresponding URL.
     */
    String buildCanonicalUrl(DocumentFactory factory, DocumentLocator locator);

    /**
     * Checks if a document name is valid.
     * @param documentName The document name.
     * @return A boolean value.
     */
    boolean isValidDocumentName(String documentName);

}