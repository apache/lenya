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

/* $Id: AbstractIndexer.java,v 1.13 2004/03/01 16:18:15 gregor Exp $  */

package org.apache.lenya.lucene.index;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Category;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.w3c.dom.Element;


/**
 * Abstract base class for indexers.
 * The factory method {@link #getDocumentCreator(String[])} is used to create a
 * DocumentCreator from the command-line arguments.
 */
public abstract class AbstractIndexer implements Indexer {
    
    private static Category log = Category.getInstance(Indexer.class); 
    
    private DocumentCreator documentCreator;
    private Element indexer;
    private String configFileName;

    /**
     * Creates a new instance of AbstractIndexer
     */
    public AbstractIndexer() {
    }

    /**
     * Returns the DocumentCreator of this indexer.
     */
    protected DocumentCreator getDocumentCreator() {
        return documentCreator;
    }

    /**
     * Initializes this indexer with command-line parameters.
     */
    public void configure(Element indexer, String configFileName) throws Exception {
        documentCreator = createDocumentCreator(indexer, configFileName);
        this.indexer = indexer;
        this.configFileName = configFileName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param element DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public abstract DocumentCreator createDocumentCreator(Element indexer, String configFileName) throws Exception;

    /**
     * Updates the index incrementally.
     * Walk directory hierarchy in uid order, while keeping uid iterator from
     * existing index in sync.  Mismatches indicate one of:
     * <ol>
     *   <li>old documents to be deleted</li>
     *   <li>unchanged documents, to be left alone, or</li>
     *   <li>new documents, to be indexed.</li>
     * </ol>
     */
    public void updateIndex(File dumpDirectory, File index)
        throws Exception {
        deleteStaleDocuments(dumpDirectory, index);
        doIndex(dumpDirectory, index, false);
    }

    /**
     * Creates a new index.
     */
    public void createIndex(File dumpDirectory, File index)
        throws Exception {
        doIndex(dumpDirectory, index, true);
    }

    /**
     * Index files
     *
     * @param dumpDirectory Directory where the files to be indexed are located
     * @param index Directory where the index shall be located
     * @param create <b>true</b> means the index will be created from scratch, <b>false</b> means it will be indexed incrementally
     */
    public void doIndex(File dumpDirectory, File index, boolean create) {
        try {
            IndexWriter writer = new IndexWriter(index.getAbsolutePath(), new StandardAnalyzer(), create);
            writer.maxFieldLength = 1000000;

            IndexInformation info = new IndexInformation(index.getAbsolutePath(), dumpDirectory, getFilter(indexer, configFileName), create);

            IndexHandler handler;

            if (create) {
                handler = new CreateIndexHandler(dumpDirectory, info, writer);
            } else {
                handler = new UpdateIndexHandler(dumpDirectory, info, writer);
            }

            IndexIterator iterator = new IndexIterator(index.getAbsolutePath(), getFilter(indexer, configFileName));
            iterator.addHandler(handler);
            iterator.iterate(dumpDirectory);

            writer.optimize();
            writer.close();
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Delete the stale documents.
     */
    protected void deleteStaleDocuments(File dumpDirectory, File index)
        throws Exception {
        log.debug("Deleting stale documents");

        IndexIterator iterator = new IndexIterator(index.getAbsolutePath(), getFilter(indexer, configFileName));
        iterator.addHandler(new DeleteHandler());
        iterator.iterate(dumpDirectory);
        log.debug("Deleting stale documents finished");
    }

    /**
     * Returns the filter used to receive the indexable files. Might be overwritten by inherited class.
     */
    public FileFilter getFilter(Element indexer, String configFileName) {
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
            indexableExtensions = iE;
        }

        /**
         *
         */
        public DefaultIndexFilter(String[] indexableExtensions) {
            this.indexableExtensions = indexableExtensions;
        }

        /** Tests whether or not the specified abstract pathname should be
         * included in a pathname list.
         *
         * @param  pathname  The abstract pathname to be tested
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
                accept = Arrays.asList(indexableExtensions).contains(extension);
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
         *
         */
        public void handleStaleDocument(IndexReader reader, Term term) {
            log.debug("deleting " +
                IndexIterator.uid2url(term.text()));

            try {
                int deletedDocuments = reader.delete(term);
                log.debug("deleted " + deletedDocuments +
                    " documents.");
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public class IndexHandler extends AbstractIndexIteratorHandler {
        /**
         * Creates a new IndexHandler object.
         *
         * @param dumpDirectory DOCUMENT ME!
         * @param info DOCUMENT ME!
         * @param writer DOCUMENT ME!
         */
        public IndexHandler(File dumpDirectory, IndexInformation info, IndexWriter writer) {
            this.info = info;
            this.dumpDirectory = dumpDirectory;
            this.writer = writer;
        }

        private IndexInformation info;

        protected IndexInformation getInformation() {
            return info;
        }

        private File dumpDirectory;

        protected File getDumpDirectory() {
            return dumpDirectory;
        }

        private IndexWriter writer;

        protected IndexWriter getWriter() {
            return writer;
        }

        protected void addFile(File file) {
            log.debug("adding document: " + file.getAbsolutePath());

            try {
                Document doc = getDocumentCreator().getDocument(file, dumpDirectory);
                writer.addDocument(doc);
            } catch (Exception e) {
                log.error(e);
            }

            info.increase();
            log.info(info.printProgress());
        }
    }

    /**
     * DOCUMENT ME!
     */
    public class CreateIndexHandler extends IndexHandler {
        /**
         * Creates a new CreateIndexHandler object.
         *
         * @param dumpDirectory DOCUMENT ME!
         * @param info DOCUMENT ME!
         * @param writer DOCUMENT ME!
         */
        public CreateIndexHandler(File dumpDirectory, IndexInformation info, IndexWriter writer) {
            super(dumpDirectory, info, writer);
        }

        /**
         * Handles a file. Used when creating a new index.
         */
        public void handleFile(IndexReader reader, File file) {
            addFile(file);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public class UpdateIndexHandler extends IndexHandler {
        /**
         * Creates a new UpdateIndexHandler object.
         *
         * @param dumpDirectory DOCUMENT ME!
         * @param info DOCUMENT ME!
         * @param writer DOCUMENT ME!
         */
        public UpdateIndexHandler(File dumpDirectory, IndexInformation info, IndexWriter writer) {
            super(dumpDirectory, info, writer);
        }

        /**
         * Handles a new document. Used when updating the index.
         */
        public void handleNewDocument(IndexReader reader, Term term, File file) {
            addFile(file);
        }
    }
}
