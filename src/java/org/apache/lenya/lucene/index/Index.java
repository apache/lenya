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

/* $Id: Index.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.lucene.index;

import java.io.File;
import java.util.Date;

import org.apache.lenya.lucene.IndexConfiguration;
import org.apache.lenya.xml.DOMUtil;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.XPath;
import org.w3c.dom.Document;

public class Index {
    /**
     * Command line interface
     *
     * @param argv Lucene Index Configuration
     */
    public static void main(String[] argv) {
        try {
            String index = "index";
            boolean create = false;
            File root = null;

            String usage = "Index <lucene.xconf> [file]";

            if (argv.length == 0) {
                System.err.println("Usage: " + usage);

                return;
            }

            IndexConfiguration ie = new IndexConfiguration(argv[0]);
            index = ie.resolvePath(ie.getIndexDir());
            root = new File(ie.resolvePath(ie.getHTDocsDumpDir()));

            if (ie.getUpdateIndexType().equals("new")) {
                create = true;
            } else if (ie.getUpdateIndexType().equals("incremental")) {
                create = false;
            } else {
                System.err.println("ERROR: No such update-index/@type: " + ie.getUpdateIndexType());

                return;
            }

            Date start = new Date();

            Indexer indexer = (Indexer) ie.getIndexerClass().newInstance();

            DOMUtil du = new DOMUtil();
            String path = argv[0];
            
            Document config = DocumentHelper.readDocument(new File(path));
            indexer.configure(du.getElement(config.getDocumentElement(), new XPath("indexer")), argv[0]);

            if (argv.length == 2) {
                indexer.indexDocument(new File(argv[1]));
                return;
            }

            if (create) {
                indexer.createIndex(root, new File(index));
            } else {
                indexer.updateIndex(root, new File(index));
            }

            Date end = new Date();

            System.out.print(end.getTime() - start.getTime());
            System.out.println(" total milliseconds");
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    /**
     * Get indexer from configuration
     */
    public static Indexer getIndexer(String luceneConfig) throws Exception {
        IndexConfiguration ie = new IndexConfiguration(luceneConfig);
        Indexer indexer = (Indexer) ie.getIndexerClass().newInstance();
        DOMUtil du = new DOMUtil();
        Document config = DocumentHelper.readDocument(new File(luceneConfig));
        indexer.configure(du.getElement(config.getDocumentElement(), new XPath("indexer")), luceneConfig);

        return indexer;
    }
}
