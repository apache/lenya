/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

package org.apache.lenya.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Similar to the UNIX sed
 */
public class SED {
    static Logger log = Logger.getLogger(SED.class);

    /**
     * Command Line Interface
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: org.apache.lenya.util.SED");
            return;
        }
    }

    /**
     * Substitute prefix, e.g. ".*world.*" by "universe"
     *
     * @param file File which sed shall be applied
     * @param prefixSubstitute Prefix which shall be replaced
     * @param substituteReplacement Prefix which is going to replace the original
     *
     * @throws IOException DOCUMENT ME!
     */
    public static void replaceAll(File file, String substitute, String substituteReplacement) throws IOException {
        log.debug("Replace " + substitute + " by " + substituteReplacement);

        Pattern pattern = Pattern.compile(substitute);

        // Open the file and then get a channel from the stream
        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();

        // Get the file's size and then map it into memory
        int sz = (int)fc.size();
        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);

	// Decode the file into a char buffer
        // Charset and decoder for ISO-8859-15
        Charset charset = Charset.forName("ISO-8859-15");
        CharsetDecoder decoder = charset.newDecoder();
	CharBuffer cb = decoder.decode(bb);

        Matcher matcher = pattern.matcher(cb);
        String outString = matcher.replaceAll(substituteReplacement);
        log.debug(outString);


        FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
        PrintStream ps =new PrintStream(fos);
        ps.print(outString);
        ps.close();
        fos.close();
    }
}
