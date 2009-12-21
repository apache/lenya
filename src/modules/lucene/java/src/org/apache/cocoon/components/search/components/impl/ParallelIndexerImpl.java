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

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.cocoon.Constants;
import org.apache.cocoon.components.search.IndexException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Parrallel Indexer Class
 * 
 * @author Nicolas Maisonneuve
 */

public class ParallelIndexerImpl extends AbstractIndexer implements
        Contextualizable {

    // Parallel specific variables
    private Stack queue;

    private boolean releaseSession, first_writing;

    /**
     * Number of threads (number of writers)
     */
    private int numThread;

    /**
     * temp dir where are stored the temporared index
     */
    private File tempDir;

    /**
     * multi-thread writer
     */
    private WriterThread[] writers;

    public ParallelIndexerImpl() {
        super();
        this.queue = new Stack();

        /**
         * @TODO see how many processor there are automatically
         */
        this.setNumThread(2);
        first_writing = true;
    }

    /**
     * Set the number of thread writer
     * 
     * @param num
     *            the number of thread
     */
    public void setNumThread(int num) {
        numThread = num;
        writers = new WriterThread[num];
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException {
        tempDir = (File) context.get(Constants.CONTEXT_WORK_DIR);
    }

    protected void release() throws IndexException {

        // ok this is the end of indexation (information for the threads)
        releaseSession = true;

        // wait for the end of writer threads
        boolean isindexing = true;
        while (isindexing) {

            // check if all the thread are died
            isindexing = false;
            for (int i = 0; i < writers.length; i++) {
                isindexing |= writers[i].alive;
            }

            // no, so sleep
            if (isindexing) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } else {
                break;
            }
        }

        // merge index
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Merging....");
        }
        this.switchToADD_MODE(false);
        Directory[] dirs = new Directory[numThread];
        for (int i = 0; i < numThread; i++) {
            dirs[i] = writers[i].dir;
        }
        try {
            this.add_writer.addIndexes(dirs);
        } catch (IOException ex1) {
            throw new IndexException("merge error ", ex1);
        }

        releaseSession = false;
        first_writing = true;
        super.release();
    }

    final protected void addDocument(Document document) throws IndexException {
        startThread();
        // put the document in the queue
        this.queue.add(document);
    }

    final protected void updateDocument(Document document)
            throws IndexException {
        del(document.get(DOCUMENT_UID_FIELD));
        addDocument(document);
    }

    /**
     * start the threads if it's not already done
     * 
     * @throws IndexException
     */
    private void startThread() throws IndexException {
        if (first_writing) {
            for (int i = 0; i < writers.length; i++) {
                writers[i] = new WriterThread();
                writers[i].start();
            }
            first_writing = false;
        }
    }

    /**
     * Writer Thread
     */
    final class WriterThread extends Thread {
        boolean alive = true;

        private IndexWriter mywriter;

        Directory dir;

        public void run() {
            // create a temp directory to store a subindex
            File file = new File(tempDir + File.separator + this.getName());
            file.mkdirs();

            // open a writer
            try {
                dir = FSDirectory.getDirectory(file, true);
                mywriter = new IndexWriter(dir, analyzer, true);
                mywriter.mergeFactor = mergeFactor;
                mywriter.minMergeDocs = mergeFactor * 2;
            } catch (IOException e) {
                e.printStackTrace();
                getLogger().error("Thread " + getName() + ": opening error", e);
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug(
                        "WriterThread " + this.getName() + " is ready....");
            }
            while (alive) {
                if (!queue.isEmpty()) {
                    try {
                        // add document
                        Document doc = (Document) queue.pop();
                        addDocument(mywriter, doc);
                    } catch (IndexException ex) {
                        ex.printStackTrace();
                        getLogger().error(
                                "Thread " + getName() + ": indexation error",
                                ex);
                    }
                } else {
                    // end session ?
                    if (releaseSession) {

                        // stop thread
                        alive = false;

                        // close writer
                        try {
                            mywriter.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            getLogger()
                                    .error(
                                            "Thread " + getName()
                                                    + ": close error", ex);
                        }
                    } else {
                        // wait new documents
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e2) {
                            getLogger()
                                    .error(
                                            "Thread " + getName()
                                                    + ": sleep error", e2);
                        }
                    }
                }

            }
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(
                        "WriterThread " + getName() + " is stoping...");

            }
        }
    }
}
