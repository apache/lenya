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

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Element;

/**
 * The indexer interface
 */
public interface Indexer {
    /**
     * Configures this indexer.
     * @param _indexer The indexer
     * @param _configFileName The name of the configuration file
     * @throws IOException if an error occurs
     */
    void configure(Element _indexer, String _configFileName) throws IOException;

    /**
     * Indexes the contents of a directory.
     * @param _dumpDirectory The dump directory to use
     * @param _index The index to use
     * @throws IOException if an error occurs
     */
    void createIndex(File _dumpDirectory, File _index) throws IOException;

    /**
     * Indexes the contents of a directory.
     * @param _dumpDirectory The dump directory to use
     * @param _index The index to use
     * @throws IOException if an error occurs
     */
    void updateIndex(File _dumpDirectory, File _index) throws IOException;

    /**
     * Indexes the content of one file
     * @param file The file to index
     * @throws IOException if an error occurs
     */
    void indexDocument(File file) throws IOException;
}
