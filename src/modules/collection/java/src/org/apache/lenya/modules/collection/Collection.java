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

package org.apache.lenya.modules.collection;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;

/**
 * A document representing a collection of documents.
 * This class is in prototyping stage.
 */
public interface Collection {

    /** collection namespace */
    String NAMESPACE = "http://apache.org/cocoon/lenya/collection/1.0";
    
    /** default namespace prefix */
    String DEFAULT_PREFIX = "col";
    
    /** document element */
    String ELEMENT_COLLECTION = "collection";
    
    /** element for single document references */
    String ELEMENT_DOCUMENT = "document";
    
    /** attribute for document IDs */
    String ATTRIBUTE_UUID = "uuid";

    /**
     * Name of the type attribute.
     */
    String ATTRIBUTE_TYPE = "type";

    /**
     * Name of the href attribute.
     */
    String ATTRIBUTE_HREF = "href";


    /**
     * Returns the documents in this collection.
     * @return An array of documents.
     * @throws DocumentException when something went wrong.
     */
    Document[] getDocuments() throws DocumentException;

    /**
     * Adds a document to the collection.
     * @param document A document.
     * @throws DocumentException when an error occurs.
     */
    void add(Document document) throws DocumentException;

    /**
     * Inserts a document into the collection at a specific position.
     * @param document A document.
     * @param position The position of the document after insertion,
     * starting with 0.
     * @throws DocumentException when something went wrong.
     */
    void add(int position, Document document) throws DocumentException;

    /**
     * Removes a document from the collection.
     * @param document A document.
     * @throws DocumentException when the document is not contained
     * or another error occurs.
     */
    void remove(Document document) throws DocumentException;
    
    /**
     * Removes all documents from this collection.
     * @throws DocumentException when something went wrong.
     */
    void clear() throws DocumentException;
    
    /**
     * Checks if this collection contains a specific document.
     * @param document The document to check.
     * @return A boolean value.
     * @throws DocumentException when something went wrong.
     */
    boolean contains(Document document) throws DocumentException;
    
    /**
     * Returns the first position of this document in the collection.
     * @param document The document.
     * @return An integer.
     * @throws DocumentException when the document is not contained.
     */
    int getFirstPosition(Document document) throws DocumentException;
    
    /**
     * Returns the number of documents in this collection.
     * @return An integer value.
     * @throws DocumentException when something went wrong.
     */
    int size() throws DocumentException;
    
    /**
     * @return The document which the collection is stored in.
     */
    Document getDelegate();
    
    /**
     * Type for automatic inclusion of child documents.
     */
    String TYPE_CHILDREN = "children";
    
    /**
     * Type for manual addition of documents.
     */
    String TYPE_MANUAL = "manual";

    /**
     * Type for manual addition of documents.
     */
    String TYPE_LINK = "link";

    /**
     * @param type One of {@link #TYPE_CHILDREN}, {@link #TYPE_MANUAL}, {@link #TYPE_LINK}.
     */
    void setType(String type);

    /**
     * @return One of {@link #TYPE_CHILDREN}, {@link #TYPE_MANUAL}, {@link #TYPE_LINK}.
     */
    String getType();
    
    /**
     * @return The link target.
     */
    String getHref();
    
    /**
     * @param href The link target. If the type is not {@link #TYPE_LINK}, calling
     * this method has no effect.
     */
    void setHref(String href);
}
