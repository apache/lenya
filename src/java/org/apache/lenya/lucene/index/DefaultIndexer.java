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

package org.apache.lenya.lucene.index;

import org.w3c.dom.Element;

import java.io.IOException;

/**
 * The default indexer
 */
public class DefaultIndexer extends AbstractIndexer {

    /**
     * Creates a new instance of DefaultIndexer 
     */
    public DefaultIndexer() {
        // do nothing
    }

    /**
     * @param indexer Indexer
     * @param configFileName Lucene Configuration File
     * @return DefaultDocumentCreator
     * @throws IOException if an error occurs
     */
    public DocumentCreator createDocumentCreator(Element indexer, String configFileName) throws IOException {
        return new DefaultDocumentCreator();
    }
}
