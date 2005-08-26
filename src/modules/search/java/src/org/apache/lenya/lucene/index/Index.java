/*
 * Copyright 1999-2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *  
 */

package org.apache.lenya.lucene.index;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lenya.lucene.IndexConfiguration;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Index.
 *
 * @version $Id$
 */
public class Index {
    /**
     * Command line interface
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

            String path = argv[0];

            Document config = DocumentHelper.readDocument(new File(path));
            Element indexerElement = DocumentHelper.getFirstChild(config.getDocumentElement(),
                    null, "indexer");
            indexer.configure(indexerElement, argv[0]);

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
        } catch (final InstantiationException e) {
            e.printStackTrace(System.out);
        } catch (final IllegalAccessException e) {
            e.printStackTrace(System.out);
        } catch (final ParserConfigurationException e) {
            e.printStackTrace(System.out);
        } catch (final SAXException e) {
            e.printStackTrace(System.out);
        } catch (final IOException e) {
            e.printStackTrace(System.out);
        }
    }

    /**
     * Get indexer from configuration.
     * @param luceneConfig The lucene configuration.
     * @return An indexer.
     * @throws IOException if an error occurs.
     */
    public static Indexer getIndexer(String luceneConfig) throws IOException {
        try {
            Indexer indexer;
            IndexConfiguration ie = new IndexConfiguration(luceneConfig);
            indexer = (Indexer) ie.getIndexerClass().newInstance();
            Document config = DocumentHelper.readDocument(new File(luceneConfig));
            Element indexerElement = DocumentHelper.getFirstChild(config.getDocumentElement(), null,
                    "indexer");
            indexer.configure(indexerElement, luceneConfig);
            return indexer;
        } catch (InstantiationException e) {
            throw new IOException(e.toString());
        } catch (IllegalAccessException e) {
            throw new IOException(e.toString());
        } catch (ParserConfigurationException e) {
            throw new IOException(e.toString());
        } catch (SAXException e) {
            throw new IOException(e.toString());
        } catch (IOException e) {
            throw new IOException(e.toString());
        }
    }
}