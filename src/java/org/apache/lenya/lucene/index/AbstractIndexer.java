/*
 * AbstractIndexer.java
 *
 * Created on 19. März 2003, 10:40
 */

package org.lenya.lucene.index;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.lenya.util.CommandLineLogger;

import org.w3c.dom.Element;

/**
 * Abstract base class for indexers.
 * The factory method {@link #getDocumentCreator(String[])} is used to create a
 * DocumentCreator from the command-line arguments.
 *
 * @author  hrt
 */
public abstract class AbstractIndexer
    implements Indexer {

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
    public void configure(Element element)
            throws Exception {
        documentCreator = createDocumentCreator(element);
    }
    
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
    
    public void doIndex(File dumpDirectory, String index, boolean create) {
    
        try {
            IndexWriter writer = new IndexWriter(index, new StandardAnalyzer(), create);
            writer.maxFieldLength = 1000000;

            IndexInformation info = new IndexInformation(index, dumpDirectory, getFilter(), create);
            
            IndexHandler handler;
            if (create) {
                handler = new CreateIndexHandler(dumpDirectory, info, writer);
            }
            else {
                handler = new UpdateIndexHandler(dumpDirectory, info, writer);
            }
            
            IndexIterator iterator = new IndexIterator(index, getFilter());
            iterator.addHandler(handler);
            iterator.iterate(dumpDirectory);
            
            writer.optimize();
            writer.close();
        }
        catch (IOException e) {
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
    public class DefaultIndexFilter
        implements FileFilter {
            
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
            }
            else {
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
    public class DeleteHandler
        extends AbstractIndexIteratorHandler {
            
        /** Handles a stale document.
         *
         */
        public void handleStaleDocument(IndexReader reader, Term term) {
            AbstractIndexer.this.getLogger().debug("deleting " + IndexIterator.uid2url(term.text()));
            try {
                int deletedDocuments = reader.delete(term);
                AbstractIndexer.this.getLogger().debug("deleted " + deletedDocuments + " documents.");
            }
            catch (IOException e) {
                getLogger().log(e);
            }
        }            

    }
    
    public class IndexHandler
        extends AbstractIndexIteratorHandler {
            
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
            }
            catch (Exception e) {
                getLogger().log(e);
            }
            
            info.increase();
            getLogger().log(info.printProgress());
        }
    }
    
    public class CreateIndexHandler
        extends IndexHandler {
            
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
    
    public class UpdateIndexHandler
        extends IndexHandler {
            
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
