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

/* $Id: SearchFiles.java,v 1.13 2004/03/01 16:18:25 gregor Exp $  */

package org.apache.lenya.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;

/**
 * Command Line Interface
 */
class SearchFiles {

    /**
     * main method
     *
     * @param args Directory of the index
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: org.apache.lenya.lucene.SearchFiles \"directory_where_index_is_located\" <word>");
            return;
        }

        File index_directory = new File(args[0]);

        if (!index_directory.exists()) {
            System.err.println("Exception: No such directory: " +
                index_directory.getAbsolutePath());

            return;
        }


        try {
            if (args.length > 1) {
                Hits hits = new SearchFiles().search(args[1], index_directory);
                return;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.print("Search: ");

                String line = in.readLine();

                if (line.length() == -1) {
                    break;
                }

		Hits hits = new SearchFiles().search(line, index_directory);

                    System.out.print("\nAnother Search (y/n) ? ");
                    line = in.readLine();

                    if ((line.length() == 0) || (line.charAt(0) == 'n')) {
                         break;
                    }
            }

        } catch (Exception e) {
            System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }
    }

    /**
     *
     */
    public Hits search(String line, File index_directory) throws Exception {
        Searcher searcher = new IndexSearcher(index_directory.getAbsolutePath());
        Analyzer analyzer = new StandardAnalyzer();

        Query query = QueryParser.parse(line, "contents", analyzer);
        System.out.println("Searching for: " + query.toString("contents"));

                Hits hits = searcher.search(query);
                System.out.println("Total matching documents: " + hits.length());

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

                }
                searcher.close();
        return hits;
    }
}
