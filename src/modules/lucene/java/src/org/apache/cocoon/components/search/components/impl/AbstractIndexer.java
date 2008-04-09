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
package org.apache.cocoon.components.search.components.impl;

import java.io.IOException;

import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.cocoon.components.search.IndexException;
import org.apache.cocoon.components.search.components.Indexer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;

/**
 * Abstract Indexer
 * 
 * @author Nicolas Maisonneuve
 */
public abstract class AbstractIndexer extends AbstractLogEnabled implements
        Indexer, Recyclable {

    /**
     * the lucene Analyzer (see lucene doc)
     */
    protected Analyzer analyzer;

    /**
     * lucene Directory (see lucene doc)
     */
    protected Directory dir;

    /**
     * MergeFactor (see lucene doc)
     */
    protected int mergeFactor;

    /**
     * clear mode (if true the index will be cleared)
     */
    protected boolean clear_mode;

    // runtime variables: lucene indexwriter and indexreader
    protected IndexReader delete_reader;

    protected IndexWriter add_writer;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.Indexer#setMergeFactor(int)
     */
    public void setMergeFactor(int value) {
        mergeFactor = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.Indexer#getMergeFactor()
     */
    public int getMergeFactor() {
        return mergeFactor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.Indexer#getIndex()
     */
    public Directory getIndex() {
        return this.dir;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.Indexer#setIndex(org.apache.lucene.store.Directory)
     */
    public void setIndex(Directory dir) throws IndexException {
        if (dir == null) {
            throw new IllegalArgumentException("set a null directory");
        }
        this.dir = dir;
        clear_mode = false;
        try {
            IndexReader reader = IndexReader.open(dir);
            reader.close();

        } catch (IOException ioe) {
            // couldn't open the index - so recreate it
            if (getLogger().isWarnEnabled()) {
                getLogger().warn("couldn't open the index - so recreate it");
            }
            this.clearIndex();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.Indexer#setAnalyzer(org.apache.lucene.analysis.Analyzer)
     */
    public void setAnalyzer(Analyzer analyzer) {
        if (analyzer == null) {
            throw new IllegalArgumentException("set a null analyzer");
        }
        this.analyzer = analyzer;

        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug(
                    "set the analyzer " + this.analyzer.getClass().getName());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.Indexer#getAnalyzer()
     */
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    protected abstract void updateDocument(Document doc) throws IndexException;

    protected abstract void addDocument(Document doc) throws IndexException;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.Indexer#index(org.apache.lucene.document.Document)
     */
    public void index(Document doc) throws IndexException {
        if (this.clear_mode) {
            // As we know the index is empty , we just add the document
            addDocument(doc);
        } else {
            updateDocument(doc);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.Indexer#del(java.lang.String)
     */
    public int del(String uid) throws IndexException {
        switchToDEL_MODE();
        return deleteDocument(delete_reader, uid);
    }

    /**
     * Delete document
     * 
     * @param deleter
     *            the lucene indexreader to delete document
     * @param uid
     *            the uid of the doucment to be deleted
     * @return the number of deleted documents
     * @throws IndexException
     */
    final protected int deleteDocument(IndexReader deleter, String uid)
            throws IndexException {
        int r = 0;
        try {
            r = deleter.delete(new Term(DOCUMENT_UID_FIELD, uid));
        } catch (IOException ex) {
            handleError("delete document (uid:" + uid + ") error", ex);
        }
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("document deleted (uid:" + uid + ")");
        }
        return r;
    }

    /**
     * add document to the index
     * 
     * @param writer
     *            the lucene indexwriter
     * @param document
     *            the document to be indexed
     * @throws IndexException
     */
    final protected void addDocument(IndexWriter writer, Document document)
            throws IndexException {
        try {
            writer.addDocument(document, analyzer);
        } catch (IOException ex) {
            handleError("add document  (uid:"
                    + document.get(DOCUMENT_UID_FIELD) + ") error", ex);
        }
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug(
                    "document added (uid:" + document.get(DOCUMENT_UID_FIELD)
                            + ")");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.Indexer#optimize()
     */
    public void optimize() throws IndexException {
        // optimize index
        //try {
            this.switchToADD_MODE(false);
            //add_writer.optimize();
        //} catch (IOException ex) {
        //    throw new IndexException("optimization error", ex);
        //}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.Indexer#clearIndex()
     */
    public void clearIndex() throws IndexException {
        this.clear_mode = true;
        this.switchToADD_MODE(true);
    }

    /**
     * releasing resources
     * 
     * @throws IndexException
     */
    protected void release() throws IndexException {
        this.closeWriter();
        this.closeReader();
        // set default value
        dir = null;
        analyzer = null;
        mergeFactor = 10;
    }

    /**
     * recylcle this object
     */
    public void recycle() {
        try {
            release();
        } catch (IndexException ex) {
            this.getLogger().error("recycle error", ex);
        }
    }

    /**
     * Switch to write mode (close read, open writer ) if it's not already done
     * 
     * @param clear
     *            clear index
     * @throws IndexException
     */
    final protected void switchToADD_MODE(boolean clear) throws IndexException {
        if (add_writer == null) {
            closeReader();
            openIndexWriter(clear);
        }
    }

    /**
     * Switch to del mode (close writer, open reader ) if it's not already done
     * 
     * @throws IndexException
     */
    final protected void switchToDEL_MODE() throws IndexException {
        if (delete_reader == null) {
            closeWriter();
            openIndexReader();
        }
    }

    /**
     * Open the index Writer
     * 
     * @param create
     *            clear index or not
     * @throws IndexException
     */
    final protected void openIndexWriter(boolean create) throws IndexException {

        // now open writer
        try {
            add_writer = new IndexWriter(dir, analyzer, create);
            // add_writer.setUseCompoundFile(true);
        } catch (IOException e) {
            throw new IndexException("open writer error", e);
        }

        if (mergeFactor > add_writer.mergeFactor) {
            add_writer.minMergeDocs = mergeFactor * 2;
            add_writer.mergeFactor = mergeFactor;
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("writer is opened");
        }
    }

    /**
     * Open Index Reader
     * 
     * @throws IndexException
     */
    final protected void openIndexReader() throws IndexException {
        try {
	    if (this.dir == null) {
	        throw new IndexException(new IllegalStateException("Index directory not set."));
	    }
            this.delete_reader = IndexReader.open(dir);
        } catch (IOException e) {
            throw new IndexException("open reader error", e);
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("reader is opened");
        }

    }

    /**
     * Close writer
     * 
     * @throws IndexException
     */
    final protected void closeWriter() throws IndexException {
        if (add_writer != null) {
            try {
                add_writer.close();
            } catch (IOException ex) {
                throw new IndexException("close writer error", ex);
            } finally {
                add_writer = null;
            }
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("writer is closed");
            }
        }
    }

    /**
     * Close reader
     * 
     * @throws IndexException
     */
    final protected void closeReader() throws IndexException {
        if (this.delete_reader != null) {
            try {
                delete_reader.close();
            } catch (IOException ex) {
                handleError("close reader error", ex);
            } finally {
                delete_reader = null;
            }
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("reader is closed");
            }
        }
    }

    /**
     * Handle error (close writer, reader,etc.. )
     * 
     * @param message
     * @param exception
     * @throws IndexException
     */
    private void handleError(String message, Exception exception)
            throws IndexException {
        try {
            release();
        } catch (IndexException e) {
        }
        throw new IndexException(message, exception);
    }

}
