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

package org.apache.lenya.cms.publication.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PublicationException;

/**
 * An ordered set of documents without duplicates.
 */
public interface DocumentSet {

    
    /**
     * Returns the documents contained in this set.
     * 
     * @return An array of documents.
     */
    public Document[] getDocuments() ;

    /**
     * Adds a document to this set.
     * 
     * @param document The document to add.
     * @throws IllegalArgumentException if the document is <code>null</code> or already contained.
     */
    public void add(Document document) ;
    
    /**
     * Adds a document set to this set.
     * 
     * @param set The documents to add.
     */
    public void addAll(DocumentSet set) ;
    
    /**
     * @param document The document.
     * @return if the document is contained.
     */
    public boolean contains(Document document);

    /**
     * Checks if this set is empty.
     * 
     * @return A boolean value.
     */
    public boolean isEmpty() ;

    /**
     * Visits the set.
     * 
     * @param visitor The visitor.
     * @throws Exception if an error occurs during visiting.
     */
    public void visit(DocumentVisitor visitor) throws Exception ;
    
    /**
     * Removes a document.
     * 
     * @param resource The document.
     * @throws PublicationException if an error occurs.
     */
    public void remove(Document resource) throws PublicationException ;
    
    /**
     * Removes all documents in a set from this set.
     * @param set The set.
     * @throws PublicationException if an error occurs.
     */
    public void removeAll(DocumentSet set) throws PublicationException ;
    
    /**
     * Removes all documents.
     */
    public void clear() ;
    
    /**
     * Reverses the document order.
     */
    public void reverse() ;

}
