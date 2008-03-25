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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.components.search.IndexException;
import org.apache.lucene.document.Document;

/**
 * 
 * @author Nicolas Maisonneuve
 */
public class DefaultIndexerImpl extends AbstractIndexer implements Configurable {

    /**
     * Buffer size is element
     */
    static public final String DOCUMENT_BUFFERED_NUM_ELEMENT = "buffer_size";

    /**
     * the default size of the buffer
     */
    private int defaultMaxBufDocs = 100;

    /**
     * Buffer Size: the number of the maximum documents buffered, before to
     * flush and index this documents (the buffer is used in the update mode)
     */
    private int bufferSize;

    /**
     * the buffer: the List where are stored the documents
     */
    private List buffer = new ArrayList();

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException {
        defaultMaxBufDocs = conf.getChild(DOCUMENT_BUFFERED_NUM_ELEMENT)
                .getValueAsInteger(100);
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug(
                    "default max buffered documents: " + defaultMaxBufDocs);
        }
    }

    /**
     * Set the maximum number of buffered documents to avoid to open and close
     * the IndexWriter a lot of times
     * 
     * @param value
     *            int number (default 100)
     */
    public void setBufferSize(int value) {
        bufferSize = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.impl.AbstractIndexer#release()
     */
    final protected void release() throws IndexException {
        // flush the last documents to update
        if (buffer.size() > 0) {
            flushBufferedDocs();
        }
        bufferSize = defaultMaxBufDocs;
        //this.optimize();
        super.release();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.impl.AbstractIndexer#addDocument(org.apache.lucene.document.Document)
     */
    final protected void addDocument(Document doc) throws IndexException {
        switchToADD_MODE(false);
        addDocument(add_writer, doc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.impl.AbstractIndexer#updateDocument(org.apache.lucene.document.Document)
     */
    final protected void updateDocument(Document doc) throws IndexException {
        // first delete the old document
        del(doc.get(DOCUMENT_UID_FIELD));

        // then store in the index queue
        buffer.add(doc);

        // flush the queue if it's necessary
        if (buffer.size() == bufferSize) {
            flushBufferedDocs();
        }
    }

    /**
     * Index the list of documents to update
     * 
     * @throws IOException
     */
    private void flushBufferedDocs() throws IndexException {
        this.switchToADD_MODE(false);
        Iterator iter = buffer.iterator();
        while (iter.hasNext()) {
            addDocument(add_writer, (Document) iter.next());
        }
        buffer.clear();
    }
}
