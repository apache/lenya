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

/* $Id: Grep.java,v 1.5 2004/03/01 16:18:28 gregor Exp $  */

package org.apache.lenya.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to provide a subset of the grep functionality. 
 */
public class Grep {

    // Charset and decoder for ISO-8859-15
    private static Charset charset = Charset.forName("ISO-8859-15");
    private static CharsetDecoder decoder = charset.newDecoder();

    /**
     * Check if the given file contains the pattern
     * 
     * @param file the file which is to be searched for the pattern
     * @param pattern the pattern that is being searched.
     * 
     * @return true if the file contains the string, false otherwise.
     * 
     * @throws IOException
     */
    public static boolean containsPattern(File file, Pattern pattern)
        throws IOException {

        // Open the file and then get a channel from the stream
        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();

        // Get the file's size and then map it into memory
        int sz = (int)fc.size();
        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);

        // Decode the file into a char buffer
        CharBuffer cb = decoder.decode(bb);

        // Perform the search
        Matcher pm = pattern.matcher(cb); // Pattern matcher

        boolean result = pm.find();

        // Close the channel and the stream
        fc.close();
        fis.close();

        return result;
    }

    /**
     * Find all occurences of pattern in a file.
     * 
     * @param file the file to search for occurences of pattern
     * @param pattern the pattern to search for
     * @param group which group in the pattern to return
     * 
     * @return an <code>array</code> of occurences of pattern 
     * (i.e. the groupth group of the match)
     * 
     * @throws IOException if the file could not be read.
     * 
     */
    public static String[] findPattern(File file, Pattern pattern, int group)
        throws IOException {

        ArrayList occurences = new ArrayList();

        // Open the file and then get a channel from the stream
        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();

        // Get the file's size and then map it into memory
        int sz = (int)fc.size();
        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);

        // Decode the file into a char buffer
        CharBuffer cb = decoder.decode(bb);

        // Perform the search
        Matcher pm = pattern.matcher(cb); // Pattern matcher

        while (pm.find()) {
            occurences.add(pm.group(group));
        }

        // Close the channel and the stream
        fc.close();
        fis.close();

        return (String[])occurences.toArray(new String[occurences.size()]);

    }

    /**
     * Find all files below the given file which contain the given pattern.
     * 
     * @param file the file where to start the search for the pattern.
     * @param pattern the pattern to search for.
     * 
     * @return an array of files which contain the pattern
     * 
     * @throws IOException if any of the files could not be opened.
     */
    private static List find_internal(File file, Pattern pattern)
        throws IOException {
        ArrayList fileList = new ArrayList();

        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                fileList.addAll(
                    find_internal(
                        new File(file.getAbsolutePath(), children[i]),
                        pattern));
            }
        } else if (file.isFile() && containsPattern(file, pattern)) {
            fileList.add(file);
        }
        return fileList;
    }

    /**
     * Find all files below the given file which contain the given search string.
     * 
     * @param file the where to start the search
     * @param searchString the string to search for.
     * 
     * @return an array of files which contain the search string.
     * 
     * @throws IOException if any of the files could not be opened.
     */
    public static File[] find(File file, String searchString)
        throws IOException {
        Pattern pattern = Pattern.compile(searchString);
        List fileList = find_internal(file, pattern);
        return (File[])fileList.toArray(new File[fileList.size()]);
    }
}
