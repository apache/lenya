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

package org.apache.cocoon.components.search;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.components.search.components.AnalyzerManager;
import org.apache.cocoon.components.search.components.Indexer;
import org.apache.cocoon.components.search.fieldmodel.DateFieldDefinition;
import org.apache.cocoon.components.search.fieldmodel.FieldDefinition;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Index Class
 * 
 * @author Nicolas Maisonneuve
 */
public class Index {

    /**
     * default analyzer ID
     */
    private String defaultAnalyzer;

    /**
     * Index Structure definition
     */
    private IndexStructure structure;

    /**
     * Index ID
     */
    private String id;

    /**
     * Lucene Directory of the index
     */
    private Directory directory;

    /**
     * Number of try to access to the indexer
     * 
     */
    private int numtries = 5;

    /**
     * is the indexer working (not released)
     */
    private boolean indexer_busy;
    
    /**
     * Indexer Role name
     */
    private String indexer_role;

    private ServiceManager manager;

    /**
     * Create a lucene document
     * 
     * @param uid
     *            String the document uid
     * @return Document a empty document
     */
    public Document createDocument(String uid) {
        Document doc = new Document();
        try {
            doc.add(createField(Indexer.DOCUMENT_UID_FIELD, uid));
        } catch (IndexException ex) {
        }
        return doc;
    }

    /**
     * create a lucene field
     * 
     * @param fieldname
     *            String fieldname (must existed in the index structure)
     * @param value
     *            String value
     */
    public Field createField(String fieldname, String value)
            throws IndexException {
        FieldDefinition f = structure.getFieldDef(fieldname);
        if (f == null) {
            throw new IndexException("Field with the name: " + fieldname
                    + " doesn't exist");
        }
        return f.createLField(value);
    }

    /**
     * create a lucene field for date value
     * 
     * @param fieldname
     *            String fieldname (must existed in the index structure)
     * @param value
     *            String value
     */
    public Field createField(String fieldname, Date value)
            throws IndexException {
        DateFieldDefinition f = (DateFieldDefinition) structure
                .getFieldDef(fieldname);
        if (f == null) {
            throw new IndexException("Field with the name: " + fieldname
                    + " doesn't exist");
        }
        return f.createLField(value);
    }
    
    /**
     * get the indexer of the index
     * 
     * @throws IndexException
     * @return Indexer
     */
    public synchronized Indexer getIndexer() throws IndexException {

        long endTime = System.currentTimeMillis() + numtries * 1000;
        // wait the end of the indexing
        while (indexer_busy && System.currentTimeMillis() < endTime) {
            try {
                wait(1000);
            } catch (InterruptedException ex) {
            }
        }

        if (indexer_busy) {
            throw new IndexException(
                    "Timeout to access to the indexer (the indexer is indexing)");
        }
        AnalyzerManager analyzerM = null;
        try {

            indexer_busy = true;
            Indexer indexer = (Indexer) this.manager.lookup(indexer_role);

            // update maybe the analyzer
            analyzerM = (AnalyzerManager) this.manager
                    .lookup(AnalyzerManager.ROLE);

            String analyzerId = getDefaultAnalyzerID();
            if (analyzerId != null) {
                Analyzer analyzer = analyzerM.getAnalyzer(analyzerId);
                indexer.setAnalyzer(analyzer);
            }
            indexer.setIndex(directory);

            return indexer;
        } catch (ServiceException ex1) {
            throw new IndexException(ex1);
        } catch (ConfigurationException ex2) {
            throw new IndexException(ex2);
        } finally {
            if (analyzerM != null) {
                manager.release(analyzerM);
            }
        }
    }

    /**
     * Release the indexer
     * 
     * @param indexer
     */
    public synchronized void releaseIndexer(Indexer indexer) {
        if (indexer != null) {
            this.manager.release(indexer);
            indexer_busy = false;
        }
        notifyAll();
    }

    /**
     * get the index ID
     * 
     * @return the index ID
     */
    public String getID() {
        return id;
    }

    /**
     * Set the index ID
     * 
     * @param id
     *            index ID
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * get the default Analyzer
     * 
     * @return the id of the default analyzer
     */
    public String getDefaultAnalyzerID() {
        return defaultAnalyzer;
    }

    /**
     * set the default Analyzer
     * 
     * @param defaultAnalyzerID
     *            the id of the default Analyzer
     */
    public void setDefaultAnalyzerID(String defaultAnalyzerID) {
        this.defaultAnalyzer = defaultAnalyzerID;
    }

    /**
     * Return the index Structure
     * 
     * @return the index Structure
     */
    public IndexStructure getStructure() {
        return structure;
    }

    /**
     * Set the index structure
     * 
     * @param structure
     *            IndexStructure
     */
    public void setStructure(IndexStructure structure) {
        this.structure = structure;
    }

    public void setManager(ServiceManager manager) {
        this.manager = manager;
    }

    /**
     * get the lucene directory
     * 
     * @return the lucene directory
     */
    public Directory getDirectory() {
        return directory;
    }

    /**
     * Set the lucene Directory
     * 
     * @param dir
     *            lucene Directory
     * @return success or not
     * @throws IOException
     */
    public boolean setDirectory(Directory dir) throws IOException {
        boolean locked = false;
        this.directory = dir;

        // if index is locked
        if (IndexReader.isLocked(directory)) {
            IndexReader.unlock(directory);
            locked = true;
        }

        // create index if the index doesn't exist
        if (!IndexReader.indexExists(directory)) {
            (new IndexWriter(directory, null, true)).close();
        }

        return locked;

    }

    /**
     * Set the index path directory
     * 
     * @param path
     *            String
     * @throws IOException
     */
    public boolean setDirectory(String path) throws IOException {
        File fpath = new File(path);
        Directory dir = FSDirectory.getDirectory(fpath, !fpath.exists());
        return setDirectory(dir);
    }

    /**
     * @param indexer The indexer to set.
     */
    public void setIndexer(String indexer) {
        this.indexer_role = indexer;
    }
}
