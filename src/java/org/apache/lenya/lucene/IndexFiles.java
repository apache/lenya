/*
 * $Id: IndexFiles.java,v 1.5 2003/03/06 20:45:52 gregor Exp $
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
import org.apache.lucene.index.IndexWriter;

import java.io.File;

import java.util.Date;


class IndexFiles {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println(
                "Usage: swx.eservices.lucene.IndexFiles \"directory_to_be_indexed\" \"directory_where_index_is_located\"");

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
