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

package org.apache.cocoon.components.search.components;

import org.apache.cocoon.ProcessingException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;

/**
 * this Searcher Component allows: <br/> - searching in several indexes <br/> - sorting hits by a
 * specified field and order
 * 
 * @author Nicolas Maisonneuve
 */
public interface Searcher {
    /**
     * The ROLE name of this avalon component.
     * <p>
     * Its value if the FQN of this interface, ie.
     * <code>org.apache.cocoon.components.search.Searcher</code>.
     * </p>
     * 
     * @since
     */
    String ROLE = Searcher.class.getName();

    /**
     * Add a lucene directory -- you can add several directories
     * <p>
     * The directory specifies the directory used for looking up the index. It defines the physical
     * place of the index
     * </p>
     * 
     * @param directory The new directory value
     */
    public void addDirectory(Directory directory);

    /**
     * Set the field by which the search results are to be sorted
     * @param field the index field
     * @param reverse reverse order or not
     */
    public void setSortField(String field, boolean reverse);

    /**
     * Search using a Lucene Query object, returning zero, or more hits.
     * 
     * @param query A lucene query
     * @return Hits zero or more hits matching the query string
     * @exception ProcessingException throwing due to processing errors while looking up the index
     *                directory, parsing the query string, generating the hits.
     */
    public Hits search(Query query) throws ProcessingException;
}
