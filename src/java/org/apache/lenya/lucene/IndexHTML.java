/*
 * $Id: IndexHTML.java,v 1.12 2003/03/06 20:45:52 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.util.Arrays;

import java.io.File;

import java.util.Date;


class IndexHTML {
    private static boolean deleting = false; // true during deletion pass
    private static IndexReader reader; // existing index
    private static IndexWriter writer; // new index being built
    private static TermEnum uidIter; // document id iterator
    private static int numberOfAddedDocs = 0;

    /**
     * DOCUMENT ME!
     *
     * @param argv DOCUMENT ME!
     */
    public static void main(String[] argv) {
        try {
            String index = "index";
            boolean create = false;
            File root = null;

            String usage = "IndexHTML <lucene.xconf>";

            if (argv.length == 0) {
                System.err.println("Usage: " + usage);

                return;
            }

            IndexEnvironment ie = new IndexEnvironment(argv[0]);
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
    private static void indexDocs(File file, String index, boolean create)
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
    private static void indexDocs(File file, File root)
        throws Exception {
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
