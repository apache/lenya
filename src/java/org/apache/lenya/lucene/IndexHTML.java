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

/* $Id: IndexHTML.java,v 1.19 2004/03/01 16:18:25 gregor Exp $  */

package org.apache.lenya.lucene;

import java.io.File;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.util.Arrays;


/**
 * DOCUMENT ME!
 */
public class IndexHTML {
    private boolean deleting = false; // true during deletion pass
    private IndexReader reader; // existing index
    private IndexWriter writer; // new index being built
    private TermEnum uidIter; // document id iterator
    private int numberOfAddedDocs = 0;

    /**
     * DOCUMENT ME!
     *
     * @param argv DOCUMENT ME!
     */
    public static void main(String[] argv) {
        new IndexHTML().startIndexing(argv);
    }

    /**
     *
     */
    public void startIndexing(String[] argv) {
        try {
            String index = "index";
            boolean create = false;
            File root = null;

            String usage = "IndexHTML <lucene.xconf>";

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

            if (!create) { // delete stale docs
                deleting = true;
                indexDocs(root, index, create);
            }

            writer = new IndexWriter(index, new StandardAnalyzer(), create);
            writer.maxFieldLength = 1000000;

            indexDocs(root, index, create); // add new docs

            System.out.println("Optimizing index...");
            writer.optimize();
            writer.close();

            Date end = new Date();

            System.out.print(end.getTime() - start.getTime());
            System.out.println(" total milliseconds");
        } catch (Exception e) {
            System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }

        System.out.println("Thanks for indexing with Lucene!");
    }

    /* Walk directory hierarchy in uid order, while keeping uid iterator from
    /* existing index in sync.  Mismatches indicate one of: (a) old documents to
    /* be deleted; (b) unchanged documents, to be left alone; or (c) new
    /* documents, to be indexed.
     */
    private void indexDocs(File file, String index, boolean create)
        throws Exception {
        if (!create) { // incrementally update
            reader = IndexReader.open(index); // open existing index
            uidIter = reader.terms(new Term("uid", "")); // init uid iterator

            indexDocs(file, file);

            if (deleting) { // delete rest of stale docs

                while ((uidIter.term() != null) && (uidIter.term().field() == "uid")) {
                    System.out.println("IndexHTML.indexDocs(): deleting " +
                        HTMLDocument.uid2url(uidIter.term().text()));
                    reader.delete(uidIter.term());
                    uidIter.next();
                }

                deleting = false;
            }

            uidIter.close(); // close uid iterator
            reader.close(); // close existing index
        } else { // don't have exisiting
            indexDocs(file, file);
        }
    }

    /**
     *
     */
    private void indexDocs(File file, File root) throws Exception {
        if (file.isDirectory()) { // if a directory

            String[] files = file.list(); // list its files
            Arrays.sort(files); // sort the files

            for (int i = 0; i < files.length; i++) { // recursively index them
                indexDocs(new File(file, files[i]), root);
            }
        } else if (file.getPath().endsWith(".html") || // index .html files
                file.getPath().endsWith(".htm") || // index .htm files
                file.getPath().endsWith(".txt")) { // index .txt files

            if (uidIter != null) {
                String uid = HTMLDocument.uid(file, root); // construct uid for doc

                while ((uidIter.term() != null) && (uidIter.term().field() == "uid") &&
                        (uidIter.term().text().compareTo(uid) < 0)) {
                    if (deleting) { // delete stale docs
                        System.out.println("IndexHTML.indexDocs(File,File): deleting " +
                            HTMLDocument.uid2url(uidIter.term().text()));
                        reader.delete(uidIter.term());
                    }

                    uidIter.next();
                }

                if ((uidIter.term() != null) && (uidIter.term().field() == "uid") &&
                        (uidIter.term().text().compareTo(uid) == 0)) {
                    uidIter.next(); // keep matching docs
                } else if (!deleting) { // add new docs

                    Document doc = null;

                    try {
                        doc = HTMLDocument.Document(file, root);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("IndexHTML.indexDocs(File,File): adding (!deleting==true) " +
                        doc.get("url"));
                    writer.addDocument(doc);
                    numberOfAddedDocs++;
                    System.out.println("IndexHTML.indexDocs(File,File): added (" +
                        numberOfAddedDocs + ")!");
                }
            } else { // creating a new index

                Document doc = null;

                try {
                    doc = HTMLDocument.Document(file, root);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("IndexHTML.indexDocs(File,File): adding (unconditionally) " +
                    doc.get("url"));
                writer.addDocument(doc); // add docs unconditionally
                numberOfAddedDocs++;
                System.out.println("IndexHTML.indexDocs(File,File): added (" + numberOfAddedDocs +
                    ")!");
            }
        }
    }
}
