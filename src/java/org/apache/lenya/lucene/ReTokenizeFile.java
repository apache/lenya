/*
$Id: ReTokenizeFile.java,v 1.15 2003/07/23 13:21:26 gregor Exp $
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

import org.apache.lenya.lucene.html.HTMLParser;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.*;

import java.util.StringTokenizer;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.15 $
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
        StringTokenizer st = new StringTokenizer(string, "<>&");

        StringBuffer sb = new StringBuffer("");

        while (st.hasMoreElements()) {
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
        String[] tags = { "<word>", "</word>" };

        while (lowerCaseString.indexOf(tags[0], index) != -1) {
            for (int tag = 0; tag < 2; tag++) {
                int subStringLength = lowerCaseString.indexOf(tags[tag], index) - index;
                String subString = string.substring(sourceIndex, sourceIndex + subStringLength);
                result += (includeInCDATA(subString) + tags[tag]);
                sourceIndex += subStringLength;
                index += (subStringLength + tags[tag].length());
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
