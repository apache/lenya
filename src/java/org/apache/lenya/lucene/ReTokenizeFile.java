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

/* $Id$  */

package org.apache.lenya.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.lenya.lucene.html.HTMLParser;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;


/**
 * Class to retokenize files
 */
public class ReTokenizeFile {
    private static final Logger log = Logger.getLogger(ReTokenizeFile.class);

    private int offset = 100;
    
    /**
     * The command line entry point
     * @param args The command line args
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: ReTokenizeFile filename word1 word2 ...");

            return;
        }

        try {
            String[] words = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                words[i - 1] = args[i];
            }

            String s = null;

            s = new ReTokenizeFile().getExcerpt(new File(args[0]), words);
            System.out.println("Excerpt: " + s);
        } catch (final FileNotFoundException e) {
            System.err.println(".main(): " + e);
        } catch (final IOException e) {
            System.err.println(".main(): " + e);
        }

    }

    /**
     * Retokenize a file
     * @param file The file to retokenize
     * @return The path to the retokenized file
     * @throws Exception if an error occurs
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
     * Returns an excerpt from a file that contains the specified words
     * @param file The file
     * @param words The words
     * @return The excerpt
     * @throws FileNotFoundException if the file cannot be found
     * @throws IOException if an IO error occurs
     *
     */
    public String getExcerpt(File file, String[] words)
        throws FileNotFoundException, IOException {
        if (file.getName().substring(file.getName().length() - 4).equals(".pdf")) {
            file = new File(file.getAbsolutePath() + ".txt");
        }
        
        String content = readFileWithEncoding(file);

	//log.debug(content);

	content = removeTags(content);

	//log.debug(content);
        
        
        /*java.io.Reader reader = new HTMLParser(file).getReader();
        char[] chars = new char[1024];
        int chars_read;
        java.io.Writer writer = new java.io.StringWriter();

        while ((chars_read = reader.read(chars)) > 0) {
            writer.write(chars, 0, chars_read);
        }*/

        //String html = writer.toString();
        //html = writer.toString();

        
        int index = -1;

        for (int i = 0; i < words.length; i++) {
            index = content.toLowerCase(Locale.ENGLISH).indexOf(words[i].toLowerCase(Locale.ENGLISH));

            if (index >= 0) {
                int start = index - this.offset;

                if (start < 0) {
                    start = 0;
                }

                int end = index + words[i].length() + this.offset;

                if (end >= content.length()) {
                    end = content.length() - 1;
                }

                return content.substring(start, end);
            }
        }

        return null;
    }

    /**
     * Remove tags
     * @param string Content with tags
     * @return Content without tags
     */
    public String removeTags(String string) {
        StringBuffer sb = new StringBuffer("");

        boolean tag = false;

        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
	    if (ch == '<') {
                tag = true;
            } else if (ch == '>') {
                tag = false;
            } else {
                if (!tag) sb.append(string.charAt(i));
            }
        }

        return sb.toString();
    }

    /**
     * Is being used by search-and-results.xsp. Is this really still necessary?
     * @param string content
     * @return content without <>&
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
     * @return The processed string
     */
    public String emphasizeAsXML(String string, String[] words) {

        String lowerCaseString = string.toLowerCase(Locale.ENGLISH);

        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase(Locale.ENGLISH);

            // use uppercase tags so that they are not replaced
            lowerCaseString = lowerCaseString.replaceAll(word, "<WORD>" + word + "</WORD>");
        }

        lowerCaseString = lowerCaseString.toLowerCase(Locale.ENGLISH);

        //if (true) return "<excerpt>" + lowerCaseString + "</excerpt>";
        String result = "";
        StringBuffer buf = new StringBuffer();

        int sourceIndex = 0;
        int index = 0;
        String[] tags = { "<word>", "</word>" };

        while (lowerCaseString.indexOf(tags[0], index) != -1) {
            for (int tag = 0; tag < 2; tag++) {
                int subStringLength = lowerCaseString.indexOf(tags[tag], index) - index;
                String subString = string.substring(sourceIndex, sourceIndex + subStringLength);
                buf.append((includeInCDATA(subString) + tags[tag]));
                sourceIndex += subStringLength;
                index += (subStringLength + tags[tag].length());
            }
        }

        buf.append(includeInCDATA(string.substring(sourceIndex)));
        result = buf.toString();

        return "<excerpt>" + result + "</excerpt>";
    }

    /**
     * Wraps a string in CDATA delimiters.
     * @param string The string to wrap
     * @return The wrapped string
     */
    protected String includeInCDATA(String string) {
        return "<![CDATA[" + string + "]]>";
    }
    
    /**
     * reads a file and if the file is an xml file, determine its encoding
     * @param file the file to read. 
     * (if the file is an xml file with an specified encoding, this will be overwritten) 
     * @return the contents of the file.
     * @throws FileNotFoundException if the file cannot be found
     * @throws IOException if an IO error occurs
     */
    protected String readFileWithEncoding(File file) throws FileNotFoundException, IOException {
        String content = readHtmlFile(file);
        // test if the file contains xml data and extract the encoding
        int endOfFirstTag = content.indexOf(">");
        if(endOfFirstTag > 0 && content.charAt(endOfFirstTag-1) == '?') {
            String upperLine = content.substring(0, endOfFirstTag).toUpperCase(Locale.ENGLISH);
            int encStart = upperLine.indexOf("ENCODING=")+10;
            int encEnd = -1;

            if (encStart > 0) {
                encEnd = upperLine.indexOf("\"", encStart);
                if (encEnd == -1) {
                    encEnd = upperLine.indexOf("\'", encStart);
                }
            }
            if(encStart > 0 && encEnd > 0) {
                String xmlCharset = upperLine.substring(encStart, encEnd);
                try {
                    if (Charset.isSupported(xmlCharset)) {
                        content = readFile(file, Charset.forName(xmlCharset));
                    }
                } catch (IllegalCharsetNameException e) {
                    // do nothing - thrown by Charset.isSupported
                }
            }
        }
        return content;
    }
    
    
    /**
     * read a html file.
     * @param file the file to read
     * @return the content of the file.
     * @throws FileNotFoundException if the file does not exists.
     * @throws IOException if something else went wrong.
     */
    protected String readHtmlFile(File file) throws FileNotFoundException, IOException {
        java.io.Reader reader = new HTMLParser(file).getReader();
        char[] chars = new char[1024];
        int chars_read;
        java.io.Writer writer = new java.io.StringWriter();

        while ((chars_read = reader.read(chars)) > 0) {
            writer.write(chars, 0, chars_read);
        }
        return writer.toString();
    }
    
    /**
     * reads a file in the specified charset.
     * @param file the file to read.
     * @param charset The charset
     * @return the content of the file.
     * @throws FileNotFoundException if the file does not exist.
     * @throws IOException if something else went wrong.
     */
    protected String readFile(File file, Charset charset) throws FileNotFoundException, IOException {
        FileInputStream inputFile = null;
        InputStreamReader inputStream = null;
        BufferedReader bufferReader = null;
        StringBuffer buffer = null;         

        try {
            inputFile = new FileInputStream(file);
            if(charset != null) {
                inputStream = new InputStreamReader(inputFile, charset);
            } else {
                inputStream = new InputStreamReader(inputFile);
            }
            bufferReader = new BufferedReader(inputStream);
            buffer = new StringBuffer();
            String line = "";
            while (bufferReader.ready()) {
                line = bufferReader.readLine();
                buffer.append(line);
            }
        } catch (FileNotFoundException e) {
            log.error("File not found " +e.toString());
        } catch (IOException e) {
            log.error("IO error " +e.toString());
        } finally {
            if (bufferReader != null)
                bufferReader.close();
            if (inputStream != null)
                inputStream.close();
            if (inputFile != null)
                inputFile.close();
        }
        return buffer.toString();
    }

    /**
     * Set offset
     * @param _offset The offset to set
     */
    public void setOffset(int _offset) {
        this.offset = _offset;
    }
}
