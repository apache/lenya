/*
 * $Id: ReTokenizeFile.java,v 1.13 2003/04/24 13:53:00 gregor Exp $
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
package org.apache.lenya.lucene;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lenya.lucene.html.HTMLParser;

import java.io.*;
import java.util.StringTokenizer;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.13 $
 */
public class ReTokenizeFile {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: ReTokenizeFile filename word1 word2 ...");

            return;
        }

        try {
            String[] words = new String[args.length - 1]; //{"Cocoon","Lenya"};

            for (int i = 1; i < args.length; i++) {
                words[i - 1] = args[i];
            }

            String s = null;

            s = new ReTokenizeFile().getExcerpt(new File(args[0]), words);
            System.err.println(".main(): Excerpt: " + s);
        } catch (Exception e) {
            System.err.println(".main(): " + e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param file DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public String reTokenize(File file) throws Exception {
        TokenStream ts = new StandardAnalyzer().tokenStream(new HTMLParser(file).getReader());

        Token token = null;

        while ((token = ts.next()) != null) {
            System.out.println("ReTokenizeFile.reTokenize(File): " + token.termText() + " " +
                token.startOffset() + " " + token.endOffset() + " " + token.type());
        }

        return file.getAbsolutePath();
    }

    /**
     *
     */
    public String getExcerpt(File file, String[] words)
        throws FileNotFoundException, IOException {
        if (file.getName().substring(file.getName().length() - 4).equals(".pdf")) {
            file = new File(file.getAbsolutePath() + ".txt");
        }

        java.io.Reader reader = new HTMLParser(file).getReader();
        char[] chars = new char[1024];
        int chars_read;
        java.io.Writer writer = new java.io.StringWriter();

        while ((chars_read = reader.read(chars)) > 0) {
            writer.write(chars, 0, chars_read);
        }

        String html = writer.toString();

        int index = -1;

        for (int i = 0; i < words.length; i++) {
            index = html.toLowerCase().indexOf(words[i].toLowerCase());

            if (index >= 0) {
                int offset = 100;
                int start = index - offset;

                if (start < 0) {
                    start = 0;
                }

                int end = index + words[i].length() + offset;

                if (end >= html.length()) {
                    end = html.length() - 1;
                }

                return html.substring(start, end);
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param string DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String tidy(String string) {
        StringTokenizer st = new StringTokenizer(string,"<>&");

        StringBuffer sb = new StringBuffer("");
        while(st.hasMoreElements()) {
            sb.append(st.nextToken());
        }
        return sb.toString();
    }

    /**
     * Encloses all words in <code>words</code> that appear in <code>string</code> in
     * &lt;word&gt; tags. The whole string is enclosed in &lt;excerpt&gt; tags.
     *
     * @param string The string to process.
     * @param words The words to emphasize.
     *
     * @return DOCUMENT ME!
     */
    public String emphasizeAsXML(String string, String[] words) {
        String emphasizedString = "... Hello <word>World</word>! ...";
        
        String lowerCaseString = string.toLowerCase();

        for (int i = 0; i < words.length; i++) {
            
            String word = words[i].toLowerCase();
            
            // use uppercase tags so that they are not replaced
            lowerCaseString = lowerCaseString.replaceAll(word, "<WORD>" + word + "</WORD>");

        }
        
        lowerCaseString = lowerCaseString.toLowerCase();
        //if (true) return "<excerpt>" + lowerCaseString + "</excerpt>";
        
        String result = "";
        
        int sourceIndex = 0;
        int index = 0;
        String tags[] = { "<word>", "</word>" };
        
        while (lowerCaseString.indexOf(tags[0], index) != -1) {
            
            for (int tag = 0; tag < 2; tag++) {
                int subStringLength = lowerCaseString.indexOf(tags[tag], index) - index;
                String subString = string.substring(sourceIndex, sourceIndex + subStringLength);
                result += includeInCDATA(subString) + tags[tag];
                sourceIndex += subStringLength;
                index += subStringLength + tags[tag].length();
            }
        }
        
        result += includeInCDATA(string.substring(sourceIndex));

        return "<excerpt>" + result + "</excerpt>";
    }
    
    /**
     * Includes a string in CDATA delimiters.
     */
    protected String includeInCDATA(String string) {
        return "<![CDATA[" + string + "]]>";
    }
    
    
}
