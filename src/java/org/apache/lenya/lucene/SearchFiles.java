/*
 * $Id: SearchFiles.java,v 1.3 2003/02/20 13:40:41 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
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
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.wyona.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;


class SearchFiles {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println(
                "Usage: swx.eservices.lucene.SearchFiles \"directory_where_index_is_located\"");

            return;
        }

        File index_directory = new File(args[0]);

        if (!index_directory.exists()) {
            System.err.println("Exception: No such directory: " +
                index_directory.getAbsolutePath());

            return;
        }

        try {
            Searcher searcher = new IndexSearcher(index_directory.getAbsolutePath());
            Analyzer analyzer = new StandardAnalyzer();

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.print("Query: ");

                String line = in.readLine();

                if (line.length() == -1) {
                    break;
                }

                Query query = QueryParser.parse(line, "contents", analyzer);
                System.out.println("Searching for: " + query.toString("contents"));

                Hits hits = searcher.search(query);
                System.out.println(hits.length() + " total matching documents");

                final int HITS_PER_PAGE = 10;

                for (int start = 0; start < hits.length(); start += HITS_PER_PAGE) {
                    int end = Math.min(hits.length(), start + HITS_PER_PAGE);

                    for (int i = start; i < end; i++) {
                        Document doc = hits.doc(i);
                        String path = doc.get("path");

                        if (path != null) {
                            System.out.println(i + ". " + path);
                        } else {
                            String url = doc.get("url");

                            if (url != null) {
                                System.out.println(i + ". " + url);
                                System.out.println("   - " + doc.get("title"));
                            } else {
                                System.out.println(i + ". " + "No path nor URL for this document");
                            }
                        }
                    }

                    if (hits.length() > end) {
                        System.out.print("more (y/n) ? ");
                        line = in.readLine();

                        if ((line.length() == 0) || (line.charAt(0) == 'n')) {
                            break;
                        }
                    }
                }
            }

            searcher.close();
        } catch (Exception e) {
            System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }
    }
}
