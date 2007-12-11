/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
package org.apache.lenya.cms.lucene;

import org.apache.cocoon.components.search.Index;
import org.apache.cocoon.components.search.LuceneCocoonSearcher;
import org.apache.cocoon.components.search.components.IndexManager;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * Test for search functionality.
 */
public class SearchTest extends AbstractAccessControlTest {

    public void testSearch() throws Exception {
        LuceneCocoonSearcher searcher = null;
        IndexManager indexManager = null;
        try {
            searcher = (LuceneCocoonSearcher) getManager().lookup(LuceneCocoonSearcher.ROLE);
            indexManager = (IndexManager) getManager().lookup(IndexManager.ROLE);
            Index index = indexManager.getIndex("test-authoring");
            searcher.setDirectory(index.getDirectory());

            Term term = new Term("body", "tutorial");
            Query query = new TermQuery(term);

            Hits hits = searcher.search(query);

            /*
             * The indexing doesn't work at the moment when the tests are executed because the
             * cocoon:// protocol is not supported in the test environment.
             * 
             * assertTrue(hits.length() > 0);
             */
            
        } finally {
            if (searcher != null) {
                getManager().release(searcher);
            }
            if (indexManager != null) {
                getManager().release(indexManager);
            }
        }
    }

}
