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

/* $Id: DocumentBuilder.java,v 1.11 2004/03/01 16:18:17 gregor Exp $  */

package org.apache.lenya.cms.publication;


/**
 * A document builder builds a document from a URL.
 */
public interface DocumentBuilder {

    /**
     * Builds a document.
     * 
     * @param publication The publication the document belongs to.
     * @param url The URL of the form /{publication-id}/{area}/{document-id}{language-suffix}.{extension}.
     * @return A document.
     * @throws DocumentBuildException when something went wrong.
     */
    Document buildDocument(Publication publication, String url)
        throws DocumentBuildException;
    
    /**
     * Checks if an URL corresponds to a CMS document.
     * 
     * @param publication The publication the document belongs to.
     * @param url The URL of the form /{publication-id}/...
     * @return A boolean value.
     * @throws DocumentBuildException when something went wrong.
     */    
    boolean isDocument(Publication publication, String url)
        throws DocumentBuildException;
        
    /**
     * Builds an URL corresponding to a cms document from the publication, 
     * the area, the document id and the language
     * 
     * @param publication The publication the document belongs to.
     * @param area The area the document belongs to.
     * @param documentid The document id of the document.
     * @param language The language of the document.
     * @return a String The builded url
     */
    String buildCanonicalUrl(
        Publication publication,
        String area,
        String documentid,
        String language);

    /**
     * Builds an URL corresponding to a cms document from the publication, 
     * the area and the document id
     * 
     * @param publication The publication the document belongs to.
     * @param area The area the document belongs to.
     * @param documentid The document id of the document.
     * @return a String The builded url
     */
    String buildCanonicalUrl(
        Publication publication,
        String area,
        String documentid);
    
    /**
     * Builds a clone of a document for another language. 
     * @param document The document to clone.
     * @param language The language of the target document.
     * @return A document.
     */
    Document buildLanguageVersion(Document document, String language);
}
