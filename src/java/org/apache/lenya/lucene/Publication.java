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

/* $Id: Publication.java,v 1.10 2004/03/01 16:18:25 gregor Exp $  */

package org.apache.lenya.lucene;

import java.util.StringTokenizer;

/**
 * Parameters to do a search by Lucene and display results
 */
public class Publication {
    public String id = null;
    public String name = null;
    public String indexDir = null;
    public String searchFields = null;
    public String excerptDir = null;
    public String prefix = null;

    /**
     *
     */
    public String[] getFields() {
        String[] fields = null;
        if (searchFields != null) {
            StringTokenizer st = new StringTokenizer(searchFields, ",");
            int length = st.countTokens();
            fields = new String[length];
            for (int i = 0; i < length; i++) {
                fields[i] = st.nextToken();
            }
        }
        return fields;
    }
}
