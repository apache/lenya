/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.lucene.index;

import org.apache.lenya.util.CommandLineLogger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import org.w3c.dom.Element;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import java.util.Arrays;


/**
 * Abstract base class for indexers.
 * The factory method {@link #getDocumentCreator(String[])} is used to create a
 * DocumentCreator from the command-line arguments.
 *
 * @author  hrt
 */
public abstract class AbstractIndexer implements Indexer {
    private CommandLineLogger logger = new CommandLineLogger(getClass());
    private DocumentCreator documentCreator;

    /** Creates a new instance of AbstractIndexer */
    public AbstractIndexer() {
    }

    protected CommandLineLogger getLogger() {
        return logger;
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
    public void configure(Element element) throws Exception {
        documentCreator = createDocumentCreator(element);
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
    public abstract DocumentCreator createDocumentCreator(Element element)
        throws Exception;

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
    public void updateIndex(File dumpDirectory, String index)
        throws Exception {
        deleteStaleDocuments(dumpDirectory, index);
        doIndex(dumpDirectory, index, false);
    }

    /**
     * Creates a new index.
     */
    public void createIndex(File dumpDirectory, String index)
        throws Exception {
        doIndex(dumpDirectory, index, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param dumpDirectory DOCUMENT ME!
     * @param index DOCUMENT ME!
     * @param create DOCUMENT ME!
     */
    public void doIndex(File dumpDirectory, String index, boolean create) {
        try {
            IndexWriter writer = new IndexWriter(index, new StandardAnalyzer(), create);
            writer.maxFieldLength = 1000000;

            IndexInformation info = new IndexInformation(index, dumpDirectory, getFilter(), create);

            IndexHandler handler;

            if (create) {
                handler = new CreateIndexHandler(dumpDirectory, info, writer);
            } else {
                handler = new UpdateIndexHandler(dumpDirectory, info, writer);
            }

            IndexIterator iterator = new IndexIterator(index, getFilter());
            iterator.addHandler(handler);
            iterator.iterate(dumpDirectory);

            writer.optimize();
            writer.close();
        } catch (IOException e) {
            getLogger().log(e);
        }
    }

    /**
     * Delete the stale documents.
     */
    protected void deleteStaleDocuments(File dumpDirectory, String index)
        throws Exception {
        getLogger().debug("Deleting stale documents");

        IndexIterator iterator = new IndexIterator(index, getFilter());
        iterator.addHandler(new DeleteHandler());
        iterator.iterate(dumpDirectory);
        getLogger().debug("Deleting stale documents finished");
    }

    /**
     * Returns the filter used to receive the indexable files.
     */
    public FileFilter getFilter() {
        return new AbstractIndexer.DefaultIndexFilter();
    }

    /**
     * FileFilter used to obtain the files to index.
     */
    public class DefaultIndexFilter implements FileFilter {
        protected final String[] indexableExtensions = { "html", "htm", "txt" };

        /** Tests whether or not the specified abstract pathname should be
         * included in a pathname list.
         *
         * @param  pathname  The abstract pathname to be tested
         * @return  <code>true</code> if and only if <code>pathname</code>
         *          should be included
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
            AbstractIndexer.this.getLogger().debug("deleting " +
                IndexIterator.uid2url(term.text()));

            try {
                int deletedDocuments = reader.delete(term);
                AbstractIndexer.this.getLogger().debug("deleted " + deletedDocuments +
                    " documents.");
            } catch (IOException e) {
                getLogger().log(e);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision: 1.6 $
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
            getLogger().debug("adding document: " + file.getAbsolutePath());

            try {
                Document doc = getDocumentCreator().getDocument(file, dumpDirectory);
                writer.addDocument(doc);
            } catch (Exception e) {
                getLogger().log(e);
            }

            info.increase();
            getLogger().log(info.printProgress());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision: 1.6 $
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
     *
     * @author $author$
     * @version $Revision: 1.6 $
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
