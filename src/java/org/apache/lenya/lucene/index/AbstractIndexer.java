/*
 * AbstractIndexer.java
 *
 * Created on 19. März 2003, 10:40
 */

package org.lenya.lucene.index;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.transform.Source;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.lenya.lucene.HTMLDocument;

/**
 * Abstract base class for indexers.
 * The factory method {@link #getDocumentCreator(String[])} is used to create a
 * DocumentCreator from the command-line arguments.
 *
 * @author  hrt
 */
public abstract class AbstractIndexer
    implements Indexer {

    private DocumentCreator documentCreator;
    
    /** Creates a new instance of AbstractIndexer */
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
    public void configure(Configuration configuration)
            throws Exception {
        documentCreator = createDocumentCreator(configuration);
    }
    
    public abstract DocumentCreator createDocumentCreator(Configuration configuration)
            throws Exception;
    
    /**
     * Updates the index incrementally.
     * Walk directory hierarchy in uid order, while keeping uid iterator from
     * existing index in sync.  Mismatches indicate one of: (a) old documents to
     * be deleted; (b) unchanged documents, to be left alone; or (c) new
     * documents, to be indexed.
     */
    public void updateIndex(File dumpDirectory, String index, IndexWriter writer)
            throws Exception {
        
        IndexReader reader = IndexReader.open(index);
        TermEnum uidIter = reader.terms(new Term("uid", ""));
        
        IndexInformation info = new IndexInformation(dumpDirectory, getFilter());

        deleteStaleDocuments(dumpDirectory, info, index);
        indexDocs(dumpDirectory, info, writer, uidIter);
        
        uidIter.close();
        reader.close();
    }
    
    /**
     * Delete the rest of stale documents.
     */
    protected void deleteStaleDocuments(File dumpDirectory, IndexInformation info, String index)
            throws Exception {
        
        IndexReader reader = IndexReader.open(index);
        TermEnum uidIter = reader.terms(new Term("uid", ""));

        deleteDocs(dumpDirectory, info, reader, uidIter);
        
        // delete the rest of stale documents
        while (uidIter.term() != null && uidIter.term().field() == "uid") {
            System.out.println("IndexHTML.indexDocs(): deleting " + HTMLDocument.uid2url(uidIter.term().text()));
            reader.delete(uidIter.term());
            uidIter.next();
        }

        uidIter.close();
        reader.close();
    }

    /**
     * Creates a new index.
     */
    public void createIndex(File dumpDirectory, String index, IndexWriter writer)
            throws Exception {
                
        IndexReader reader = IndexReader.open(index);
        TermEnum uidIter = reader.terms(new Term("uid", ""));

        IndexInformation info = new IndexInformation(dumpDirectory, getFilter());
        indexDocs(dumpDirectory, info, writer, uidIter);
        
        uidIter.close();
        reader.close();
    }
    
    /**
     * Recursive deleting.
     */
    protected void deleteDocs(File root, IndexInformation info, IndexReader reader, TermEnum uidIter)
            throws Exception {
        //System.out.println("IndexHTML.indexDocs(File,File): "+file+" "+root);
        
        File[] files = info.getFiles();
        for (int i = 0; i < files.length; i++) {
            deleteDocument(files[i], root, reader, uidIter);
        }
    }

    /**
     * Deletes a stale document.
     */
    protected void deleteDocument(File file, File root, IndexReader reader, TermEnum uidIter)
            throws Exception {
                
        String uid = HTMLDocument.uid(file, root);

        while (uidIter.term() != null
                && uidIter.term().field() == "uid"
                && uidIter.term().text().compareTo(uid) < 0) {

            System.out.println("IndexHTML.indexDocs(File,File): deleting " + HTMLDocument.uid2url(uidIter.term().text()));
            reader.delete(uidIter.term());
            uidIter.next();
        }

        if (uidIter.term() != null
                && uidIter.term().field() == "uid"
                && uidIter.term().text().compareTo(uid) == 0) {

            // keep matching docs
            uidIter.next();
        }
    }
    
    /**
     * Recursive indexing.
     */
    protected void indexDocs(File root, IndexInformation info, IndexWriter writer, TermEnum uidIter)
            throws Exception {
        //System.out.println("IndexHTML.indexDocs(File,File): "+file+" "+root);
        
        File[] files = info.getFiles();
        for (int i = 0; i < files.length; i++) {
            indexDocument(files[i], root, writer, uidIter, info);
        }
    }
    
    /**
     * Indexes a document.
     */
    protected void indexDocument(File file, File root, IndexWriter writer, TermEnum uidIter, IndexInformation info)
            throws Exception {
                
        boolean add = false;
                
        if (uidIter != null) {
            String uid = HTMLDocument.uid(file, root);

            while (uidIter.term() != null
                    && uidIter.term().field() == "uid"
                    && uidIter.term().text().compareTo(uid) < 0) {

                uidIter.next();
            }

            if (uidIter.term() != null
                    && uidIter.term().field() == "uid"
                    && uidIter.term().text().compareTo(uid) == 0) {

                // keep matching docs
                uidIter.next();
            }

            // add new docs
            else {
                add = true;
            }
        }

        // creating a new index
        else {
            add = true;
        }
        
        if (add) {
            System.out.println(getClass().getName() + " adding document: " + file.getAbsolutePath());
            Document doc = documentCreator.getDocument(file, root);
            writer.addDocument(doc);
            
            info.increase();
            System.out.println(getClass().getName() + " " + info.printProgress());
        }
    }
    
    /**
     * Returns the filter used to receive the indexable files.
     */
    public FileFilter getFilter() {
        return new AbstractIndexer.DefaultIndexFilter();
    }
    
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
    
}
