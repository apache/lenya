/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
     * Returns a document identifier for a web application URL.
     * @param webappUrl The web application URL.
     * @return A document identifier.
     * @throws DocumentBuildException if an error occurs.
     */
    DocumentIdentifier getIdentitfier(String webappUrl) throws DocumentBuildException;

    /**
     * Builds a document.
     * @param map The identity map the document belongs to.
     * @param identifier The document identifier.
     * @return A document.
     * @throws DocumentBuildException when something went wrong.
     */
    Document buildDocument(DocumentIdentityMap map, DocumentIdentifier identifier)
            throws DocumentBuildException;

    /**
     * Checks if an URL corresponds to a CMS document.
     * @param url The URL of the form /{publication-id}/...
     * @return A boolean value.
     * @throws DocumentBuildException when something went wrong.
     */
    boolean isDocument(String url) throws DocumentBuildException;

    /**
     * Builds an URL corresponding to a CMS document.
     * @param identifier The document identifier.
     * @return a String The corresponding URL.
     */
    String buildCanonicalUrl(DocumentIdentifier identifier);

    /**
     * Checks if a document name is valid.
     * @param documentName The document name.
     * @return A boolean value.
     */
    boolean isValidDocumentName(String documentName);

}