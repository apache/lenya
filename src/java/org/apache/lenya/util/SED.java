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
 * Performs pattern substitution similar to the UNIX sed
 */
public class SED {
    private static final Logger log = Logger.getLogger(SED.class);

    /**
     * Substitute prefix, e.g. ".*world.*" by "universe"
     * @param file File which sed shall be applied
     * @param substitute Prefix which shall be replaced
     * @param substituteReplacement Prefix which is going to replace the original
     * @throws IOException if an IO error occurs
     */
    public static void replaceAll(File file, String substitute, String substituteReplacement) throws IOException {
    	PrintStream ps = null;
    	FileOutputStream fos = null;
    	FileInputStream fis = null;
    	FileChannel fc = null;
    	
    	log.debug("Replace " + substitute + " by " + substituteReplacement);        
        
        Pattern pattern = Pattern.compile(substitute);
        
        try {

	        // Open the file and then get a channel from the stream
	        fis = new FileInputStream(file);
	        fc = fis.getChannel();
	
	        // Get the file's size and then map it into memory
	        int sz = (int)fc.size();
	        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);
	
	        // Decode the file into a char buffer
	        Charset charset = Charset.forName("UTF-8");
	        CharsetDecoder decoder = charset.newDecoder();
	        CharBuffer cb = decoder.decode(bb);
	
	        Matcher matcher = pattern.matcher(cb);
	        String outString = matcher.replaceAll(substituteReplacement);
	        log.debug(outString);
	
	        fos = new FileOutputStream(file.getAbsolutePath());
	        ps =new PrintStream(fos);
	        ps.print(outString);
        } catch(final IOException e) {
        	log.error("SED caught exception: " + e.toString());
        } finally {
        	if (ps != null)
        		ps.close();
	        if (fc != null)
	        	fc.close();
	        if (fis != null)
	        	fis.close();
	        if (fos != null)
	        	fos.close();
        }
    }
}
