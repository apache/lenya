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

/* $Id: IndexFiles.java,v 1.11 2004/03/01 16:18:25 gregor Exp $  */

package org.apache.lenya.lucene;

import java.io.File;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;


class IndexFiles {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println(
                "Usage: org.apache.lenya.lucene.IndexFiles \"directory_to_be_indexed\" \"directory_where_index_is_located\"");

            return;
        }

        File index_directory = new File(args[1]);
        File directory_to_be_indexed = new File(args[0]);

        if (!directory_to_be_indexed.exists()) {
            System.err.println("Exception: No such directory: " +
                index_directory.getAbsolutePath());

            return;
        }

        try {
            Date start = new Date();

            IndexWriter writer = new IndexWriter(index_directory, new StandardAnalyzer(), true);
            System.out.println("Warning: Directory will be created: " +
                index_directory.getAbsolutePath());
            indexDocs(writer, directory_to_be_indexed);

            writer.optimize();
            writer.close();

            Date end = new Date();

            System.out.print(end.getTime() - start.getTime());
            System.out.println(" total milliseconds");
        } catch (Exception e) {
            System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param writer DOCUMENT ME!
     * @param file DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public static void indexDocs(IndexWriter writer, File file)
        throws Exception {
        if (file.isDirectory()) {
            String[] files = file.list();

            for (int i = 0; i < files.length; i++)
                indexDocs(writer, new File(file, files[i]));
        } else {
            System.out.println("adding " + file);
            writer.addDocument(FileDocument.Document(file));
        }
    }
}
