/*
$Id: SearchFiles.java,v 1.10 2003/07/23 13:21:26 gregor Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.lucene;

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
                "Usage: org.apache.lenya.lucene.SearchFiles \"directory_where_index_is_located\"");

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
