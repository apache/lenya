
/*
 * $Id: Grep.java,v 1.1 2003/09/25 13:59:08 gregor Exp $
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

import java.util.regex.*;
import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.channels.*;


public class Grep {

	// Charset and decoder for ISO-8859-15
	private static Charset charset = Charset.forName("ISO-8859-15");
	private static CharsetDecoder decoder = charset.newDecoder();

	// The input pattern that we're looking for
	private static Pattern pattern;

	// Compile the pattern from the command line
	//
	private static void compile(String pat) {
	   try {
	      pattern = Pattern.compile(pat);
	   } catch (PatternSyntaxException x) {
	      System.err.println(x.getMessage());
	      System.exit(1);
	   }
	}

	// Search for occurrences of the input pattern in the given file
	//
	private static void grep(File f) throws IOException {

	   // Open the file and then get a channel from the stream
	   FileInputStream fis = new FileInputStream(f);
	   FileChannel fc = fis.getChannel();

	   // Get the file's size and then map it into memory
	   int sz = (int)fc.size();
	   MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);

	   // Decode the file into a char buffer
	   CharBuffer cb = decoder.decode(bb);

	   // Perform the search
	   Matcher pm = pattern.matcher(cb);			// Pattern matcher
	   if (pm.matches()) {
	      System.out.print(f + " matches");
	   }

	   // Close the channel and the stream
	   fc.close();
	}

// Array matches;

  public static void main(String[] args){
     if(args.length == 0){
        return;
     }
     System.out.print(args[0] +" " + args[1]);
     compile(args[0]);
     find(new File(args[1]));
  }

  public static void find(File fileOrDir){
     if(fileOrDir.isDirectory()){
       String[] filesAndDirs=fileOrDir.list();
       for(int i=0;i<filesAndDirs.length;i++){
         find(new File(fileOrDir.getAbsolutePath()+"/"+filesAndDirs[i]));
       }
     }
     else if(fileOrDir.isFile()){
		try {
		grep(fileOrDir);
		} catch (IOException x) {
		System.err.println(fileOrDir + ": " + x);
		}     }
     else{
     }  
  }      
 }
