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

/* $Id$  */

package org.apache.lenya.lucene;

import java.util.StringTokenizer;

/**
 * Parameters to do a search by Lucene and display results
 */
public class Publication {
    /**
     * <code>id</code> The publication id
     */
    public String id = null;
    /**
     * <code>name</code> The publication name
     */
    public String name = null;
    /**
     * <code>indexDir</code> The index directory
     */
    public String indexDir = null;
    /**
     * <code>searchFields</code> The search fields
     */
    public String searchFields = null;
    /**
     * <code>excerptDir</code> The excerpt directory
     */
    public String excerptDir = null;
    /**
     * <code>prefix</code> The prefix
     */
    public String prefix = null;

    /**
     * Returns the search fields
     * @return The search fields
     */
    public String[] getFields() {
        String[] fields = null;
        if (this.searchFields != null) {
            StringTokenizer st = new StringTokenizer(this.searchFields, ",");
            int length = st.countTokens();
            fields = new String[length];
            for (int i = 0; i < length; i++) {
                fields[i] = st.nextToken();
            }
        }
        return fields;
    }
}
