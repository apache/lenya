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

/* $Id: DocumentSet.java,v 1.5 2004/03/01 16:18:17 gregor Exp $  */

package org.apache.lenya.cms.publication;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of documents.
 */
public class DocumentSet {

    /**
	 * Ctor.
	 */
    public DocumentSet() {
    }
    
    /**
     * Ctor.
     * @param documents The initial documents.
     */
    public DocumentSet(Document[] documents) {
        for (int i = 0; i < documents.length; i++) {
            add(documents[i]);
        }
    }

    private List documents = new ArrayList();

    /**
	 * Returns the documents contained in this set.
	 * 
	 * @return An array of documents.
	 */
    public Document[] getDocuments() {
        return (Document[]) documents.toArray(new Document[documents.size()]);
    }

    /**
	 * Adds a document to this set.
	 * 
	 * @param document The document to add.
	 */
    public void add(Document document) {
        assert document != null;
        assert !documents.contains(document);
        documents.add(document);
    }

    /**
	 * Checks if this set is empty.
	 * 
	 * @return A boolean value.
	 */
    public boolean isEmpty() {
        return documents.isEmpty();
    }
    
}
