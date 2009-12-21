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
import java.util.List;

import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.components.search.components.Searcher;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.store.Directory;

/**
 * @author Nicolas Maisonneuve
 * 
 */
abstract class AbstractSearcher extends AbstractLogEnabled implements Searcher,
        Disposable, Recyclable {
    /**
     * Lucene Directory
     */
    protected List directories = new ArrayList();

    /**
     * Lucene SortField
     */
    protected SortField sortfield;

    /**
     * Lucene Searcher
     */
    protected org.apache.lucene.search.Searcher luceneSearcher;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.Searcher#addDirectory(org.apache.lucene.store.Directory)
     */
    public void addDirectory(Directory directory) {
        directories.add(directory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.Searcher#search(org.apache.lucene.search.Query)
     */
    public Hits search(Query query) throws ProcessingException {
        try {
            getLuceneSearcher();
            
            if (sortfield==null) {
                return luceneSearcher.search(query);
            }
            else {
                return luceneSearcher.search(query, new Sort(sortfield));
            }
        } catch (IOException e) {
            throw new ProcessingException(e);
        }

    }

    protected abstract void getLuceneSearcher()
            throws IOException;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.Searcher#setSortField(java.lang.String,
     *      boolean)
     */
    public void setSortField(String field, boolean reverse) {
        sortfield = new SortField(field, reverse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        recycle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.avalon.excalibur.pool.Recyclable#recycle()
     */
    public void recycle() {
        try {
            directories.clear();
            sortfield = null;
            luceneSearcher.close();
        } catch (IOException ex) {
            this.getLogger().error("release error", ex);
        }

    }

}
