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

package org.apache.lenya.lucene.index;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.lenya.lucene.IndexConfiguration;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import org.w3c.dom.Element;

/**
 * Abstract base class for indexers.
 * The factory method {@link #getDocumentCreator} is used to create a
 * DocumentCreator from the command-line arguments.
 */
public abstract class AbstractIndexer implements Indexer {
    static Logger log = Logger.getLogger(AbstractIndexer.class); 
    
    private DocumentCreator documentCreator;
    private Element indexer;
    private String configFileName;

    /**
     * Creates a new instance of AbstractIndexer
     */
    public AbstractIndexer() {
        // do nothing
    }

    /**
     * Returns the DocumentCreator of this indexer.
     * @return The document creator
     */
    protected DocumentCreator getDocumentCreator() {
        return this.documentCreator;
    }

    /**
     * Initializes this indexer with command-line parameters.
     * @param _indexer The indexer
     * @param _configFileName The config file name
     * @throws IOException
     */
    public void configure(Element _indexer, String _configFileName) throws IOException {
        this.documentCreator = createDocumentCreator(_indexer, _configFileName);
        this.indexer = _indexer;
        this.configFileName = _configFileName;
    }

    /**
     * Creates the document creator
     * @param _indexer The indexer
     * @param _configFileName The config file name
     * @return The document creator
     * @throws IOException if an error occurs
     */
    public abstract DocumentCreator createDocumentCreator(Element _indexer, String _configFileName) throws IOException;

    /**
     * Updates the index incrementally.
     * Walk directory hierarchy in uid order, while keeping uid iterator from
     * existing index in sync.  Mismatches indicate one of:
     * <ol>
     *   <li>old documents to be deleted</li>
     *   <li>unchanged documents, to be left alone, or</li>
     *   <li>new documents, to be indexed.</li>
     * </ol>
     * @param dumpDirectory
     * @param index
     * @throws IOException
     */
    public void updateIndex(File dumpDirectory, File index) throws IOException {
        deleteStaleDocuments(dumpDirectory, index);
        doIndex(dumpDirectory, index, false);
    }

    /**
     * Updates the index for the document specified
     * <ol>
     *   <li>old documents to be deleted</li>
     *   <li>unchanged documents, to be left alone, or</li>
     *   <li>new documents, to be indexed.</li>
     * </ol>
     * @param file The document
     * @throws IOException if an error occurs
     */
    public void indexDocument(File file) throws IOException {
        IndexConfiguration config = new IndexConfiguration(this.configFileName);
        log.debug("File: " + file);

        File dumpDir = new File(config.resolvePath(config.getHTDocsDumpDir()));
        log.debug("Dump dir: " + dumpDir);

        File indexDir = new File(config.resolvePath(config.getIndexDir()));
        log.debug("Index dir: " + indexDir);


	String id = IndexIterator.createID(file, dumpDir);

	boolean createNewIndex = false;
        if (!IndexReader.indexExists(indexDir)) {
            log.warn("Index does not exist yet: " + indexDir);
            createNewIndex = true;
        } else {
	    // Delete from index
            IndexReader reader = IndexReader.open(indexDir.getAbsolutePath());
	    Term term = new Term("id", id);
            log.debug(term.toString());
            int numberOfDeletedDocuments = reader.delete(term);
            if (numberOfDeletedDocuments == 1) {
                log.info("Document has been deleted: " + term);
            } else {
                log.warn("No such document found in this index: " + term);
            }
            //log.debug("Number of deleted documents: " + numberOfDeletedDocuments);
            //log.debug("Current number of documents in this index: " + reader.numDocs());
            reader.close();
        }

	// Append to index
        Document doc = getDocumentCreator().getDocument(new File(dumpDir, id), dumpDir);
        IndexWriter writer = new IndexWriter(indexDir, new StandardAnalyzer(), createNewIndex);
        writer.maxFieldLength = 1000000;
        writer.addDocument(doc);
        //log.debug("Document has been added: " + doc);
        log.info("Document has been added: " + id);
        writer.optimize();
        writer.close();
    }

    /**
     * Creates a new index.
     * @param dumpDirectory The dump directory to use
     * @param index The index
     * @throws IOException if an error occurs
     */
    public void createIndex(File dumpDirectory, File index)
        throws IOException {
        doIndex(dumpDirectory, index, true);
    }

    /**
     * Index files
     * @param dumpDirectory Directory where the files to be indexed are located
     * @param index Directory where the index shall be located
     * @param create <strong>true</strong> means the index will be created from scratch, <strong>false</strong> means it will be indexed incrementally
     * @throws IOException if an error occurs
     */
    public void doIndex(File dumpDirectory, File index, boolean create) throws IOException {
        if (!index.isDirectory()) {
            index.mkdirs();
            log.warn("Directory has been created: " + index.getAbsolutePath());
        }
            IndexWriter writer = new IndexWriter(index.getAbsolutePath(), new StandardAnalyzer(), create);
            writer.maxFieldLength = 1000000;

            IndexInformation info = new IndexInformation(index.getAbsolutePath(), dumpDirectory, getFilter(this.indexer, this.configFileName), create);

            IndexHandler handler;

            if (create) {
                handler = new CreateIndexHandler(dumpDirectory, info, writer);
            } else {
                handler = new UpdateIndexHandler(dumpDirectory, info, writer);
            }

            IndexIterator iterator = new IndexIterator(index.getAbsolutePath(), getFilter(this.indexer, this.configFileName));
            iterator.addHandler(handler);
            iterator.iterate(dumpDirectory);

            writer.optimize();
            writer.close();
    }

    /**
     * Delete stale documents.
     * @param _dumpDirectory The dump directory to use
     * @param _index The index
     */
    protected void deleteStaleDocuments(File _dumpDirectory, File _index) {
        log.debug("Deleting stale documents");

        IndexIterator iterator = new IndexIterator(_index.getAbsolutePath(), getFilter(this.indexer, this.configFileName));
        iterator.addHandler(new DeleteHandler());
        iterator.iterate(_dumpDirectory);
        log.debug("Deleting stale documents finished");
    }

    /**
     * Returns the filter used to receive the indexable files. May be overwritten by inherited class.
     * @param _indexer The indexer
     * @param _configFileName The name of the configuration file
     * @return The filter
     */
    public FileFilter getFilter(Element _indexer, String _configFileName) {
        String[] indexableExtensions = { "html", "htm", "txt" };
        return new AbstractIndexer.DefaultIndexFilter(indexableExtensions);
    }

    /**
     * FileFilter used to obtain the files to index.
     */
    public class DefaultIndexFilter implements FileFilter {
        protected String[] indexableExtensions;

        /**
         * Default indexable extensions: html, htm, txt
         */
        public DefaultIndexFilter() {
            String[] iE = { "html", "htm", "txt" };
            this.indexableExtensions = iE;
        }

        /**
         * Constructor
         * @param _indexableExtensions Array of extensions
         *
         */
        public DefaultIndexFilter(String[] _indexableExtensions) {
            this.indexableExtensions = _indexableExtensions;
        }

        /** Tests whether or not the specified file should be
         * included in a pathname list.
         * @param file The file to be tested
         *
         * @return  <code>true</code> if and only if <code>pathname</code> should be included
         *
         */
        public boolean accept(File file) {
            boolean accept;

            if (file.isDirectory()) {
                accept = true;
            } else {
                String fileName = file.getName();
                String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
                accept = Arrays.asList(this.indexableExtensions).contains(extension);
            }

            return accept;
        }
    }

    /**
     * Deletes all stale documents up to the document representing the next file.
     * The following documents are deleted:
     * <ul>
     *   <li>representing files that where removed</li>
     *   <li>representing the same file but are older than the current file</li>
     * </ul>
     */
    public class DeleteHandler extends AbstractIndexIteratorHandler {
        /** Handles a stale document.
         * @param _reader The reader
         * @param _term The term
         *
         */
        public void handleStaleDocument(IndexReader _reader, Term _term) {
            log.debug("deleting " +
                IndexIterator.uid2url(_term.text()));

            try {
                int deletedDocuments = _reader.delete(_term);
                log.debug("deleted " + deletedDocuments +
                    " documents.");
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    /**
     * The index handler
     */
    public class IndexHandler extends AbstractIndexIteratorHandler {
        /**
         * Creates a new IndexHandler object.
         *
         * @param _dumpDirectory The dump directory
         * @param _info The index information
         * @param _writer The index writer
         */
        public IndexHandler(File _dumpDirectory, IndexInformation _info, IndexWriter _writer) {
            this.info = _info;
            this.dumpDirectory = _dumpDirectory;
            this.writer = _writer;
        }

        private IndexInformation info;

        protected IndexInformation getInformation() {
            return this.info;
        }

        private File dumpDirectory;

        protected File getDumpDirectory() {
            return this.dumpDirectory;
        }

        private IndexWriter writer;

        protected IndexWriter getWriter() {
            return this.writer;
        }

        /**
         * Add document to index
         * @param file The file to add
         */
        protected void addFile(File file) {
            log.debug("adding document: " + file.getAbsolutePath());

            try {
                Document doc = getDocumentCreator().getDocument(file, this.dumpDirectory);
                this.writer.addDocument(doc);
            } catch (Exception e) {
                log.error(e);
            }

            this.info.increase();
            log.info(this.info.printProgress());
        }
    }

    /**
     * The factory for the index handler
     */
    public class CreateIndexHandler extends IndexHandler {
        /**
         * Creates a new CreateIndexHandler object.
         *
         * @param dumpDirectory The dump directory to use
         * @param info The index information
         * @param writer The index writer
         */
        public CreateIndexHandler(File dumpDirectory, IndexInformation info, IndexWriter writer) {
            super(dumpDirectory, info, writer);
        }

        /**
         * Handles a file. Used when creating a new index.
         * @param reader The reader
         * @param file The file
         */
        public void handleFile(IndexReader reader, File file) {
            addFile(file);
        }
    }

    /**
     * Class to update the index
     */
    public class UpdateIndexHandler extends IndexHandler {
        /**
         * Creates a new UpdateIndexHandler object.
         *
         * @param dumpDirectory The dump directory to use
         * @param info The index information
         * @param writer The index writer
         */
        public UpdateIndexHandler(File dumpDirectory, IndexInformation info, IndexWriter writer) {
            super(dumpDirectory, info, writer);
        }

        /**
         * Handles a new document. Used when updating the index.
         * @param reader The index reader
         * @param term The term
         * @param file The file
         */
        public void handleNewDocument(IndexReader reader, Term term, File file) {
            addFile(file);
        }
    }
}
