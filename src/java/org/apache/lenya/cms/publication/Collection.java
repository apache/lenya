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

/* $Id: Collection.java,v 1.6 2004/03/04 14:59:03 edith Exp $  */

package org.apache.lenya.cms.publication;

/**
 * A document representing a collection of documents.
 * This class is in prototyping stage.
 */
public interface Collection extends Document {

    String NAMESPACE = "http://apache.org/cocoon/lenya/collection/1.0";
    String DEFAULT_PREFIX = "col";
    
    String ELEMENT_COLLECTION = "collection";
    String ELEMENT_DOCUMENT = "document";
    String ATTRIBUTE_ID = "id";

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
     * Saves the XML source of this collection.
     * @throws DocumentException when something went wrong.
     */
    void save() throws DocumentException;
}
