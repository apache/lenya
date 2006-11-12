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
import java.util.Collections;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PublicationException;

/**
 * An ordered set of documents without duplicates.
 */
public class DocumentSet {

    /**
     * Ctor.
     */
    public DocumentSet() {
        // do nothing
    }

    /**
     * Ctor.
     * 
     * @param _documents The initial documents.
     */
    public DocumentSet(Document[] _documents) {
        for (int i = 0; i < _documents.length; i++) {
            add(_documents[i]);
        }
    }

    private List documents = new ArrayList();

    /**
     * Returns the list object that stores the documents.
     * 
     * @return A list.
     */
    protected List getList() {
        return this.documents;
    }

    /**
     * Returns the documents contained in this set.
     * 
     * @return An array of documents.
     */
    public Document[] getDocuments() {
        return (Document[]) this.documents.toArray(new Document[this.documents.size()]);
    }

    /**
     * Adds a document to this set.
     * 
     * @param document The document to add.
     * @throws IllegalArgumentException if the document is <code>null</code> or already contained.
     */
    public void add(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("The document is null!");
        }
        if (this.documents.contains(document)) {
            throw new IllegalArgumentException("The document [" + document
                    + "] is already contained!");
        }
        this.documents.add(document);
    }

    /**
     * Adds a document set to this set.
     * 
     * @param set The documents to add.
     */
    public void addAll(DocumentSet set) {
        assert set != null;
        Document[] documents = set.getDocuments();
        for (int i = 0; i < documents.length; i++) {
            if (!contains(documents[i])) {
                add(documents[i]);
            }
        }
    }

    /**
     * @param document The document.
     * @return if the document is contained.
     */
    public boolean contains(Document document) {
        return getList().contains(document);
    }

    /**
     * Checks if this set is empty.
     * 
     * @return A boolean value.
     */
    public boolean isEmpty() {
        return getList().isEmpty();
    }

    /**
     * Visits the set.
     * 
     * @param visitor The visitor.
     * @throws PublicationException if an error occurs during visiting.
     */
    public void visit(DocumentVisitor visitor) throws PublicationException {
        Document[] resources = getDocuments();
        for (int i = 0; i < resources.length; i++) {
            resources[i].accept(visitor);
        }
    }

    /**
     * Removes a document.
     * 
     * @param resource The document.
     * @throws PublicationException if an error occurs.
     */
    public void remove(Document resource) throws PublicationException {
        if (resource == null) {
            throw new IllegalArgumentException("The resource is null!");
        }
        if (!getList().contains(resource)) {
            throw new IllegalArgumentException("The resource [" + resource + "] is not contained!");
        }
        getList().remove(resource);
    }
    
    /**
     * Removes all documents in a set from this set.
     * @param set The set.
     * @throws PublicationException if an error occurs.
     */
    public void removeAll(DocumentSet set) throws PublicationException {
        Document[] documents = set.getDocuments();
        for (int i = 0; i < documents.length; i++) {
            remove(documents[i]);
        }
    }

    /**
     * Removes all documents.
     */
    public void clear() {
        getList().clear();
    }

    /**
     * Reverses the document order.
     */
    public void reverse() {
        Collections.reverse(getList());
    }

}
