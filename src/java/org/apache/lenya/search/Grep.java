
/*
 * $Id: Grep.java,v 1.2 2003/09/30 09:03:28 egli Exp $
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
 *    by lenya (http://cocoon.apache.org/lenya/)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact board@apache.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://cocoon.apache.org/lenya/)"
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
 * 
 * Utility class to provide a subset of the grep functionality. 
 *  
 * @version $Revision: 1.2 $
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
