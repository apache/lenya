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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.lenya.cms.metadata.MetaDataOwner;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
//florent : removed import as they create cyclic dependencies
/*import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.publication.Node;*/
//florent remove to change way of dependency : it's now repository that depend to document
//import org.apache.lenya.cms.repository.Node;
//import org.apache.lenya.cms.repository.History;
//import org.apache.lenya.cms.repository.RepositoryException;

//TODO : this include a dependency to cocoon-pipeline-api... see how to remove it as it a lot for a less...
import org.apache.cocoon.ResourceNotFoundException;

/**
 * A CMS document.
 */
//florent : not still repository dependent
//public interface Document extends Node, MetaDataOwner {
public interface Document extends MetaDataOwner {
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
     * The meta data namespace.
     */
    public static final String METADATA_NAMESPACE = "http://apache.org/lenya/metadata/document/1.0";
    
    /**
     * The name of the resource type attribute. A resource has a resource type; this information can
     * be used e.g. for different rendering of different types.
     */
    public static final String METADATA_RESOURCE_TYPE = "resourceType";

    /**
     * The name of the mime type attribute.
     */
    public static final String METADATA_MIME_TYPE = "mimeType";

    /**
     * The name of the content type attribute. Any content managed by Lenya has a type; this
     * information can be used e.g. to provide an appropriate management interface.
     */
    public static final String METADATA_CONTENT_TYPE = "contentType";

    /**
     * The number of seconds from the request that a document can be cached before it expires
     */
    public static final String METADATA_EXPIRES = "expires";

    /**
     * The extension to use for the document source.
     */
    public static final String METADATA_EXTENSION = "extension";

    /**
     * <code>DOCUMENT_META_SUFFIX</code> The suffix for document meta Uris
     */
    final String DOCUMENT_META_SUFFIX = ".meta";
    
    /**
     * Returns the date at which point the requested document is considered expired
     * @return a string in RFC 1123 date format
     * @throws DocumentException if an error occurs.
     */
    Date getExpires() throws DocumentException;

    /**
     * Returns the document name of this document.
     * @return the document-name of this document.
     */
    String getName();
    
    /**
     * Returns the publication this document belongs to.
     * @return A publication object.
     */
  //florent : seems never use, imply cyclic dependencies
    /*
    Publication getPublication();
    */
    
    /**
     * returns the publication id that hold the documents
     */
    public String getPublicationId();
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
     * document with the same document-uuid is also available in. 
     * 
     * @return An array of strings denoting the languages.
     */
    String[] getLanguages();

    /**
     * Returns the date of the last modification of this document.
     * @return A date denoting the date of the last modification.
     * @throws DocumentException if an error occurs.
     */
    long getLastModified() throws DocumentException;

    /**
     * Returns the area this document belongs to.
     * @return The area.
     */
    String getArea();

    /**
     * Returns the extension in the URL without the dot.
     * @return A string.
     */
    String getExtension();

    /**
     * Returns the UUID.
     * @return A string.
     */
    String getUUID();
    
    /**
     * Check if a document with the given document-uuid, language and in the given
     * area actually exists.
     * 
     * @return true if the document exists, false otherwise
     */
    boolean exists() throws DocumentException;
    
    /**
     * Check if a document exists with the given document-uuid and the given area
     * independently of the given language.
     * 
     * @return true if a document with the given document-uuid and area exists, false otherwise
     */
    boolean existsInAnyLanguage();
    
    /**
     * Returns the URI to resolve the document's source.
     * The source can only be used for read-only access.
     * For write access, use {@link #getOutputStream()}.
     * @return A string.
     */
    String getSourceURI();
    
    /**
     * @return The output stream to write the document content to.
     */
    OutputStream getOutputStream();
    
    /**
     * Accepts a document visitor.
     * @param visitor The visitor.
     * @throws Exception if an error occurs.
     */
    void accept(DocumentVisitor visitor) throws Exception;

    /**
     * Deletes the document.
     * @throws DocumentException if an error occurs.
     */
    void delete() throws DocumentException;
    
    /**
     * @return The resource type of this document (formerly known as doctype)
     * @throws DocumentException if the resource type has not been set.
     */
    ResourceType getResourceType() throws DocumentException;
    
    /**
     * @param resourceType The resource type of this document.
     */
    void setResourceType(ResourceType resourceType);
    
    /**
     * @return The source extension used by this document, without the dot.
     */
    String getSourceExtension();
    
    /**
     * @param extension The source extension used by this document, without the dot.
     */
    void setSourceExtension(String extension);
    
    /**
     * Sets the mime type of this document.
     * @param mimeType The mime type.
     */
    void setMimeType(String mimeType);
    
    /**
     * @return The mime type of this document.
     * @throws DocumentException if the mime type has not been set.
     */
    String getMimeType() throws DocumentException;
    
    /**
     * @return The content length of the document.
     */
    long getContentLength();
    
    /**
     * @return The document identifier for this document.
     */
    DocumentIdentifier getIdentifier();
    
    /**
     * This is a shortcut to getLink().getNode().getPath().
     * @return The path of this document in the site structure.
     * @throws DocumentException if the document is not linked in the site structure.
     */
    String getPath() throws DocumentException;

    /**
     * Checks if a certain translation (language version) of this document exists.
     * @param language The language.
     * @return A boolean value.
     */
    boolean existsTranslation(String language);
    
    /**
     * Returns a certain translation (language version) of this document.
     * @param language The language.
     * @return A document.
     * @throws ResourceNotFoundException if the language version doesn't exist.
     */
    Document getTranslation(String language) throws ResourceNotFoundException;
    
    /**
     * Checks if this document exists in a certain area.
     * @param area The area.
     * @return A boolean value.
     */
    boolean existsAreaVersion(String area);
    
    /**
     * Returns the document in a certain area.
     * @param area The area.
     * @return A document.
     * @throws ResourceNotFoundException if the area version doesn't exist.
     */
    Document getAreaVersion(String area) throws ResourceNotFoundException;

    /**
     * Checks if a translation of this document exists in a certain area.
     * @param area The area.
     * @param language The language.
     * @return A boolean value.
     */
    boolean existsVersion(String area, String language);
    
    /**
     * Returns a translation of this document in a certain area.
     * @param area The area.
     * @param language The language.
     * @return A document.
     * @throws ResourceNotFoundException if the area version doesn't exist.
     */
    Document getVersion(String area, String language) throws ResourceNotFoundException;
    
    /**
     * @return A document locator.
     */
    DocumentLocator getLocator();
    
    /**
     * @return The link to this document in the site structure.
     * @throws DocumentException if the document is not referenced in the site structure.
     */
  //florent : imply cyclic dependencies with publication
    /*
    Link getLink() throws DocumentException;
    */
    /**
     * @return The area the document belongs to.
     */
    //florent : imply cyclic dependencies with publication
    /*
    Area area();
*/
    /**
     * @return if the document is linked in the site structure.
     */
    boolean hasLink();

    /**
     * @return The input stream to obtain the document's content.
     */
    InputStream getInputStream();
    
    /**
     * @param i The revision number.
     * @return A revision.
     * @throws RepositoryException if the revision doesn't exist.
     */
    //florent Document getRevision(int i) throws RepositoryException;
    Document getRevision(int i) throws DocumentException;

    /**
     * @return The revision number of this document.
     */
    int getRevisionNumber();
    
    //History getHistory();

}
