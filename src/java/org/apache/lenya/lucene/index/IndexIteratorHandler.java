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

/* $Id: IndexIteratorHandler.java,v 1.6 2004/03/01 16:18:15 gregor Exp $  */

package org.apache.lenya.lucene.index;

import java.io.File;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

public interface IndexIteratorHandler {
    /**
     * Handles a file. This is called for every file and mainly used for creating a new index.
     */
    void handleFile(IndexReader reader, File file);

    /**
     * Handles a stale document.
     */
    void handleStaleDocument(IndexReader reader, Term term);

    /**
     * Handles an unmodified document and the file that represents it.
     */
    void handleUnmodifiedDocument(IndexReader reader, Term term, File file);

    /**
     * Handles a new document and the file that represents it.
     */
    void handleNewDocument(IndexReader reader, Term term, File file);
}
