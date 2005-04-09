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

package org.apache.lenya.cms.publication.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.transaction.TransactionException;

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
	 * @param document The document to add.
	 */
    public void add(Document document) {
        assert document != null;
        assert !this.documents.contains(document);
        this.documents.add(document);
    }

    /**
	 * Checks if this set is empty.
	 * @return A boolean value.
	 */
    public boolean isEmpty() {
        return getList().isEmpty();
    }
    
    /**
     * Visits the set.
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
     * @param resource The document.
     * @throws PublicationException if an error occurs.
     */
    public void remove(Document resource) throws PublicationException {
        assert resource != null;
        assert getList().contains(resource);
        getList().remove(resource);
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
    
    /**
     * Locks all contained documents.
     * @throws TransactionException if an error occurs.
     */
    public void lock() throws TransactionException  {
        Document[] docs = getDocuments();
        for (int i = 0; i < docs.length; i++) {
            docs[i].lock();
        }
    }

}
