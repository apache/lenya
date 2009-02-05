/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.components.search.components;

import org.apache.cocoon.components.search.IndexException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;

public interface Indexer {

    public static final String ROLE = Indexer.class.getName();

    /**
     * All lucene documents must have a unique identifier field
     */
    public static final String DOCUMENT_UID_FIELD = "uid";

    /**
     * Index document (update or add if {@link #clearIndex()} is called before)
     * @param doc
     *            Document
     * @throws IndexException
     */
    public void index(Document doc) throws IndexException;

    /**
     * Delete document
     * 
     * @param uid
     *            the uid of the document
     * @return int the number of deleted documents
     * @throws IndexException
     */
    public int del(String uid) throws IndexException;

    /**
     * Optimize the index
     */
    public void optimize() throws IndexException;

    /**
     * Set a lucene analyzer
     * 
     * @param analyzer
     *            the analazer
     */
    public void setAnalyzer(Analyzer analyzer);

    /**
     * Get the lucene analyzer
     */
    public Analyzer getAnalyzer();

    /**
     * Set a merge factor value + set minMergeDocs=2*mergeFactor (see lucene
     * docs)
     * 
     * @param value
     *            the new merge factor
     */
    public void setMergeFactor(int value);

    /**
     * 
     * @return the mergeFactor
     */
    public int getMergeFactor();

    /**
     * clear the index
     */
    public void clearIndex() throws IndexException;

    /**
     * Set the index directory
     * 
     * @param directory
     *            the index directory
     */
    public void setIndex(Directory directory) throws IndexException;

    public Directory getIndex() throws IndexException;
}
