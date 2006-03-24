/*
 * Copyright  1999-2005 The Apache Software Foundation
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

package org.apache.lenya.cms.publication;

import java.io.File;
import java.util.Date;

import org.apache.lenya.cms.metadata.MetaDataOwner;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.transaction.Identifiable;

/**
 * A CMS document.
 * @version $Id$
 */
public interface Document extends MetaDataOwner, Identifiable {
    
    /**
     * The document namespace URI.
     */
    String NAMESPACE = "http://apache.org/cocoon/lenya/document/1.0";
    
    /**
     * The default namespace prefix.
     */
    String DEFAULT_PREFIX = "lenya";
    
    /**
     * The transactionable type for document objects.
     */
    String TRANSACTIONABLE_TYPE = "document";
    
    /**
     * <code>DOCUMENT_META_SUFFIX</code> The suffix for document meta Uris
     */
    final String DOCUMENT_META_SUFFIX = ".meta";

    /**
     * Returns the document ID of this document.
     * @return the document-id of this document.
     */
    String getId();
    
    /**
     * Returns the document name of this document.
     * @return the document-name of this document.
     */
    String getName();
    
    /**
     * Returns the publication this document belongs to.
     * @return A publication object.
     */
    Publication getPublication();
    
    /**
     * Returns the canonical web application URL.
     * @return A string.
     */
    String getCanonicalWebappURL();

    /**
     * Returns the canonical document URL.
     * @return A string.
     */
    String getCanonicalDocumentURL();

    /**
     * Returns the language of this document.
     * Each document has one language associated to it. 
     * @return A string denoting the language.
     */
    String getLanguage();

    /**
     * Returns all the languages this document is available in.
     * A document has one associated language (@see Document#getLanguage)
     * but there are possibly a number of other languages for which a 
     * document with the same document-id is also available in. 
     * 
     * @return An array of strings denoting the languages.
     * 
     * @throws DocumentException if an error occurs
     */
    String[] getLanguages() throws DocumentException;

    /**
     * Get the navigation label associated with this document 
     * for the language.
     * 
     * @return the label String
     * 
     * @throws DocumentException if an error occurs
     */
    String getLabel() throws DocumentException;

    /**
     * Returns the date of the last modification of this document.
     * @return A date denoting the date of the last modification.
     */
    Date getLastModified();

    /**
     * Returns the area this document belongs to.
     * @return The area.
     */
    String getArea();

    /**
     * Returns the file for this document.
     * @return A file object.
     * @deprecated This implies the usage of a filesystem based storage. Use {@link #getSourceURI()} instead.
     */
    File getFile();

    /**
     * Returns the extension in the URL.
     * @return A string.
     */
    String getExtension();

    /**
     * Returns the UUID.
     * @return A string.
     */
    String getUUID() throws DocumentException;
    
    /**
     * Check if a document with the given document-id, language and in the given
     * area actually exists.
     * 
     * @return true if the document exists, false otherwise
     * 
     * @throws DocumentException if an error occurs
     */
    boolean exists() throws DocumentException;
    
    /**
     * Check if a document exists with the given document-id and the given area
     * independently of the given language.
     * 
     * @return true if a document with the given document-id and area exists, false otherwise
     * 
     * @throws DocumentException if an error occurs
     */
    boolean existsInAnyLanguage() throws DocumentException;
    
    /**
     * Returns the identity map this document belongs to.
     * @return A document identity map.
     */
    DocumentIdentityMap getIdentityMap();
    
    /**
     * Returns the URI to resolve the document's source.
     * @return A string.
     */
    String getSourceURI();
    
    /**
     * Accepts a document visitor.
     * @param visitor The visitor.
     * @throws PublicationException if an error occurs.
     */
    void accept(DocumentVisitor visitor) throws PublicationException;

    /**
     * Deletes the document.
     * @throws DocumentException if an error occurs.
     */
    void delete() throws DocumentException;
    
    /**
     * @return The repository node that represents this document.
     */
    Node getRepositoryNode();

    /**
     * @return The resource type of this document (formerly known as doctype)
     * @throws DocumentException if an error occurs.
     */
    ResourceType getResourceType() throws DocumentException;
    
    /**
     * @return The source extension used by this document.
     */
    String getSourceExtension();
}
